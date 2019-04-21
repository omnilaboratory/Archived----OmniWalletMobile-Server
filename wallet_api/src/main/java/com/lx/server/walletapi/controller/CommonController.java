package com.lx.server.walletapi.controller;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSON;
import com.lx.server.bean.ResultTO;
import com.lx.server.enums.EnumFolderURI;
import com.lx.server.enums.EnumKafkaTopic;
import com.lx.server.kafka.bean.KafkaMessage;
import com.lx.server.pojo.UserClient;
import com.lx.server.service.UserClientService;
import com.lx.server.utils.Tools;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("api/common")
@Api(tags = {"公共接口"})
public class CommonController extends AbstractController{
	
	@Autowired
	private UserClientService userClientService;
	
	@SuppressWarnings("serial")
	@PostMapping("createUser")
	@ApiOperation("创建新用户")
	public ResultTO createUser(String userId,String nickname) {
		this.logger.info("createUser");
		Assert.isTrue(Tools.isValidMessageAudio(userId), "userId is not md5 type");
		UserClient userClient = userClientService.createNewUser(userId,nickname);
		if (userClient==null) {
			return ResultTO.newFailResult("create user fail");
		}
		
		return ResultTO.newSuccessResult(new HashMap<String,Object>() {{
			put("userId", userClient.getId());
		}});
	}
	
	
	@SuppressWarnings("serial")
	@PostMapping("restoreUser")
	@ApiOperation("根据助记词恢复用户")
	public ResultTO restoreUser(String userId) {
		this.logger.info("restoreUser");
		Assert.isTrue(Tools.isValidMessageAudio(userId), "userId is not md5 type");
		UserClient userClient = userClientService.selectObject(userId);
		if (userClient==null) {
			return ResultTO.newFailResult("your mnemonic is wrong");
		}
		return ResultTO.newSuccessResult(new HashMap<String,Object>() {{
			put("userId", userClient.getId());
			put("nickname", userClient.getNickname());
			put("faceUrl", userClient.getFaceUrl());
		}});
	}
	
	/**
	 * 单图片上传
	 * @param file
	 * @return
	 */
	@PostMapping("uploadImage")
	@ApiOperation("图片上传")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "file", value = "图片"),
	})
	public ResultTO uploadFile(MultipartFile file){
		Assert.isTrue(file!=null, "图片不存在");
		String url = this.uploadImage(EnumFolderURI.getEnumByType(0).value,file);
		if (url!=null) {
			return ResultTO.newSuccessResult("上传成功",url);
		}
		return ResultTO.newFailResult("上传失败");
	}
	
	@Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;
	
	@GetMapping("testKafka")
	@ApiOperation("测试kafka")
	public void testKafka() {
		kafkaTemplate.send(EnumKafkaTopic.UserTopic.value,JSON.toJSONString(new KafkaMessage(1,null,"msg",null)));
	}
}
