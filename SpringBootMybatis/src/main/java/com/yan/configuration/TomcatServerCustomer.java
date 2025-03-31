package com.yan.configuration;

import org.apache.catalina.connector.Connector;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.stereotype.Component;

@Component
public class TomcatServerCustomer implements WebServerFactoryCustomizer<TomcatServletWebServerFactory> {

    @Override
    public void customize(TomcatServletWebServerFactory factory) {
        final Connector httpConn = new Connector("HTTP/1.1");
        httpConn.setPort(8080);
        factory.addAdditionalTomcatConnectors(httpConn);
    }
}
