package com.lx.server.schedule;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lx.server.enums.EnumKafkaTopic;
import com.lx.server.kafka.bean.KafkaMessage;
import com.lx.server.pojo.LogUserAsset;
import com.lx.server.pojo.LogUserAssetPrice;
import com.lx.server.pojo.WalletAsset;
import com.lx.server.service.LogUserAssetPriceService;
import com.lx.server.service.WalletAssetService;
import com.lx.server.service.WalletService;

@Component
public class WalletSchedule {

	private final Log logger = LogFactory.getLog(getClass());
	
	@Autowired
	private WalletAssetService assetService;
	@Autowired
	private WalletService walletService;
	
	@Autowired
	private LogUserAssetPriceService assetPriceService;
	
	
	@Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;
	
	@SuppressWarnings("serial")
	@Scheduled(cron = "0 0/5 * * * ?")
	private void createUserAssetLog() throws Exception {
		logger.info("createUserAssetLog");
		//从交易所获取资产汇率信息
		
		//btc
		String url = "https://blockchain.info/ticker";
		RestTemplate client = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<MultiValueMap<String, String>>(null,headers);
		ResponseEntity<String> response = client.exchange(url, HttpMethod.GET, requestEntity, String.class);
		JSONObject json = JSON.parseObject(response.getBody());
		JSONObject usd = json.getJSONObject("USD");
		JSONObject cny = json.getJSONObject("CNY");
		JSONObject eur = json.getJSONObject("EUR");
		
		Date now = new Date();
		LogUserAssetPrice assetPrice = new LogUserAssetPrice();
		assetPrice.setAssetId((long) 0);
		assetPrice.setPriceCny(cny.getBigDecimal("last"));
		assetPrice.setPriceEur(eur.getBigDecimal("last"));
		assetPrice.setPriceUsd(usd.getBigDecimal("last"));
		assetPrice.setPriceInfo(json.toJSONString());
		assetPrice.setCreateTime(now);
		assetPriceService.insert(assetPrice);
		
		//获取所有的地址下的资产
		List<WalletAsset> assets = assetService.selectObjectList(new HashMap<String,Object>(){{
			put("assetId", 0);
		}});
		//插入数据到用户资产价值记录表
		Map<String, Object> node=null;
		LogUserAsset  log = null;
		for (WalletAsset walletAsset : assets) {
			node =walletService.getBtcBalance(walletAsset.getAddress());
			log = new LogUserAsset();
			log.setUserId(walletAsset.getUserId());
			log.setAddr(walletAsset.getAddress());
			log.setAssetId(assetPrice.getAssetId());
			log.setAmount(new BigDecimal(node.get("balance").toString()));
			log.setCreateTime(assetPrice.getCreateTime());
			KafkaMessage message = new KafkaMessage(EnumKafkaTopic.LogUserAsset.value,walletAsset.getUserId(), null, log);
			this.kafkaTemplate.send(EnumKafkaTopic.LogUserAsset.value, JSON.toJSONString(message));
		}
		//usdt
	}
}
