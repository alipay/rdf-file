package com.alipay.rdf.file.summary;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 *
 * @author hongwei.quhw
 * @version $Id: IntegerSummaryPair.java, v 0.1 2018年3月12日 下午4:27:52 hongwei.quhw Exp $
 */
public class IntegerSummaryPair extends AbstractSummaryPair<Integer> {

    @Override
    protected void doAddColValue(Integer columnValue) {
        summaryValue = summaryValue + columnValue;
    }

    @Override
    public Integer initDefaultColumnValue() {
        return new Integer("0");
    }
}
