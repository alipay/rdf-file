package com.alipay.rdf.file.writer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.alipay.rdf.file.interfaces.FileFactory;
import com.alipay.rdf.file.interfaces.FileStorage;
import com.alipay.rdf.file.interfaces.FileWriter;
import com.alipay.rdf.file.model.FileConfig;
import com.alipay.rdf.file.model.FileDefaultConfig;
import com.alipay.rdf.file.model.StorageConfig;
import com.alipay.rdf.file.util.TemporaryFolderUtil;
import com.alipay.rdf.file.util.TestLog;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 *
 * 写文件时没有数据测试
 *
 * @author hongwei.quhw
 * @version $Id: NoDataWriterTest.java, v 0.1 2018年9月20日 下午4:42:30 hongwei.quhw Exp $
 */
public class NoDataWriterTest {
    TemporaryFolderUtil tf = new TemporaryFolderUtil();

    @Before
    public void setUp() throws IOException {
        tf.create();
        new FileDefaultConfig().setCommonLog(new TestLog());
    }

    @Test
    public void testWriter() throws Exception {
        String filePath = tf.getRoot().getAbsolutePath();
        System.out.println(filePath);

        FileConfig config = new FileConfig(new File(filePath, "test.txt").getAbsolutePath(),
            "/writer/template/template1.json", new StorageConfig("nas"));
        FileStorage fileStorage = FileFactory.createStorage(new StorageConfig("nas"));

        FileWriter fileWriter = FileFactory.createWriter(config);
        Map<String, Object> head = new HashMap<String, Object>();
        head.put("totalCount", 2);
        head.put("totalAmount", new BigDecimal("23.22"));
        fileWriter.writeHead(head);
        fileWriter.close();
        Assert.assertTrue(fileStorage.getFileInfo(config.getFilePath()).isExists());

    }

    @Test
    public void testWriter2() throws Exception {
        String filePath = tf.getRoot().getAbsolutePath();
        System.out.println(filePath);

        FileConfig config = new FileConfig(new File(filePath, "test.txt").getAbsolutePath(),
            "/writer/template/template1.json", new StorageConfig("nas"));
        FileStorage fileStorage = FileFactory.createStorage(new StorageConfig("nas"));

        FileWriter fileWriter = FileFactory.createWriter(config);
        fileWriter.close();
        Assert.assertFalse(fileStorage.getFileInfo(config.getFilePath()).isExists());

    }

    @Test
    public void testWriter3() throws Exception {
        String filePath = tf.getRoot().getAbsolutePath();
        System.out.println(filePath);

        FileConfig config = new FileConfig(new File(filePath, "test.txt").getAbsolutePath(),
            "/writer/template/template1.json", new StorageConfig("nas"));
        config.setCreateEmptyFile(true);
        FileStorage fileStorage = FileFactory.createStorage(new StorageConfig("nas"));

        FileWriter fileWriter = FileFactory.createWriter(config);
        fileWriter.close();
        Assert.assertTrue(fileStorage.getFileInfo(config.getFilePath()).isExists());

    }

    /**
     * 带汇总信息  数据内容为空
     */
    @Test
    public void testWriter4() throws Exception {
        String filePath = tf.getRoot().getAbsolutePath();
        System.out.println(filePath);

        FileConfig config = new FileConfig(new File(filePath, "test.txt").getAbsolutePath(),
            "/writer/template/template2.json", new StorageConfig("nas"));
        config.setCreateEmptyFile(true);
        config.setSummaryEnable(true);
        FileStorage fileStorage = FileFactory.createStorage(new StorageConfig("nas"));

        FileWriter fileWriter = FileFactory.createWriter(config);
        fileWriter.writeHead(fileWriter.getSummary().summaryHeadToMap());
        fileWriter.writeTail(fileWriter.getSummary().summaryTailToMap());

        fileWriter.close();
        Assert.assertTrue(fileStorage.getFileInfo(config.getFilePath()).isExists());

        //校验文件
        BufferedReader reader = new BufferedReader(
            new InputStreamReader(new FileInputStream(new File(config.getFilePath())), "UTF-8"));
        Assert.assertEquals("总笔数:0|总金额:0", reader.readLine());
        Assert.assertEquals("流水号|基金公司订单号|订单申请时间|普通日期|普通日期时间|普通数字|金额|年龄|长整型|布尔值|备注",
            reader.readLine());
        Assert.assertEquals("0|0", reader.readLine());
        Assert.assertNull(reader.readLine());
        reader.close();
    }

    @After
    public void after() {
        tf.delete();
    }
}
