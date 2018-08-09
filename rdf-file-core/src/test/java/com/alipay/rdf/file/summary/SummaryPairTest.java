package com.alipay.rdf.file.summary;

import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Test;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 *
 * @author hongwei.quhw
 * @version $Id: BigDecimalSummaryPairTest.java, v 0.1 2018年8月9日 下午9:04:17 hongwei.quhw Exp $
 */
public class SummaryPairTest {

    @Test
    public void testBigDecimal() {
        BigDecimalSummaryPair bdSummary = new BigDecimalSummaryPair();

        Assert.assertTrue(bdSummary.isSummaryEquals());

        bdSummary.setHeadValue(new BigDecimal("0.00"));
        Assert.assertTrue(bdSummary.isSummaryEquals());

        bdSummary.setHeadValue(new BigDecimal("0"));
        Assert.assertTrue(bdSummary.isSummaryEquals());

        bdSummary.addColValue(new BigDecimal("0.0"));
        Assert.assertTrue(bdSummary.isSummaryEquals());
    }

    @Test
    public void testInteger() {
        IntegerSummaryPair isummary = new IntegerSummaryPair();

        Assert.assertTrue(isummary.isSummaryEquals());

        isummary.setHeadValue(new Integer("00"));

        Assert.assertTrue(isummary.isSummaryEquals());

        isummary.setHeadValue(new Integer("000"));

        Assert.assertTrue(isummary.isSummaryEquals());

        isummary.setHeadValue(new Integer("0"));

        Assert.assertTrue(isummary.isSummaryEquals());

        isummary.addColValue(0);
        Assert.assertTrue(isummary.isSummaryEquals());

        isummary.addColValue(new Integer(0));
        Assert.assertTrue(isummary.isSummaryEquals());

        isummary.addColValue(new Integer("00000000"));
        Assert.assertTrue(isummary.isSummaryEquals());

        isummary.addColValue(new Integer("100"));
        Assert.assertFalse(isummary.isSummaryEquals());
    }

    @Test
    public void testFloat() {
        FloatSummaryPair summary = new FloatSummaryPair();
        Assert.assertTrue(summary.isSummaryEquals());

        summary.addColValue(new Float(11));
        Assert.assertFalse(summary.isSummaryEquals());

        summary = new FloatSummaryPair();
        Assert.assertTrue(summary.isSummaryEquals());

        summary.setHeadValue(new Float(0.00));
        Assert.assertTrue(summary.isSummaryEquals());

        summary.setHeadValue(new Float("0000"));
        Assert.assertTrue(summary.isSummaryEquals());

        summary.setHeadValue(new Float("00.0000"));
        Assert.assertTrue(summary.isSummaryEquals());

    }

    @Test
    public void testLong() {
        LongSummaryPair summary = new LongSummaryPair();
        Assert.assertTrue(summary.isSummaryEquals());

        summary.addColValue(new Long(11));
        Assert.assertFalse(summary.isSummaryEquals());

        summary = new LongSummaryPair();
        Assert.assertTrue(summary.isSummaryEquals());

        summary.setHeadValue(new Long(000));
        Assert.assertTrue(summary.isSummaryEquals());

        summary.setHeadValue(new Long("0000"));
        Assert.assertTrue(summary.isSummaryEquals());

        summary.setHeadValue(new Long("0"));
        Assert.assertTrue(summary.isSummaryEquals());

        summary.addColValue(new Long(0));
        Assert.assertTrue(summary.isSummaryEquals());
    }

    @Test
    public void testDouble() {
        DoubleSummaryPair summary = new DoubleSummaryPair();
        Assert.assertTrue(summary.isSummaryEquals());

        summary.addColValue(new Double(11));
        Assert.assertFalse(summary.isSummaryEquals());

        summary = new DoubleSummaryPair();
        Assert.assertTrue(summary.isSummaryEquals());

        summary.setHeadValue(new Double(000));
        Assert.assertTrue(summary.isSummaryEquals());

        summary.setHeadValue(new Double("00.00"));
        Assert.assertTrue(summary.isSummaryEquals());

        summary.setHeadValue(new Double("0"));
        Assert.assertTrue(summary.isSummaryEquals());

        summary.addColValue(new Double(0));
        Assert.assertTrue(summary.isSummaryEquals());
    }

}
