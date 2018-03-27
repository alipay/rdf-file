package com.alipay.rdf.file.spi;

import com.alipay.rdf.file.meta.FileColumnMeta;
import com.alipay.rdf.file.model.FileConfig;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 * 
 * 字段格式化spi接口
 * 
 * @author hongwei.quhw
 * @version $Id: RdfFormatSpi.java, v 0.1 2017年8月8日 上午11:02:01 hongwei.quhw Exp $
 */
public interface RdfFileFormatSpi {
    /**
     格式化一个字段数据到文件
     */
    String serialize(String field, FileColumnMeta columnMeta, FileConfig fileConfig);

    /**
     * 从文件反格式化一个字段数据 
     */
    String deserialize(String field, FileColumnMeta columnMeta, FileConfig fileConfig);
}
