package com.lx.server.walletapi.config;

import org.springframework.security.core.AuthenticationException;

public class CustomAuthException extends AuthenticationException {

    public CustomAuthException(String message){
        super(message);
    }
    public CustomAuthException(String msg, Throwable t) {
        super(msg, t);
    }
}
