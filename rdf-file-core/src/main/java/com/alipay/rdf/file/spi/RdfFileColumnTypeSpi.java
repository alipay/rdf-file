package com.alipay.rdf.file.spi;

import com.alipay.rdf.file.type.ColumnTypeCodec;
import com.alipay.rdf.file.type.AddOperation;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 * 
 * 类型可扩展标记接口
 * 
 * @author hongwei.quhw
 * @version $Id: FileColumnTypeCodec.java, v 0.1 2016-12-21 下午2:24:55 hongwei.quhw Exp $
 */
public interface RdfFileColumnTypeSpi<T> extends ColumnTypeCodec<T>, AddOperation<T> {
}
