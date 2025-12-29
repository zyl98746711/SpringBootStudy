package com.zyl.controller;

import com.zyl.annotations.MeterCountAnnotation;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/micro/meter")
public class MicroMeterController {

    @GetMapping
    @MeterCountAnnotation
    public String test(@RequestParam("name") String name) {
        return "Hello:" + name;
    }

}
