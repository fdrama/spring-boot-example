package io.github.padago.springdocker.util;

/**
 * @author fdrama
 * date 2023年11月03日 15:06
 */
public class SnowflakeIDGenerator {
    private static SnowflakeIDGenerator instance;

    private final long epoch;
    private final long machineId;
    private long sequence = 0L;
    private final long machineIdBits = 10L;
    private final long maxMachineId = -1L ^ (-1L << machineIdBits);
    private final long sequenceBits = 12L;
    private final long machineIdShift = sequenceBits;
    private final long timestampLeftShift = sequenceBits + machineIdBits;
    private final long sequenceMask = -1L ^ (-1L << sequenceBits);
    private long lastTimestamp = -1L;

    private SnowflakeIDGenerator(long machineId) {
        this.epoch = 1514764800000L;
        this.machineId = machineId;
    }

    public static synchronized SnowflakeIDGenerator getInstance(long machineId) {
        if (instance == null) {
            instance = new SnowflakeIDGenerator(machineId);
        }
        return instance;
    }

    public long generateID() {
        long timestamp = System.currentTimeMillis();

        // 如果时钟回退，抛出异常或等待时钟前进
        if (timestamp < lastTimestamp) {
            throw new RuntimeException("Clock moved backwards. Refusing to generate ID");
        }

        // 如果在同一毫秒内多次生成ID，增加序列号
        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & sequenceMask;
            if (sequence == 0) {
                // 序列号溢出，等待下一毫秒
                timestamp = waitNextMillis(timestamp);
            }
        } else {
            sequence = 0;
        }

        lastTimestamp = timestamp;

        // 组合生成的ID
        return ((timestamp - epoch) << timestampLeftShift) |
                (machineId << machineIdShift) |
                sequence;
    }

    private long waitNextMillis(long lastTimestamp) {
        long timestamp = System.currentTimeMillis();
        while (timestamp <= lastTimestamp) {
            timestamp = System.currentTimeMillis();
        }
        return timestamp;
    }
}

