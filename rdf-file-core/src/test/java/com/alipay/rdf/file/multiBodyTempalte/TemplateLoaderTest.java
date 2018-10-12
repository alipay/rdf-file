package com.alipay.rdf.file.multiBodyTempalte;

import org.junit.Test;

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
    }
}
