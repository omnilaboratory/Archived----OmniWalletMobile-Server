package com.lx.server.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lx.server.dao.MyBatisBaseDao;
import com.lx.server.service.impl.MybatisBaseServiceImpl;
import com.lx.server.dao.UserClientDao;
import com.lx.server.service.UserClientService;

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

}
