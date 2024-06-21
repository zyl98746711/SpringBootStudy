package com.yan.configuration;


@HttpClient(method = "GET", url = "http://localhost:8080/id/1")
public interface Client {

    void sayHello();
}
