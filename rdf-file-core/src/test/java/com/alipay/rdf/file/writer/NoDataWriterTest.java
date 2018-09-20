package com.alipay.rdf.file.writer;

import java.io.File;
import java.io.IOException;
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

    @After
    public void after() {
        tf.delete();
    }
}
