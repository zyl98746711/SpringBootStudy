package com.zyl.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api")
public class StreamController {

    private final ExecutorService executor = Executors.newCachedThreadPool();

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    // 设置 MIME 类型为 text/event-stream
    public ResponseBodyEmitter streamData(HttpServletResponse response) {
        ResponseBodyEmitter emitter = new ResponseBodyEmitter();

        executor.execute(() -> {
            try {
                for (int i = 1; i <= 10; i++) {
                    // 模拟数据处理
                    Thread.sleep(1000);

                    // 发送数据
                    emitter.send("Data chunk " + i + "\n");
                }

                // 完成响应
                emitter.complete();
            } catch (IOException | InterruptedException e) {
                emitter.completeWithError(e);
            }
        });

        return emitter;
    }
}