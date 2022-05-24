package com.alipay.rdf.file.storage;

import com.alipay.rdf.file.model.FileDefaultConfig;
import com.alipay.rdf.file.util.RdfFileUtil;
import com.aliyun.oss.ClientConfiguration;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 *
 * oss 配置对象
 *
 * @author hongwei.quhw
 * @version $Id: OssConfig.java, v 0.1 2017年8月24日 上午11:38:34 hongwei.quhw Exp $
 */
public class OssConfig {
    public static final String        OSS_STORAGE_CONFIG_KEY            = "oss_storage_config";
    public static final String        DEFAUT_OSS_TEMP_ROOT_KEY          = "defaut_oss_temp_root";
    public static final String        DEFAULT_OSS_BIG_FILE_SIZE_KEY     = "default_oss_big_file_size";
    public static final String        DEFAULT_OSS_APPEND_SIZE_LIMIT_KEY = "default_oss_append_size_limit_size";

    /**
     * 写文件时OSS本地文件根目录
     * <li>写文件时会先写在本地再上传到OSS
     * <li>OSS本地目录为：tempRoot + OSS路径
     */
    private static String             DEFAUT_OSS_TEMP_ROOT              = "/home/admin/logs/ossLocal/";

    /** 使用大文件上传接口的文件大小 1G*/
    private static final long         DEFAULT_BIG_FILE_SIZE             = 1024L * 1024L * 1024L;

    /**追加上传的次数没有限制，文件大小上限为5GB*/
    private static final long         OSS_APPEND_SIZE_LIMIT             = 5 * 1024L * 1024L * 1024L;

    private String              bucketName;


    private String              endpoint;
    private String              accessKeyId;
    private String              accessKeySecret;
    private ClientConfiguration clientConfiguration;

    /**
     * 用于业务自定义想要初始化ossclient的信息
     * 比如: ddsoss://datasourceName@version
     * */
    private String connectionURL = "";

    /**
     * 默认使用com.alipay.rdf.file.storage.DefaultOssClientFactory进行创建
     */
    private String ossClientFactoryType = "default";


    /**
     * 写文件时OSS本地文件根目录
     * <li>写文件时会先写在本地再上传到OSS
     * <li>OSS本地目录为：tempRoot + OSS路径
     */
    private String                    ossTempRoot;

    private Long                      ossBigFileSize;

    private Long                      ossAppendSizeLimit;

    /**
     * oss指定范围读取时，大小小于等于零 返回数据行为不同于nas（本地磁盘）返回的是空数据
     * 默认实现开启了校验，直接报错，
     * emptyLeZero = true 返回空数据
     */
    private boolean                   emptyLeZero = false;

    public OssConfig(String bucketName, String endpoint, String accessKeyId,
                     String accessKeySecret) {
        this(bucketName, endpoint, accessKeyId, accessKeySecret, null);
    }

    public OssConfig(String bucketName, String connectionURL, String ossClientFactoryType) {
        this.connectionURL = connectionURL;
        this.bucketName = bucketName;
        this.ossClientFactoryType = ossClientFactoryType;
    }

    public OssConfig(String bucketName, String connectionURL, String ossClientFactoryType, ClientConfiguration clientConfiguration) {
        this.connectionURL = connectionURL;
        this.bucketName = bucketName;
        this.ossClientFactoryType = ossClientFactoryType;
        this.clientConfiguration = clientConfiguration;
    }

    public OssConfig(String bucketName, String endpoint, String accessKeyId, String accessKeySecret,
                     ClientConfiguration clientConfiguration) {
        this.bucketName = bucketName;
        this.endpoint = endpoint;
        this.accessKeyId = accessKeyId;
        this.accessKeySecret = accessKeySecret;
        this.clientConfiguration = clientConfiguration;
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

    public ClientConfiguration getClientConfiguration() {
        return this.clientConfiguration;
    }

    public boolean isEmptyLeZero() {
        return emptyLeZero;
    }

    public void setEmptyLeZero(boolean emptyLeZero) {
        this.emptyLeZero = emptyLeZero;
    }

    public String getConnectionURL() {
        return connectionURL;
    }

    public void setConnectionURL(String connectionURL) {
        this.connectionURL = connectionURL;
    }

    public String getOssClientFactoryType() {
        return ossClientFactoryType;
    }

    public void setOssClientFactoryType(String ossClientFactoryType) {
        this.ossClientFactoryType = ossClientFactoryType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OssConfig ossConfig = (OssConfig) o;

        if (bucketName != null ? !bucketName.equals(ossConfig.bucketName) : ossConfig.bucketName != null) return false;
        if (endpoint != null ? !endpoint.equals(ossConfig.endpoint) : ossConfig.endpoint != null) return false;
        if (accessKeyId != null ? !accessKeyId.equals(ossConfig.accessKeyId) : ossConfig.accessKeyId != null)
            return false;
        if (accessKeySecret != null ? !accessKeySecret.equals(ossConfig.accessKeySecret) : ossConfig.accessKeySecret != null)
            return false;
        if (connectionURL != null ? !connectionURL.equals(ossConfig.connectionURL) : ossConfig.connectionURL != null)
            return false;
        return ossClientFactoryType != null ? ossClientFactoryType.equals(ossConfig.ossClientFactoryType) : ossConfig.ossClientFactoryType == null;
    }

    @Override
    public int hashCode() {
        int result = bucketName != null ? bucketName.hashCode() : 0;
        result = 31 * result + (endpoint != null ? endpoint.hashCode() : 0);
        result = 31 * result + (accessKeyId != null ? accessKeyId.hashCode() : 0);
        result = 31 * result + (accessKeySecret != null ? accessKeySecret.hashCode() : 0);
        result = 31 * result + (connectionURL != null ? connectionURL.hashCode() : 0);
        result = 31 * result + (ossClientFactoryType != null ? ossClientFactoryType.hashCode() : 0);
        return result;
    }
}
