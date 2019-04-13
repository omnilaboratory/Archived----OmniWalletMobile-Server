package com.lx.server.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface WalletServcie{

	String createNewAddress(String account) throws Exception;

	Object getBtcBalance(String token) throws Exception;

	Object getOmniBalanceOfPropertyId(String address, Long propertyId) throws Exception;
	
	Object getOmniAllBalance(String address) throws Exception;

	Object getOmniListProperties() throws Exception;

	Object getOmniProperty(Long propertyId) throws Exception;

	/**
	 * 创建新的token资产  Create new tokens with fixed supply
	 * @param data TODO
	 * @param fromAddress: the address to send from
	 * @param ecosystem: the ecosystem to create the tokens in (1 for main ecosystem, 2 for test ecosystem)
	 * @param type: the type of the tokens to create: (1 for indivisible tokens, 2 for divisible tokens)
	 * @param name: the name of the new tokens to create
	 * @param url:an URL for further information about the new tokens (can be "")
	 * @param amount:the number of tokens to create
	 * @return
	 * @throws Exception 
	 */
	String createFixedProperty(String fromAddress, Integer ecosystem, Integer type, String name, String url,String data, BigDecimal amount) throws Exception;

	/**
	 * 
	 * @param txid
	 * @return
	 * @throws Exception 
	 */
	Map<String, Object> getOmniTransaction(String txid) throws Exception;

	/**
	 * 铸币
	 * @param fromaddress
	 * @param propertyId
	 * @param amount
	 * @return
	 * @throws Exception 
	 */
	Object omniSendRevoke(String fromaddress, Long propertyId, String amount) throws Exception;

	/**
	 * 烧币
	 * @param fromaddress
	 * @param propertyId
	 * @param amount
	 * @return
	 * @throws Exception 
	 */
	Object omniSendGrant(String fromaddress, Long propertyId, String amount) throws Exception;

	/**
	 * omni的转账操作
	 * @param fromaddress
	 * @param toaddress
	 * @param propertyId
	 * @param amount
	 * @return
	 * @throws Exception 
	 */
	String omniSend(String fromaddress, String toaddress, Long propertyId, String amount) throws Exception;

	Object getBtcTransaction(String txid) throws Exception;

	Object omniSendFreeze(String fromAddress, String toAddress, Long propertyId, String amount) throws Exception;

	Object omniSendUnfreeze(String fromAddress, String toAddress, Long propertyId, String amount) throws Exception;

	String btcSend(String fromBitCoinAddress, String toBitCoinAddress, String amount,String note) throws Exception;

	String createManageProperty(String fromAddress, Integer ecosystem, Integer type, String name, String url) throws Exception;

	List<Map<String, Object>> getAllBalanceByAddress(String address) throws Exception;

	String getBTCAccount(String fromBitCoinAddress) throws Exception;

	/**
	 * btc转账
	 * @param fromBitCoinAddress
	 * @param toBitCoinAddress
	 * @param amount
	 * @param note
	 * @return
	 * @throws Exception
	 */
	Object btcRawTransaction(String fromBitCoinAddress, String toBitCoinAddress, BigDecimal amount, String note) throws Exception;

}
