package com.lx.server.walletapi.controller;

import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lx.server.bean.ResultTO;
import com.lx.server.utils.Tools;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("api/common")
@Api(tags = { "公共接口" })
public class CommonController extends AbstractController{
	
	@GetMapping("createUser")
	@ApiOperation("获取用户信息")
	public ResultTO createUser(String userId) {
		this.logger.info("test");
		Assert.isTrue(Tools.isValidMessageAudio(userId), "不正确的用户id");
		return ResultTO.newSuccessResult("ok",userId);
	}
}
