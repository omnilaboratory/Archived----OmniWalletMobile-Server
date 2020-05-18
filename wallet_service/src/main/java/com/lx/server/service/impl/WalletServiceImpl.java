package com.lx.server.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.googlecode.jsonrpc4j.JsonRpcHttpClient;
import com.lx.server.service.BtcTransactionService;
import com.lx.server.service.WalletService;
import com.lx.server.utils.Tools;

/**
 * 钱包管理
 * omni rpc https://github.com/OmniLayer/omnicore/blob/master/src/omnicore/doc/rpc-api.md
 * omni 中文版  https://blog.csdn.net/wm609972715/article/details/82891064
 * btc rpc  https://bitcoin.org/en/developer-reference#bitcoin-core-apis
 *
 */
@Service(value = "walletService")
public class WalletServiceImpl implements WalletService {
	
	protected final Log logger = LogFactory.getLog(getClass());

	@Autowired
	private JsonRpcHttpClient jsonRpcHttpClient;
	
	@Autowired
	private BtcTransactionService btcTransactionService;
	

	// btc 创建token omni和btc公用 getnewaddress.
	@Override
	public String createNewAddress(String account) throws Exception {
		String address = this.sendCmd("getnewaddress", new Object[]{account},String.class);
		return address;
	}
	// omni 发行资产 omni_sendissuancefixed
	@Override
	public String createFixedProperty(String fromAddress,Integer ecosystem,Integer type,String name,String url,String data, BigDecimal amount) throws Exception {
		Object hash = this.sendCmd("omni_sendissuancefixed", new Object[]{
			fromAddress,ecosystem,type,0,"","",name,url,data,amount.toString()
		},String.class);
		if (hash==null) {
			return "";
		}
		return hash.toString();
	}
	
	// omni 发行资产 omni_sendissuancefixed
	@Override
	public String createManageProperty(String fromAddress,Integer ecosystem,Integer type,String name,String url) throws Exception {
		Object hash = this.sendCmd("omni_sendissuancemanaged", new Object[]{
			fromAddress,ecosystem,type,0,"","",name,url,""
		},String.class);
		if (hash==null) {
			return "";
		}
		return hash.toString();
	}
	
	// omni 获取资产详情 omni_getproperty
	@SuppressWarnings("unchecked")
	@Override
	public Object getOmniProperty(Long propertyId) throws Exception {
		Map<String, Object> node = this.sendCmd("omni_getproperty", new Object[] {propertyId},Map.class);
		return node;
	}
	
	/**
	 * 获取某个地址某种资产类型的余额  omni_getbalance
	 * @throws Exception 
	 */
	@Override
	public Object getOmniBalanceOfPropertyId(String address, Long propertyId) throws Exception {
		Object object = this.sendCmd("omni_getbalance", new Object[] {address,propertyId},Map.class);
		return object;
	}
	
	/**
	 * omni 获取钱包信息 usdt+房产信息 omni_getallbalancesforaddress
	 * @throws Exception 
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String, Object>>  getOmniAllBalance(String address)  {
		Assert.isTrue(Tools.checkStringExist(address), "address be empty");
		List<Map<String, Object>> list;
		try {
			list = this.sendCmd("omni_getallbalancesforaddress", new Object[] { address },ArrayList.class);
		} catch (Exception e) {
			list = new ArrayList<>();
		}
		return list;
	}

	// btc 获取钱包信息 getbalance
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> getBtcBalance(String address) throws Exception {
		Assert.isTrue(Tools.checkStringExist(address), "address be empty");
		if (this.validateAddress(address)==false) {
			this.importAddress(address);
		}
		List<Map<String, Object>> list = null;
		try {
			List<String> fromAddress = new ArrayList<>();
			fromAddress.add(address);
			list = this.sendCmd("listunspent", new Object[] {1,Integer.MAX_VALUE,fromAddress},ArrayList.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		BigDecimal balance = BigDecimal.ZERO;
		Map<String, Object> btcNode = null;
		if (list!=null) {
			for (Map<String, Object> item : list) {
				if (item.containsKey("address")&&item.get("address").equals(address)) {
					if (btcNode==null) {
						btcNode = new HashMap<>();
						btcNode.put("propertyid", 0);
						btcNode.put("name", "BTC");
						btcNode.put("balance", item.get("amount")==null?"0":item.get("amount").toString());
						btcNode.put("reserved", 0);
						btcNode.put("address", item.get("address"));
						btcNode.put("account", item.get("account"));
						btcNode.put("frozen", 0);
					}
					balance = balance.add(new BigDecimal(item.get("amount").toString()));
				}
			}
		}
		if (btcNode!=null) {
			btcNode.put("balance", balance);
		}else{
			btcNode = new HashMap<>();
			btcNode.put("propertyid", 0);
			btcNode.put("name", "BTC");
			btcNode.put("balance", balance);
			btcNode.put("reserved", 0);
			btcNode.put("address", address);
			btcNode.put("account", "");
			btcNode.put("frozen", 0);
		}
		return btcNode;
	}
	/**
	 * 获取这个地址的所有的币种余额
	 * @param address
	 * @return
	 * @throws Exception
	 */
	@Override
	public List<Map<String, Object>> getAllBalanceByAddress(String address) throws Exception {
		Assert.isTrue(Tools.checkStringExist(address), "address be empty");
		
		if (this.validateAddress(address)==false) {
			this.importAddress(address);
		}
		
		List<Map<String, Object>> omniList =getOmniAllBalance(address);
		
		for (Map<String, Object> map : omniList) {
			map.put("address", address);
			map.put("account", "");
		}
		
		Map<String, Object> btcNode = this.getBtcBalance(address);
		if (btcNode==null) {
			btcNode = new HashMap<>();
			btcNode.put("propertyid", 0);
			btcNode.put("name", "BTC");
			btcNode.put("address", address);
			btcNode.put("account", "");
			btcNode.put("balance", 0);
			btcNode.put("reserved", 0);
			btcNode.put("frozen", 0);
		}else {
			for (Map<String, Object> map : omniList) {
				if (map.get("propertyid").equals(0)==false) {
					map.put("account", btcNode.get("account"));
				}
			}
		}
		omniList.add(btcNode);
		
		return omniList;
	}
	
	// omni 获取资产列表 omni_listproperties
	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String, Object>>  getOmniListProperties() throws Exception {
		List<Map<String, Object>> list = this.sendCmd("omni_listproperties", new Object[] {},ArrayList.class);
		return list;
	}
	// 
	/**
	 * omni发起转账 omni_send 
	 * @param fromaddress
	 * @param toaddress
	 * @param propertyId
	 * @param amount
	 * @return transaction hash
	 * @throws Exception 
	 */
	@Override
	public String omniSend(String fromAddress,String toAddress,Long propertyId,String amount) throws Exception {
		String object = this.sendCmd("omni_send", new Object[] {fromAddress,toAddress,propertyId,amount},String.class);
		return object;
	}

	/**
	 * btc的转账 从某个btcdi地址转账到某个btc地址
	 * btc 发起转账sendfrom
	 * 如果创建btc address的时候没有指定account，就不能转账
	 * @param fromAccount
	 * @param toBitCoinAddress
	 * @param amount
	 * @return transaction ID
	 * @throws Exception 
	 */
	@Override
	public String btcSend(String fromBitCoinAddress,String privkey,String toBitCoinAddress,String amount,String note) throws Exception {
		return this.btcRawTransaction(fromBitCoinAddress,privkey, toBitCoinAddress, new BigDecimal(amount),new BigDecimal(0.0005), note);
	}
	
	
	/**
	 * btc的转账
	 */
	@Override
	public String btcRawTransaction(String fromBitCoinAddress,String privkey,String toBitCoinAddress,BigDecimal amount,BigDecimal mineFee,String note) throws Exception {
		List<String> privkeys = null;
		if (Tools.checkStringExist(privkey)) {
			privkeys = new ArrayList<>();
			privkeys.add(privkey);
		}
		return this.btcRawTransactionMultiSign(fromBitCoinAddress, privkeys, toBitCoinAddress, amount, mineFee, note);
	}
	
	/**
	 * btc的转账多签
	 */
	@SuppressWarnings({ "unchecked", "unused" })
	@Override
	public String btcRawTransactionMultiSign(String fromBitCoinAddress,List<String> privkeys,String toBitCoinAddress,BigDecimal amount,BigDecimal mineFee,String note) throws Exception {
		Assert.isTrue(amount!=null&&amount.compareTo(BigDecimal.ZERO)==1,"amount must greater 0");
		Assert.isTrue(mineFee!=null&&mineFee.compareTo(BigDecimal.ZERO)==1,"mineFee must greater 0");
		Assert.isTrue(Tools.checkStringExist(fromBitCoinAddress),"fromBitCoinAddress can not be null");
		Assert.isTrue(Tools.checkStringExist(toBitCoinAddress),"toBitCoinAddress can not be null");
		List<String> fromAddress = new ArrayList<>();
		fromAddress.add(fromBitCoinAddress);
		List<Map<String, Object>> list = this.sendCmd("listunspent", new Object[] {0,Integer.MAX_VALUE,fromAddress},ArrayList.class);
		
		logger.info("list.size: "+list.size());
		Assert.isTrue(list!=null&&list.isEmpty()==false, "empty balance");
		logger.info(list);
		//矿工费
		BigDecimal fee = mineFee;
		BigDecimal out= fee.add(amount);
		List<Map<String, Object>> myList = new ArrayList<>();
		Map<String, Object> node = new HashMap<>();
		BigDecimal balance = BigDecimal.ZERO;
		String scriptPubKey = "";
		for (Map<String, Object> item : list) {
			if (item.containsKey("address")&&item.get("address").equals(fromBitCoinAddress)) {
				node = new HashMap<>();
				node.put("txid", item.get("txid"));
				node.put("vout", item.get("vout"));
				if (privkeys!=null&&privkeys.size()>0) {
					node.put("redeemScript", item.get("redeemScript"));
				}
				if (scriptPubKey.length()==0) {
					scriptPubKey = item.get("scriptPubKey").toString();
				}
				myList.add(node);
				logger.info("item.get(amount) " +item.get("amount"));
				balance = balance.add(new BigDecimal(item.get("amount").toString()));
				if (balance.compareTo(out)>-1) {
					break;
				}
			}
		}
		logger.info("balance "+balance);
		Assert.isTrue(myList.size()>0&&balance.compareTo(out)>-1, "not enough balance");
		
		if (myList.size()>0&&balance.compareTo(out)>-1) {
			BigDecimal back= balance.subtract(out);
			Map<String, Object> address= new HashMap<>();
			address.put(toBitCoinAddress, amount);
			address.put(fromBitCoinAddress, back);
			
			String hexstring =  this.sendCmd("createrawtransaction", new Object[] {myList,address}, String.class);
			
			Map<String, Object> hexMap =  this.sendCmd("decoderawtransaction", new Object[] {hexstring}, Map.class);
			logger.info(hexMap);
			
			for (Map<String, Object> map : myList) {
				map.put("scriptPubKey", scriptPubKey);
			}
			
			Map<String, Object> hex;
			if (privkeys!=null&&privkeys.size()>0) {
				hex =  this.sendCmd("signrawtransactionwithkey", new Object[] {hexstring,privkeys,myList,"ALL"}, Map.class);
			}else {
				Assert.isTrue(isAddressCreateByOmnicore(fromBitCoinAddress), "the address is not created by omnicore");
				hex =  this.sendCmd("signrawtransactionwithwallet", new Object[] {hexstring}, Map.class);
			}
			String hexStr = hex.get("hex").toString();
			hexMap =  this.sendCmd("decoderawtransaction", new Object[] {hexStr}, Map.class);
			logger.info(hexMap);
			
			String txId =  this.sendCmd("sendrawtransaction", new Object[] {hexStr}, String.class);
			logger.info(txId);
			return txId;
		}
		return null;
	}
	
	@Override
	public String getBTCAccount(String fromBitCoinAddress) throws Exception {
		String account = this.sendCmd("getaccount", new Object[] {fromBitCoinAddress},String.class);
		return account;
	}
	
	
	// 
	/**
	 * btc 获取交易结果 gettransaction
	 * @param txid 
	 * @return json
	 * @throws Exception 
	 */
	@Override
	public Object getBtcTransaction(String txid) throws Exception {
		Object object = this.sendCmd("gettransaction", new Object[] {txid},Map.class);
		return object;
	}

	/**
	 * omni 冻结房产数量 omni_sendfreeze
	 * @param fromAddress
	 * @param toAddress
	 * @param propertyId
	 * @param amount
	 * @return the hex-encoded transaction hash
	 * @throws Exception 
	 */
	@Override
	public String omniSendFreeze(String fromAddress,String toAddress,Long propertyId,String amount) throws Exception {
		String object = this.sendCmd("omni_sendfreeze", new Object[] {fromAddress,toAddress,propertyId,amount},String.class);
		return object;
	}
	
	/**
	 * omni 解冻房产数量 omni_sendunfreeze
	 * @param fromAddress
	 * @param toAddress
	 * @param propertyId
	 * @param amount
	 * @return (string) the hex-encoded transaction hash
	 * @throws Exception 
	 */
	@Override
	public String omniSendUnfreeze(String fromAddress,String toAddress,Long propertyId,String amount) throws Exception {
		String object = this.sendCmd("omni_sendunfreeze", new Object[] {fromAddress,toAddress,propertyId,amount},String.class);
		return object;
	}
	
	/**
	 * 获取有关Omni事务的详细信息 omni_gettransaction
	 *  txid transactionId
	 * @throws Exception 
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> getOmniTransaction(String txid) throws Exception {
		Map<String, Object> object = this.sendCmd("omni_gettransaction", new Object[] {txid},Map.class);
		return object;
	}
	
	@Override
	public <T> T sendCmd(String methodName, Object argument,Class<T> clazz) throws Exception {
		T object = null;
		try {
			object = this.jsonRpcHttpClient.invoke(methodName, argument, clazz);
		} catch (Throwable e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
		return object;
	}
	@SuppressWarnings("unchecked")
	@Override
	public Object omniRawTransaction(Long propertyId,
			String fromBitCoinAddress, String privkey, String toBitCoinAddress,
			BigDecimal minerFee, BigDecimal amount, String note) throws Exception {

		Assert.isTrue(propertyId!=null&&propertyId>=0, "propertyId is wrong");
		Assert.isTrue(amount!=null&&amount.compareTo(BigDecimal.ZERO)==1,"amount must greater 0");
		Assert.isTrue(Tools.checkStringExist(fromBitCoinAddress),"fromBitCoinAddress can not be null");
		Assert.isTrue(Tools.checkStringExist(toBitCoinAddress),"toBitCoinAddress can not be null");
		
		Assert.isTrue(minerFee!=null&&minerFee.compareTo(BigDecimal.ZERO)==1,"minerFee must greater 0");
		
		logger.info("0.importaddress");
		if (this.validateAddress(fromBitCoinAddress)==false) {
			this.importAddress(fromBitCoinAddress);
		}
		if (this.validateAddress(fromBitCoinAddress)==false) {
			this.importAddress(fromBitCoinAddress);
		}
		
		logger.info("1.读取指定地址的UTXO（listunspent）");
		//1.读取指定地址的UTXO（listunspent）
		List<String> fromAddress = new ArrayList<>();
		fromAddress.add(fromBitCoinAddress);
		List<Map<String, Object>> list = this.sendCmd("listunspent", new Object[] {1,Integer.MAX_VALUE,fromAddress},ArrayList.class);
		logger.info(list);
		Assert.isTrue(list!=null&&list.isEmpty()==false, "empty balance");
		//矿工费
		BigDecimal pMoney = new BigDecimal("0.00000546");
		BigDecimal out= minerFee.add(pMoney);
		BigDecimal balance = BigDecimal.ZERO;
 		for (Map<String, Object> item : list) {
			if (item.containsKey("address")&&item.get("address").equals(fromBitCoinAddress)) {
				logger.info("item.get(amount) " +item.get("amount"));
				balance = balance.add(new BigDecimal(item.get("amount").toString()));
				if (balance.compareTo(out)>-1) {
					break;
				}
			}
		}
 		logger.info("1 balance "+balance);
 		Assert.isTrue(balance.compareTo(out)>-1, "not enough balance");
		
 		logger.info("2.构造发送代币类型和代币数量数据");
//		2.构造发送代币类型和代币数量数据（payload）
		String payload = this.sendCmd("omni_createpayload_simplesend", new Object[] {propertyId,amount.toString()}, String.class);
		
		logger.info("3.构造交易基本数据（transaction base）");
//		3.构造交易基本数据（transaction base）
		Map<String, Object> address= new HashMap<>();
		String createrawtransactionStr =  this.sendCmd("createrawtransaction", new Object[] {list,address}, String.class);
		
//		4.在交易数据中加上omni代币数据  这一步把omni代币数据也组合到交易数据上
		logger.info("4.在交易数据中加上omni代币数据  这一步把omni代币数据也组合到交易数据上");
		String opreturn = this.sendCmd("omni_createrawtx_opreturn", new Object[] {createrawtransactionStr,payload}, String.class);
		
//		5.在交易数据上加上接收地址
		logger.info("5.在交易数据上加上接收地址");
		String referenc = this.sendCmd("omni_createrawtx_reference", new Object[] {opreturn,toBitCoinAddress}, String.class);
		
//		6.在交易数据上指定矿工费用
		logger.info("6.在交易数据上指定矿工费用");
		List<Map<String, Object>> myList = new ArrayList<>();
		Map<String, Object> node = new HashMap<>();
		for (Map<String, Object> item : list) {
			node = new HashMap<>();
			node.put("txid", item.get("txid"));
			node.put("vout", item.get("vout"));
			node.put("scriptPubKey", item.get("scriptPubKey"));
			node.put("value", item.get("amount"));
			myList.add(node);
		}
		
		String change = this.sendCmd("omni_createrawtx_change", new Object[] {referenc,myList,fromBitCoinAddress,minerFee}, String.class);
		logger.info("6 change "+change);
		
//		7.交易签名
		logger.info("7.交易签名");
		Map<String, Object> signrawtransaction ;
		if (Tools.checkStringExist(privkey)) {
			List<String> privkeys = new ArrayList<>();
			privkeys.add(privkey);
			signrawtransaction =  this.sendCmd("signrawtransactionwithkey", new Object[] {change,privkeys,list,"ALL"}, Map.class);
		}else {
			Assert.isTrue(isAddressCreateByOmnicore(fromBitCoinAddress), "the address is not created by omnicore");
			signrawtransaction =  this.sendCmd("signrawtransactionwithwallet", new Object[] {change}, Map.class);
		}
		logger.info("7.交易签名 结果 ");
		
//		8.广播
		logger.info("8.广播");
		String hexStr = signrawtransaction.get("hex").toString();
		String txId = this.sendCmd("sendrawtransaction", new Object[] {hexStr}, String.class);
		logger.info(txId);
		return txId;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String, Object>> listTransactions(Integer pageIndex, Integer pageSize) throws Exception {
		if (pageIndex==null||pageIndex<1) {
			pageIndex=1;
		}
		if (pageSize==null&&pageIndex<1) {
			pageSize = 10;
		}
		return this.sendCmd("listtransactions", new Object[] {"*",pageSize,(pageIndex-1)*pageSize,true}, ArrayList.class);
	}
	
	@SuppressWarnings({ "unchecked" })
	@Override
	public List<Map<String, Object>> getOmniTransactions(String address) throws Exception {
		List<Map<String, Object>> nodes = this.sendCmd("omni_listtransactions", new Object[] {address,10000}, ArrayList.class);
		return nodes;
	}
	@SuppressWarnings({ "unchecked" })
	@Override
	public List<Map<String, Object>> getOmniPendingTransactions(String address) throws Exception {
		List<Map<String, Object>> nodes = this.sendCmd("omni_listpendingtransactions", new Object[] {address}, ArrayList.class);
		return nodes;
	}
	
	@Override
	public void sycBlockTransactions() throws Exception {
		
		Integer pageSize = 100;
		//获取更新日志
		Integer pageIndex = 1;
		//抓取新的数据
		List<String> txids = new ArrayList<>();
		boolean goToNext =true;
		while (true) {
			goToNext =true;
			List<Map<String, Object>> nodes = this.sendCmd("listtransactions", new Object[] {"*",pageSize,(pageIndex-1)*pageSize,true}, ArrayList.class);
			for (Map<String, Object> node : nodes) {
				String txid = node.get("txid").toString();
				if (txids.contains(txid)==false) {
					int count = btcTransactionService.pageCount(new HashMap<String,Object>() {{
						put("id", txid);
					}});
					if(count>0) {
						goToNext =false;
					}
					txids.add(txid);
				}
			}
			if (goToNext==false) {
				break;
			}
			pageIndex++;
		}
		//分析数据 插入到交易表去
		for (String txid : txids) {
			
		}
	}
	
	@SuppressWarnings("unchecked")
	public Boolean validateAddress(String address) throws Exception {
		Map<String, Object> map = this.sendCmd("getaddressinfo", new Object[] {address},Map.class);
		Boolean ismine = (Boolean) map.get("ismine");
		if (ismine!=null&&ismine==true) {
			return true;
		}
		Boolean iswatchonly = (Boolean) map.get("iswatchonly");
		if (iswatchonly!=null&&iswatchonly==true) {
			return true;
		}
		return false;
	}
	
	private void importAddress(String address){
		try {
			this.sendCmd("importaddress", new Object[] {address ,"",false},String.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	private Boolean isAddressCreateByOmnicore(String address){
		Map<String, Object> map;
		try {
			map = this.sendCmd("getaddressinfo", new Object[] {address},Map.class);
			Boolean ismine = (Boolean) map.get("ismine");
			if (ismine!=null&&ismine==true) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
