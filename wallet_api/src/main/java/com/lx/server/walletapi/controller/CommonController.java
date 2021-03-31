package com.lx.server.walletapi.controller;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.crypto.NoSuchPaddingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONObject;
import com.lx.server.bean.Page;
import com.lx.server.bean.ResultTO;
import com.lx.server.enums.EnumFolderURI;
import com.lx.server.pojo.DefaultAsset;
import com.lx.server.pojo.UserClient;
import com.lx.server.pojo.UserFeedback;
import com.lx.server.service.AppVersionService;
import com.lx.server.service.CommonService;
import com.lx.server.service.DefaultAssetService;
import com.lx.server.service.UserClientService;
import com.lx.server.utils.AESUtil;
import com.lx.server.utils.RSAEncrypt;
import com.lx.server.utils.Tools;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("api/common")
@Api(tags = {"Common Interfaces"})
public class CommonController extends AbstractController{
	
	@Autowired
	private UserClientService userClientService;
	
	@Autowired
	private CommonService commonService;
	
	@PostConstruct
	public void InitBinder() throws NoSuchAlgorithmException, NoSuchPaddingException {
		RSAEncrypt.init();
	}
	
	
	@SuppressWarnings("serial")
	@PostMapping("createUser")
	@ApiOperation("create new user")
	public ResultTO createUser(String userId,String nickname,String password) throws NoSuchAlgorithmException, InvocationTargetException, IllegalAccessException {
		this.logger.info("createUser");
		Assert.isTrue(Tools.isValidMessageAudio(userId), "userId is not md5 type");
		Assert.isTrue(Tools.isValidMessageAudio(password), "password is wrong");
		Map<String, Object> userClient = userClientService.createNewUser(userId,nickname,password);
		if (userClient==null) {
			return ResultTO.newFailResult("create user fail");
		}
		
		return ResultTO.newSuccessResult(new HashMap<String,Object>() {{
			put("userId", userClient.get("id"));
			put("token", userClient.get("token"));
		}});
	}
	
	
	@SuppressWarnings("serial")
	@PostMapping("restoreUser")
	@ApiOperation("restore account according to mnemonic words")
	public ResultTO restoreUser(String userId,String password,String newPsw) {
		this.logger.info("restoreUser");
		Assert.isTrue(Tools.isValidMessageAudio(userId), "userId is not md5 type");
		Assert.isTrue(Tools.isValidMessageAudio(newPsw), "pinPsw is not md5 type");
		UserClient userClient = userClientService.selectObject(userId);
		if (userClient==null) {
			return ResultTO.newFailResult("your mnemonic is wrong");
		}
		if (Tools.checkStringExist(userClient.getPassword())) {
			Assert.isTrue(userClient.getPassword().equals(password), "old pin is wrong");
		}
		
		userClient.setLastLoginTime(new Date());
		userClientService.update(new HashMap<String,Object>() {{
			put("lastLoginTime", userClient.getLastLoginTime());
			put("password", newPsw);
			put("id", userClient.getId());
		}});
		
		Map<String, Object> info =  getFpUserInfo(userClient);
		info.put("userId", userClient.getId());
		info.put("token", userClientService.generateToken(userClient));
		return ResultTO.newSuccessResult(info);
	}
	
	/**
	 * 单图片上传
	 * @param file
	 * @return
	 */
	@PostMapping("uploadImage")
	@ApiOperation("upload a image")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "file", value = "image"),
	})
	public ResultTO uploadFile(MultipartFile file){
		Assert.isTrue(file!=null, "image does not exist");
		String url = this.uploadImage(EnumFolderURI.getEnumByType(0).value,file);
		if (url!=null) {
			return ResultTO.newSuccessResult("upload succeed",url);
		}
		return ResultTO.newFailResult("upload failed");
	}
	/**
	 * 多图片上传
	 * @param file
	 * @return
	 */
	@PostMapping("uploadImages")
	@ApiOperation("upload multiple images")
	@ApiImplicitParams({
	})
	public ResultTO uploadFile(MultipartFile files[]){
		Assert.isTrue(files!=null, "images do not exist");
		String url = Tools.uploadImages(EnumFolderURI.getEnumByType(0).value,files);
		if (url!=null) {
			return ResultTO.newSuccessResult("upload succeed",url);
		}
		return ResultTO.newFailResult("upload failed");
	}
	
//	@Autowired
//    private KafkaTemplate<String, Object> kafkaTemplate;
	
	/**
	 * get exchange rate
	 * @return
	 */
	@GetMapping("btcAndUsdtExchangeRate")
	@ApiOperation("real time exchange rates for BTC, USDT and EU")
	public ResultTO btcAndUsdtExchangeRates(){
		List<Map<String, Object>> data = new ArrayList<>();
		JSONObject jsonObject = commonService.getRateFromBlockChain();
		if (jsonObject!=null) {
			//获取对应数字币的汇率
			BigDecimal rate = jsonObject.getJSONObject("USD").getBigDecimal("last");
			Map<String, Object> node = new HashMap<>();
			node.put("name", "btc");
			node.put("rate", rate);
			data.add(0,node);
			
			rate = jsonObject.getJSONObject("CNY").getBigDecimal("last");
			node = new HashMap<>();
			node.put("name", "btcCNY");
			node.put("rate", rate);
			data.add(1,node);
			return ResultTO.newSuccessResult(data);
		}
		return ResultTO.newFailResult("fail");
		
//		BigDecimal rate = commonService.getCoinExchangeRate("btc","usd");
//		Assert.notNull(rate, "fail to get  btc rate ");
//		Map<String, Object> node = new HashMap<>();
//		node.put("name", "btc");
//		node.put("rate", rate);
//		data.add(0,node);
//		
//		rate = commonService.getCoinExchangeRate("btc","cny");
//		Assert.notNull(rate, "fail to get  btc rate ");
//		node = new HashMap<>();
//		node.put("name", "btcCNY");
//		node.put("rate", rate);
//		data.add(1,node);
//		
//		rate =commonService.getCoinExchangeRate("usdt","usd");
//		Assert.notNull(rate, "fail to get  usdt rate ");
//		node = new HashMap<>();
//		node.put("name", "usdt");
//		node.put("rate", rate);
//		data.add(2,node);
//		
//		
//		rate =commonService.getCoinExchangeRate("usdt","cny");
//		Assert.notNull(rate, "fail to get  usdt rate ");
//		node = new HashMap<>();
//		node.put("name", "usdtCny");
//		node.put("rate", rate);
//		data.add(3,node);
//		
//		rate =commonService.getExchangeRateBaseEUR("USD");
//		Assert.notNull(rate, "fail to get  usd rate ");
//		node = new HashMap<>();
//		node.put("name", "EUR");
//		node.put("rate", BigDecimal.ONE.divide(rate, 4, RoundingMode.FLOOR));
//		data.add(4,node);
	}
	
	 @Autowired
	 private AppVersionService appVersionService;
	
	@GetMapping("getNewestVersion")
	@ApiOperation("get the latest version info")
	public ResultTO validateVersion() throws Exception {
		Page page = appVersionService.page(new HashMap<String, Object>(), 1, 1);
        if (page!=null) {
			List<Object> datas = page.getData();
			if (datas!=null&&datas.size()==1) {
				return ResultTO.newSuccessResult(datas.get(0));	
			}
		}
		return ResultTO.newFailResult("");
	}
	
	@GetMapping("getVersionList")
	@ApiOperation("get historical versions information")
	public ResultTO getVersionList() throws Exception {
		Page page = appVersionService.page(new HashMap<String, Object>(), 1, 10);
		if (page!=null) {
			return ResultTO.newSuccessResult(page);
		}
		return ResultTO.newFailResult("");
	}
	
	@Autowired
	private DefaultAssetService defaultAssetService;
	
	
	@GetMapping("getDefautAssetList")
	@ApiOperation("get default assets list")
	public ResultTO getDefautAssetList() throws Exception {
		List<DefaultAsset> assets = defaultAssetService.selectObjectList(null);
		if (assets!=null) {
			return ResultTO.newSuccessResult(assets);
		}
		return ResultTO.newFailResult("");
	}
	@GetMapping("getTransactionsByAddress")
	@ApiOperation("getTransactionsByAddress")
	public ResultTO getTransactionsByAddress(String address) throws Exception {
		return ResultTO.newSuccessResult(commonService.getTransactionsByAddress(address) );
	}
	
	
//	@GetMapping("testAES")
//	@ApiOperation("testAES")
	public ResultTO testAES() throws Exception {
		String cIv = "e9cc3f037e8ed880";
        String s = "L5i7jigYHYLyZbAsf9QUAc8BWtXq7YD6U7b7m8717Jg4xuxk5mAx";
//        String s = "++Vbcs3XporPFdLiZMdKlqgH2GSQc1aG86RSqo+kucg5dGlEPJ5GxQjiS0p4JL4jkwfZG6bTuZDzErSsQTCywQ==";
        String key = "e9cc3f037e8ed88090a7ed47d304b129";
        System.out.println("s:" + s);

        String s1 = AESUtil.encrypt(s, key,cIv);
        
        System.out.println("s1:" + s1);
        System.out.println("s2:"+AESUtil.decrypt(s1, key,cIv));
        
		return ResultTO.newSuccessResult(AESUtil.decrypt(s1, key,cIv));
	}
	
//	@Autowired
//    private KafkaTemplate<String, Object> kafkaTemplate;
	
//	@PostMapping("testKafka")
//	public ResultTO testKafka(UserFeedback feedback) {
//		KafkaMessage message = new KafkaMessage(EnumKafkaTopic.UserFeedback.value,"ef8c6d919538a26f4065989597a652aa", null, feedback);
//		this.kafkaTemplate.send(EnumKafkaTopic.UserFeedback.value, JSON.toJSONString(message));
//		return ResultTO.newSuccessResult("success");
//	}
	
	private Integer index = 0;
//	@Scheduled(fixedRate=1000)
	private void schedule() {
		index++;
		UserFeedback feedback = new UserFeedback();
		feedback.setTitle("title"+index);
		feedback.setContent("content"+index);
		feedback.setEmail("email"+index);
		feedback.setImageUrls("imageUrls"+index);
//		this.testKafka(feedback);
	}
	
}
