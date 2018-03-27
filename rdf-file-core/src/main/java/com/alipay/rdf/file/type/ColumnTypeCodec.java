package com.alipay.rdf.file.type;

import com.alipay.rdf.file.meta.FileColumnMeta;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 * 
 * 字段编码解码接口
 * 
 * @author hongwei.quhw
 * @version $Id: ColumnTypeCodec.java, v 0.1 2017年8月19日 下午3:33:40 hongwei.quhw Exp $
 */
public interface ColumnTypeCodec<T> {

    /**
     * 序列化一个字段数据到文件
     */
    String serialize(T field, FileColumnMeta columnMeta);

    /**
     * 从文件反序列化一个字段数据 
     */
    T deserialize(String field, FileColumnMeta columnMeta);
}
