package com.alipay.rdf.file.multiBodyTempalte;

import org.junit.Assert;
import org.junit.Test;

import com.alipay.rdf.file.exception.RdfErrorEnum;
import com.alipay.rdf.file.exception.RdfFileException;
import com.alipay.rdf.file.loader.TemplateLoader;

/**
 * 
 * Copyright (C) 2013-2018 Ant Financial Services Group
 *
 * @author hongwei.quhw
 * @version $Id: TemplateLoaderTest.java, v 0.1 2018年10月11日 下午5:19:18 hongwei.quhw Exp $
 */
public class TemplateLoaderTest {

    @Test
    public void testMult() {
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
            Assert.assertEquals(RdfErrorEnum.COLUMN_NOT_DEFINED, e.getErrorEnum());
        }

        try {
            TemplateLoader.load("/multiBodyTemplate/template/template2_summary_error2.json",
                "utf-8");
            Assert.fail();
        } catch (RdfFileException e) {
            Assert.assertEquals(RdfErrorEnum.SUMMARY_DEFINED_ERROR, e.getErrorEnum());
        }

        TemplateLoader.load("/multiBodyTemplate/template/template2_summary_ok.json", "utf-8");
    }
}
