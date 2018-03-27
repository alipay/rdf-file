package com.alipay.rdf.file.loader;

import com.alipay.rdf.file.exception.RdfErrorEnum;
import com.alipay.rdf.file.exception.RdfFileException;
import com.alipay.rdf.file.init.RdfInit;
import com.alipay.rdf.file.model.FileDefaultConfig;
import com.alipay.rdf.file.resource.RdfInputStream;
import com.alipay.rdf.file.spi.RdfFileResourceSpi;
import com.alipay.rdf.file.util.RdfFileLogUtil;
import com.alipay.rdf.file.util.RdfFileUtil;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 * 
 * @author hongwei.quhw
 * @version $Id: ResourceLoader.java, v 0.1 2017年3月31日 下午9:39:12 hongwei.quhw Exp $
 */
public class ResourceLoader {
    private static final String DEFAULT_TYPE = "classpath";

    private static final String SPLIT        = ":";

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static RdfInputStream getInputStream(String path) {
        path = RdfFileUtil.assertTrimNotBlank(path, "rdf-file#ResourceLoader.getInputStream path为空",
            RdfErrorEnum.ILLEGAL_ARGUMENT);

        int idx = path.indexOf(SPLIT);

        String resourceType = null;

        if (idx < 0) {
            resourceType = DEFAULT_TYPE;
        } else {
            resourceType = path.substring(0, idx);
            path = path.substring(idx + 1);
        }

        RdfFileResourceSpi rdfResource = ExtensionLoader
            .getExtensionLoader(RdfFileResourceSpi.class).getExtension(resourceType);

        if (null == rdfResource) {
            throw new RdfFileException("rdf-file#ResourceLoader.getInputStream(path=" + path
                                       + ")  resourceType=" + resourceType + "没有对应的实现!",
                RdfErrorEnum.NOT_EXSIT);
        }

        rdfResource.resourceType(resourceType);

        if (rdfResource instanceof RdfInit) {
            ((RdfInit) rdfResource).init(FileDefaultConfig.DEFAULT_FILE_PARAMS.get(resourceType));
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
