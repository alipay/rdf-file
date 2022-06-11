/*
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.rdf.file.ossclientfactory;

import com.alipay.rdf.file.storage.OssConfig;
import com.aliyun.oss.ClientConfiguration;

/**
 * @author wanhaofan
 * @version TestOssConfig.java, v 0.1 2022年05月25日 10:00 AM wanhaofan
 */
public class TestOssConfig extends OssConfig {

    private String connectionURL;


    public TestOssConfig(String bucketName, String connectionURL, String ossClientFactoryType){
        super(bucketName, ossClientFactoryType);
        this.connectionURL = connectionURL;
    }

    public TestOssConfig(String bucketName, String connectionURL, String ossClientFactoryType, ClientConfiguration clientConfiguration){
        super(bucketName, ossClientFactoryType, clientConfiguration);
        this.connectionURL = connectionURL;
    }

    public String getConnectionURL() {
        return connectionURL;
    }

    public void setConnectionURL(String connectionURL) {
        this.connectionURL = connectionURL;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        TestOssConfig that = (TestOssConfig) o;

        return connectionURL != null ? connectionURL.equals(that.connectionURL) : that.connectionURL == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (connectionURL != null ? connectionURL.hashCode() : 0);
        return result;
    }
}