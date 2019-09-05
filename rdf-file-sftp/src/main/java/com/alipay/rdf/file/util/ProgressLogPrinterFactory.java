/**
 * Alipay.com Inc. Copyright (c) 2004-2019 All Rights Reserved.
 */
package com.alipay.rdf.file.util;

import com.alipay.rdf.file.exception.RdfErrorEnum;
import com.alipay.rdf.file.exception.RdfFileException;
import com.alipay.rdf.file.storage.SftpConfig;
import com.jcraft.jsch.SftpProgressMonitor;

/**
 * 进度监控日志打印器工厂
 * @author iminright-ali
 * @version : ProgressLogPrinterFactory.java, v 0.1 2019年09月05日 11:17 iminright-ali Exp $
 */
public class ProgressLogPrinterFactory {

    /**
     * 获取进度日志打印工具实例
     * @param sftpConfig
     * @return
     */
    public static SftpProgressMonitor generate(SftpConfig sftpConfig){
        SftpProgressMonitor sftpProgressMonitor = null;
        switch (sftpConfig.getProgressLogPrintTypeEnum()){
            case SYNC:
                sftpProgressMonitor = new SFTPLogMonitor();
                break;
            case FIXED_PERIOD:
                sftpProgressMonitor = new SFTPLogMonitorAtFixedRate(sftpConfig);
                break;
            default:
                throw new RdfFileException("rdf-file#SftpProgressMonitor.generate,ProgressLogPrintTypeEnum="
                        + sftpConfig.getProgressLogPrintTypeEnum() + " doesn't support yet.", RdfErrorEnum.ILLEGAL_ARGUMENT);
        }
        return sftpProgressMonitor;
    }



}