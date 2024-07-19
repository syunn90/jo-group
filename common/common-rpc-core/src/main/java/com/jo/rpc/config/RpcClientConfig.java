package com.jo.rpc.config;

import com.jo.rpc.comm.constant.CompressType;
import com.jo.rpc.comm.constant.LoadBalanceRule;
import com.jo.rpc.comm.constant.RpcConstant;
import com.jo.rpc.comm.constant.SerializeType;
import lombok.Data;

/**
 * @author Jo
 * @date 2024/7/8
 */
@Data
public class RpcClientConfig extends TLSConfig{


    private Integer channelWorkerThreads = RpcConstant.DEFAULT_THREADS; //channel处理工作线程数，连接数量多时可调大

    private Integer callBackTaskThreads = 200; //回调任务处理线程池，0为不设置
    private Integer callBackTaskQueueSize = 500; //回调任务线程池队列大小

    private Integer connectionTimeout = 5; //连接超时时间(秒)
    private Integer requestTimeout = 10; //请求超时时间(秒)
    private Integer connectionSizePerNode = 3; //每个节点连接数
    private Integer connectionIdleTime = 180; //超过连接空闲时间(秒)未收发数据则关闭连接
    private Integer heartBeatTimeInterval = 30; //发送心跳包间隔时间(秒)

    private CompressType compressType = CompressType.BZIP2; //压缩算法类型，无需压缩为NONE
    private SerializeType serializeType = SerializeType.PROTOSTUFF; //序列化类型，默认protostuff

    private LoadBalanceRule loadBalanceRule = LoadBalanceRule.ROUND; //集群负载均衡策略
    private boolean excludeUnAvailableNodesEnable = true; //集群模式下是否排除不可用的节点
    private Integer nodeErrorTimes = 3; //节点连接或请求超时/异常超过设置次数则置为节点不可用
    private Integer nodeHealthCheckTimeInterval = 10; //节点健康检查周期(秒),心跳包响应成功则恢复不可用的节点

    private Integer sendBuf = 65535; //tcp发送缓冲区
    private Integer receiveBuf = 65535; //tcp接收缓冲区
    private Integer lowWaterLevel = 1024 * 1024; //netty单个连接低水位
    private Integer highWaterLevel = 10 * 1024 * 1024; //netty单个连接高水位(避免内存溢出)

    private boolean trafficMonitorEnable = false; //是否开启流量控制
    private Long maxReadSpeed = 10 * 1000 * 1000L; //带宽限制，最大读取速度
    private Long maxWriteSpeed = 10 * 1000 * 1000L; //带宽限制，最大写出速度


}
