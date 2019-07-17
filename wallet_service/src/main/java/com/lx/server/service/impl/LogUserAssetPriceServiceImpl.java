package com.lx.server.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lx.server.dao.MyBatisBaseDao;
import com.lx.server.service.impl.MybatisBaseServiceImpl;
import com.lx.server.dao.LogUserAssetPriceDao;
import com.lx.server.service.LogUserAssetPriceService;

/**
 * 【某个时刻的价格信息】 服务类 实现类
 *
 * @author AutoCode 309444359@qq.com
 * @date 2019-07-16 16:01:30
 *
 */
@Service(value = "logUserAssetPriceService")
public class LogUserAssetPriceServiceImpl extends MybatisBaseServiceImpl implements LogUserAssetPriceService {

    @Autowired
    private LogUserAssetPriceDao logUserAssetPriceDao;

    @Override
    public MyBatisBaseDao getDao() {
        return logUserAssetPriceDao;
    }

}
