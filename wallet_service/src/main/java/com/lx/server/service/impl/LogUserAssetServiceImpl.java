package com.lx.server.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lx.server.dao.MyBatisBaseDao;
import com.lx.server.service.impl.MybatisBaseServiceImpl;
import com.lx.server.dao.LogUserAssetDao;
import com.lx.server.service.LogUserAssetService;

/**
 * 【某人某个时刻的某个地址某个资产的价值记录】 服务类 实现类
 *
 * @author AutoCode 309444359@qq.com
 * @date 2019-07-16 13:41:52
 *
 */
@Service(value = "logUserAssetService")
public class LogUserAssetServiceImpl extends MybatisBaseServiceImpl implements LogUserAssetService {

    @Autowired
    private LogUserAssetDao logUserAssetDao;

    @Override
    public MyBatisBaseDao getDao() {
        return logUserAssetDao;
    }

}
