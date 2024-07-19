package com.jo.rpc.comm.id;

/**
 * @author Jo
 * @date 2024/7/8
 */
public class IdGenerator {

    private static volatile ISnowFlakeId snowFlakeId;

    /**
     * 生成id
     *
     * @return id
     */
    public static Long getId() {
        if (snowFlakeId == null) {
            synchronized (IdGenerator.class) {
                if (snowFlakeId == null) {
                    snowFlakeId = new SnowflakeId(1, 1);
                }
            }
        }
        return snowFlakeId.nextId();
    }

}
