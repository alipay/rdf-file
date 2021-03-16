package com.alipay.rdf.file.multiBodyTempalte;

import com.alipay.rdf.file.interfaces.FileFactory;
import com.alipay.rdf.file.interfaces.FileReader;
import com.alipay.rdf.file.model.FileConfig;
import com.alipay.rdf.file.model.StorageConfig;
import com.alipay.rdf.file.util.DateUtil;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 *
 * @author hongwei.quhw
 * @version $Id: MultiTemplateReadTest.java, v 0.1 2018年10月15日 下午1:56:23 hongwei.quhw Exp $
 */
public class MultiTemplateReadTest {

    @Test
    public void test() {
        String filePath = File.class.getResource("/multiBodyTemplate/data/test.txt").getPath();

        FileConfig config = new FileConfig(filePath,
            "/multiBodyTemplate/template//template3_sp.json", new StorageConfig("nas"));

        FileReader fileReader = FileFactory.createReader(config);

        Map<String, Object> head = fileReader.readHead(HashMap.class);
        Assert.assertEquals(new Long(2), head.get("totalCount"));
        Assert.assertEquals(new BigDecimal("23.22"), head.get("totalAmount"));

        Map<String, Object> row = fileReader.readRow(HashMap.class);
        Assert.assertEquals("seq12345", row.get("seq"));
        Assert.assertEquals("303", row.get("instSeq"));
        Assert.assertEquals(true, row.get("bol"));
        Assert.assertEquals("memo1", row.get("memo"));

        row = fileReader.readRow(HashMap.class);
        Assert.assertEquals("seq14345", row.get("seq"));
        Assert.assertEquals(new BigDecimal("1.09"), row.get("amount"));
        Assert.assertEquals(33, row.get("age"));
        Assert.assertEquals(new Long(125), row.get("longN"));
        Assert.assertEquals(false, row.get("bol"));
        Assert.assertEquals("memo2", row.get("memo"));

        row = fileReader.readRow(HashMap.class);
        Assert.assertEquals("seq4521", row.get("seq"));
        Assert.assertEquals("505", row.get("instSeq"));
        Assert.assertEquals("2016-02-03 12:22:33",
            DateUtil.format((Date) row.get("gmtApply"), "yyyy-MM-dd HH:mm:ss"));
        Assert.assertEquals("20160203", DateUtil.format((Date) row.get("date"), "yyyyMMdd"));
        Assert.assertEquals("20160203 12:22:33",
            DateUtil.format((Date) row.get("dateTime"), "yyyyMMdd HH:mm:ss"));
        Assert.assertEquals(12, row.get("applyNumber"));
        Assert.assertEquals(new BigDecimal("1.09"), row.get("amount"));
        Assert.assertEquals(33, row.get("age"));
        Assert.assertEquals(new Long(67), row.get("longN"));
        Assert.assertEquals(false, row.get("bol"));
        Assert.assertEquals("memo2", row.get("memo"));

        Assert.assertNull(fileReader.readRow(HashMap.class));

        fileReader.close();
    }

}
