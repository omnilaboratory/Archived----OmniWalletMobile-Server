package com.lx.server.walletapi.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lx.server.bean.ResultTO;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("api/user")
@Api(tags = { "用户接口" })
public class UserController extends AbstractController{
	
	@GetMapping("getUser")
	@ApiOperation("获取用户信息")
	public ResultTO getUserInfo() {
		this.logger.info("test");
		return ResultTO.newSuccessResult(getUser());
	}
}
