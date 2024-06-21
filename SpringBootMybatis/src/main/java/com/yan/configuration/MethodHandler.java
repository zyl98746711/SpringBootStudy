package com.yan.configuration;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import lombok.Data;

@Data
public class MethodHandler implements InvocationHandler {

    private String methodName;

    private String url;

    public MethodHandler(String methodName, String url) {
        this.methodName = methodName;
        this.url = url;
    }

    public MethodHandler() {
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
        }
        System.out.println("callback:" + method.getName() + ",method:" + methodName + ",url:" + url);
        return this;
    }
}
