package com.alipay.rdf.file.summary;

import com.alipay.rdf.file.loader.SummaryLoader;
import com.alipay.rdf.file.loader.TemplateLoader;
import com.alipay.rdf.file.meta.FileMeta;
import com.alipay.rdf.file.model.FileConfig;
import com.alipay.rdf.file.model.StorageConfig;
import com.alipay.rdf.file.model.Summary;
import com.alipay.rdf.file.processor.ProcessCotnext;
import com.alipay.rdf.file.processor.ProcessorTypeEnum;
import com.alipay.rdf.file.util.RdfFileConstants;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 *
 * 汇总统计测试
 * 
 * 多模板测试
 *
 * @author hongwei.quhw
 * @version $Id: SummaryProcessorTest.java, v 0.1 2018年11月15日 上午11:37:10 hongwei.quhw Exp $
 */
public class SummaryProcessorTest2 {

    @Test
    public void test1() {
        FileConfig fileConfig = new FileConfig("/summary/template2.json", new StorageConfig("nas"));
        FileMeta fileMeta = TemplateLoader.load(fileConfig);
        Summary summary = SummaryLoader.getNewSummary(fileMeta);

        ProcessCotnext pc = new ProcessCotnext(fileConfig, ProcessorTypeEnum.AFTER_WRITE_ROW);
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("bol", true);
        data.put("count", 12);
        //data.put("amount", new BigDecimal("10")); // 模拟多模板利用不存在值进行决策
        pc.putBizData(RdfFileConstants.DATA, data);
        pc.putBizData(RdfFileConstants.SUMMARY, summary);

        SummaryProcessor processor = new SummaryProcessor();
        processor.process(pc);

        Assert.assertNull(summary.getSummaryPairs().get(0).getSummaryValue());
        Assert.assertEquals(new Integer("12"), summary.getSummaryPairs().get(1).getSummaryValue());
        Assert.assertNull(summary.getStatisticPairs().get(0).getStaticsticValue());

        data = new HashMap<String, Object>();
        data.put("bol", false);
        data.put("count", 22);
        data.put("amount", new BigDecimal("100")); // 模拟多模板利用不存在值进行决策
        pc.putBizData(RdfFileConstants.DATA, data);
        pc.putBizData(RdfFileConstants.SUMMARY, summary);
        processor.process(pc);

        Assert.assertEquals(new BigDecimal("100"),
            summary.getSummaryPairs().get(0).getSummaryValue());
        Assert.assertEquals(new Integer("12"), summary.getSummaryPairs().get(1).getSummaryValue());
        Assert.assertEquals(new Long(1), summary.getStatisticPairs().get(0).getStaticsticValue());

        data = new HashMap<String, Object>();
        data.put("bol", true);
        data.put("count", 22);
        data.put("amount", new BigDecimal("1001")); // 模拟多模板利用不存在值进行决策
        pc.putBizData(RdfFileConstants.DATA, data);
        pc.putBizData(RdfFileConstants.SUMMARY, summary);
        processor.process(pc);

        Assert.assertEquals(new BigDecimal("100"),
            summary.getSummaryPairs().get(0).getSummaryValue());
        Assert.assertEquals(new Integer("34"), summary.getSummaryPairs().get(1).getSummaryValue());
        Assert.assertEquals(new Long(2), summary.getStatisticPairs().get(0).getStaticsticValue());

    }

}
