package com.lx.server.service.impl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.lx.server.dao.MyBatisBaseDao;
import com.lx.server.dao.UserClientDao;
import com.lx.server.pojo.UserClient;
import com.lx.server.service.UserClientService;
import com.lx.server.utils.Tools;

/**
 * 【钱包客户端用户】 服务类 实现类
 *
 * @author AutoCode 309444359@qq.com
 * @date 2019-04-13 23:59:45
 *
 */
@Service(value = "userClientService")
public class UserClientServiceImpl extends MybatisBaseServiceImpl implements UserClientService {

    @Autowired
    private UserClientDao userClientDao;

    @Override
    public MyBatisBaseDao getDao() {
        return userClientDao;
    }

	@Override
	public UserClient createNewUser(String userId,String nickname) {
		UserClient userClient =  userClientDao.selectObject(userId);
		Assert.isNull(userClient, "用户已经存在");
		userClient = new UserClient();
		userClient.setId(userId);
		if (Tools.checkStringExist(nickname)==false) {
			nickname = "匿名用户";
		}
		userClient.setNickname(nickname);
		userClient.setGoogleAuthCode("");
		userClient.setGoogleAuthEnable(false);
		userClient.setCreateTime(new Date());
		userClient.setLastLoginTime(new Date());
		if (userClientDao.insert(userClient)>0) {
			return userClient;
		}
		return null;
	}

}
