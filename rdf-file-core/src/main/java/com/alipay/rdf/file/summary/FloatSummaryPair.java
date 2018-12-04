package com.alipay.rdf.file.summary;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 *
 * @author hongwei.quhw
 * @version $Id: FloatSummaryPair.java, v 0.1 2018年3月12日 下午4:27:45 hongwei.quhw Exp $
 */
public class FloatSummaryPair extends AbstractSummaryPair<Float> {

    @Override
    protected void doAddColValue(Float columnValue) {
        summaryValue = summaryValue + columnValue;
    }

    @Override
    public Float initDefaultColumnValue() {
        return new Float("0");
    }
}
