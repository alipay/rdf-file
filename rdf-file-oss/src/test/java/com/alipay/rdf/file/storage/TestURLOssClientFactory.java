/*
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.rdf.file.storage;

import com.alipay.rdf.file.spi.RdfFileOssClientFactorySpi;
import com.aliyun.oss.OSSClient;

/**
 * @author wanhaofan
 * @version TestURLOssClientFactory.java, v 0.1 2022年05月24日 5:18 PM wanhaofan
 */
public class TestURLOssClientFactory implements RdfFileOssClientFactorySpi {

    private OssConfig ossConfig;

    @Override
    public void init(OssConfig ossConfig) {
        this.ossConfig = ossConfig;
    }

    @Override
    public OSSClient create() {
        String connectionURL = ossConfig.getConnectionURL();

        String[] infoArray = connectionURL.split("@");
        String endpoint = infoArray[0];
        String ak = infoArray[1];
        String secret = infoArray[2];

        return new OSSClient(endpoint, ak, secret, ossConfig.getClientConfiguration());
    }
}