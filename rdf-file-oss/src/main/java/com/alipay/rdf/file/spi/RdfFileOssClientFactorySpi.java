/*
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.rdf.file.spi;

import com.alipay.rdf.file.storage.OssConfig;
import com.aliyun.oss.OSSClient;

/**
 *
 * 用于扩展OSSClient的创建方式
 *
 * @author wanhaofan
 * @version RdfFileOssClientFactorySpi.java, v 0.1 2022年05月24日 11:35 AM wanhaofan
 */
public interface RdfFileOssClientFactorySpi {

    /**
     * 使用方可以贡献自定义实现OSSClient，用于扩展一些特殊的能力，比如容灾等
     * @param ossConfig
     * @return
     */
    OSSClient create(OssConfig ossConfig);

}