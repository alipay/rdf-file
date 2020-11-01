/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2020 All Rights Reserved.
 */
package com.alipay.rdf.file.resource;

import com.alipay.rdf.file.model.StorageConfig;
import com.alipay.rdf.file.spi.RdfFileResourceSpi;

import java.io.IOException;
import java.io.InputStream;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 *
 * @author quhongwei
 * @version : TestResource.java, v 0.1 2020年11月01日 09:48 quhongwei Exp $
 */
public class TestResource extends AbstractRdfResources {
    @Override
    public RdfInputStream getInputStream(String path) {
        return new TestInputStream(storageConfig, resourceType, path);
    }

    public static class TestInputStream extends RdfInputStream {
        private final StorageConfig config;
        private final String resourceType;
        private final String path;

        public TestInputStream(StorageConfig config, String resourceType, String path) {
            super(new InputStream() {
                @Override
                public int read() throws IOException {
                    return 0;
                }
            });
            this.config = config;
            this.resourceType = resourceType;
            this.path = path;
        }

        public StorageConfig getConfig() {
            return config;
        }

        public String getResourceType() {
            return resourceType;
        }

        public String getPath() {
            return path;
        }
    }
}