package com.alipay.rdf.file.spi;

import com.alipay.rdf.file.init.RdfInit;
import com.alipay.rdf.file.meta.FileBodyMeta;
import com.alipay.rdf.file.model.FileConfig;
import com.alipay.rdf.file.util.BeanMapWrapper;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 *
 * body数据条件决策spi
 *
 * @author hongwei.quhw
 * @version $Id: RdfFileConditionSpi.java, v 0.1 2018年10月11日 下午8:38:11 hongwei.quhw Exp $
 */
public interface RdfFileRowConditionSpi extends RdfInit<FileBodyMeta> {

    /**
     * 写入文件是条件决策
     * 
     * @param config
     * @param column
     * @return
     */
    boolean serialize(FileConfig config, BeanMapWrapper row);

    /**
     * 读取文件时条件决策
     * 
     * @param fileConfig
     * @param row
     * @return
     */
    boolean deserialize(FileConfig fileConfig, String[] row);

}
