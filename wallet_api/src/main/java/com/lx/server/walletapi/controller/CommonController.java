package com.lx.server.walletapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lx.server.bean.ResultTO;
import com.lx.server.service.UserAdminService;
import com.lx.server.utils.Tools;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("api/common")
@Api(tags = { "公共接口" })
public class CommonController extends AbstractController{
	
	@Autowired
	private UserAdminService userAdminService;
	
//	c4ca4238a0b923820dcc509a6f75849b
	@GetMapping("createUser")
	@ApiOperation("获取用户信息")
	public ResultTO createUser(String userId) {
		this.logger.info("test");
		Assert.isTrue(Tools.isValidMessageAudio(userId), "不正确的用户id");
		
		return ResultTO.newSuccessResult("ok",userAdminService.selectObject(userId));
	}
}
