/*
 * Alipay.com Inc.
 * Copyright (c) 2004-2021 All Rights Reserved.
 */
package com.alipay.rdf.file.writer;

import com.alipay.rdf.file.AbstractFileTestCase;
import com.alipay.rdf.file.common.RawFileWriter;
import com.alipay.rdf.file.interfaces.FileFactory;
import com.alipay.rdf.file.interfaces.FileStorage;
import com.alipay.rdf.file.interfaces.FileWriter;
import com.alipay.rdf.file.model.FileConfig;
import com.alipay.rdf.file.model.StorageConfig;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author wanhaofan
 * @version LineBreakTest.java, v 0.1 2021年09月26日 3:16 PM wanhaofan
 */
public class LineBreakTest extends AbstractFileTestCase {

    @Test
    public void testSetting() throws IOException {
        // 1.config设置不追加但是template不设置，期望不追加
        FileConfig config = new FileConfig(createLocalFile("test.txt"),
                "/writer/template/template5.json", new StorageConfig("nas"));
        config.setAppendLinebreakAtLast(false);
        Map<String, Object> head = new HashMap<String, Object>();
        FileWriter fileWriter = FileFactory.createWriter(config);
        head.put("totalCount", 1);
        fileWriter.writeHead(head);

        fileWriter.close();
        int lastByte = getLastByteOfFile(config.getFilePath());
        Assert.assertTrue(lastByte == 'q');

        // 2.config追加但是template设置不追加，期望不追加
        FileConfig config2 = new FileConfig(createLocalFile("test2.txt"),
                "/writer/template/template6.json", new StorageConfig("nas"));
        FileWriter fileWriter2 = FileFactory.createWriter(config2);
        fileWriter2.writeHead(head);
        fileWriter2.close();
        lastByte = getLastByteOfFile(config2.getFilePath());
        Assert.assertTrue(lastByte == 'q');

        // 3.config,template都追加，期望追加
        FileConfig config3 = new FileConfig(createLocalFile("test3.txt"),
                "/writer/template/template5.json", new StorageConfig("nas"));
        FileWriter fileWriter3 = FileFactory.createWriter(config3);
        fileWriter3.writeHead(head);
        fileWriter3.close();
        lastByte = getLastByteOfFile(config3.getFilePath());
        Assert.assertTrue(lastByte == '\n');

        // 3.config,template都不追加，期望不追加
        FileConfig config4 = new FileConfig(createLocalFile("test3.txt"),
                "/writer/template/template6.json", new StorageConfig("nas"));
        config4.setAppendLinebreakAtLast(false);
        FileWriter fileWriter4 = FileFactory.createWriter(config4);
        fileWriter4.writeHead(head);
        fileWriter4.close();
        lastByte = getLastByteOfFile(config4.getFilePath());
        Assert.assertTrue(lastByte == 'q');
    }

    @Test
    public void testEmptyFile() {
        FileConfig config = new FileConfig(createLocalFile("test.txt"),
                "/writer/template/template1.json", new StorageConfig("nas"));
        config.setCreateEmptyFile(true);
        config.setAppendLinebreakAtLast(false);

        FileWriter fileWriter = FileFactory.createWriter(config);
        fileWriter.close();
        FileStorage fileStorage = FileFactory.createStorage(new StorageConfig("nas"));
        Assert.assertTrue(fileStorage.getFileInfo(config.getFilePath()).isExists());
    }

    @Test
    public void testRawWriter() throws IOException {
        // 追加
        FileConfig config = new FileConfig(createLocalFile("test.txt"),
                "/writer/template/template1.json", new StorageConfig("nas"));
        RawFileWriter writer = new RawFileWriter();
        writer.init(config);

        writer.writeLine("1");
        writer.close();
        int lastByte = getLastByteOfFile(config.getFilePath());
        Assert.assertTrue(lastByte == '\n');

        // 不追加
        FileConfig config2 = new FileConfig(createLocalFile("test2.txt"),
                "/writer/template/template1.json", new StorageConfig("nas"));
        config2.setAppendLinebreakAtLast(false);
        RawFileWriter writer2 = new RawFileWriter();
        writer2.init(config2);
        writer2.writeLine("1");
        writer2.close();
        lastByte = getLastByteOfFile(config2.getFilePath());
        Assert.assertTrue(lastByte == '1');
    }



    @Test
    public void testWithLastLineBreak_writeAll() throws IOException {
        FileConfig config = new FileConfig(createLocalFile("test.txt"),
                "/writer/template/template5.json", new StorageConfig("nas"));
        Map<String, Object> head = new HashMap<String, Object>();
        FileWriter fileWriter = FileFactory.createWriter(config);
        head.put("totalCount", 1);
        fileWriter.writeHead(head);

        Map<String, Object> body = new HashMap<String, Object>();
        body.put("seq", "seq12345");
        fileWriter.writeRow(body);

        fileWriter.writeTail(head);

        fileWriter.close();
        int lastByte = getLastByteOfFile(config.getFilePath());
        Assert.assertTrue(lastByte == '\n');
    }

    @Test
    public void testWithLastLineBreak_writeHead() throws IOException {
        FileConfig config = new FileConfig(createLocalFile("test.txt"),
                "/writer/template/template5.json", new StorageConfig("nas"));
        Map<String, Object> head = new HashMap<String, Object>();
        FileWriter fileWriter = FileFactory.createWriter(config);
        head.put("totalCount", 1);
        fileWriter.writeHead(head);

        fileWriter.close();
        int lastByte = getLastByteOfFile(config.getFilePath());
        Assert.assertTrue(lastByte == '\n');
    }

    @Test
    public void testWithLastLineBreak_writeBody() throws IOException {
        FileConfig config = new FileConfig(createLocalFile("test.txt"),
                "/writer/template/template5.json", new StorageConfig("nas"));
        FileWriter fileWriter = FileFactory.createWriter(config);

        Map<String, Object> body = new HashMap<String, Object>();
        body.put("seq", "seq12345");
        fileWriter.writeRow(body);

        fileWriter.close();
        int lastByte = getLastByteOfFile(config.getFilePath());
        Assert.assertTrue(lastByte == '\n');
    }

    @Test
    public void testWithLastLineBreak_writeTail() throws IOException {
        FileConfig config = new FileConfig(createLocalFile("test.txt"),
                "/writer/template/template5.json", new StorageConfig("nas"));
        Map<String, Object> tail = new HashMap<String, Object>();
        FileWriter fileWriter = FileFactory.createWriter(config);
        tail.put("totalCount", 1);
        fileWriter.writeTail(tail);

        fileWriter.close();
        int lastByte = getLastByteOfFile(config.getFilePath());
        Assert.assertTrue(lastByte == '\n');
    }

    @Test
    public void testWithOutLastLineBreak_writeAll() throws IOException {
        FileConfig config = new FileConfig(createLocalFile("test.txt"),
                "/writer/template/template6.json", new StorageConfig("nas"));
        Map<String, Object> head = new HashMap<String, Object>();
        FileWriter fileWriter = FileFactory.createWriter(config);
        head.put("totalCount", 1);
        fileWriter.writeHead(head);

        Map<String, Object> body = new HashMap<String, Object>();
        body.put("seq", "seq12345");
        fileWriter.writeRow(body);

        fileWriter.writeTail(head);

        fileWriter.close();
        int lastByte = getLastByteOfFile(config.getFilePath());
        Assert.assertTrue(lastByte == '1');
    }

    @Test
    public void testWithOutLastLineBreak_writeHead() throws IOException {
        FileConfig config = new FileConfig(createLocalFile("test.txt"),
                "/writer/template/template6.json", new StorageConfig("nas"));
        Map<String, Object> head = new HashMap<String, Object>();
        FileWriter fileWriter = FileFactory.createWriter(config);
        head.put("totalCount", 1);
        fileWriter.writeHead(head);

        fileWriter.close();
        int lastByte = getLastByteOfFile(config.getFilePath());
        Assert.assertTrue(lastByte == 'q');
    }

    @Test
    public void testWithOutLastLineBreak_writeBody() throws IOException {
        FileConfig config = new FileConfig(createLocalFile("test.txt"),
                "/writer/template/template6.json", new StorageConfig("nas"));
        FileWriter fileWriter = FileFactory.createWriter(config);

        Map<String, Object> body = new HashMap<String, Object>();
        body.put("seq", "seq12345");
        fileWriter.writeRow(body);

        fileWriter.close();
        int lastByte = getLastByteOfFile(config.getFilePath());
        Assert.assertTrue(lastByte == '5');
    }

    @Test
    public void testWithOutLastLineBreak_writeTail() throws IOException {
        FileConfig config = new FileConfig(createLocalFile("test.txt"),
                "/writer/template/template6.json", new StorageConfig("nas"));
        FileWriter fileWriter = FileFactory.createWriter(config);

        Map<String, Object> tail = new HashMap<String, Object>();
        tail.put("totalCount", 1);
        fileWriter.writeTail(tail);

        fileWriter.close();
        int lastByte = getLastByteOfFile(config.getFilePath());
        Assert.assertTrue(lastByte == '1');
    }
    
}