package com.yan.controller;

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

    public IdController(SnowFlakeUtil snowFlakeUtil) {
        this.snowFlakeUtil = snowFlakeUtil;
    }

    @GetMapping("{num}")
    public List<String> getIds(@PathVariable("num") Integer num) {
        return Arrays.stream(snowFlakeUtil.nextIds(num)).mapToObj(String::valueOf).toList();
    }
}
