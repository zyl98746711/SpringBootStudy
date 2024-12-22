package com.yan.listener;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 信令响应接收监听
 */
@Component
public class SignalResponseListener {

    private static final Map<String, Boolean> MAPS = new ConcurrentHashMap<>(16);

    public boolean isSuccess(String key) {
        if (!MAPS.containsKey(key)) {
            return false;
        }
        return MAPS.getOrDefault(key, Boolean.FALSE);
    }

    public void setSuccess(String key) {
        MAPS.put(key, Boolean.TRUE);
    }

    public void remove(String key) {
        MAPS.remove(key);
    }
}
