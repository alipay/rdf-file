/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.alipay.rdf.file.util;

import com.jcraft.jsch.ChannelSftp;

/**
 *
 * @author haofan.whf
 * @version $Id: SftpThreadContext.java, v 0.1 2018年10月06日 下午4:05 haofan.whf Exp $
 */
public class SftpThreadContext {

    private static final ThreadLocal<ChannelSftp> CHANNEL_SFTP_THREAD_LOCAL = new ThreadLocal<ChannelSftp>();


    public static ChannelSftp getChannelSftp(){
        return CHANNEL_SFTP_THREAD_LOCAL.get();
    }

    public static void setChannelSftp(ChannelSftp sftp){
        CHANNEL_SFTP_THREAD_LOCAL.set(sftp);
    }

    public static void clearChannelSftp(){
        CHANNEL_SFTP_THREAD_LOCAL.set(null);
    }

}