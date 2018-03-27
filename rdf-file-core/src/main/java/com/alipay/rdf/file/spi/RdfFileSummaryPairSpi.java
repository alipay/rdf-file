package com.alipay.rdf.file.spi;

import com.alipay.rdf.file.model.SummaryPair;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 * 
 * 汇总字段扩展spi
 * 
 * @author hongwei.quhw
 * @version $Id: SummaryPairSpi.java, v 0.1 2017年8月8日 上午11:03:35 hongwei.quhw Exp $
 */
public interface RdfFileSummaryPairSpi<T> extends SummaryPair<T> {

    /**
     * 增加列值
     */
    void addColValue(T columnValue);

    /**
     * 设置头部字段
     */
    void setHeadValue(T headValue);

    void setTailValue(T tailValue);

    void setHeadKey(String headKey);

    void setTailKey(String tailKey);

    void setColumnKey(String columnKey);
}
