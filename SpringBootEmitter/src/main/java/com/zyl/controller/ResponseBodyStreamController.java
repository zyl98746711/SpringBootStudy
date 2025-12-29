package com.zyl.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import java.io.IOException;

import jakarta.servlet.http.HttpServletResponse;

@RestController
public class ResponseBodyStreamController {

    @GetMapping(value = "/stream")
    public ResponseBodyEmitter streamData(HttpServletResponse response) {
        // 关键配置：显式设置所有必要的响应头
        response.setContentType(MediaType.TEXT_EVENT_STREAM_VALUE);
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Connection", "keep-alive");

        // 创建ResponseBodyEmitter并设置较长的超时时间
        ResponseBodyEmitter emitter = new ResponseBodyEmitter(60000L);

        new Thread(() -> {
            try {
                // 先发送一个空行，确保连接建立
                emitter.send("\n");

                for (int i = 10; i <= 100; i += 10) {
                    // 严格遵循SSE标准格式
                    String data = "data: Progress: " + i + "%\n\n";
                    emitter.send(data);

                    // 模拟任务耗时，直接添加延时
                    Thread.sleep(1000);
                }

                // 发送完成后再等待一小段时间，确保所有数据都已发送
                Thread.sleep(100);
                emitter.complete(); // 正常完成连接
            } catch (IOException e) {
                // IO异常通常是客户端断开连接，不需要报错
                emitter.complete();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                emitter.completeWithError(e);
            } catch (Exception e) {
                emitter.completeWithError(e);
            }
        }).start();

        return emitter;
    }
}