package com.alipay.rdf.file.summary;

import java.util.Map;
import java.math.BigDecimal;
import java.util.HashMap;

import org.junit.Test;

import com.alipay.rdf.file.loader.SummaryLoader;
import com.alipay.rdf.file.loader.TemplateLoader;
import com.alipay.rdf.file.meta.FileMeta;
import com.alipay.rdf.file.model.FileConfig;
import com.alipay.rdf.file.model.StorageConfig;
import com.alipay.rdf.file.model.Summary;
import com.alipay.rdf.file.model.SummaryPair;
import com.alipay.rdf.file.processor.ProcessCotnext;
import com.alipay.rdf.file.processor.ProcessorTypeEnum;
import com.alipay.rdf.file.util.RdfFileConstants;

import junit.framework.Assert;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 *
 * 汇总统计测试
 *
 * @author hongwei.quhw
 * @version $Id: SummaryProcessorTest.java, v 0.1 2018年11月15日 上午11:37:10 hongwei.quhw Exp $
 */
public class SummaryProcessorTest {

    @SuppressWarnings("rawtypes")
    @Test
    public void test1() {
        FileConfig fileConfig = new FileConfig("/summary/template1.json", new StorageConfig("nas"));
        FileMeta fileMeta = TemplateLoader.load(fileConfig);
        Summary summary = SummaryLoader.getNewSummary(fileMeta);

        ProcessCotnext pc = new ProcessCotnext(fileConfig, ProcessorTypeEnum.AFTER_READ_HEAD);
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("totalCount", new BigDecimal("10"));
        data.put("totalAmount", new BigDecimal("99.99"));
        data.put("successAmount", new BigDecimal("66.66"));
        data.put("successCount", new Long(10));
        data.put("failcount", new Long(33));
        pc.putBizData(RdfFileConstants.DATA, data);
        pc.putBizData(RdfFileConstants.SUMMARY, summary);

        SummaryProcessor processor = new SummaryProcessor();
        processor.process(pc);

        Assert.assertEquals(summary.getSummaryPairs().get(0).getHeadKey(), "totalAmount");
        Assert.assertEquals(summary.getSummaryPairs().get(0).getColumnKey(), "amount");
        Assert.assertEquals(summary.getSummaryPairs().get(0).getHeadValue(),
            new BigDecimal("99.99"));
        Assert.assertNull(summary.getSummaryPairs().get(0).getTailKey());

        Assert.assertEquals(summary.getSummaryPairs().get(1).getHeadKey(), "successAmount");
        Assert.assertEquals(summary.getSummaryPairs().get(1).getColumnKey(), "amount");
        Assert.assertEquals(summary.getSummaryPairs().get(1).getHeadValue(),
            new BigDecimal("66.66"));
        Assert.assertNull(summary.getSummaryPairs().get(1).getTailKey());

        Assert.assertEquals("successCount", summary.getStatisticPairs().get(0).getHeadKey());
        Assert.assertEquals(new Long(10), summary.getStatisticPairs().get(0).getHeadValue());
        Assert.assertNull(summary.getStatisticPairs().get(0).getTailKey());

        Assert.assertNull(summary.getStatisticPairs().get(1).getHeadKey());
        Assert.assertEquals("failcount", summary.getStatisticPairs().get(1).getTailKey());
        Assert.assertNull(summary.getStatisticPairs().get(1).getTailValue());

        pc = new ProcessCotnext(fileConfig, ProcessorTypeEnum.AFTER_READ_TAIL);
        pc.putBizData(RdfFileConstants.DATA, data);
        pc.putBizData(RdfFileConstants.SUMMARY, summary);
        processor.process(pc);

        Assert.assertEquals(summary.getSummaryPairs().get(0).getHeadKey(), "totalAmount");
        Assert.assertEquals(summary.getSummaryPairs().get(0).getColumnKey(), "amount");
        Assert.assertEquals(summary.getSummaryPairs().get(0).getHeadValue(),
            new BigDecimal("99.99"));
        Assert.assertNull(summary.getSummaryPairs().get(0).getTailKey());

        Assert.assertEquals(summary.getSummaryPairs().get(1).getHeadKey(), "successAmount");
        Assert.assertEquals(summary.getSummaryPairs().get(1).getColumnKey(), "amount");
        Assert.assertEquals(summary.getSummaryPairs().get(1).getHeadValue(),
            new BigDecimal("66.66"));
        Assert.assertNull(summary.getSummaryPairs().get(1).getTailKey());

        Assert.assertEquals("successCount", summary.getStatisticPairs().get(0).getHeadKey());
        Assert.assertEquals(new Long(10), summary.getStatisticPairs().get(0).getHeadValue());
        Assert.assertNull(summary.getStatisticPairs().get(0).getTailKey());

        Assert.assertNull(summary.getStatisticPairs().get(1).getHeadKey());
        Assert.assertEquals("failcount", summary.getStatisticPairs().get(1).getTailKey());
        Assert.assertEquals(new Long(33), summary.getStatisticPairs().get(1).getTailValue());

        data = new HashMap<String, Object>();
        data.put("amount", new BigDecimal("33.33"));
        data.put("bol", true);
        data.put("seq", "eabcd");
        data.put("age", 15);
        pc = new ProcessCotnext(fileConfig, ProcessorTypeEnum.AFTER_READ_ROW);
        pc.putBizData(RdfFileConstants.DATA, data);
        pc.putBizData(RdfFileConstants.SUMMARY, summary);
        processor.process(pc);

        Assert.assertEquals(new BigDecimal("33.33"),
            summary.getSummaryPairs().get(0).getSummaryValue());
        Assert.assertNull(summary.getSummaryPairs().get(1).getSummaryValue());

        Assert.assertEquals(new Long(1), summary.getStatisticPairs().get(0).getStaticsticValue());
        Assert.assertEquals(new Integer(1),
            summary.getStatisticPairs().get(1).getStaticsticValue());

        data = new HashMap<String, Object>();
        data.put("amount", new BigDecimal("33.33"));
        data.put("bol", true);
        data.put("seq", "abcdd");
        data.put("age", 15);
        pc = new ProcessCotnext(fileConfig, ProcessorTypeEnum.AFTER_READ_ROW);
        pc.putBizData(RdfFileConstants.DATA, data);
        pc.putBizData(RdfFileConstants.SUMMARY, summary);
        processor.process(pc);

        Assert.assertEquals(new BigDecimal("66.66"),
            summary.getSummaryPairs().get(0).getSummaryValue());
        Assert.assertEquals(new BigDecimal("33.33"),
            summary.getSummaryPairs().get(1).getSummaryValue());
        Assert.assertEquals(new Long(1), summary.getStatisticPairs().get(0).getStaticsticValue());
        Assert.assertEquals(new Integer(2),
            summary.getStatisticPairs().get(1).getStaticsticValue());

        data = new HashMap<String, Object>();
        data.put("amount", new BigDecimal("33.33"));
        data.put("bol", true);
        data.put("seq", "abcdd");
        data.put("age", 15);
        pc = new ProcessCotnext(fileConfig, ProcessorTypeEnum.AFTER_READ_ROW);
        pc.putBizData(RdfFileConstants.DATA, data);
        pc.putBizData(RdfFileConstants.SUMMARY, summary);
        processor.process(pc);

        Assert.assertEquals(new BigDecimal("99.99"),
            summary.getSummaryPairs().get(0).getSummaryValue());
        Assert.assertEquals(new BigDecimal("66.66"),
            summary.getSummaryPairs().get(1).getSummaryValue());
        Assert.assertEquals(new Long(1), summary.getStatisticPairs().get(0).getStaticsticValue());
        Assert.assertEquals(new Integer(3),
            summary.getStatisticPairs().get(1).getStaticsticValue());

        for (SummaryPair pair : summary.getSummaryPairs()) {
            Assert.assertTrue(pair.isSummaryEquals());
        }

        for (StatisticPair pair : summary.getStatisticPairs()) {
            Assert.assertFalse(pair.isStatisticEquals());
            System.out.println(pair.staticsticMsg());
        }
    }

}
