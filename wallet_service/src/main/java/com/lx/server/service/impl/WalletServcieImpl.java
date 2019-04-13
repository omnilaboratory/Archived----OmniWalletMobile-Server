package com.lx.server.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.googlecode.jsonrpc4j.JsonRpcHttpClient;
import com.lx.server.service.WalletServcie;
import com.lx.server.utils.Tools;

/**
 * 钱包管理
 * omni rpc https://github.com/OmniLayer/omnicore/blob/master/src/omnicore/doc/rpc-api.md
 * omni 中文版  https://blog.csdn.net/wm609972715/article/details/82891064
 * btc rpc  https://bitcoin.org/en/developer-reference#bitcoin-core-apis
 *
 */
@Service(value = "walletServcie")
public class WalletServcieImpl implements WalletServcie {

	@Autowired
	private JsonRpcHttpClient jsonRpcHttpClient;

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
	 * 铸币
	 * @param fromaddress
	 * @param propertyId
	 * @param amount
	 * @return hash
	 * @throws Exception 
	 */
	@Override
	public String omniSendRevoke(String fromaddress, Long propertyId,String amount) throws Exception {
		String object = this.sendCmd("omni_sendrevoke", new Object[] {fromaddress,propertyId,amount},String.class);
		return object;
	}
	/**
	 * 烧币
	 * @param fromaddress
	 * @param propertyId
	 * @param amount
	 * @return hash
	 * @throws Exception 
	 */
	@Override
	public Object omniSendGrant(String fromaddress, Long propertyId,String amount) throws Exception {
		Object object = this.sendCmd("omni_sendgrant", new Object[] {fromaddress,"",propertyId,amount},String.class);
		return object;
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
	public Object getOmniAllBalance(String address) throws Exception {
		Assert.isTrue(Tools.checkStringExist(address), "address be empty");
		List<Map<String, Object>> object = this.sendCmd("omni_getallbalancesforaddress", new Object[] { address },ArrayList.class);
		return object;
	}

	// btc 获取钱包信息 getbalance
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> getBtcBalance(String address) throws Exception {
		Assert.isTrue(Tools.checkStringExist(address), "address be empty");
		List<Map<String, Object>> list = null;
		try {
			List<String> fromAddress = new ArrayList<>();
			fromAddress.add(address);
			list = this.sendCmd("listunspent", new Object[] {1,10000,fromAddress},ArrayList.class);
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
						btcNode.put("name", "Btc");
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
			btcNode.put("name", "Btc");
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
	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String, Object>> getAllBalanceByAddress(String address) throws Exception {
		Assert.isTrue(Tools.checkStringExist(address), "address be empty");
		List<Map<String, Object>> omniList = null;
		try {
			omniList = this.jsonRpcHttpClient.invoke("omni_getallbalancesforaddress", new Object[] { address }, List.class);
			
		} catch (Throwable e) {
			e.printStackTrace();
			omniList = new ArrayList<>();
		}
		
		for (Map<String, Object> map : omniList) {
			map.put("address", address);
			map.put("account", "");
		}
		
		Map<String, Object> btcNode = this.getBtcBalance(address);
		if (btcNode==null) {
			btcNode = new HashMap<>();
			btcNode.put("propertyid", 0);
			btcNode.put("name", "Btc");
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
		
//		for (Map<String, Object> map : omniList) {
//			Long propertyid = Long.parseLong(map.get("propertyid").toString());
//			map.put("logoUrl", "");
//			if (propertyid!=0) {
//				DigitalHouseToken node = commonDao.selectHouseTokenByPropertyId(map.get("propertyid"));
//				if (node!=null) {
//					map.put("logoUrl", node.getLogoUrl());
//				}
//			}
//		}
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
	public String btcSend(String fromBitCoinAddress,String toBitCoinAddress,String amount,String note) throws Exception {
		return this.btcRawTransaction(fromBitCoinAddress, toBitCoinAddress, new BigDecimal(amount), note);
	}
	
	/**
	 * btc的转账
	 */
	@SuppressWarnings({ "unchecked", "unused" })
	@Override
	public String btcRawTransaction(String fromBitCoinAddress,String toBitCoinAddress,BigDecimal amount,String note) throws Exception {
		Assert.isTrue(amount!=null&&amount.compareTo(BigDecimal.ZERO)==1,"amount must greater 0");
		Assert.isTrue(Tools.checkStringExist(fromBitCoinAddress),"fromBitCoinAddress can not be null");
		Assert.isTrue(Tools.checkStringExist(toBitCoinAddress),"toBitCoinAddress can not be null");
		List<String> fromAddress = new ArrayList<>();
		fromAddress.add(fromBitCoinAddress);
		List<Map<String, Object>> list = this.sendCmd("listunspent", new Object[] {1,Integer.MAX_VALUE,fromAddress},ArrayList.class);
		//矿工费
		BigDecimal fee = new BigDecimal("0.00005");
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
				if (scriptPubKey.length()==0) {
					scriptPubKey = item.get("scriptPubKey").toString();
				}
				myList.add(node);
				balance = balance.add(new BigDecimal(item.get("amount").toString()));
				if (balance.compareTo(out)>-1) {
					break;
				}
			}
		}
 		
 		if (myList.size()>0&&balance.compareTo(out)>-1) {
 			BigDecimal back= balance.subtract(out);
 			Map<String, Object> address= new HashMap<>();
 			address.put(toBitCoinAddress, amount);
 			address.put(fromBitCoinAddress, back);
 			
			String hexstring =  this.sendCmd("createrawtransaction", new Object[] {myList,address}, String.class);
			
			Map<String, Object> hexMap =  this.sendCmd("decoderawtransaction", new Object[] {hexstring}, Map.class);
			for (Map<String, Object> map : myList) {
				map.put("scriptPubKey", scriptPubKey);
			}
			Map<String, Object> hex =  this.sendCmd("signrawtransaction", new Object[] {hexstring,myList,null,"ALL"}, Map.class);
			String hexStr = hex.get("hex").toString();
			hexMap =  this.sendCmd("decoderawtransaction", new Object[] {hexStr}, Map.class);
			String txId =  this.sendCmd("sendrawtransaction", new Object[] {hexStr}, String.class);
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

	private <T> T sendCmd(String methodName, Object argument,Class<T> clazz) throws Exception {
		T object = null;
		try {
			object = this.jsonRpcHttpClient.invoke(methodName, argument, clazz);
		} catch (Throwable e) {
			e.printStackTrace();
			throw new Exception(e.getMessage()+" 传入参数值有误");
		}
		return object;
	}
}
