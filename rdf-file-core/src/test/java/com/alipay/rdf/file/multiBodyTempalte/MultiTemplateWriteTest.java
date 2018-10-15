package com.alipay.rdf.file.multiBodyTempalte;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.alipay.rdf.file.exception.RdfErrorEnum;
import com.alipay.rdf.file.exception.RdfFileException;
import com.alipay.rdf.file.interfaces.FileFactory;
import com.alipay.rdf.file.interfaces.FileWriter;
import com.alipay.rdf.file.model.FileConfig;
import com.alipay.rdf.file.model.StorageConfig;
import com.alipay.rdf.file.util.DateUtil;
import com.alipay.rdf.file.util.TemporaryFolderUtil;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 *
 * @author hongwei.quhw
 * @version $Id: MultiTemplateWriteTest.java, v 0.1 2018年10月15日 上午11:07:14 hongwei.quhw Exp $
 */
public class MultiTemplateWriteTest {
    TemporaryFolderUtil tf = new TemporaryFolderUtil();

    @Before
    public void setUp() throws IOException {
        tf.create();
    }

    @Test
    public void testWriter() throws Exception {
        String filePath = tf.getRoot().getAbsolutePath();
        System.out.println(filePath);

        FileConfig config = new FileConfig(new File(filePath, "test.txt").getAbsolutePath(),
            "/multiBodyTemplate/template//template3_sp.json", new StorageConfig("nas"));

        FileWriter fileWriter = FileFactory.createWriter(config);

        Map<String, Object> head = new HashMap<String, Object>();
        head.put("totalCount", 2);
        head.put("totalAmount", new BigDecimal("23.22"));
        fileWriter.writeHead(head);

        Map<String, Object> body = new HashMap<String, Object>();

        Date testDate = DateUtil.parse("2017-01-03 12:22:33", "yyyy-MM-dd HH:mm:ss");

        body.put("seq", "seq12345");
        body.put("instSeq", "303");
        body.put("gmtApply", testDate);
        body.put("date", testDate);
        body.put("dateTime", testDate);
        body.put("applyNumber", 12);
        body.put("amount", new BigDecimal("1.22"));
        body.put("age", new Integer(33));
        body.put("longN", new Long(33));
        body.put("bol", true);
        body.put("memo", "memo1");
        fileWriter.writeRow(body);

        testDate = DateUtil.parse("2016-02-03 12:22:33", "yyyy-MM-dd HH:mm:ss");

        body.put("seq", "seq14345");
        body.put("instSeq", "505");
        body.put("gmtApply", testDate);
        body.put("date", testDate);
        body.put("dateTime", testDate);
        body.put("applyNumber", 12);
        body.put("amount", new BigDecimal("1.09"));
        body.put("age", 33);
        body.put("longN", 125);
        body.put("bol", false);
        body.put("memo", "memo2");
        fileWriter.writeRow(body);

        body.put("seq", "seq4521");
        body.put("longN", 67L);

        fileWriter.writeRow(body);

        try {
            body.put("longN", 77L);
            fileWriter.writeRow(body);
            Assert.fail();
        } catch (RdfFileException e) {
            Assert.assertEquals(RdfErrorEnum.UNSUPPORTED_OPERATION, e.getErrorEnum());
        }

        fileWriter.close();

        //校验文件
        BufferedReader reader = new BufferedReader(
            new InputStreamReader(new FileInputStream(new File(config.getFilePath())), "UTF-8"));
        Assert.assertEquals("2|23.22", reader.readLine());
        Assert.assertEquals("seq12345|303|true|memo1", reader.readLine());
        Assert.assertEquals("seq14345|1.09|33|125|false|memo2", reader.readLine());
        Assert.assertEquals(
            "seq4521|505|2016-02-03 12:22:33|20160203|20160203 12:22:33|12|1.09|33|67|false|memo2",
            reader.readLine());

        Assert.assertNull(reader.readLine());

        reader.close();

    }

    /**
     * de不支持多模板
     * 
     * @throws Exception
     */
    @Test
    public void testWriter2() throws Exception {
        String filePath = tf.getRoot().getAbsolutePath();
        System.out.println(filePath);

        FileConfig config = new FileConfig(new File(filePath, "test.txt").getAbsolutePath(),
            "/multiBodyTemplate/template//template1.json", new StorageConfig("nas"));

        FileWriter fileWriter = FileFactory.createWriter(config);

        try {
            Map<String, Object> head = new HashMap<String, Object>();
            head.put("totalCount", 2);
            head.put("totalAmount", new BigDecimal("23.22"));
            fileWriter.writeHead(head);
            Assert.fail();
        } catch (RdfFileException e) {
            Assert.assertEquals(RdfErrorEnum.FUNCTION_ERROR, e.getErrorEnum());
        }
    }

    @After
    public void after() {
        tf.delete();
    }
}
