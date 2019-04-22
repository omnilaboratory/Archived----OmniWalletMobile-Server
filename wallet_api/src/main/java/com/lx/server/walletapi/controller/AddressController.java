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
import com.lx.server.service.WalletAddressService;
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
	private WalletServcie walletServcie;
	
	@PostMapping("create")
	@ApiOperation("创建新地址")
	public ResultTO createAddress(WalletAddress walletAddress) {
		Assert.isTrue(Tools.checkStringExist(walletAddress.getAddress()), "address is empty");
		Assert.isTrue(Tools.checkStringExist(walletAddress.getAddressName()), "addressName is empty");
		
		walletAddress.setCreateTime(new Date());
		walletAddress.setIsEnable(true);
		walletAddress.setUserId(getUserId());
		
		int count =  walletAddressService.pageCount(new HashMap<String,Object>() {{
			put("uesrId", walletAddress.getUserId());
			put("address", walletAddress.getAddress());
		}});
		Assert.isTrue(count==0, "address is exist");
		
		KafkaMessage message = new KafkaMessage(1,getUserId(), null, walletAddress);
		this.kafkaTemplate.send(EnumKafkaTopic.WalletAddressTopic.value, JSON.toJSONString(message));
		return ResultTO.newSuccessResult("success");
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
			for (Object object : nodes) {
				Map<String, Object> node = (Map<String, Object>)object;
				List<Map<String, Object>> list = walletServcie.getAllBalanceByAddress(node.get("address").toString());
				node.put("assets", list);
			}
		}
		return ResultTO.newSuccessResult("success",page);
	}
}
