package com.alipay.rdf.file.merger;

import com.alipay.rdf.file.interfaces.FileFactory;
import com.alipay.rdf.file.interfaces.FileMerger;
import com.alipay.rdf.file.interfaces.FileReader;
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
public class ProtocolFileMergerTest3 {
    TemporaryFolderUtil tf = new TemporaryFolderUtil();

    @Before
    public void setUp() throws IOException {
        tf.create();
        new FileDefaultConfig().setCommonLog(new TestLog());
    }

    /**
     * 同存储 已存文件合并
     */
    @Test
    public void testMerge() {
        String filePath = tf.getRoot().getAbsolutePath();
        System.out.println(filePath);
        FileConfig fileConfig = new FileConfig(new File(filePath, "test.txt").getAbsolutePath(),
            "/meger/summary/de2.json", new StorageConfig("nas"));
        FileMerger fileMerger = FileFactory.createMerger(fileConfig);
        MergerConfig mergerConfig = new MergerConfig();
        List<String> existFilePaths = new ArrayList<String>();
        existFilePaths.add(File.class.getResource("/meger/summary/de_all1.txt").getPath());
        existFilePaths.add(File.class.getResource("/meger/summary/de_all2.txt").getPath());
        existFilePaths.add(File.class.getResource("/meger/summary/de_all3.txt").getPath());
        mergerConfig.setExistFilePaths(existFilePaths);

        List<String> headFilePaths = new ArrayList<String>();
        headFilePaths.add(File.class.getResource("/meger/summary/de_head1.txt").getPath());
        mergerConfig.setHeadFilePaths(headFilePaths);

        List<String> tailFilePaths = new ArrayList<String>();
        tailFilePaths.add(File.class.getResource("/meger/summary/de_tail1.txt").getPath());
        mergerConfig.setTailFilePaths(tailFilePaths);

        fileMerger.merge(mergerConfig);

        FileReader reader = FileFactory.createReader(fileConfig);
        Map<String, Object> head = reader.readHead(HashMap.class);
        Assert.assertEquals(new Integer(300), (Integer) head.get("totalCount"));
        Assert.assertEquals(new BigDecimal("900.26"), (BigDecimal) head.get("totalAmount"));
        Assert.assertEquals(new Integer(26), head.get("inst_1"));

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
        Assert.assertEquals(new BigDecimal("423"), tail.get("amount"));
        Assert.assertEquals(new Long(61), tail.get("inst_0"));
    }

    @After
    public void after() {
        tf.delete();
    }
}
