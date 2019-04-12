package com.lx.server.walletapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lx.server.bean.ResultTO;
import com.lx.server.service.UserService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("api")
@Api(tags = { "Test接口" })
public class TestController {
	
	
	@Autowired
	private UserService userService;
	
	@GetMapping("test")
	@ApiOperation("test")
	public ResultTO test() {
		return ResultTO.newSuccessResult(this.userService.selectObjectList(null));
	}
}
