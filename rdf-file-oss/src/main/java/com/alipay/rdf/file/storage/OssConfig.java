package com.alipay.rdf.file.storage;

import com.alipay.rdf.file.model.FileDefaultConfig;
import com.alipay.rdf.file.util.RdfFileUtil;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 * 
 * oss 配置对象
 * 
 * @author hongwei.quhw
 * @version $Id: OssConfig.java, v 0.1 2017年8月24日 上午11:38:34 hongwei.quhw Exp $
 */
public class OssConfig {
    public static final String OSS_STORAGE_CONFIG_KEY            = "oss_storage_config";
    public static final String DEFAUT_OSS_TEMP_ROOT_KEY          = "defaut_oss_temp_root";
    public static final String DEFAULT_OSS_BIG_FILE_SIZE_KEY     = "default_oss_big_file_size";
    public static final String DEFAULT_OSS_APPEND_SIZE_LIMIT_KEY = "default_oss_append_size_limit_size";

    /** 
     * 写文件时OSS本地文件根目录
     * <li>写文件时会先写在本地再上传到OSS
     * <li>OSS本地目录为：tempRoot + OSS路径
     */
    private static String      DEFAUT_OSS_TEMP_ROOT              = "/home/admin/logs/ossLocal/";

    /** 使用大文件上传接口的文件大小 1G*/
    private static final long  DEFAULT_BIG_FILE_SIZE             = 1024L * 1024L * 1024L;

    /**追加上传的次数没有限制，文件大小上限为5GB*/
    private static final long  OSS_APPEND_SIZE_LIMIT             = 5 * 1024L * 1024L * 1024L;

    private final String       bucketName;
    private final String       endpoint;
    private final String       accessKeyId;
    private final String       accessKeySecret;
    /** 
     * 写文件时OSS本地文件根目录
     * <li>写文件时会先写在本地再上传到OSS
     * <li>OSS本地目录为：tempRoot + OSS路径
     */
    private String             ossTempRoot;

    private Long               ossBigFileSize;

    private Long               ossAppendSizeLimit;

    public OssConfig(String bucketName, String endpoint, String accessKeyId,
                     String accessKeySecret) {
        this.bucketName = bucketName;
        this.endpoint = endpoint;
        this.accessKeyId = accessKeyId;
        this.accessKeySecret = accessKeySecret;
    }

    public String getBucketName() {
        return bucketName;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public String getAccessKeyId() {
        return accessKeyId;
    }

    public String getAccessKeySecret() {
        return accessKeySecret;
    }

    public String getOssTempRoot() {
        if (RdfFileUtil.isNotBlank(ossTempRoot)) {
            return ossTempRoot;
        }

        String defaultOssTempRoot = (String) FileDefaultConfig.DEFAULT_FILE_PARAMS
            .get(DEFAUT_OSS_TEMP_ROOT_KEY);
        if (RdfFileUtil.isNotBlank(defaultOssTempRoot)) {
            return defaultOssTempRoot;
        }

        return DEFAUT_OSS_TEMP_ROOT;
    }

    public void setOssTempRoot(String ossTempRoot) {
        this.ossTempRoot = ossTempRoot;
    }

    public Long getOssBigFileSize() {
        if (null != ossBigFileSize) {
            return ossBigFileSize;
        }

        Long defaultOssBigFileSize = (Long) FileDefaultConfig.DEFAULT_FILE_PARAMS
            .get(DEFAULT_OSS_BIG_FILE_SIZE_KEY);
        if (null != defaultOssBigFileSize) {
            return defaultOssBigFileSize;
        }

        return DEFAULT_BIG_FILE_SIZE;
    }

    public void setOssBigFileSize(Long ossBigFileSize) {
        this.ossBigFileSize = ossBigFileSize;
    }

    public long getOssAppendSizeLimit() {
        if (null != ossAppendSizeLimit) {
            return ossAppendSizeLimit;
        }

        Long defaultOssAppendSizeLimit = (Long) FileDefaultConfig.DEFAULT_FILE_PARAMS
            .get(DEFAULT_OSS_APPEND_SIZE_LIMIT_KEY);
        if (null != defaultOssAppendSizeLimit) {
            return defaultOssAppendSizeLimit;
        }

        return OSS_APPEND_SIZE_LIMIT;
    }

    public void setOssAppendSizeLimit(Long ossAppendSizeLimit) {
        this.ossAppendSizeLimit = ossAppendSizeLimit;
    }
}
