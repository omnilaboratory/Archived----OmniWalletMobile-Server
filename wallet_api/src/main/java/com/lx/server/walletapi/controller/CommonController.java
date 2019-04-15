package com.lx.server.walletapi.controller;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.lx.server.bean.ResultTO;
import com.lx.server.enums.EnumKafkaTopic;
import com.lx.server.kafka.bean.KafkaMessage;
import com.lx.server.pojo.UserClient;
import com.lx.server.service.UserClientService;
import com.lx.server.utils.Tools;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("api/common")
@Api(tags = {"公共接口"})
public class CommonController extends AbstractController{
	
	@Autowired
	private UserClientService userClientService;
	
	@Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;
	
	@SuppressWarnings("serial")
	@PostMapping("createUser")
	@ApiOperation("创建新用户")
	public ResultTO createUser(String userId,String nickname) {
		this.logger.info("createUser");
		Assert.isTrue(Tools.isValidMessageAudio(userId), "不正确的用户id");
		UserClient userClient = userClientService.createNewUser(userId,nickname);
		if (userClient==null) {
			return ResultTO.newFailResult("创建失败");
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
		Assert.isTrue(Tools.isValidMessageAudio(userId), "不正确的用户id");
		UserClient userClient = userClientService.selectObject(userId);
		if (userClient==null) {
			return ResultTO.newFailResult("恢复失败");
		}
		return ResultTO.newSuccessResult(new HashMap<String,Object>() {{
			put("userId", userClient.getId());
		}});
	}
	
	@GetMapping("testKafka")
	@ApiOperation("测试kafka")
	public void testKafka() {
		kafkaTemplate.send(EnumKafkaTopic.UserTopic.value,JSON.toJSONString(new KafkaMessage(1,null,"msg",null)));
	}
}
