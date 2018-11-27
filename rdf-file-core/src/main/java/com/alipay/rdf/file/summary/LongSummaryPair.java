package com.alipay.rdf.file.summary;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 *
 * @author hongwei.quhw
 * @version $Id: LongSummaryPair.java, v 0.1 2018年3月12日 下午4:27:58 hongwei.quhw Exp $
 */
public class LongSummaryPair extends AbstractSummaryPair<Long> {

    @Override
    protected void doAddColValue(Long columnValue) {
        summaryValue = summaryValue + columnValue;
    }

    @Override
    public Long initDefaultColumnValue() {
        return new Long("0");
    }
}
