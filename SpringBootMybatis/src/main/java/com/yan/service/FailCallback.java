package com.yan.service;

/**
 * 失败的回调
 */
public interface FailCallback {
    /**
     * 失败的回调
     *
     * @param message 错误消息
     */
    void call(String message);
}
