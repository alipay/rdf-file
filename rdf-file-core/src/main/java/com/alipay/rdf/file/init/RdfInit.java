package com.alipay.rdf.file.init;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 * 
 * Rdf 回调初始化接口
 * 
 * @author hongwei.quhw
 * @version $Id: RdfInit.java, v 0.1 2017年4月13日 下午4:35:01 hongwei.quhw Exp $
 */
public interface RdfInit<T> {

    void init(T t);
}
