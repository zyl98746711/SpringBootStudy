package com.yan.controller;

import com.yan.configuration.Client;
import com.yan.util.SnowFlakeUtil;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("id")
public class IdController {

    private final SnowFlakeUtil snowFlakeUtil;
    private final Client client;

    public IdController(SnowFlakeUtil snowFlakeUtil, Client client) {
        this.snowFlakeUtil = snowFlakeUtil;
        this.client = client;
    }

    @GetMapping("{num}")
    public List<String> getIds(@PathVariable("num") Integer num) {
        client.sayHello();
        return Arrays.stream(snowFlakeUtil.nextIds(num)).mapToObj(String::valueOf).toList();
    }
}
