package com.lx.server.config;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.googlecode.jsonrpc4j.JsonRpcHttpClient;
import com.lx.server.enums.EnumRunMode;

@Configuration
public class BeanConfig {
	
	@Value("${config.runMode}")
	private String runMode;
	
    @Bean
    public JsonRpcHttpClient client() throws Throwable {
    	
    	GlobalConfig.runMode = runMode;
    	//获取操作系统的类型
    	String sysType = System.getProperties().getProperty("os.name");
    	// 正式环境
    	String omniIp = "127.0.0.1";
    	
    	if (sysType.toLowerCase().startsWith("win")) {
    		omniIp = "62.234.169.68";
		}
        // 身份认证
        String cred = Base64.encodeBase64String(("uprets" + ":" + "pass").getBytes());
        Map<String, String>  headers = new HashMap <>(1);
        headers.put("Authorization", "Basic " + cred);
//        如果是测试完环境regnet
        if (GlobalConfig.runMode.equals(EnumRunMode.test.value)) {
        	omniIp = "62.234.216.108";
        	cred = Base64.encodeBase64String(("omniwallet" + ":" + "cB3]iL2@eZ1?cB2?").getBytes());
            headers.put("Authorization", "Basic " + cred);
        	return new JsonRpcHttpClient(new URL("http://" + omniIp + ":" + "18332"), headers);
		}
        //正式环境
        return new JsonRpcHttpClient(new URL("http://" + omniIp + ":" + "8332"), headers);
    }
}
