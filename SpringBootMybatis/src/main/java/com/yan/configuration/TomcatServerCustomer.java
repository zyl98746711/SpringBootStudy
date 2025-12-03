package com.yan.configuration;

import org.apache.catalina.connector.Connector;
import org.springframework.boot.tomcat.servlet.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.stereotype.Component;

@Component
public class TomcatServerCustomer implements WebServerFactoryCustomizer<@org.jetbrains.annotations.NotNull TomcatServletWebServerFactory> {

    @Override
    public void customize(TomcatServletWebServerFactory factory) {
        Connector connector = new Connector("HTTP/1.1");
        connector.setPort(8080);
        factory.addAdditionalConnectors(connector);
    }
}
