package com.yan.controller;

import com.common.ResponseBody;
import com.yan.configuration.Client;
import com.yan.service.IdService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("id")
public class IdController {


    private final Client client;
    private final IdService idService;


    @GetMapping("/{num}")
    public List<String> getIds(@PathVariable("num") Integer num) {
        //  client.sayHello();
        return idService.get(num);
    }


    @GetMapping("callback")
    public ResponseBody<Void> callback() {
        idService.callback();
        return ResponseBody.success(null);
    }
}
