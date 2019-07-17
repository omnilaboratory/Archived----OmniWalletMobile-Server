package com.lx.server.kafka.consumer;

import java.util.Date;
import java.util.Optional;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lx.server.kafka.bean.KafkaMessage;
import com.lx.server.pojo.LogTransaction;
import com.lx.server.pojo.LogUserAsset;
import com.lx.server.pojo.UserFeedback;
import com.lx.server.service.LogTransactionService;
import com.lx.server.service.LogUserAssetService;
import com.lx.server.service.UserFeedbackService;
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
	
	@Autowired
	private UserFeedbackService userFeedbackService;
	
	@Autowired
	private LogTransactionService logTransactionService;
	
	private final Log logger = LogFactory.getLog(getClass());
	
	
	@KafkaListener(topics = { "user.feedback" })
	public void userFeedback(ConsumerRecord<?, ?> record) {
		logger.info("user.feedback");
		KafkaMessage info = getKafkaMsg(record);
		JSONObject jsonObject = (JSONObject) info.getData();
		UserFeedback feedback = new UserFeedback();
		feedback.setTitle(jsonObject.getString("title"));
		feedback.setContent(jsonObject.getString("content"));
		feedback.setEmail(jsonObject.getString("email"));
		feedback.setImageUrls(jsonObject.getString("imageUrls"));
		feedback.setCreateTime(new Date());
		feedback.setUserId(info.getUserId());
		feedback.setState((byte) 0);
		userFeedbackService.insert(feedback);
	}
	
	@KafkaListener(topics = { "wallet.LogTransaction" })
	public void walletLogTransaction(ConsumerRecord<?, ?> record) {
		logger.info("wallet.LogTransaction");
		KafkaMessage info = getKafkaMsg(record);
		JSONObject jsonObject = (JSONObject) info.getData();
		LogTransaction logTransaction = new LogTransaction();
		logTransaction.setUserId(jsonObject.getString("userId"));
		logTransaction.setFromAddr(jsonObject.getString("fromAddr"));
		logTransaction.setToAddr(jsonObject.getString("toAddr"));
		logTransaction.setAssetId(jsonObject.getLong("assetId"));
    	logTransaction.setAmount(jsonObject.getBigDecimal("amount"));
    	logTransaction.setFee(jsonObject.getBigDecimal("fee"));
    	logTransaction.setTxid(jsonObject.getString("txid"));
    	logTransaction.setCreateTime(jsonObject.getDate("createTime"));
    	logTransactionService.insert(logTransaction);
	}
	
	
	@Autowired
	private LogUserAssetService logUserAssetService;
	
	@KafkaListener(topics = { "wallet.LogUserAsset" })
	public void userAssetLog(ConsumerRecord<?, ?> record) {
		logger.info("wallet.LogUserAsset");
		KafkaMessage info = getKafkaMsg(record);
		JSONObject jsonObject = (JSONObject) info.getData();
		LogUserAsset  log = new LogUserAsset();
		log.setAddr(jsonObject.getString("addr"));
		log.setAssetId(jsonObject.getLong("assetId"));
		log.setAmount(jsonObject.getBigDecimal("amount"));
		log.setCreateTime(jsonObject.getDate("createTime"));
		log.setUserId(jsonObject.getString("userId"));
		logUserAssetService.insert(log);
	}
	
//	@KafkaListener(topics = {"wallet.userTopic"})
//    public void userTopicListen(ConsumerRecord<?,?> record){
//		System.err.println("wallet.userTopic");
//		KafkaMessage info = getKafkaMsg(record);
//    }
//	
//	@KafkaListener(topics = {"wallet.addressTopic"})
//	public void WalletAddressTopicListen(ConsumerRecord<?,?> record){
//		KafkaMessage info = getKafkaMsg(record);
//		switch (info.getType()) {
//		case 1://创建新的钱包地址
//			this.createWalletAddress(info);
//			break;
//		case 2://创建新的钱包资产地址
//			this.createWalletAssetOfAddress(info);
//			break;
//		}
//	}
//	
//	private void createWalletAddress(KafkaMessage info) {
//		logger.info("createWalletAddress");
//		logger.info(info);
//		JSONObject jsonObject = (JSONObject) info.getData();
//		if (info!=null&&jsonObject.containsKey("address")) {
//			WalletAddress address = new WalletAddress();
//			address.setUserId(info.getUserId());
//			address.setAddress(jsonObject.getString("address"));
//			int count = walletAddressService.pageCount(new HashMap<String,Object>(){
//				{
//					put("userId", address.getUserId());
//					put("address", address.getAddress());
//					
//				}
//			});
//			
//			if (count==0) {
//				address.setAddressName(jsonObject.getString("addressName"));
//				address.setAddressIndex(jsonObject.getInteger("addressIndex"));
//				address.setCreateTime(new Date());
//				address.setIsEnable(true);
//				address.setVisible(true);
//				walletAddressService.insert(address);
//				
//				WalletAsset asset = new WalletAsset();
//				asset.setUserId(info.getUserId());
//				asset.setAssetName("BTC");
//				asset.setAddress(address.getAddress());
//				asset.setVisible(true);
//				asset.setAssetType((byte) 0);
//				asset.setAssetId(0);
//				asset.setCreateTime(new Date());
//				walletAssetService.insert(asset);
//				
//				asset = new WalletAsset();
//				asset.setUserId(info.getUserId());
//				asset.setAssetName("OMNI");
//				asset.setAddress(address.getAddress());
//				asset.setVisible(true);
//				asset.setAssetType((byte) 1);
//				asset.setAssetId(1);
//				asset.setCreateTime(new Date());
//				walletAssetService.insert(asset);
//				
//				asset = new WalletAsset();
//				asset.setUserId(info.getUserId());
//				asset.setAssetName("LunarX");
//				asset.setAddress(address.getAddress());
//				asset.setVisible(true);
//				asset.setAssetType((byte) 1);
//				asset.setAssetId(361);
//				asset.setCreateTime(new Date());
//				walletAssetService.insert(asset);
//			}
//			
//		}
//	}
//	@Transactional(isolation = Isolation.DEFAULT, propagation = Propagation.REQUIRED)
//	private void createWalletAssetOfAddress(KafkaMessage info) {
//		JSONObject jsonObject = (JSONObject) info.getData();
//		if (info!=null&&jsonObject.containsKey("assetId")) {
//			WalletAsset asset = new WalletAsset();
//			asset.setUserId(info.getUserId());
//			asset.setAddress(jsonObject.getString("address"));
//			asset.setAssetType(jsonObject.getByte("assetType"));
//			asset.setAssetId(jsonObject.getInteger("assetId"));
//			asset.setCreateTime(new Date());
//			walletAssetService.insert(asset);
//		}
//	}
	
	private KafkaMessage getKafkaMsg(ConsumerRecord<?, ?> record) {
		Optional<Object> kafkaMessage = (Optional<Object>) Optional.ofNullable(record.value());
		Object message = kafkaMessage.get();
		JSONObject jsonObject = JSON.parseObject(message.toString());
		KafkaMessage info = new KafkaMessage(
				jsonObject.getString("type"), 
				jsonObject.getString("userId"),
				jsonObject.getString("title"),
				jsonObject.getJSONObject("data")
				);
		return info;
	}
}
