package com.lx.server.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lx.server.dao.MyBatisBaseDao;
import com.lx.server.service.impl.MybatisBaseServiceImpl;
import com.lx.server.dao.LogTransactionDao;
import com.lx.server.service.LogTransactionService;

/**
 * 【某人在某个地址的某个资产在什么时候发起了一笔转出到某个地址的操作日志】 服务类 实现类
 *
 * @author AutoCode 309444359@qq.com
 * @date 2019-07-16 11:37:56
 *
 */
@Service(value = "logTransactionService")
public class LogTransactionServiceImpl extends MybatisBaseServiceImpl implements LogTransactionService {

    @Autowired
    private LogTransactionDao logTransactionDao;

    @Override
    public MyBatisBaseDao getDao() {
        return logTransactionDao;
    }

}
