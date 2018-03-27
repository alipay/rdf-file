package com.alipay.rdf.file.type;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 * 
 * 同类型字段add操作
 * 
 * @author hongwei.quhw
 * @version $Id: TypeOperationSpi.java, v 0.1 2017年4月21日 下午3:18:34 hongwei.quhw Exp $
 */
public interface AddOperation<T> {

    /**
     * 累加
     * 
     * @param columnMeta
     * @param a
     * @param b
     * @return
     */
    T add(T a, T b);
}
