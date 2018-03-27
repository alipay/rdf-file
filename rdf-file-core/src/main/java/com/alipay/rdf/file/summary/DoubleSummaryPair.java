package com.alipay.rdf.file.summary;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 *
 * @author hongwei.quhw
 * @version $Id: DoubleSummaryPair.java, v 0.1 2018年3月12日 下午4:27:38 hongwei.quhw Exp $
 */
public class DoubleSummaryPair extends AbstractSummaryPair<Double> {

    @Override
    protected void doAddColValue(Double columnValue) {
        summaryValue = summaryValue + columnValue;
    }

}
