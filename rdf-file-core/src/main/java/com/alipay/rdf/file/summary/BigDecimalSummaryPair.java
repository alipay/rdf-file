package com.alipay.rdf.file.summary;

import java.math.BigDecimal;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 *
 * @author hongwei.quhw
 * @version $Id: BigDecimalSummaryPair.java, v 0.1 2018年3月12日 下午4:27:30 hongwei.quhw Exp $
 */
public class BigDecimalSummaryPair extends AbstractSummaryPair<BigDecimal> {

    @Override
    protected void doAddColValue(BigDecimal columnValue) {
        summaryValue = summaryValue.add(columnValue);
    }

    @Override
    public BigDecimal initDefaultColumnValue() {
        return new BigDecimal("0");
    }
}
