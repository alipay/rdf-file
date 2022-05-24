/*
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.rdf.file.loader;

import com.alipay.rdf.file.init.RdfInit;
import com.alipay.rdf.file.spi.OssClientFactory;
import com.alipay.rdf.file.spi.RdfFileOssClientFactorySpi;
import com.alipay.rdf.file.storage.OssConfig;
import com.alipay.rdf.file.util.RdfFileUtil;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * @author wanhaofan
 * @version OssClientFactoryLoader.java, v 0.1 2022年05月24日 3:46 PM wanhaofan
 */
public class OssClientFactoryLoader {

    private static final Map<OssConfig, OssClientFactory> STORAGE_CACHE = Collections
            .synchronizedMap(new WeakHashMap<OssConfig, OssClientFactory>());

    private static final Object                           LOCK          = new Object();

    @SuppressWarnings("unchecked")
    public static OssClientFactory getOssClientFactory(OssConfig ossConfig) {
        OssClientFactory factory = STORAGE_CACHE.get(ossConfig);

        if (null == factory) {
            synchronized (LOCK) {
                factory = STORAGE_CACHE.get(ossConfig);

                if (null == factory) {
                    factory = ExtensionLoader.getExtensionLoader(RdfFileOssClientFactorySpi.class)
                            .getNewExtension(ossConfig.getOssClientFactoryType());

                    RdfFileUtil.assertNotNull(
                            factory,
                            "rdf-file#OssClientFactoryLoader.getOssClientFactory(factoryType="
                                    + ossConfig.getOssClientFactoryType() + ")没有对应的实现");

                    //初始化
                    ((RdfInit<OssConfig>) factory).init(ossConfig);

                    STORAGE_CACHE.put(ossConfig, factory);
                }
            }
        }

        return factory;
    }

}