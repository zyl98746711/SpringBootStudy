package com.yan.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.service.registry.ImportHttpServices;

@Configuration
// 注解声明
@ImportHttpServices(basePackages = "com.yan.exchange")
public class HttpExchangeConfiguration {

    // 显式声明
//    @Bean
//    public UserExchange userExchange() {
//        RestClient restClient = RestClient.builder()
//                .baseUrl("http://localhost:8080")
//                .build();
//        RestClientAdapter restClientAdapter = RestClientAdapter.create(restClient);
//        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(restClientAdapter)
//                .build();
//        return factory.createClient(UserExchange.class);
//
//    }
}
