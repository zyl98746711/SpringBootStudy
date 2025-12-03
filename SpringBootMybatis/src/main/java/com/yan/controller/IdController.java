package com.yan.controller;

import com.common.ResponseBody;
import com.yan.service.IdService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("id")
public class IdController {


    private final IdService idService;
    
    @GetMapping("/{num}")
    public List<String> getIds(@PathVariable("num") Integer num) {
        return idService.get(num);
    }


    @GetMapping("callback")
    public ResponseBody<Void> callback() {
        idService.callback();
        return ResponseBody.success(null);
    }
}
