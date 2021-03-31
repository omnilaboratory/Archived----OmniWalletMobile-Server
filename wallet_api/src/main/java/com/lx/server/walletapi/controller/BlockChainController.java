package com.lx.server.walletapi.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.lx.server.bean.ResultTO;
import com.lx.server.pojo.LogTransaction;
import com.lx.server.pojo.UserClient;
import com.lx.server.service.LogTransactionService;
import com.lx.server.service.WalletService;
import com.lx.server.utils.AESUtil;
import com.lx.server.utils.Tools;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;


@RestController
@RequestMapping("api/blockChain")
@Api(tags = { "Blockchain interfaces" })
public class BlockChainController extends AbstractController{
	
	@Autowired
    private WalletService walletService;
	
//	@Autowired
//    private KafkaTemplate<String, Object> kafkaTemplate;
	
	@Autowired
	private LogTransactionService logTransactionService;
    
    //	@GetMapping("go")
	public String client() {
		String url = "http://39.105.139.121:8888/createAccount";
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("acc_name", "a");
		RestTemplate client = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		// 请勿轻易改变此提交方式，大部分的情况下，提交方式都是表单提交
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<MultiValueMap<String, String>>(params,headers);
		// 执行HTTP请求
		ResponseEntity<String> response = client.exchange(url, HttpMethod.POST, requestEntity, String.class);
		return response.getBody();
	}
    
    @GetMapping("createAddr")
    @ApiOperation("Create new address")
    public ResultTO createAddr(String account)throws Throwable{
    	return ResultTO.newSuccessResult(walletService.createNewAddress(account));
    }
    @GetMapping("createFixedProperty")
    public ResultTO createFixedProperty(String fromaddress,Integer ecosystem,Integer divideType,String name,BigDecimal amount) throws Exception{
    	return ResultTO.newSuccessResult(walletService.createFixedProperty(fromaddress,ecosystem,divideType,name,"",null, amount));
    }
    
    @GetMapping("createManageProperty")
    public ResultTO createManageProperty(String fromAddress,Integer ecosystem,Integer divideType,String name,String url) throws Exception{
    	return ResultTO.newSuccessResult(walletService.createManageProperty(fromAddress, ecosystem, divideType, name, url));
    }
    
    @GetMapping("getBTCAccount")
    @ApiOperation("get BTC account")
    public ResultTO getBTCAccount(String address) throws Exception{
    	return ResultTO.newSuccessResult("",walletService.getBTCAccount(address));
    }
    @GetMapping("getBtcBalance")
    @ApiOperation("get BTC Balance")
    public ResultTO getBtcBalance(String address) throws Exception{
    	return ResultTO.newSuccessResult(walletService.getBtcBalance(address));
    }
    @ApiOperation("Given an address, get all omni assets information of it")
    @GetMapping("getOmniAllBalance")
    public ResultTO getOmniAllBalance(String address) throws Exception{
    	return ResultTO.newSuccessResult(walletService.getOmniAllBalance(address));
    }
    
    @ApiOperation("Given an address, get all balances of omni assets belonging to it")
    @GetMapping("getAllBalanceByAddress")
    public ResultTO getAllBalanceByAddress(String address) throws Exception{
    	return ResultTO.newSuccessResult(walletService.getAllBalanceByAddress(address));
    }
    
    @ApiOperation("Given an address, get omni asset balance by its property ID")
    @GetMapping("getOmniBalanceOfPropertyId")
    public ResultTO getOmniBalanceOfPropertyId(String address,Long propertyId) throws Exception{
    	return ResultTO.newSuccessResult(walletService.getOmniBalanceOfPropertyId(address, propertyId));
    }
    @ApiOperation("get the list of omni assets")
    @GetMapping("getOmniListproperties")
    public ResultTO getOmniListproperties() throws Exception{
    	return ResultTO.newSuccessResult(walletService.getOmniListProperties());
    }
    @ApiOperation("get omni asset information by property ID ")
    @GetMapping("getOmniProperty")
    public ResultTO getOmniProperty(Long propertyId) throws Exception{
    	return ResultTO.newSuccessResult(walletService.getOmniProperty(propertyId));
    }
    
    @ApiOperation("Freeze omni account")
    @GetMapping("omniSendFreeze")
    public ResultTO omniSendFreeze(String fromAddress,String toAddress,String name,Long propertyId,BigDecimal amount) throws Exception{
    	return ResultTO.newSuccessResult(walletService.omniSendFreeze(fromAddress, toAddress, propertyId, amount.toString()));
    }
    
    @ApiOperation("Unfreeze omni account")
    @GetMapping("omniSendUnfreeze")
    public ResultTO omniSendUnfreeze(String fromAddress,String toAddress,String name,Long propertyId,BigDecimal amount) throws Exception{
    	return ResultTO.newSuccessResult(walletService.omniSendUnfreeze(fromAddress, toAddress, propertyId, amount.toString()));
    }
    
    
    @ApiOperation("construct omni raw transaction ")
    @PostMapping("omniRawTransaction")
    public ResultTO omniRawTransaction(Long propertyId, String fromBitCoinAddress,String privkey, String toBitCoinAddress, BigDecimal minerFee,BigDecimal amount, String note) throws Exception{
    	logger.info("omni raw transaction");
    	UserClient userClient = getUser();
    	Assert.notNull(userClient, "userClient is wrong");
    	Assert.isTrue(Tools.checkStringExist(userClient.getPassword()), "pin is wrong");
    	privkey = AESUtil.decrypt(privkey, userClient.getPassword(), userClient.getId().substring(0, 16));
    	Assert.notNull(privkey, "privkey is wrong");
    	String txid = (String) walletService.omniRawTransaction(propertyId, fromBitCoinAddress, privkey, toBitCoinAddress, minerFee, amount, note);
    	//发送kafka信息，写入交易记录
    	sendLogTransactionToKafka(propertyId, fromBitCoinAddress, toBitCoinAddress, minerFee, amount, userClient, txid);
		
    	return ResultTO.newSuccessResult(txid);
    }

	private void sendLogTransactionToKafka(Long propertyId, String fromBitCoinAddress, String toBitCoinAddress,BigDecimal minerFee, BigDecimal amount, UserClient userClient, String txid) {
		LogTransaction logTransaction = new LogTransaction();
    	logTransaction.setAmount(amount);
    	logTransaction.setAssetId(propertyId);
    	logTransaction.setFee(minerFee);
    	logTransaction.setFromAddr(fromBitCoinAddress);
    	logTransaction.setToAddr(toBitCoinAddress);
    	logTransaction.setUserId(userClient.getId());
    	logTransaction.setTxid(txid);
    	logTransaction.setCreateTime(new Date());
    	
    	logTransactionService.insert(logTransaction);
//    	KafkaMessage message = new KafkaMessage(EnumKafkaTopic.LogTransaction.value,userClient.getId(), null, logTransaction);
//		this.kafkaTemplate.send(EnumKafkaTopic.LogTransaction.value, JSON.toJSONString(message));
	}
    
    @ApiOperation("For testing purpose only: omni raw transaction absent encryption")
    @PostMapping("omniRawTransaction2")
    public ResultTO omniRawTransaction2(Long propertyId, String fromBitCoinAddress,String privkey, String toBitCoinAddress, BigDecimal minerFee,BigDecimal amount, String note) throws Exception{
    	logger.info("omni raw transaction absent encryption");
    	Assert.notNull(privkey, "privkey is wrong");
    	return ResultTO.newSuccessResult(walletService.omniRawTransaction(propertyId, fromBitCoinAddress, privkey, toBitCoinAddress, minerFee, amount, note));
    }
    
    @ApiOperation("send btc")
    @PostMapping("btcSend")
    public ResultTO btcSend(String fromBitCoinAddress,String privkey,String toBitCoinAddress,BigDecimal amount,BigDecimal minerFee) throws Exception{
    	logger.info("send btc");
    	UserClient userClient = getUser();
    	Assert.isTrue(Tools.checkStringExist(userClient.getPassword()), "pin is wrong");
    	privkey = AESUtil.decrypt(privkey, userClient.getPassword(), userClient.getId().substring(0, 16));
    	Assert.notNull(privkey, "privkey is wrong");
    	String ret =walletService.btcRawTransaction(fromBitCoinAddress, privkey, toBitCoinAddress, amount, minerFee,"");
    	sendLogTransactionToKafka((long) 0, fromBitCoinAddress, toBitCoinAddress, minerFee, amount, userClient, ret);
    	if (ret!=null) {
    		return ResultTO.newSuccessResult("success",ret);
		}
    	return ResultTO.newFailResult("fail");
    }
    
    @ApiOperation("send btc by multiple signatures")
    @PostMapping("btcSend2")
    public ResultTO btcSend2(String fromBitCoinAddress,String[] privkeys,String toBitCoinAddress,BigDecimal amount,BigDecimal minerFee) throws Exception{
    	logger.info("send btc by multiple signatures");
    	Assert.notNull(privkeys, "privkey is wrong");
    	List<String> keys = new ArrayList<>();
    	for (String item : privkeys) {
			keys.add(item);
		}
    	String ret =walletService.btcRawTransactionMultiSign(fromBitCoinAddress, keys, toBitCoinAddress, amount, minerFee,"");
    	if (ret!=null) {
    		return ResultTO.newSuccessResult("success",ret);
    	}
    	return ResultTO.newFailResult("fail");
    }
    
    
    @ApiOperation("get omni asset transaction by a transaction ID")
    @GetMapping("getOmniTransaction")
    public ResultTO getOmniTransaction(String txid) throws Exception{
    	return ResultTO.newSuccessResult(walletService.getOmniTransaction(txid));
    }
    @ApiOperation("get BTC transaction by a transaction ID")
    @GetMapping("getBtcTransaction")
    public ResultTO getBtcTransaction(String txid) throws Exception{
    	return ResultTO.newSuccessResult(walletService.getBtcTransaction(txid));
    }
    @ApiOperation("paginate BTC transactions")
    @GetMapping("getBtcTransactions")
    public ResultTO getBtcTransactions(Integer pageIndex,Integer pageSize) throws Exception{
    	if (pageSize==null) {
			pageSize = 10;
		}
    	if (pageIndex==null) {
    		pageIndex = 1;
    	}
    	return ResultTO.newSuccessResult(walletService.listTransactions(pageIndex, pageSize));
    }

}
