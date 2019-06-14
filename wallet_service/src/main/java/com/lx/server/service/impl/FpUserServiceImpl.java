package com.lx.server.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lx.server.dao.MyBatisBaseDao;
import com.lx.server.service.impl.MybatisBaseServiceImpl;
import com.lx.server.dao.FpUserDao;
import com.lx.server.service.FpUserService;

/**
 * 【flashPay 快速支付的用户】 服务类 实现类
 *
 * @author AutoCode 309444359@qq.com
 * @date 2019-06-14 10:53:56
 *
 */
@Service(value = "fpUserService")
public class FpUserServiceImpl extends MybatisBaseServiceImpl implements FpUserService {

    @Autowired
    private FpUserDao fpUserDao;

    @Override
    public MyBatisBaseDao getDao() {
        return fpUserDao;
    }

}
