package com.lx.server.walletapi.config;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.alibaba.fastjson.JSON;
import com.lx.server.bean.ResultTO;
import com.lx.server.service.UserClientService;
import com.lx.server.utils.Tools;


@Component
public class InputDataInterceptor extends HandlerInterceptorAdapter {
	
	/** Logger available to subclasses */
	protected final Log logger = LogFactory.getLog(getClass());
	
	@Value("${jwt.secret}")
	private String secret;
	
	
	@Autowired
	UserClientService userClientService;
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		logger.info("requestURL： "+request.getRequestURL());
		
		if (!(HttpMethod.POST.name().equals(request.getMethod()))) {
            return true;
        }
		String dataStr = request.getParameter("dataStr");
		String dataMD5 = request.getParameter("dataMD5");
		String dataMD5Locale = Tools.MD5Encode(Tools.MD5Encode(dataStr+this.secret));
		if (dataMD5Locale.equals(dataMD5)==false) {
			response.setCharacterEncoding("UTF-8");
            response.setContentType("text/html;charset=utf-8");
            response.setHeader("Cache-Control", "no-cache, must-revalidate");
            response.getWriter().write(JSON.toJSONString(ResultTO.newFailResult("error data")));
			return false;
		}
		return super.preHandle(request, response, handler);
	}
}
