/*
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.rdf.file.spi;

import com.alipay.rdf.file.init.RdfInit;
import com.alipay.rdf.file.storage.OssConfig;
import com.aliyun.oss.OSSClient;

/**
 *
 * 用于扩展OSSClient的创建方式
 *
 * @author wanhaofan
 * @version RdfFileOssClientFactorySpi.java, v 0.1 2022年05月24日 11:35 AM wanhaofan
 */
public interface RdfFileOssClientFactorySpi extends OssClientFactory, RdfInit<OssConfig> {



}