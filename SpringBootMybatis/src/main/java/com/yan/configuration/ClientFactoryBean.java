package com.yan.configuration;

import org.springframework.beans.factory.FactoryBean;

import java.lang.reflect.Proxy;

public class ClientFactoryBean implements FactoryBean<Object> {

    private String method;

    private String url;

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public Object getObject() throws Exception {
        return Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[]{Client.class}, new MethodHandler(method, url));

    }

    @Override
    public Class<?> getObjectType() {
        return Client.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
