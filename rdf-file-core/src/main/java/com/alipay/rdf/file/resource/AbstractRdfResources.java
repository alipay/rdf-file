package com.alipay.rdf.file.resource;

import com.alipay.rdf.file.model.StorageConfig;
import com.alipay.rdf.file.spi.RdfFileResourceSpi;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 * 
 * 资源加载抽象类
 * 
 * @author hongwei.quhw
 * @version $Id: AbstractRdfResouces.java, v 0.1 2017年8月22日 下午2:18:26 hongwei.quhw Exp $
 */
public abstract class AbstractRdfResources implements RdfFileResourceSpi {
    /**资源类型*/
    protected String        resourceType;

    protected StorageConfig storageConfig;

    @Override
    public void init(StorageConfig t) {
        this.storageConfig = t;
    }

    @Override
    public void resourceType(String resourceType) {
        this.resourceType = resourceType;
    }
}
