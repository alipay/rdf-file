package com.alipay.rdf.file.interfaces;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 * 
 * rdf-file-oss 手动指定执行processor 
 * 
 * @author hongwei.quhw
 * @version $Id: FileCoreManualProcessorConstants.java, v 0.1 2017年8月24日 下午3:42:00 hongwei.quhw Exp $
 */
public interface FileOssManualProcessorConstants {
    /**读时做长度校验*/
    public static final String READ_OSS_MD5_VALIDATOR  = "md5OSSReadValidator";
    /**读时做长度校验*/
    public static final String WRITE_OSS_MD5_VALIDATOR = "md5OSSWriteValidator";
}
