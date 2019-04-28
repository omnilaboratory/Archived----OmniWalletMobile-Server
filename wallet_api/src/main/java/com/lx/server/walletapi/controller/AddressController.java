package com.lx.server.walletapi.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.lx.server.bean.Page;
import com.lx.server.bean.ResultTO;
import com.lx.server.enums.EnumKafkaTopic;
import com.lx.server.kafka.bean.KafkaMessage;
import com.lx.server.pojo.WalletAddress;
import com.lx.server.pojo.WalletAsset;
import com.lx.server.service.CommonService;
import com.lx.server.service.WalletAddressService;
import com.lx.server.service.WalletAssetService;
import com.lx.server.service.WalletServcie;
import com.lx.server.utils.Tools;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("api/wallet/address")
@Api(tags = {"钱包地址"})
public class AddressController extends AbstractController{
	

	@Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;
	
	@Autowired
	private WalletAddressService walletAddressService;
	
	@Autowired
	private WalletAssetService assetService;
	
	@Autowired
	private WalletServcie walletServcie;
	
	@Autowired
	private CommonService commonService;
	
	@PostMapping("create")
	@ApiOperation("创建新地址")
	public ResultTO createAddress(WalletAddress walletAddress) {
		Assert.isTrue(Tools.checkStringExist(walletAddress.getAddress()), "address is empty");
		Assert.isTrue(Tools.checkStringExist(walletAddress.getAddressName()), "addressName is empty");
		
		walletAddress.setCreateTime(new Date());
		walletAddress.setIsEnable(true);
		walletAddress.setUserId(getUserId());
		
		int count =  walletAddressService.pageCount(new HashMap<String,Object>() {{
			put("userId", walletAddress.getUserId());
			put("address", walletAddress.getAddress());
		}});
		Assert.isTrue(count==0, "address is exist");
		
		KafkaMessage message = new KafkaMessage(1,getUserId(), null, walletAddress);
		this.kafkaTemplate.send(EnumKafkaTopic.WalletAddressTopic.value, JSON.toJSONString(message));
		return ResultTO.newSuccessResult("success");
	}
	
	@SuppressWarnings("serial")
	@PostMapping("addAsset")
	@ApiOperation("添加资产")
	public ResultTO setVisible(String address, Integer assetId,String assetName) {
		Assert.isTrue(Tools.checkStringExist(address), "address is null");
		Assert.notNull(assetId, "assetId is null");
		
		WalletAsset asset = new WalletAsset();
		asset.setUserId(getUserId());
		asset.setAssetName("Btc");
		asset.setAddress(address);
		asset.setVisible(true);
		asset.setAssetType((byte) 0);
		asset.setAssetId(assetId);
		asset.setCreateTime(new Date());
		
		int count = assetService.pageCount(new HashMap<String,Object>() {{
			put("address", asset.getAddress());
			put("assetId", asset.getAssetId());
		}});
		if (count>0) {
			return ResultTO.newSuccessResult("success");
		}
		
		if (assetService.insert(asset)>0) {
			return ResultTO.newSuccessResult("success");
		}
		return ResultTO.newFailResult("fail");
	}
	
	@SuppressWarnings("serial")
	@PostMapping("setVisible")
	@ApiOperation("设置address是否显示")
	public ResultTO setVisible(String address, Boolean visible) {
		Assert.isTrue(Tools.checkStringExist(address), "address is null");
		Assert.notNull(visible, "visible is null");
		if (walletAddressService.update(new HashMap<String,Object>() {{
			put("n_address", address);
			put("visible", visible);
		}})>0) {
			return ResultTO.newSuccessResult("success");
		}
		return ResultTO.newFailResult("fail");
	}
	
	
	
	@SuppressWarnings({ "serial", "unchecked" })
	@GetMapping("list")
	@ApiOperation("获取地址列表")
	public ResultTO getAddressList(Integer pageIndex,Integer pageSize) throws Exception {
		if (pageIndex==null||pageIndex<1) {
			pageIndex =1;
		}
		if (pageSize==null||pageSize<1) {
			pageSize =100;
		}
		Page page = this.walletAddressService.page(new HashMap<String,Object>() {{
			put("userId", getUserId());
		}}, pageIndex, pageSize);
		
		if (page!=null) {
			List<Object> nodes = page.getData();
			logger.info(nodes);
			for (Object object : nodes) {
				Map<String, Object> node = (Map<String, Object>)object;
				
				List<Map<String, Object>> list = walletServcie.getAllBalanceByAddress(node.get("address").toString());
				logger.info("assetList from omni");
				logger.info(list);
				String address = node.get("address").toString();
				List<Map<String, Object>> assetList = assetService.selectMapList(new HashMap<String,Object>() {{
					put("address", address);
				}});
				logger.info("assetList from mysql");
				logger.info(assetList);
				boolean flag = false;
				for (Map<String, Object> map : assetList) {
					Integer assetId = (Integer) map.get("assetId");
					flag =true;
					for (Map<String, Object> btcNode : list) {
						Integer tempId = (Integer) btcNode.get("propertyid");
						if (assetId.compareTo(tempId)==0&&tempId.compareTo(0)!=0) {
							btcNode.put("visible", map.get("visible"));
							flag = false;
							break;
						}
						if (tempId.compareTo(0)==0) {
							flag=false;
						}
					}
					if (flag) {
						Map<String, Object> btcNode = new HashMap<>();
						btcNode.put("propertyid", map.get("assetId"));
						btcNode.put("name", map.get("assetName"));
						btcNode.put("address", address);
						btcNode.put("account", "");
						btcNode.put("balance", 0);
						btcNode.put("reserved", 0);
						btcNode.put("frozen", 0);
						list.add(btcNode);
					}
					
				}
				node.put("assets", list);
			}
		}
		return ResultTO.newSuccessResult("success",page);
	}
	
	
	/**
	 * 根据address获取btc交易记录
	 * @return
	 * @throws Exception 
	 */
	@GetMapping("getTransactionsByAddress")
	@ApiOperation("根据address获取btc交易记录")
	public ResultTO getTransactionsByAddress(String address) throws Exception{
		Assert.isTrue(Tools.checkStringExist(address), "address is empty");
		Map<String, Object> data = commonService.getTransactionsByAddress(address);
		if (data!=null) {
			return ResultTO.newSuccessResult(data);
		}
		return ResultTO.newFailResult("fail");
	}
	
	/**
	 * 根据address获取omni交易记录
	 * @return
	 * @throws Exception 
	 */
	@GetMapping("getOmniTransactionsByAddress")
	@ApiOperation("根据address获取omni交易记录")
	public ResultTO getOmniTransactionsByAddress(String address,Integer assetId) throws Exception{
		Assert.isTrue(Tools.checkStringExist(address), "address is empty");
		Assert.isTrue(assetId!=null&&assetId>0, "assetId is empty");
		Map<String, Object> data = commonService.getOmniTransactions(address,assetId);
		if (data!=null) {
			return ResultTO.newSuccessResult(data);
		}
		return ResultTO.newFailResult("fail");
	}
}
