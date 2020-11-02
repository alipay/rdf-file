package com.alipay.rdf.file.spi;

import com.alipay.rdf.file.init.RdfInit;
import com.alipay.rdf.file.model.StorageConfig;
import com.alipay.rdf.file.resource.RdfInputStream;

/** 
 * Copyright (C) 2013-2018 Ant Financial Services Group
 * 
 * rdf-file 资源接口spi
 * 
 * @author hongwei.quhw
 * @version $Id: RdfResourceSpi.java, v 0.1 2017年8月8日 上午11:03:13 hongwei.quhw Exp $
 */
public interface RdfFileResourceSpi extends RdfInit<StorageConfig> {
    /**
     * 资源加载类型
     * 
     * @param resourceType
     */
    void resourceType(String resourceType);

    /***
     * 根据路径获取输入流
     * 
     * @param path
     * @return
     */
    RdfInputStream getInputStream(String path);

}
