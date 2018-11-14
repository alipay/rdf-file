package com.alipay.rdf.file.model;

import com.alipay.rdf.file.spi.RdfFileRowConditionSpi;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 *
 * @author hongwei.quhw
 * @version $Id: SummaryPair.java, v 0.1 2018年3月12日 下午4:22:44 hongwei.quhw Exp $
 */
public interface SummaryPair<T> {

    /**
     * 汇总字段是否一致
     * 
     * @return
     */
    boolean isSummaryEquals();

    /**
     * 汇总字段信息
     * 
     * @return
     */
    String summaryMsg();

    /**
     * 获取head汇总字段
     */
    String getHeadKey();

    /**
     * 获取body的汇总字段
     */
    String getColumnKey();

    String getTailKey();

    T getTailValue();

    /**
     * 汇总后的值
     */
    T getSummaryValue();

    /**
     * 头部汇总值
     */
    T getHeadValue();

    /**
     * 行条件判定
     */
    RdfFileRowConditionSpi getRowCondition();
}
