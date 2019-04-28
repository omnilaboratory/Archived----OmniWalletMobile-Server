package com.lx.server.walletapi.controller;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.lx.server.bean.ResultTO;
import com.lx.server.enums.EnumKafkaTopic;
import com.lx.server.kafka.bean.KafkaMessage;
import com.lx.server.pojo.WalletAsset;
import com.lx.server.service.PopularAssetService;
import com.lx.server.service.WalletAssetService;
import com.lx.server.utils.Tools;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("api/wallet/asset")
@Api(tags = {"钱包资产地址"})
public class AssetController extends AbstractController{
	

	@Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;
	
	@Autowired
	private WalletAssetService walletAssetService;
	
	@Autowired
	private PopularAssetService popularAssetService;
	
	@PostMapping("create")
	@ApiOperation("创建新地址")
	public ResultTO createAddress(WalletAsset info) {
		KafkaMessage message = new KafkaMessage(2,getUserId(), null, info);
		this.kafkaTemplate.send(EnumKafkaTopic.WalletAddressTopic.value, JSON.toJSONString(message));
		return ResultTO.newSuccessResult("success");
	}
	
	
	@SuppressWarnings("serial")
	@GetMapping("list")
	@ApiOperation("获取地址资产列表")
	public ResultTO getAddressList(String address) {
		List<WalletAsset> list = this.walletAssetService.selectObjectList(new HashMap<String,Object>() {{
			put("userId", getUserId());
			put("address", address);
		}});
		return ResultTO.newSuccessResult(list);
	}
	
	@SuppressWarnings("serial")
	@PostMapping("setAssetVisible")
	@ApiOperation("设置asset是否显示")
	public ResultTO setAssetVisible(String address,Integer assetId, Boolean visible) {
		Assert.isTrue(Tools.checkStringExist(address), "address is null");
		Assert.isTrue(assetId!=null && assetId>0, "assetId is wrong");
		Assert.notNull(visible, "visible is null");
		if (walletAssetService.update(new HashMap<String,Object>() {{
			put("n_assetId", assetId);
			put("n_address", address);
			put("visible", visible);
		}})>0) {
			return ResultTO.newSuccessResult("success");
		}
		return ResultTO.newFailResult("fail");
	}
	
	@GetMapping("getPopularAssetList")
	@ApiOperation("获取推广资产列表")
	public ResultTO getPopularAssetList() {
		return ResultTO.newSuccessResult(popularAssetService.selectObjectList(null));
	}
	
	
	
}
