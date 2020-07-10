package com.alipay.rdf.file.format;

import com.alipay.rdf.file.meta.FileColumnMeta;
import com.alipay.rdf.file.model.FileConfig;
import com.alipay.rdf.file.spi.RdfFileFormatSpi;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 * 
 * 字段不做格式化处理
 * 
 * @author hongwei.quhw
 * @version $Id: RawFormat.java, v 0.1 2017年8月1日 下午2:52:28 hongwei.quhw Exp $
 */
public class RawFormat implements RdfFileFormatSpi {

    @Override
    public String serialize(String field, FileColumnMeta columnMeta, FileConfig fileConfig) {
        return field;
    }

    @Override
    public String deserialize(String field, FileColumnMeta columnMeta, FileConfig fileConfig) {
        return field;
    }

}
