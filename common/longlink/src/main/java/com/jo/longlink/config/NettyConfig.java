package com.jo.longlink.config;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author xtc
 * @date 2023/6/21
 */
public class NettyConfig {
    private static volatile ChannelGroup channelGroup = null;

    private static volatile ConcurrentHashMap<String, Channel> channelMap = null;

    private static final Object lock1 = new Object();
    private static final Object lock2 = new Object();

    public static ChannelGroup getChannelGroup() {
        if (null == channelGroup){
            synchronized (lock1){
                if (null == channelGroup){
                    channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
                }
            }
        }
        return channelGroup;
    }

    public static ConcurrentHashMap<String,Channel> getChannelMap() {
        if (null == channelMap){
            synchronized (lock2){
                if (null == channelMap){
                    channelMap = new ConcurrentHashMap<String, Channel>();
                }

            }
        }
        return channelMap;
    }

    public static Channel getChannel(String userId){
        if (null == channelMap){
            return getChannelMap().get(userId);
        }
        return channelMap.get(userId);
    }
}
