package com.web;

import lombok.Data;
import org.springframework.beans.factory.FactoryBean;

import java.lang.reflect.Proxy;

@Data
public class HttpClientFactoryBean implements FactoryBean<Object> {

    private String method;
    private String url;
    private Class<?> type;

    @Override
    public Object getObject() throws Exception {
        return Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[]{type}, new HttpClientHandler(method, url));
    }

    @Override
    public Class<?> getObjectType() {
        return type;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
