package com.alipay.rdf.file.split;

import org.junit.Test;

import com.alipay.rdf.file.util.RdfFileUtil;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 *
 * 分隔符测试
 *
 * @author hongwei.quhw
 * @version $Id: SeparatorTest.java, v 0.1 2019年3月20日 下午4:49:13 hongwei.quhw Exp $
 */
public class SeparatorTest {

    @Test
    public void test() {
        String sep = "\u0001";
        System.out.println(RdfFileUtil.isBlank(sep));
    }
}
