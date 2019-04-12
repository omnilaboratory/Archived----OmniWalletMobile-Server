package com.lx.server.walletapi.controller;

import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lx.server.bean.ResultTO;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("api")
@Api(tags = { "Test接口" })
public class TestController extends AbstractController{
	
	@GetMapping("test")
	@ApiOperation("test")
	public ResultTO test(String name) {
		this.logger.info("test");
		Assert.notNull(name, "名称不能为空");
		return ResultTO.newSuccessResult(getUser());
	}
}
