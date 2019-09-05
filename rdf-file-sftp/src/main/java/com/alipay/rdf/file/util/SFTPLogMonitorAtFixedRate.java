/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2012 All Rights Reserved.
 */
package com.alipay.rdf.file.util;

import com.alipay.rdf.file.storage.SftpConfig;
import com.jcraft.jsch.SftpProgressMonitor;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * SFTP进度监控-日志方式输出。
 * 定时输出
 * 
 * @author haofan.whf
 * @version $Id: SFTPLogMonitor.java, v 0.1 2018-10-4 下午20:41:40 haofan.whf Exp $
 */
public class SFTPLogMonitorAtFixedRate extends SFTPLogMonitor {
    private static final AtomicInteger THREAD_COUNT = new AtomicInteger(1);

    private ScheduledExecutorService printService;

    private SftpConfig sftpConfig;

    private ScheduledFuture<?> printLogFuture;

    public SFTPLogMonitorAtFixedRate(SftpConfig sftpConfig){
        this.sftpConfig = sftpConfig;
    }

    /**
     * @see SftpProgressMonitor#count(long)
     */
    public boolean count(long count) {
        this.current += count;
        return true;
    }

    /**
     * @see SftpProgressMonitor#end()
     */
    public void end() {
        if(printLogFuture != null){
            printLogFuture.cancel(true);
            if(printService != null && !printService.isShutdown()){
                printService.shutdown();
            }
        }
        RdfFileLogUtil.common.info("rdf-file#SFTPLogMonitorAtFixedRate.end文件传输完成"
                + "，源文件：{" + this.srcFile + "},目标文件：{" + this.destFile + "}");
    }

    /**
     * @see SftpProgressMonitor#init(int, String, String, long)
     */
    public void init(int op, String src, String dest, long max) {
        super.init(op, src, dest, max);
        printService = Executors.newScheduledThreadPool(1, new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "SFTPProgressMonitor-" + THREAD_COUNT.getAndIncrement());
            }
        });
        final Runnable printProgressLog = new Runnable() {
            public void run() {
                doPrint("SFTPLogMonitorAtFixedRate");
            }
        };
        printLogFuture = printService.scheduleAtFixedRate(printProgressLog, 0
                , sftpConfig.getProgressPrintLogPeriod(), SECONDS);
    }

    public SftpConfig getSftpConfig() {
        return sftpConfig;
    }

    public void setSftpConfig(SftpConfig sftpConfig) {
        this.sftpConfig = sftpConfig;
    }
}
