package com.yan.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 雪花算法
 */
@Component
public class SnowFlakeUtil {

    /**
     * 机器id， 通过yml配置的方式声明
     */
    @Value("${snowflake.machineId:0}")
    private long machineId = 0;

    /**
     * 服务id， 通过yml配置的方式声明
     */
    @Value("${snowflake.serviceId:0}")
    private long serviceId = 0;

    /**
     * 自增序列
     */
    private long sequence;

    // 需要做机器id和服务id的兼容性校验， 不能超过了5位的最大值

    /**
     * 机器id占用的bit位数
     */
    private final long machineIdBits = 5L;

    /**
     * 服务id占用的bit位数
     */
    private final long serviceIdBits = 5L;

    /**
     * 序列占用的bit位数
     */
    private final long sequenceBits = 12L;

    /**
     * 计算出机器id的最大值 -1 往左移 machineIdBits 位， 再做亦或运算
     */
    private final long maxMachineId = -1 ^ (-1 << machineIdBits); // -1 往左移 machineIdBits 位， 再做亦或运算
    // 11111111 11111111 11111111 11111111 11111111
    // 11111111 11111111 11111111 11111111 11100000
    // 00000000 00000000 00000000 00000000 00011111

    /**
     * 计算出服务id的最大值
     */
    private final long maxServiceId = -1 ^ (-1 << serviceIdBits);


    /**
     * 服务id需要位移的位数， 即从右侧开始， 将数字左移 sequenceBits 到固定的位置
     */
    private final long serviceIdShift = sequenceBits;

    /**
     * 机器id需要位移的位数， 即从右侧开始， 将数字左移 sequenceBits + serviceIdBits  到固定的位置
     */
    private final long machineIdShift = sequenceBits + serviceIdBits;

    /**
     * 时间戳需要位移的位数, 即从右侧开始， 将数字左移 sequenceBits + serviceIdBits + machineIdBits 到固定的位置
     */
    private final long timestampShift = sequenceBits + serviceIdBits + machineIdBits;

    /**
     * 序列的最大值 -1 往左移 sequenceBits 位， 再做亦或运算
     */
    private final long maxSequenceId = -1 ^ (-1 << sequenceBits);

    /**
     * 记录最近一次获取id的时间
     */
    private long lastTimestamp = -1;

    /**
     * 拿到当前系统时间的毫秒值
     *
     * @return
     */
    private long timeGen() {
        return System.currentTimeMillis();
    }

    /**
     * 生成全局唯一id
     * 因为有很多服务调用这个方法， 所以需要加sychronized锁
     */
    public synchronized long nextId() {
        //1. 拿到当前系统时间的毫秒值
        long timestamp = timeGen();
        // 避免时间回拨造成出现重复的id
        if (timestamp < lastTimestamp) {
            // 说明出现了时间回拨
            System.out.println("当前服务出现时间回拨");
        }

        //2. 41个bit的时间知道了存什么了， 但是序列也需要计算一下。 如果是同一毫秒，序列就需要 还原 或者 ++
        // 判读当前生成的id的时间 和 上一次生成的时间
        if (timestamp == lastTimestamp) {
            // 同一毫秒值生成id
            sequence = (sequence + 1) & maxSequenceId; // 加1最大值进行与运算， 结果是如果超过了maxSequenceId则为0， 小于则不变
            if (sequence == 0) {
                // 进到这个if，说明已经超出了sequence序列的最大取值范围
                // 需要等到下一个毫秒值再回来生成具体的值
                timestamp = timeGen();
                // 写 <= 而不 写 == 是为了避免出现时间回拨的问题
                while (timestamp <= lastTimestamp) {
                    // 时间还没动
                    timestamp = timeGen();
                }
            }
        } else {
            // 另一个时间点生成id
            sequence = 0;
        }
        //3. 重新给 lastTimestamp 赋值
        lastTimestamp = timestamp;

        //4. 计算id，将几位值拼接起来， 41bit位的时间， 5位的机器， 5位的服务， 12位的序列
        /**
         * 41 个bit位存储时间戳， 从0开始计算， 最多可以存储 69.7年。
         * 如果从默认使用， 从1970年到现在，最多可以用到2040年。
         * 按照从 2023-12-28号开始计算，存储41个bit位， 最多可以使用到2093年
         */
        long timeStart = 1703692800000L;
        return ((timestamp - timeStart) << timestampShift) | // 相减的差值 往左移  timestampShift
                (machineId << machineIdShift) |  // machineId 往左移  machineIdShift
                (serviceId << serviceIdShift) |  // serviceId 往左移  serviceIdShift
                sequence &
                        Long.MAX_VALUE;
    }

    public long[] nextIds(int num) {
        long[] ids = new long[num];
        for (int i = 0; i < num; i++) {
            ids[i] = nextId();
        }
        return ids;
    }
}
