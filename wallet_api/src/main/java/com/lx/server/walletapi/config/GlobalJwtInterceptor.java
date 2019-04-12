package com.lx.server.walletapi.config;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.lx.server.pojo.User;
import com.lx.server.service.UserService;


@Component
public class GlobalJwtInterceptor extends HandlerInterceptorAdapter {
	
	/** Logger available to subclasses */
	protected final Log logger = LogFactory.getLog(getClass());
	
	@Value("${jwt.header}")
	private String tokenHeader;
	
	@Value("${jwt.tokenHead}")
	private String tokenHead;
	
	@Autowired
    private SimpleHandlerExceptionResolver resolver;
	
	@Autowired
	UserService userService;
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		String oldToken = request.getHeader(tokenHeader);
		String authHeader = request.getHeader(this.tokenHeader);
		logger.info("requestURL： "+request.getRequestURL());
		
		if ("OPTIONS".equals(request.getMethod())) {
			response.setStatus(HttpServletResponse.SC_OK);
			return super.preHandle(request, response, handler);
		}else{
			if (authHeader != null && authHeader.startsWith(tokenHead)) {
				final String token = oldToken.substring(tokenHead.length());
				User user = userService.selectObject(token);
				if (user==null) {
					this.resolver.resolveException(request, response, 1, new CustomAuthException("用户无效，请重新登录"));
					return false;
				}
				request.setAttribute("claims", token);
				request.setAttribute("user", user);
				return super.preHandle(request, response, handler);
			} else {
				super.preHandle(request, response, handler);
				this.resolver.resolveException(request, response, 1, new CustomAuthException("用户无效，请重新登录"));
				return false;
			}
		}
	}
}
