package com.lx.server.walletapi.controller;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.lx.server.bean.ResultTO;
import com.lx.server.service.HyperledgerService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("api/common/hyper")
@Api(tags = {"hyperledger interfaces"})
public class HyperledgerController {

	@Autowired
	private HyperledgerService hyperledgerService;
	
	@GetMapping("getBaseAccount")
	@ApiOperation("basic account information")
	private ResultTO getBaseAccount() {
		JSONObject js = hyperledgerService.getBaseAccount();
		if (js!=null) {
			return getResult(js);
		}
		return ResultTO.newFailResult("fail");
	}
	
	@GetMapping("createAccount")
	@ApiOperation("create account")
	private ResultTO createAccount(String username) {
		JSONObject js = hyperledgerService.createAccount(username);
		if (js!=null) {
			return getResult(js);
		}
		return ResultTO.newFailResult("fail");
	}
	
	@GetMapping("accountInfo")
	@ApiOperation("get account information")
	private ResultTO accountInfo(String username) {
		JSONObject js = hyperledgerService.accountInfo(username);
		if (js!=null) {
			return getResult(js);
		}
		return ResultTO.newFailResult("fail");
	}
	@GetMapping("balances")
	@ApiOperation("get balance given a username")
	private ResultTO balances(String username) {
		JSONObject js = hyperledgerService.balances(username);
		if (js!=null) {
			return getResult(js);
		}
		return ResultTO.newFailResult("fail");
	}
	
	
	@PostMapping("initToken")
	@ApiOperation("issue tokens named by tkSymbol")
	private ResultTO initToken(String tkSymbol) {
		JSONObject js = hyperledgerService.initToken(tkSymbol);
		if (js!=null) {
			return getResult(js);
		}
		return ResultTO.newFailResult("fail");
	}
	
	@PostMapping("mintToken")
	@ApiOperation("mint amount tokens ")
	private ResultTO mintToken(String tkSymbol,BigDecimal amount) {
		JSONObject js = hyperledgerService.mintToken(tkSymbol,amount);
		if (js!=null) {
			return getResult(js);
		}
		return ResultTO.newFailResult("fail");
	}
	
	@PostMapping("burnToken")
	@ApiOperation("burn amount tokens")
	private ResultTO burnToken(String tkSymbol,BigDecimal amount) {
		JSONObject js = hyperledgerService.burnToken(tkSymbol,amount);
		if (js!=null) {
			return getResult(js);
		}
		return ResultTO.newFailResult("fail");
	}
	
	@PostMapping("frozenAccount")
	@ApiOperation("freez account")
	private ResultTO frozenAccount(String username) {
		JSONObject js = hyperledgerService.frozenAccount(username);
		if (js!=null) {
			return getResult(js);
		}
		return ResultTO.newFailResult("fail");
	}
	@PostMapping("unfrozenAccount")
	@ApiOperation("unfreez account")
	private ResultTO unfrozenAccount(String username) {
		JSONObject js = hyperledgerService.unfrozenAccount(username);
		if (js!=null) {
			return getResult(js);
		}
		return ResultTO.newFailResult("fail");
	}
	
	@PostMapping("transferToken")
	@ApiOperation("transfer amount tokens")
	private ResultTO transferToken(String from,String to,String symbol,BigDecimal amount) {
		JSONObject js = hyperledgerService.transferToken(from,to,symbol,amount);
		if (js!=null) {
			return getResult(js);
		}
		return ResultTO.newFailResult("fail");
	}

	private ResultTO getResult(JSONObject js) {
		if (js.containsKey("myError")==false) {
			return ResultTO.newSuccessResult(js);
		}else {
			if (js.getBooleanValue("myError")) {
				return ResultTO.newFailResult(js.getString("msg"));
			}
			return ResultTO.newSuccessResult(js.getString("msg"));
			
		}
	}
	
}
