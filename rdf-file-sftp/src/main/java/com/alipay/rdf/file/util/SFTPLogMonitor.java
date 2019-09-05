/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2012 All Rights Reserved.
 */
package com.alipay.rdf.file.util;

import com.jcraft.jsch.SftpProgressMonitor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * SFTP进度监控-日志方式输出。
 * 
 * @author haofan.whf
 * @version $Id: SFTPLogMonitor.java, v 0.1 2018-10-4 下午20:41:40 haofan.whf Exp $
 */
public class SFTPLogMonitor implements SftpProgressMonitor {


    /** 总数 */
    protected long                total            = 0;

    /** 当前 */
    protected long                current          = 0;

    /** op */
    protected int                 op               = -1;

    protected static final String DOWNLOAD_DESC = "下载操作";
    protected static final String UPLOAD_DESC = "上传操作";

    /** 源文件 */
    protected String srcFile = null;

    /** 目标文件 */
    protected String destFile = null;

    public SFTPLogMonitor(){

    }

    private String descOp(){
        return "[" + (this.op == PUT ? UPLOAD_DESC : DOWNLOAD_DESC) + "]";
    }

    /** 
     * @see SftpProgressMonitor#count(long)
     */
    public boolean count(long count) {
        this.current += count;
        doPrint("SFTPLogMonitor");
        return true;
    }

    protected void doPrint(String logPrefix){
        if(this.op == PUT){
            RdfFileLogUtil.common.info("rdf-file#" + logPrefix + ".count" + descOp()
                    + ",文件大小：{" + RdfFileUtil.humanReadableByteCount(this.total, false) + "}"
                    + ",当前进度：{" + RdfFileUtil.humanReadableByteCount(this.current, false) + "}");
        }else{
            RdfFileLogUtil.common.info("rdf-file#" + logPrefix + ".count" + descOp()
                    + ",当前进度：{" + RdfFileUtil.humanReadableByteCount(this.current, false) + "}");
        }
    }

    /**
     * @see SftpProgressMonitor#end()
     */
    public void end() {
        RdfFileLogUtil.common.info("rdf-file#SFTPLogMonitor.end文件传输完成"
                + "，源文件：{" + this.srcFile + "},目标文件：{" + this.destFile + "}");
    }

    /**
     * @see SftpProgressMonitor#init(int, java.lang.String, java.lang.String, long)
     */
    public void init(int op, String src, String dest, long max) {
        this.srcFile = src;
        this.destFile = dest;
        this.op = op;

        //如果是上传，则把源文件大小计算出来
        if (op == PUT) {
            if(!"-".equals(src)){
                FileInputStream stream = null;
                try {
                    File file = new File(src);
                    stream = new FileInputStream(file);
                    this.total = stream.available();
                } catch (Throwable e) {
                    RdfFileLogUtil.common.warn("rdf-file#SFTPLogMonitor.init异常"
                            + ",源文件：{" + this.srcFile + "},目标文件：{" + this.destFile + "}");
                } finally {
                    try {
                        if(stream != null){
                            stream.close();
                        }
                    } catch (IOException e) {
                        RdfFileLogUtil.common.warn("rdf-file#SFTPLogMonitor.init关闭源文件流异常"
                                + ",源文件：{" + this.srcFile + "},目标文件：{" + this.destFile + "}");
                    }
                }
            }
        }

        RdfFileLogUtil.common.info("rdf-file#SFTPLogMonitor.init" + descOp()
                + ",源文件：{" + this.srcFile + "},目标文件：{" + this.destFile + "}");
    }
}
