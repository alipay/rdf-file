package com.alipay.rdf.file.interfaces;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 * 
 * 日志回调接口
 * 
 * @author hongwei.quhw
 * @version $Id: LogCallback.java, v 0.1 2017年7月22日 下午11:10:10 hongwei.quhw Exp $
 */
public interface LogCallback {

    /**
     * 是否打印debug日志
     * */
    boolean isDebug();

    /**
     * 是否打印warn日志
     * @return
     */
    boolean isWarn();

    /**
     * 是否打印info日志
     * 
     * @return
     */
    boolean isInfo();

    /**
     * 打印info日志
     * 
     * @param msg
     */
    void info(String msg);

    /**
     * 打印info日志
     * 
     * @param msg
     * @param throwable
     */
    void info(String msg, Throwable throwable);

    /**
     * 打印warn日志
     * 
     * @param msg
     */
    void warn(String msg);

    /**
     * 打印warn日志
     * 
     * @param msg
     * @param throwable
     */
    void warn(String msg, Throwable throwable);

    /**
     * 打印debug日志
     * 
     * @param msg
     */
    void debug(String msg);

    /**
     * 打印debug日志
     * 
     * @param msg
     * @param throwable
     */
    void debug(String msg, Throwable throwable);

    /**
     * 打印error日志
     * 
     * @param msg
     */
    void error(String msg);

    /**
     * 打印errorriz
     * 
     * @param msg
     * @param throwable
     */
    void error(String msg, Throwable throwable);
}
