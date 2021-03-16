package com.alipay.rdf.file.merger;

import com.alipay.rdf.file.exception.RdfErrorEnum;
import com.alipay.rdf.file.exception.RdfFileException;
import com.alipay.rdf.file.interfaces.FileFactory;
import com.alipay.rdf.file.interfaces.FileMerger;
import com.alipay.rdf.file.interfaces.FileReader;
import com.alipay.rdf.file.interfaces.FileStorage;
import com.alipay.rdf.file.model.FileConfig;
import com.alipay.rdf.file.model.FileDefaultConfig;
import com.alipay.rdf.file.model.MergerConfig;
import com.alipay.rdf.file.model.StorageConfig;
import com.alipay.rdf.file.util.DateUtil;
import com.alipay.rdf.file.util.TemporaryFolderUtil;
import com.alipay.rdf.file.util.TestLog;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

/**
 * 协议文件合并
 * 默认body readline方式
 * @author hongwei.quhw
 * @version $Id: ProtocolFileMergerTest.java, v 0.1 2017年8月12日 下午4:40:26 hongwei.quhw Exp $
 */
public class ProtocolFileMergerTest {
    TemporaryFolderUtil tf = new TemporaryFolderUtil();

    @Before
    public void setUp() throws IOException {
        tf.create();
        new FileDefaultConfig().setCommonLog(new TestLog());
    }

    @Test
    public void testDuplicateSummaryKey() {
        String filePath = tf.getRoot().getAbsolutePath();
        System.out.println(filePath);
        FileConfig fileConfig = new FileConfig(new File(filePath, "test.txt").getAbsolutePath(),
            "/meger/template/de.json", new StorageConfig("nas"));
        try {
            FileFactory.createMerger(fileConfig);
            Assert.fail();
        } catch (RdfFileException e) {
            Assert.assertEquals(RdfErrorEnum.DUPLICATE_DEFINED, e.getErrorEnum());
        }

        fileConfig = new FileConfig(new File(filePath, "test.txt").getAbsolutePath(),
            "/meger/template/de2.json", new StorageConfig("nas"));
        FileMerger fileMerger = FileFactory.createMerger(fileConfig);
        MergerConfig mergerConfig = new MergerConfig();
        List<String> existFilePaths = new ArrayList<String>();
        existFilePaths.add(File.class.getResource("/meger/data/de_all1.txt").getPath() + "xxx");
        mergerConfig.setExistFilePaths(existFilePaths);

        try {
            fileMerger.merge(mergerConfig);
        } catch (RdfFileException e) {
            Assert.assertEquals(RdfErrorEnum.NOT_EXSIT, e.getErrorEnum());
        }
    }

    /**
     * 同存储 已存文件合并
     */
    @Test
    public void testMerge() {
        String filePath = tf.getRoot().getAbsolutePath();
        System.out.println(filePath);
        FileConfig fileConfig = new FileConfig(new File(filePath, "test.txt").getAbsolutePath(),
            "/meger/template/de2.json", new StorageConfig("nas"));
        FileMerger fileMerger = FileFactory.createMerger(fileConfig);
        MergerConfig mergerConfig = new MergerConfig();
        List<String> existFilePaths = new ArrayList<String>();
        existFilePaths.add(File.class.getResource("/meger/data/de_all1.txt").getPath());
        existFilePaths.add(File.class.getResource("/meger/data/de_all2.txt").getPath());
        existFilePaths.add(File.class.getResource("/meger/data/de_all3.txt").getPath());
        mergerConfig.setExistFilePaths(existFilePaths);

        fileMerger.merge(mergerConfig);

        FileReader reader = FileFactory.createReader(fileConfig);
        Map<String, Object> head = reader.readHead(HashMap.class);
        Assert.assertEquals(new Integer(300), (Integer) head.get("totalCount"));
        Assert.assertEquals(new BigDecimal("900.26"), (BigDecimal) head.get("totalAmount"));

        Map<String, Object> row = reader.readRow(HashMap.class);
        Assert.assertEquals("seq_0", row.get("seq"));
        row = reader.readRow(HashMap.class);
        Assert.assertEquals("seq_1", row.get("seq"));
        row = reader.readRow(HashMap.class);
        Assert.assertEquals("seq_2", row.get("seq"));
        row = reader.readRow(HashMap.class);
        Assert.assertEquals("seq_3", row.get("seq"));
        row = reader.readRow(HashMap.class);
        Assert.assertEquals("seq_10", row.get("seq"));
        row = reader.readRow(HashMap.class);
        Assert.assertEquals("seq_11", row.get("seq"));
        row = reader.readRow(HashMap.class);
        Assert.assertNull(row);

        Map<String, Object> tail = reader.readTail(HashMap.class);
        Assert.assertEquals("OFDCFEND", tail.get("fileEnd"));
        Assert.assertEquals("20131109", DateUtil.format((Date) tail.get("date"), "yyyyMMdd"));
        Assert.assertEquals(new BigDecimal("323"), tail.get("amount"));
    }

    /**
     * 同存储 分片文件合并
     */
    @Test
    public void testMerge2() {
        String filePath = tf.getRoot().getAbsolutePath();
        System.out.println(filePath);
        FileConfig fileConfig = new FileConfig(new File(filePath, "test.txt").getAbsolutePath(),
            "/meger/template/de2.json", new StorageConfig("nas"));
        FileMerger fileMerger = FileFactory.createMerger(fileConfig);
        MergerConfig mergerConfig = new MergerConfig();
        List<String> headFilePaths = new ArrayList<String>();
        headFilePaths.add(File.class.getResource("/meger/data/de_head1.txt").getPath());
        headFilePaths.add(File.class.getResource("/meger/data/de_head2.txt").getPath());
        headFilePaths.add(File.class.getResource("/meger/data/de_head3.txt").getPath());
        mergerConfig.setHeadFilePaths(headFilePaths);

        List<String> bodyFilePaths = new ArrayList<String>();
        bodyFilePaths.add(File.class.getResource("/meger/data/de_body1.txt").getPath());
        bodyFilePaths.add(File.class.getResource("/meger/data/de_body2.txt").getPath());
        bodyFilePaths.add(File.class.getResource("/meger/data/de_body3.txt").getPath());
        mergerConfig.setBodyFilePaths(bodyFilePaths);

        List<String> tailFilePaths = new ArrayList<String>();
        tailFilePaths.add(File.class.getResource("/meger/data/de_tail1.txt").getPath());
        tailFilePaths.add(File.class.getResource("/meger/data/de_tail2.txt").getPath());
        tailFilePaths.add(File.class.getResource("/meger/data/de_tail3.txt").getPath());
        mergerConfig.setTailFilePaths(tailFilePaths);

        fileMerger.merge(mergerConfig);

        FileReader reader = FileFactory.createReader(fileConfig);
        Map<String, Object> head = reader.readHead(HashMap.class);
        Assert.assertEquals(new Integer(331), (Integer) head.get("totalCount"));
        Assert.assertEquals(new BigDecimal("511.16"), (BigDecimal) head.get("totalAmount"));

        Map<String, Object> row = reader.readRow(HashMap.class);
        Assert.assertEquals("seq_0", row.get("seq"));
        row = reader.readRow(HashMap.class);
        Assert.assertEquals("seq_1", row.get("seq"));
        row = reader.readRow(HashMap.class);
        Assert.assertEquals("seq_3", row.get("seq"));
        row = reader.readRow(HashMap.class);
        Assert.assertEquals("seq_4", row.get("seq"));
        row = reader.readRow(HashMap.class);
        Assert.assertEquals("seq_5", row.get("seq"));
        row = reader.readRow(HashMap.class);
        Assert.assertEquals("seq_6", row.get("seq"));
        row = reader.readRow(HashMap.class);
        Assert.assertNull(row);

        Map<String, Object> tail = reader.readTail(HashMap.class);
        Assert.assertEquals("OFDCFEND", tail.get("fileEnd"));
        Assert.assertEquals("20131109", DateUtil.format((Date) tail.get("date"), "yyyyMMdd"));
        Assert.assertEquals(new BigDecimal("232"), tail.get("amount"));
    }

    /**
     * 同存储 所有文件合并
     */
    @Test
    public void testMerge3() {
        String filePath = tf.getRoot().getAbsolutePath();
        System.out.println(filePath);
        FileConfig fileConfig = new FileConfig(new File(filePath, "test.txt").getAbsolutePath(),
            "/meger/template/de2.json", new StorageConfig("nas"));
        FileMerger fileMerger = FileFactory.createMerger(fileConfig);
        MergerConfig mergerConfig = new MergerConfig();
        List<String> headFilePaths = new ArrayList<String>();
        headFilePaths.add(File.class.getResource("/meger/data/de_head1.txt").getPath());
        headFilePaths.add(File.class.getResource("/meger/data/de_head2.txt").getPath());
        headFilePaths.add(File.class.getResource("/meger/data/de_head3.txt").getPath());
        mergerConfig.setHeadFilePaths(headFilePaths);

        List<String> bodyFilePaths = new ArrayList<String>();
        bodyFilePaths.add(File.class.getResource("/meger/data/de_body1.txt").getPath());
        bodyFilePaths.add(File.class.getResource("/meger/data/de_body2.txt").getPath());
        bodyFilePaths.add(File.class.getResource("/meger/data/de_body3.txt").getPath());
        mergerConfig.setBodyFilePaths(bodyFilePaths);

        List<String> tailFilePaths = new ArrayList<String>();
        tailFilePaths.add(File.class.getResource("/meger/data/de_tail1.txt").getPath());
        tailFilePaths.add(File.class.getResource("/meger/data/de_tail2.txt").getPath());
        tailFilePaths.add(File.class.getResource("/meger/data/de_tail3.txt").getPath());
        mergerConfig.setTailFilePaths(tailFilePaths);

        List<String> existFilePaths = new ArrayList<String>();
        existFilePaths.add(File.class.getResource("/meger/data/de_all1.txt").getPath());
        existFilePaths.add(File.class.getResource("/meger/data/de_all2.txt").getPath());
        existFilePaths.add(File.class.getResource("/meger/data/de_all3.txt").getPath());
        mergerConfig.setExistFilePaths(existFilePaths);

        fileMerger.merge(mergerConfig);

        FileReader reader = FileFactory.createReader(fileConfig);
        Map<String, Object> head = reader.readHead(HashMap.class);
        Assert.assertEquals(new Integer(631), (Integer) head.get("totalCount"));
        Assert.assertEquals(new BigDecimal("1411.42"), (BigDecimal) head.get("totalAmount"));

        Map<String, Object> row = reader.readRow(HashMap.class);
        Assert.assertEquals("seq_0", row.get("seq"));
        row = reader.readRow(HashMap.class);
        Assert.assertEquals("seq_1", row.get("seq"));
        row = reader.readRow(HashMap.class);
        Assert.assertEquals("seq_2", row.get("seq"));
        row = reader.readRow(HashMap.class);
        Assert.assertEquals("seq_3", row.get("seq"));
        row = reader.readRow(HashMap.class);
        Assert.assertEquals("seq_10", row.get("seq"));
        row = reader.readRow(HashMap.class);
        Assert.assertEquals("seq_11", row.get("seq"));
        row = reader.readRow(HashMap.class);
        Assert.assertEquals("seq_0", row.get("seq"));
        row = reader.readRow(HashMap.class);
        Assert.assertEquals("seq_1", row.get("seq"));
        row = reader.readRow(HashMap.class);
        Assert.assertEquals("seq_3", row.get("seq"));
        row = reader.readRow(HashMap.class);
        Assert.assertEquals("seq_4", row.get("seq"));
        row = reader.readRow(HashMap.class);
        Assert.assertEquals("seq_5", row.get("seq"));
        row = reader.readRow(HashMap.class);
        Assert.assertEquals("seq_6", row.get("seq"));
        row = reader.readRow(HashMap.class);
        Assert.assertNull(row);

        Map<String, Object> tail = reader.readTail(HashMap.class);
        Assert.assertEquals("OFDCFEND", tail.get("fileEnd"));
        Assert.assertEquals("20131109", DateUtil.format((Date) tail.get("date"), "yyyyMMdd"));
        Assert.assertEquals(new BigDecimal("555"), tail.get("amount"));
    }

    @Test
    public void testMerge4() {
        String filePath = tf.getRoot().getAbsolutePath();
        System.out.println(filePath);
        FileConfig fileConfig = new FileConfig(
            new File(filePath, "testEmpty.txt").getAbsolutePath(), "/meger/template/de3.json",
            new StorageConfig("nas"));
        FileMerger fileMerger = FileFactory.createMerger(fileConfig);
        MergerConfig mergerConfig = new MergerConfig();
        fileMerger.merge(mergerConfig);

        FileStorage fileStorage = FileFactory.createStorage(new StorageConfig("nas"));
        Assert.assertFalse(fileStorage.getFileInfo(fileConfig.getFilePath()).isExists());
    }

    @Test
    public void testMerge5() {
        String filePath = tf.getRoot().getAbsolutePath();
        System.out.println(filePath);
        FileConfig fileConfig = new FileConfig(
            new File(filePath, "testEmpty.txt").getAbsolutePath(), "/meger/template/de3.json",
            new StorageConfig("nas"));
        fileConfig.setCreateEmptyFile(true);
        FileMerger fileMerger = FileFactory.createMerger(fileConfig);
        MergerConfig mergerConfig = new MergerConfig();
        fileMerger.merge(mergerConfig);

        FileStorage fileStorage = FileFactory.createStorage(new StorageConfig("nas"));
        Assert.assertTrue(fileStorage.getFileInfo(fileConfig.getFilePath()).isExists());
    }

    @After
    public void after() {
        tf.delete();
    }
}
