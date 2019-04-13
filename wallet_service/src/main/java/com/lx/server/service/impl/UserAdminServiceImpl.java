package com.lx.server.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lx.server.dao.MyBatisBaseDao;
import com.lx.server.service.impl.MybatisBaseServiceImpl;
import com.lx.server.dao.UserAdminDao;
import com.lx.server.service.UserAdminService;

/**
 * 【用户表】 服务类 实现类
 *
 * @author AutoCode 309444359@qq.com
 * @date 2019-04-14 00:09:20
 *
 */
@Service(value = "userAdminService")
public class UserAdminServiceImpl extends MybatisBaseServiceImpl implements UserAdminService {

    @Autowired
    private UserAdminDao userAdminDao;

    @Override
    public MyBatisBaseDao getDao() {
        return userAdminDao;
    }

}
