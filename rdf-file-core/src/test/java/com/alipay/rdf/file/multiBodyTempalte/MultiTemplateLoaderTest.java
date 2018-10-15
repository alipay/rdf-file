package com.alipay.rdf.file.multiBodyTempalte;

import org.junit.Assert;
import org.junit.Test;

import com.alipay.rdf.file.exception.RdfErrorEnum;
import com.alipay.rdf.file.exception.RdfFileException;
import com.alipay.rdf.file.loader.TemplateLoader;
import com.alipay.rdf.file.meta.FileBodyMeta;
import com.alipay.rdf.file.meta.FileColumnMeta;
import com.alipay.rdf.file.meta.FileMeta;

/**
 * 
 * Copyright (C) 2013-2018 Ant Financial Services Group
 *
 * @author hongwei.quhw
 * @version $Id: TemplateLoaderTest.java, v 0.1 2018年10月11日 下午5:19:18 hongwei.quhw Exp $
 */
public class MultiTemplateLoaderTest {

    @Test
    public void testMulti() {
        TemplateLoader.load("/multiBodyTemplate/template/template1.json", "utf-8");

        try {
            TemplateLoader.load("/multiBodyTemplate/template/template2_error.json", "utf-8");
            Assert.fail();
        } catch (RdfFileException e) {
            Assert.assertEquals(RdfErrorEnum.TEMPLATE_ERROR, e.getErrorEnum());
        }

        try {
            TemplateLoader.load("/multiBodyTemplate/template/template2_summary_error.json",
                "utf-8");
            Assert.fail();
        } catch (RdfFileException e) {
            Assert.assertEquals(RdfErrorEnum.SUMMARY_DEFINED_ERROR, e.getErrorEnum());
        }

        TemplateLoader.load("/multiBodyTemplate/template/template2_summary_ok.json", "utf-8");
    }

    @Test
    public void testMulti2() {
        FileMeta fileMeta = TemplateLoader.load("/multiBodyTemplate/template/template1.json",
            "utf-8");

        FileColumnMeta totalCount = fileMeta.getHeadColumn("totalCount");
        Assert.assertEquals("总笔数", totalCount.getDesc());

        FileColumnMeta totalAmount = fileMeta.getHeadColumn("totalAmount");
        Assert.assertEquals("BigDecimal", totalAmount.getType().getName());

        try {
            fileMeta.getBodyColumns();
            Assert.fail();
        } catch (RdfFileException e) {
            Assert.assertEquals(e.getErrorEnum(), RdfErrorEnum.UNSUPPORTED_OPERATION);
        }

        for (FileBodyMeta bodyMeta : fileMeta.getBodyMetas()) {
            System.out.println(bodyMeta.getName());
        }

        FileBodyMeta bodyMeta = fileMeta.getBodyMetas().get(0);
        Assert.assertEquals("columnTemplate1", bodyMeta.getName());
        Assert.assertEquals("MatchRowCondition",
            bodyMeta.getRowCondition().getClass().getSimpleName());
        Assert.assertEquals("bol=true", bodyMeta.getRowConditionParam());
        Assert.assertNotNull(bodyMeta.getColumn("seq"));
        Assert.assertNotNull(bodyMeta.getColumn("instSeq"));
        Assert.assertNotNull(bodyMeta.getColumn("bol"));
        Assert.assertNotNull(bodyMeta.getColumn("memo"));

        bodyMeta = fileMeta.getBodyMetas().get(1);
        Assert.assertEquals("columnTemplate2", bodyMeta.getName());
        Assert.assertEquals("MatchRowCondition",
            bodyMeta.getRowCondition().getClass().getSimpleName());
        Assert.assertEquals("bol=false|seq(0,4)=aaa|age=15", bodyMeta.getRowConditionParam());
        Assert.assertNotNull(bodyMeta.getColumn("seq"));
        Assert.assertNotNull(bodyMeta.getColumn("age"));
        Assert.assertNotNull(bodyMeta.getColumn("bol"));
        Assert.assertNotNull(bodyMeta.getColumn("memo"));
        Assert.assertNotNull(bodyMeta.getColumn("amount"));
        Assert.assertNotNull(bodyMeta.getColumn("longN"));

        bodyMeta = fileMeta.getBodyMetas().get(2);
        Assert.assertEquals("columnTemplate3", bodyMeta.getName());
        Assert.assertEquals("CallbackRowCondition",
            bodyMeta.getRowCondition().getClass().getSimpleName());
        Assert.assertEquals("com.alipay.rdf.file.multiBodyTempalte.BizCallbackRowCondition",
            bodyMeta.getRowConditionParam());
        Assert.assertNotNull(bodyMeta.getColumn("seq"));
        Assert.assertNotNull(bodyMeta.getColumn("instSeq"));
        Assert.assertNotNull(bodyMeta.getColumn("date"));
        Assert.assertNotNull(bodyMeta.getColumn("dateTime"));
        Assert.assertNotNull(bodyMeta.getColumn("applyNumber"));
        Assert.assertNotNull(bodyMeta.getColumn("amount"));
        Assert.assertNotNull(bodyMeta.getColumn("age"));
        Assert.assertNotNull(bodyMeta.getColumn("longN"));
        Assert.assertNotNull(bodyMeta.getColumn("bol"));
        Assert.assertNotNull(bodyMeta.getColumn("memo"));
    }
}
