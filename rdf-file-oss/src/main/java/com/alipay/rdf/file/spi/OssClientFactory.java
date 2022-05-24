/*
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.rdf.file.spi;

import com.aliyun.oss.OSSClient;

/**
 * @author wanhaofan
 * @version OssClientFactory.java, v 0.1 2022年05月24日 3:48 PM wanhaofan
 */
public interface OssClientFactory {

    /**
     * 使用方可以贡献自定义实现OSSClient，用于扩展一些特殊的能力，比如容灾等
     * @return
     */
    OSSClient create();

}