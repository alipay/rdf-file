package com.alipay.rdf.file.merger;

import com.alipay.rdf.file.interfaces.FileFactory;
import com.alipay.rdf.file.interfaces.FileMerger;
import com.alipay.rdf.file.interfaces.FileReader;
import com.alipay.rdf.file.interfaces.FileStorage;
import com.alipay.rdf.file.model.FileConfig;
import com.alipay.rdf.file.model.FileDefaultConfig;
import com.alipay.rdf.file.model.MergerConfig;
import com.alipay.rdf.file.model.StorageConfig;
import com.alipay.rdf.file.storage.FileInnterStorage;
import com.alipay.rdf.file.util.*;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.*;

/**
 * 协议文件合并
 * 
 * 使用stream append方式
 * 
 * @author hongwei.quhw
 * @version $Id: ProtocolFileMergerTest.java, v 0.1 2017年8月12日 下午4:40:26 hongwei.quhw Exp $
 */
public class ProtocolFileMergerTest2 {
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
            "/meger/template/de2.json", new StorageConfig("nas"));
        FileMerger fileMerger = FileFactory.createMerger(fileConfig);
        MergerConfig mergerConfig = new MergerConfig();
        mergerConfig.setStreamAppend(true);
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
    public void testMerge2() throws Exception {
        String filePath = tf.getRoot().getAbsolutePath();
        System.out.println(filePath);
        FileConfig fileConfig = new FileConfig(new File(filePath, "test.txt").getAbsolutePath(),
            "/meger/template/de2.json", new StorageConfig("nas"));
        FileMerger fileMerger = FileFactory.createMerger(fileConfig);
        MergerConfig mergerConfig = new MergerConfig();
        mergerConfig.setStreamAppend(true);
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

        FileInnterStorage fileStorage = (FileInnterStorage) FileFactory
            .createStorage(new StorageConfig("nas"));
        int size = (int) ((FileStorage) fileStorage).getFileInfo(fileConfig.getFilePath())
            .getSize();
        InputStream is = fileStorage.getInputStream(fileConfig.getFilePath());
        byte[] bs = StreamUtil.read(is, size);
        System.out.println(new String(bs));

        /**
         * body 分片后面的空行没有过滤掉 
         * */
        String content = "总笔数:331|总金额:511.16\r\n"
                         + "流水号|基金公司订单号|订单申请时间|普通日期|普通日期时间|普通数字|金额|年龄|长整型|布尔值|备注\r\n"
                         + "seq_0|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\n"
                         + "seq_1|inst_seq_1|2013-11-10 15:56:12|20131110|20131113 12:33:34|23.34|11.88|33|56789|false|seq_3|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\n"
                         + "seq_4|inst_seq_1|2013-11-10 15:56:12|20131110|20131113 12:33:34|23.34|11.88|33|56789|false|\n"
                         + "\n\n\n\n"
                         + "seq_5|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\n"
                         + "seq_6|inst_seq_1|2013-11-10 15:56:12|20131110|20131113 12:33:34|23.34|11.88|33|56789|false|\n"
                         + "\n" + "OFDCFEND|20131109|232\r\n";
        Assert.assertEquals(content, new String(bs, RdfFileUtil.getFileEncoding(fileConfig)));
    }

    /**
     * 同存储 所有文件合并
     */
    @Test
    public void testMerge3() throws Exception {
        String filePath = tf.getRoot().getAbsolutePath();
        //filePath = "/var/folders/pd/t0ck64755qb57z2_46lxz28c0000gn/T/4724181737328769739";
        System.out.println(filePath);
        FileConfig fileConfig = new FileConfig(new File(filePath, "test.txt").getAbsolutePath(),
            "/meger/template/de2.json", new StorageConfig("nas"));
        FileMerger fileMerger = FileFactory.createMerger(fileConfig);
        MergerConfig mergerConfig = new MergerConfig();
        mergerConfig.setStreamAppend(true);
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

        FileInnterStorage fileStorage = (FileInnterStorage) FileFactory
            .createStorage(new StorageConfig("nas"));
        int size = (int) ((FileStorage) fileStorage).getFileInfo(fileConfig.getFilePath())
            .getSize();
        InputStream is = fileStorage.getInputStream(fileConfig.getFilePath());
        byte[] bs = StreamUtil.read(is, size);
        System.out.println(new String(bs));

        /**
         * body 分片后面的空行没有过滤掉 
         * */
        String content = "总笔数:631|总金额:1411.42\r\n"
                         + "流水号|基金公司订单号|订单申请时间|普通日期|普通日期时间|普通数字|金额|年龄|长整型|布尔值|备注\r\n"
                         + "seq_0|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\n"
                         + "seq_1|inst_seq_1|2013-11-10 15:56:12|20131110|20131113 12:33:34|23.34|11.88|33|56789|false|\n"
                         + "seq_2|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\n"
                         + "seq_3|inst_seq_1|2013-11-10 15:56:12|20131110|20131113 12:33:34|23.34|11.88|33|56789|false|\n"
                         + "seq_10|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\n"
                         + "seq_11|inst_seq_1|2013-11-10 15:56:12|20131110|20131113 12:33:34|23.34|11.88|33|56789|false|\n"
                         + "seq_0|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\n"
                         + "seq_1|inst_seq_1|2013-11-10 15:56:12|20131110|20131113 12:33:34|23.34|11.88|33|56789|false|seq_3|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\n"
                         + "seq_4|inst_seq_1|2013-11-10 15:56:12|20131110|20131113 12:33:34|23.34|11.88|33|56789|false|\n"
                         + "\n\n\n\n"
                         + "seq_5|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\n"
                         + "seq_6|inst_seq_1|2013-11-10 15:56:12|20131110|20131113 12:33:34|23.34|11.88|33|56789|false|\n"
                         + "\n" + "OFDCFEND|20131109|555\r\n";
        Assert.assertEquals(content, new String(bs, RdfFileUtil.getFileEncoding(fileConfig)));

        System.out.println(content);
    }

    @After
    public void after() {
        tf.delete();
    }
}
