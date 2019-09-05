/**
 * Alipay.com Inc. Copyright (c) 2004-2019 All Rights Reserved.
 */
package com.alipay.rdf.file.util;

import com.alipay.rdf.file.storage.SftpConfig;

/**
 * sftp操作上下文
 * @author iminright-ali
 * @version : SftpOperationContext.java, v 0.1 2019年09月05日 14:43 iminright-ali Exp $
 */
public class SftpOperationContextHolder {

    private static final ThreadLocal<SftpConfig> SFTP_CONFIG_THREAD_LOCAL = new ThreadLocal<SftpConfig>();


    public static SftpConfig getSftpConfig(){
        return SFTP_CONFIG_THREAD_LOCAL.get();
    }

    public static void clearSftpConfig(){
        SFTP_CONFIG_THREAD_LOCAL.set(null);
    }

    public static void setSftpConfig(SftpConfig sftpConfig){
        SFTP_CONFIG_THREAD_LOCAL.set(sftpConfig);
    }


}