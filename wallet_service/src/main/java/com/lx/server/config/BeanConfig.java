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
	
	@Value("${config.omni.host}")
	private String omniHost; 
	
    @Bean
    public JsonRpcHttpClient client() throws Throwable {
    	
    	GlobalConfig.runMode = runMode;
    	
    	//获取操作系统的类型
    	System.out.println("omniIp: "+this.omniHost);
    	
        // 身份认证
        String cred = Base64.encodeBase64String(("uprets" + ":" + "pass").getBytes());
        
        if (GlobalConfig.runMode.equals(EnumRunMode.test.value)) {
        	cred = Base64.encodeBase64String(("omniwallet" + ":" + "cB3]iL2@eZ1?cB2?").getBytes());
		}
        
        Map<String, String>  headers = new HashMap <>(1);
        headers.put("Authorization", "Basic " + cred);
        
        //正式环境
        return new JsonRpcHttpClient(new URL("http://" + this.omniHost), headers);
    }
}
