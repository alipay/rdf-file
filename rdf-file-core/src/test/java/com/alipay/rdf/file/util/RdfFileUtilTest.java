package com.alipay.rdf.file.util;

import org.junit.Test;

import junit.framework.Assert;

/**
 * 工具类测试
 * 
 * @author hongwei.quhw
 * @version $Id: RdfFileUtilTest.java, v 0.1 2017年8月8日 下午5:43:24 hongwei.quhw Exp $
 */
public class RdfFileUtilTest {

    /**
     * 测试分割符
     */
    @Test
    public void testSplitPreserveAllTokens() {
        String[] cols = RdfFileUtil.split("a|b|c", "|");
        Assert.assertEquals(3, cols.length);
        Assert.assertEquals("c", cols[2]);

        cols = RdfFileUtil.split("a|b|c|", "|");
        Assert.assertEquals(4, cols.length);
        Assert.assertEquals("", cols[3]);

        cols = RdfFileUtil.split("|a|b|c|", "|");
        Assert.assertEquals(5, cols.length);
        Assert.assertEquals("", cols[0]);

        cols = RdfFileUtil.split("a|@|b|@|c", "|@|");
        Assert.assertEquals(3, cols.length);
        Assert.assertEquals("a", cols[0]);

        cols = RdfFileUtil.split("a|@|b|@|c@", "|@|");
        Assert.assertEquals(3, cols.length);
        Assert.assertEquals("c@", cols[2]);

        cols = RdfFileUtil.split("a|@|b|@|c|", "|@|");
        Assert.assertEquals(3, cols.length);
        Assert.assertEquals("c|", cols[2]);
    }
}
