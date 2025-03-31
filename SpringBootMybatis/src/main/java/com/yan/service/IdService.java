package com.yan.service;

import com.yan.listener.SignalResponseListener;
import com.yan.util.SnowFlakeUtil;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class IdService {

    private final SignalResponseListener listener;
    private final SnowFlakeUtil snowFlakeUtil;

    public List<String> get(int num) {
        AtomicReference<List<String>> result = new AtomicReference<>();
        waitCallback((n) -> result.set(Arrays.stream(snowFlakeUtil.nextIds(n)).mapToObj(String::valueOf).toList()), message -> {
            throw new RuntimeException(message);
        }, 10L, TimeUnit.SECONDS, "ABC", num);
        return result.get();
    }

    public void callback() {
        listener.setSuccess("ABC");
    }


    private void waitCallback(SuccessCallback successCallback, FailCallback failCallback, Long time, TimeUnit timeUnit, String key, int num) {
        CountDownLatch latch = new CountDownLatch(1);
        try {
            new WaitThread(latch, key).start();
            if (!latch.await(time, timeUnit)) {
                failCallback.call("响应超时");
            }
        } catch (InterruptedException e) {
            failCallback.call(e.getMessage());
            Thread.currentThread().interrupt();
        } finally {
            listener.remove(key);
        }
        successCallback.call(num);
    }

    public class WaitThread extends Thread {

        private final CountDownLatch countDownLatch;
        private final String key;

        public WaitThread(CountDownLatch countDownLatch, String key) {
            this.countDownLatch = countDownLatch;
            this.key = key;
        }

        @Override
        public void run() {
            while (true) {
                if (listener.isSuccess(key)) {
                    countDownLatch.countDown();
                    return;
                }
            }
        }
    }
}
