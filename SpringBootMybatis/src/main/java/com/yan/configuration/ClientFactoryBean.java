package com.yan.configuration;

import org.springframework.beans.factory.FactoryBean;

import java.lang.reflect.Proxy;

import lombok.Data;

@Data
public class ClientFactoryBean implements FactoryBean<Object> {

    private String method;

    private String url;

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
