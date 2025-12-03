package com.zyl.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

import jakarta.servlet.http.HttpServletResponse;

@RequestMapping("/sse")
@RestController
public class SseStreamController {

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamData(HttpServletResponse response) {
        // 设置必要的响应头
        response.setContentType(MediaType.TEXT_EVENT_STREAM_VALUE);
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Connection", "keep-alive");
        
        SseEmitter emitter = new SseEmitter(60000L);
        new Thread(() -> {
            try {
                // 注意：使用SseEmitter时，不需要手动添加"data: "前缀和"\n\n"后缀
                // SseEmitter会自动处理这些格式
                
                for (int i = 10; i <= 100; i += 10) {
                    // 直接发送数据内容，不需要添加SSE格式前缀
                    String data = "Progress: " + i + "%";
                    emitter.send(data);

                    // 模拟任务耗时
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