/*
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.rdf.file.storage;

import com.alipay.rdf.file.spi.RdfFileOssClientFactorySpi;
import com.alipay.rdf.file.util.RdfFileLogUtil;
import com.aliyun.oss.OSSClient;

/**
 * 获取ossclient的默认实现
 *
 * @author wanhaofan
 * @version DefaultOssClientFactory.java, v 0.1 2022年05月24日 11:49 AM wanhaofan
 */
public class DefaultOssClientFactory implements RdfFileOssClientFactorySpi {

    @Override
    public OSSClient create(OssConfig ossConfig) {
        if (RdfFileLogUtil.common.isInfo()) {
            RdfFileLogUtil.common.info("rdf-file#DefaultOssClientFactory.create(endpoint=" + ossConfig.getEndpoint() +
                    ",bucket=" + ossConfig.getBucketName() +
                    ",ak=" + ossConfig.getAccessKeyId() + ")");
        }
        return new OSSClient(ossConfig.getEndpoint(), ossConfig.getAccessKeyId(),
                ossConfig.getAccessKeySecret(), ossConfig.getClientConfiguration());
    }
}