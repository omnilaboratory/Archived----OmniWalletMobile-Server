package com.lx.server.service;

import java.math.BigDecimal;
import java.util.Map;

public interface CommonService {

	BigDecimal getCoinExchangeRate(String coinName,String unit);

	BigDecimal getExchangeRateBaseEUR(String type);

	Map<String, Object> getTransactionsByAddress(String address);

}
