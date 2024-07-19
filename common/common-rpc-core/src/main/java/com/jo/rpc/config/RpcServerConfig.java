package com.jo.rpc.config;

import com.jo.rpc.comm.constant.CompressType;
import com.jo.rpc.comm.constant.RpcConstant;
import com.jo.rpc.comm.constant.SerializeType;

/**
 * @author Jo
 * @date 2024/7/8
 */
public class RpcServerConfig extends TLSConfig{

    private Integer port = 9987; //绑定端口

    private Integer channelWorkerThreads = RpcConstant.DEFAULT_THREADS; //channel处理工作线程数，连接数量多时可调大

    private Integer businessThreads = 200; //业务处理线程池，0为不设置
    private Integer businessQueueSize = 500; //业务线程池队列大小

    private Integer connectionIdleTime = 180;//超过连接空闲时间(秒)未收发数据则关闭连接
    private Integer printConnectionNumInterval = 0; //打印服务端当前连接数信息，时间间隔(秒), 0为不打印
    private Boolean isPrintHearBeatPacketInfo = false; //是否打印心跳包信息

    private CompressType compressType = CompressType.BZIP2; //压缩算法类型，无需压缩为NONE
    private SerializeType serializeType = SerializeType.PROTOSTUFF; //序列化类型，默认protostuff

    private Integer sendBuf = 65535; //tcp发送缓冲区
    private Integer receiveBuf = 65535; //tcp接收缓冲区
    private Integer lowWaterLevel = 1024 * 1024; //netty低水位
    private Integer highWaterLevel = 10 * 1024 * 1024; //netty高水位

    private boolean deDuplicateEnable = false; //是否开启请求去重处理
    private Integer duplicateCheckTime = 10; //请求去重缓存时长(秒)
    private Long duplicateMaxSize = 1024 * 64L; //最大缓存请求个数

    private boolean trafficMonitorEnable = false; //是否开启流量控制
    private Long maxReadSpeed = 10 * 1000 * 1000L; //带宽限制，最大读取速度
    private Long maxWriteSpeed = 10 * 1000 * 1000L; //带宽限制，最大写出速度

    public Integer getPort() {
        return port;
    }

    public RpcServerConfig setPort(Integer port) {
        this.port = port;
        return this;
    }

    public Integer getChannelWorkerThreads() {
        return channelWorkerThreads;
    }

    public RpcServerConfig setChannelWorkerThreads(Integer channelWorkerThreads) {
        this.channelWorkerThreads = channelWorkerThreads;
        return this;
    }

    public Integer getBusinessThreads() {
        return businessThreads;
    }

    public RpcServerConfig setBusinessThreads(Integer businessThreads) {
        this.businessThreads = businessThreads;
        return this;
    }

    public Integer getConnectionIdleTime() {
        return connectionIdleTime;
    }

    public RpcServerConfig setConnectionIdleTime(Integer connectionIdleTime) {
        this.connectionIdleTime = connectionIdleTime;
        return this;
    }

    public Integer getPrintConnectionNumInterval() {
        return printConnectionNumInterval;
    }

    public RpcServerConfig setPrintConnectionNumInterval(Integer printConnectionNumInterval) {
        this.printConnectionNumInterval = printConnectionNumInterval;
        return this;
    }

    public Boolean getPrintHearBeatPacketInfo() {
        return isPrintHearBeatPacketInfo;
    }

    public RpcServerConfig setPrintHearBeatPacketInfo(Boolean printHearBeatPacketInfo) {
        isPrintHearBeatPacketInfo = printHearBeatPacketInfo;
        return this;
    }

    public Integer getSendBuf() {
        return sendBuf;
    }

    public RpcServerConfig setSendBuf(Integer sendBuf) {
        this.sendBuf = sendBuf;
        return this;
    }

    public Integer getReceiveBuf() {
        return receiveBuf;
    }

    public RpcServerConfig setReceiveBuf(Integer receiveBuf) {
        this.receiveBuf = receiveBuf;
        return this;
    }

    public Integer getLowWaterLevel() {
        return lowWaterLevel;
    }

    public RpcServerConfig setLowWaterLevel(Integer lowWaterLevel) {
        this.lowWaterLevel = lowWaterLevel;
        return this;
    }

    public Integer getHighWaterLevel() {
        return highWaterLevel;
    }

    public RpcServerConfig setHighWaterLevel(Integer highWaterLevel) {
        this.highWaterLevel = highWaterLevel;
        return this;
    }

    public boolean isDeDuplicateEnable() {
        return deDuplicateEnable;
    }

    public RpcServerConfig setDeDuplicateEnable(boolean deDuplicateEnable) {
        this.deDuplicateEnable = deDuplicateEnable;
        return this;
    }

    public Integer getDuplicateCheckTime() {
        return duplicateCheckTime;
    }

    public RpcServerConfig setDuplicateCheckTime(Integer duplicateCheckTime) {
        this.duplicateCheckTime = duplicateCheckTime;
        return this;
    }

    public Long getDuplicateMaxSize() {
        return duplicateMaxSize;
    }

    public RpcServerConfig setDuplicateMaxSize(Long duplicateMaxSize) {
        this.duplicateMaxSize = duplicateMaxSize;
        return this;
    }

    public boolean isTrafficMonitorEnable() {
        return trafficMonitorEnable;
    }

    public RpcServerConfig setTrafficMonitorEnable(boolean trafficMonitorEnable) {
        this.trafficMonitorEnable = trafficMonitorEnable;
        return this;
    }

    public Long getMaxReadSpeed() {
        return maxReadSpeed;
    }

    public RpcServerConfig setMaxReadSpeed(Long maxReadSpeed) {
        this.maxReadSpeed = maxReadSpeed;
        return this;
    }

    public Long getMaxWriteSpeed() {
        return maxWriteSpeed;
    }

    public RpcServerConfig setMaxWriteSpeed(Long maxWriteSpeed) {
        this.maxWriteSpeed = maxWriteSpeed;
        return this;
    }

    public CompressType getCompressType() {
        return compressType;
    }

    public RpcServerConfig setCompressType(CompressType compressType) {
        this.compressType = compressType;
        return this;
    }

    public SerializeType getSerializeType() {
        return serializeType;
    }

    public RpcServerConfig setSerializeType(SerializeType serializeType) {
        this.serializeType = serializeType;
        return this;
    }

    public Integer getBusinessQueueSize() {
        return businessQueueSize;
    }

    public RpcServerConfig setBusinessQueueSize(Integer businessQueueSize) {
        this.businessQueueSize = businessQueueSize;
        return this;
    }
}
