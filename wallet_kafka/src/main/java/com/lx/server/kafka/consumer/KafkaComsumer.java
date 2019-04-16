package com.lx.server.kafka.consumer;

import java.util.Date;
import java.util.Optional;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lx.server.kafka.bean.KafkaMessage;
import com.lx.server.pojo.WalletAddress;
import com.lx.server.pojo.WalletAsset;
import com.lx.server.service.WalletAddressService;
import com.lx.server.service.WalletAssetService;


//DefaultTopic("wallet.defaultTopic"),
//UserTopic("wallet.userTopic"),
//WalletAddressTopic("wallet.addressTopic"),


@Component
public class KafkaComsumer {
	
	@Autowired
	private WalletAddressService walletAddressService;
	
	@Autowired
	private WalletAssetService walletAssetService;
	
	@KafkaListener(topics = {"wallet.userTopic"})
    public void userTopicListen(ConsumerRecord<?,?> record){
		KafkaMessage info = getKafkaMsg(record);
    }
	
	@KafkaListener(topics = {"wallet.addressTopic"})
	public void WalletAddressTopicListen(ConsumerRecord<?,?> record){
		KafkaMessage info = getKafkaMsg(record);
		switch (info.getType()) {
		case 1://创建新的钱包地址
			this.createWalletAddress(info);
			break;
		case 2://创建新的钱包资产地址
			this.createWalletAssetAddress(info);
			break;
		}
	}
	
	private void createWalletAddress(KafkaMessage info) {
		JSONObject jsonObject = (JSONObject) info.getData();
		if (info!=null&&jsonObject.containsKey("address")) {
			WalletAddress address = new WalletAddress();
			address.setUserId(info.getUserId());
			address.setAddress(jsonObject.getString("address"));
			address.setAddressName(jsonObject.getString("addressName"));
			address.setAddressIndex(jsonObject.getInteger("addressIndex"));
			address.setCreateTime(new Date());
			address.setIsEnable(true);
			walletAddressService.insert(address);
		}
	}
	
	private void createWalletAssetAddress(KafkaMessage info) {
		JSONObject jsonObject = (JSONObject) info.getData();
		if (info!=null&&jsonObject.containsKey("assetId")) {
			WalletAsset asset = new WalletAsset();
			asset.setUserId(info.getUserId());
			asset.setAddressId(jsonObject.getInteger("addressId"));
			asset.setAssetType(jsonObject.getByte("assetType"));
			asset.setAssetId(jsonObject.getInteger("assetId"));
			asset.setCreateTime(new Date());
			walletAssetService.insert(asset);
		}
	}
	
	
	
	
	private KafkaMessage getKafkaMsg(ConsumerRecord<?, ?> record) {
		Optional<KafkaMessage> kafkaMessage = (Optional<KafkaMessage>) Optional.ofNullable(record.value());
		Object message = kafkaMessage.get();
		JSONObject jsonObject = JSON.parseObject(message.toString());
		KafkaMessage info = new KafkaMessage(
				jsonObject.getInteger("type"), 
				jsonObject.getString("userId"),
				jsonObject.getString("title"),
				jsonObject.getJSONObject("data")
				);
		System.out.println(info);
		return info;
	}
}
