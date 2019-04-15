package com.lx.server.walletapi.controller;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
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
	
	@PostMapping("create")
	@ApiOperation("创建新地址")
	public ResultTO createAddress(WalletAddress address) {
		KafkaMessage message = new KafkaMessage(1,getUserId(), null, address);
		this.kafkaTemplate.send(EnumKafkaTopic.WalletAddressTopic.value, JSON.toJSONString(message));
		return ResultTO.newSuccessResult("success");
	}
	@SuppressWarnings("serial")
	@GetMapping("list")
	@ApiOperation("获取地址列表")
	public ResultTO getAddressList(Integer pageIndex,Integer pageSize) {
		if (pageIndex==null||pageIndex<1) {
			pageIndex =1;
		}
		if (pageSize==null||pageSize<1) {
			pageSize =10;
		}
		Page page = this.walletAddressService.page(new HashMap<String,Object>() {{
			put("userId", getUserId());
		}}, pageIndex, pageSize);
		return ResultTO.newSuccessResult("success",page);
	}
}
