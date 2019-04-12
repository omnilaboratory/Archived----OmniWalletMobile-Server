package com.lx.server.admin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lx.server.bean.ResultTO;
import com.lx.server.service.UserService;

@RestController
@RequestMapping("admin")
public class TestController {
	
	
	@Autowired
	private UserService userService;
	
	@GetMapping("test")
	public ResultTO test() {
		return ResultTO.newSuccessResult(this.userService.selectObjectList(null));
	}
}
