package com.yan.configuration;

import com.common.ResponseBody;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(Exception.class)
    public <T> ResponseBody<T> exceptionHandler(Exception e) {
        return ResponseBody.fail("请求异常:" + e.getMessage());
    }
}
