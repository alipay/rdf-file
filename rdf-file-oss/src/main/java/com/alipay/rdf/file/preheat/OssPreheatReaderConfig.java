package com.alipay.rdf.file.preheat;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 *
 * Oss预热读配置
 *
 * @author hongwei.quhw
 * @version $Id: OssPreheatReaderConfig.java, v 0.1 2018年4月4日 下午3:48:48 hongwei.quhw Exp $
 */
public class OssPreheatReaderConfig {
    /**oss 预热读配置key*/
    public static final String OSS_PREHEAT_READER_CONFIG_KEY = "OssPreheatReaderConfigKey";

    /**文件路径*/
    private List<String>       paths                         = new ArrayList<String>();
    /**执行线程池*/
    private ThreadPoolExecutor executor;
    /**内部构建队列或者容器，设置大小*/
    private int                capacity                      = 19;
    /**内部分片大小*/
    private int                sliceBlockSize                = 32 * 1024 * 1024;
    /**配置监控时间间隔*/
    private int                monitorPeriod                 = 10000;
    /**是否要监控线程池*/
    private boolean            monitorThreadPool             = false;

    //以下是组件默认构建线程池参数
    private int                corePoolSize                  = 3;
    private int                maxPoolSize                   = 6;
    private long               keepAliveTime                 = 10;
    private int                blockingQueueSize             = 12;

    public List<String> getPaths() {
        return paths;
    }

    public void setPaths(List<String> paths) {
        this.paths = paths;
    }

    public ThreadPoolExecutor getExecutor() {
        return executor;
    }

    public void setExecutor(ThreadPoolExecutor executor) {
        this.executor = executor;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getSliceBlockSize() {
        return sliceBlockSize;
    }

    public void setSliceBlockSize(int sliceBlockSize) {
        this.sliceBlockSize = sliceBlockSize;
    }

    public int getMonitorPeriod() {
        return monitorPeriod;
    }

    public void setMonitorPeriod(int monitorPeriod) {
        this.monitorPeriod = monitorPeriod;
    }

    public boolean isMonitorThreadPool() {
        return monitorThreadPool;
    }

    public void setMonitorThreadPool(boolean monitorThreadPool) {
        this.monitorThreadPool = monitorThreadPool;
    }

    public int getCorePoolSize() {
        return corePoolSize;
    }

    public void setCorePoolSize(int corePoolSize) {
        this.corePoolSize = corePoolSize;
    }

    public int getMaxPoolSize() {
        return maxPoolSize;
    }

    public void setMaxPoolSize(int maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
    }

    public long getKeepAliveTime() {
        return keepAliveTime;
    }

    public void setKeepAliveTime(long keepAliveTime) {
        this.keepAliveTime = keepAliveTime;
    }

    public int getBlockingQueueSize() {
        return blockingQueueSize;
    }

    public void setBlockingQueueSize(int blockingQueueSize) {
        this.blockingQueueSize = blockingQueueSize;
    }
}
