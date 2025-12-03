package com.yan.service;

import com.yan.listener.SignalResponseListener;
import com.yan.util.SnowFlakeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@RequiredArgsConstructor
@Service
public class IdService {

    private final SignalResponseListener listener;
    private final SnowFlakeUtil snowFlakeUtil;

    public List<String> get(int num) {
        return waitCallback(() -> Arrays.stream(snowFlakeUtil.nextIds(num)).mapToObj(String::valueOf).toList(), message -> {
            throw new RuntimeException(message);
        }, 10L, TimeUnit.SECONDS, "ABC");
    }

    public void callback() {
        listener.setSuccess("ABC");
    }


    private List<String> waitCallback(Supplier<List<String>> consumer, FailCallback failCallback, Long time, TimeUnit timeUnit, String key) {
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
        return consumer.get();
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
