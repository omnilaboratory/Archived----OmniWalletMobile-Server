package com.lx.server.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lx.server.dao.MyBatisBaseDao;
import com.lx.server.service.impl.MybatisBaseServiceImpl;
import com.lx.server.dao.UserDao;
import com.lx.server.service.UserService;

/**
 * 【用户表】 服务类 实现类
 *
 * @author AutoCode 309444359@qq.com
 * @date 2019-04-12 20:03:53
 *
 */
@Service(value = "userService")
public class UserServiceImpl extends MybatisBaseServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Override
    public MyBatisBaseDao getDao() {
        return userDao;
    }

}
