package com.alipay.rdf.file.loader;

import com.alipay.rdf.file.exception.RdfErrorEnum;
import com.alipay.rdf.file.exception.RdfFileException;
import com.alipay.rdf.file.init.RdfInit;
import com.alipay.rdf.file.interfaces.FileStorage;
import com.alipay.rdf.file.model.FileDefaultConfig;
import com.alipay.rdf.file.model.StorageConfig;
import com.alipay.rdf.file.resource.RdfInputStream;
import com.alipay.rdf.file.spi.RdfFileResourceSpi;
import com.alipay.rdf.file.util.RdfFileLogUtil;
import com.alipay.rdf.file.util.RdfFileUtil;

import java.util.Collections;
import java.util.Map;
import java.util.Queue;
import java.util.WeakHashMap;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 * 
 * @author hongwei.quhw
 * @version $Id: ResourceLoader.java, v 0.1 2017年3月31日 下午9:39:12 hongwei.quhw Exp $
 */
public class ResourceLoader {
    private static final String DEFAULT_TYPE = "classpath";

    private static final String SPLIT = ":";
    // 扩展配置项key，同种协议可以指定不同配置
    private static final String RESOURCE_KEY = "resourceKey";

    private static final Map<String, RdfFileResourceSpi> RESOURCE_CACHE = Collections.synchronizedMap(new WeakHashMap<String, RdfFileResourceSpi>());

    private static final Object LOCK = new Object();

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static RdfInputStream getInputStream(String path) {
        path = RdfFileUtil.assertTrimNotBlank(path, "rdf-file#ResourceLoader.getInputStream path为空",
            RdfErrorEnum.ILLEGAL_ARGUMENT);

        int idx = path.indexOf(SPLIT);

        String resourceType = null;
        String resourceValue = RdfFileUtil.parsePathParams(path).get(RESOURCE_KEY);

        if (idx < 0) {
            resourceType = DEFAULT_TYPE;
        } else {
            resourceType = path.substring(0, idx);
            path = path.substring(idx + 1);
        }

        idx = path.indexOf(RdfFileUtil.QUESTION);
        if (idx > -1) {
            path = path.substring(idx + 1);
        }

        String cacheKey = resourceType + (resourceValue == null ? RdfFileUtil.EMPTY : resourceValue);

        RdfFileResourceSpi rdfResource = RESOURCE_CACHE.get(cacheKey);

        if (null == rdfResource) {
            synchronized (LOCK) {
                rdfResource = RESOURCE_CACHE.get(cacheKey);
                if (null == rdfResource) {
                    rdfResource = ExtensionLoader.getExtensionLoader(RdfFileResourceSpi.class).getNewExtension(resourceType);

                    if (null == rdfResource) {
                        throw new RdfFileException("rdf-file#ResourceLoader.getInputStream(path=" + path
                                + ")  resourceType=" + resourceType + "没有对应的实现!",
                                RdfErrorEnum.NOT_EXSIT);
                    }

                    rdfResource.resourceType(resourceType);

                    if (rdfResource instanceof RdfInit) {
                        Object configValue = null;
                        if (RdfFileUtil.isNotBlank(resourceValue)) {
                            // 指定了特殊配置
                            configValue = FileDefaultConfig.DEFAULT_FILE_PARAMS.get(resourceValue);
                        }
                        if (null == configValue) {
                            // 使用协议通用配置
                            configValue = FileDefaultConfig.DEFAULT_FILE_PARAMS.get(resourceType);
                        }
                        ((RdfInit) rdfResource).init(configValue);
                    }

                    RESOURCE_CACHE.put(cacheKey, rdfResource);
                }
            }
        }

        return rdfResource.getInputStream(path);
    }

    public static String buildResource(String resourcePath, String defaultResourceType) {
        resourcePath = RdfFileUtil.assertTrimNotBlank(resourcePath,
            "rdf-file#ResourceLoader.buildResource resourcePath为空", RdfErrorEnum.ILLEGAL_ARGUMENT);
        defaultResourceType = RdfFileUtil.assertTrimNotBlank(defaultResourceType,
            "rdf-file#ResourceLoader.buildResource defaultResourceType为空",
            RdfErrorEnum.ILLEGAL_ARGUMENT);

        int idx = resourcePath.indexOf(SPLIT);

        if (idx == 0) {
            throw new RdfFileException(
                "rdf-file#ResourceLoader.buildResource resourcePath=" + resourcePath + "格式不对",
                RdfErrorEnum.ILLEGAL_ARGUMENT);
        } else if (idx < 0) {
            resourcePath = defaultResourceType + resourcePath;
        }

        if (RdfFileLogUtil.common.isDebug()) {
            RdfFileLogUtil.common
                .debug("rdf-file#ResourceLoader.buildResource(resourcePath=" + resourcePath
                       + ", defaultResourceType=" + defaultResourceType
                       + ") buildResource后resourcePath=" + resourcePath);
        }

        return resourcePath;
    }
}
