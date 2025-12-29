package com.yan.web;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public class HttpClientHandler implements InvocationHandler {

    private String methodName;

    private String url;

    public HttpClientHandler(String methodName, String url) {
        this.methodName = methodName;
        this.url = url;
    }

    public HttpClientHandler() {
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        switch (method.getName()) {
            case "toString" -> {
                return this.toString();
            }
            case "equals" -> {
                Object otherHandler = args.length > 0 && args[0] != null ? Proxy.getInvocationHandler(args[0]) : null;
                return this.equals(otherHandler);
            }
            case "hashCode" -> {
                return this.hashCode();
            }
            default -> {
                return callMethod(method, args);
            }
        }

    }

    private Object callMethod(Method method, Object[] args) {
        log.info("call method:{},methodName:{},url:{}", method.getName(), methodName, url);
        return this;
    }
}
