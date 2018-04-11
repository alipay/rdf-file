package com.alipay.rdf.file.interfaces;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 * 
 * Oss processor key
 *
 * @author hongwei.quhw
 * @version $Id: FileOssProcessorConstants.java, v 0.1 2018年4月11日 上午10:55:12 hongwei.quhw Exp $
 */
public interface FileOssProcessorConstants {
    /**oss写 md5校验key， 组件非默认*/
    public static final String MD5_OSS_READ_VALIDATOR      = "md5OSSReadValidator";
    /**oss读 md5校验key， 组件非默认*/
    public static final String MD5_OSS_WRITE_VALIDATOR     = "md5OSSWriteValidator";

    /**oss写完本地自动上传到oss， 组件默认*/
    public static final String UPLOAD_OSS_AFTER_WRITECLOSE = "uploadOSSAfterWriteClose";
    /**创建oss写前校验， 组件非默认*/
    public static final String BEFORE_CREATE_OSS_WRITER    = "beforeCreateOssWriter";
    /**oss读行之前校验， 组件非默认*/
    public static final String OSS_BEFORE_READROW          = "ossBeforeReadRow";
}
