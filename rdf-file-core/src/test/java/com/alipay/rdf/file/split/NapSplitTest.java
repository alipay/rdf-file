package com.alipay.rdf.file.split;

import com.alipay.rdf.file.exception.RdfErrorEnum;
import com.alipay.rdf.file.exception.RdfFileException;
import com.alipay.rdf.file.loader.ProtocolLoader;
import com.alipay.rdf.file.model.FileSlice;
import com.alipay.rdf.file.spi.RdfFileSplitterSpi;
import com.alipay.rdf.file.spi.RdfFileStorageSpi;
import com.alipay.rdf.file.storage.FileNasStorage;
import com.alipay.rdf.file.util.RdfFileLogUtil;
import com.alipay.rdf.file.util.StreamUtil;
import com.alipay.rdf.file.util.TemporaryFolderUtil;
import com.alipay.rdf.file.util.TestLog;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.List;

/**
 * 文件切分
 * 
 * @author hongwei.quhw
 * @version $Id: NapSplitTest.java, v 0.1 2017年7月31日 下午8:32:31 hongwei.quhw Exp $
 */
public class NapSplitTest {
    private TemporaryFolderUtil tf           = new TemporaryFolderUtil();
    private RdfFileSplitterSpi     fileSplitter = new NasFileSliceSplitter();
    private RdfFileStorageSpi      storage      = new FileNasStorage();

    @Before
    public void setUp() throws Exception {
        Field field = ProtocolLoader.class.getDeclaredField("PD_CACHE");
        field.setAccessible(true);
        RdfFileLogUtil.common = new TestLog();
        tf.create();
    }

    /**
     * 一行一个分片
     * 
     * @throws Exception
     */
    @Test
    public void testSplit() throws Exception {
        String content = "总笔数:100|总金额:300.03\r\n"
                         + "流水号|基金公司订单号|订单申请时间|普通日期|普通日期时间|普通数字|金额|年龄|长整型|布尔值|备注\r\n"
                         + "seq_0|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r"
                         + "seq_1|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\n"
                         + "seq_2|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n"
                         + "seq_3|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r"
                         + "seq_4|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n\r\r\r\n\n";

        File file = new File(tf.getRoot(), "test.txt");
        FileWriter writer = new FileWriter(file);
        writer.write(content);
        writer.close();

        String path = file.getAbsolutePath();

        List<FileSlice> slices = fileSplitter.split(path, 10);
        Assert.assertEquals(8, slices.size());

        FileSlice slice = slices.get(0);
        InputStream is = storage.getInputStream(path, slice.getStart(), slice.getLength());
        byte[] bs = StreamUtil.read(is, (int) slice.getLength());
        Assert.assertEquals("总笔数:100|总金额:300.03\r\n", new String(bs));

        slice = slices.get(1);
        is = storage.getInputStream(path, slice.getStart(), slice.getLength());
        bs = StreamUtil.read(is, (int) slice.getLength());
        Assert.assertEquals("流水号|基金公司订单号|订单申请时间|普通日期|普通日期时间|普通数字|金额|年龄|长整型|布尔值|备注\r\n",
            new String(bs));

        slice = slices.get(2);
        is = storage.getInputStream(path, slice.getStart(), slice.getLength());
        bs = StreamUtil.read(is, (int) slice.getLength());
        Assert.assertEquals(
            "seq_0|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r",
            new String(bs));

        slice = slices.get(3);
        is = storage.getInputStream(path, slice.getStart(), slice.getLength());
        bs = StreamUtil.read(is, (int) slice.getLength());
        Assert.assertEquals(
            "seq_1|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\n",
            new String(bs));

        slice = slices.get(4);
        is = storage.getInputStream(path, slice.getStart(), slice.getLength());
        bs = StreamUtil.read(is, (int) slice.getLength());
        Assert.assertEquals(
            "seq_2|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n",
            new String(bs));

        slice = slices.get(5);
        is = storage.getInputStream(path, slice.getStart(), slice.getLength());
        bs = StreamUtil.read(is, (int) slice.getLength());
        Assert.assertEquals(
            "seq_3|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r",
            new String(bs));

        slice = slices.get(6);
        is = storage.getInputStream(path, slice.getStart(), slice.getLength());
        bs = StreamUtil.read(is, (int) slice.getLength());
        Assert.assertEquals(
            "seq_4|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n",
            new String(bs));

        slice = slices.get(7);
        is = storage.getInputStream(path, slice.getStart(), slice.getLength());
        bs = StreamUtil.read(is, (int) slice.getLength());
        Assert.assertEquals("\r\r\r\n\n", new String(bs));
    }

    @Test
    public void tesSplitToTwoSlice() throws Exception {
        String content = "总笔数:100|总金额:300.03\r\n"
                         + "流水号|基金公司订单号|订单申请时间|普通日期|普通日期时间|普通数字|金额|年龄|长整型|布尔值|备注\r\n"
                         + "seq_0|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r"
                         + "seq_1|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\n"
                         + "seq_2|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n"
                         + "seq_3|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r"
                         + "seq_4|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n\r\r\r\n\n";

        File file = new File(tf.getRoot(), "test.txt");
        FileWriter writer = new FileWriter(file);
        writer.write(content);
        writer.close();

        String path = file.getAbsolutePath();

        List<FileSlice> slices = fileSplitter.split(path, 300);
        Assert.assertEquals(2, slices.size());

        FileSlice slice = slices.get(0);
        InputStream is = storage.getInputStream(path, slice.getStart(), slice.getLength());
        byte[] bs = StreamUtil.read(is, (int) slice.getLength());
        Assert.assertEquals(
            "总笔数:100|总金额:300.03\r\n" + "流水号|基金公司订单号|订单申请时间|普通日期|普通日期时间|普通数字|金额|年龄|长整型|布尔值|备注\r\n"
                            + "seq_0|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r"
                            + "seq_1|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\n",
            new String(bs));

        slice = slices.get(1);
        is = storage.getInputStream(path, slice.getStart(), slice.getLength());
        bs = StreamUtil.read(is, (int) slice.getLength());
        Assert.assertEquals(
            "seq_2|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n"
                            + "seq_3|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r"
                            + "seq_4|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n\r\r\r\n\n",
            new String(bs));
    }

    @Test
    public void tesSplitAllFile() throws Exception {
        String content = "总笔数:100|总金额:300.03\r\n"
                         + "流水号|基金公司订单号|订单申请时间|普通日期|普通日期时间|普通数字|金额|年龄|长整型|布尔值|备注\r\n"
                         + "seq_0|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r"
                         + "seq_1|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\n"
                         + "seq_2|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n"
                         + "seq_3|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r"
                         + "seq_4|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n\r\r\r\n\n";

        File file = new File(tf.getRoot(), "test.txt");
        FileWriter writer = new FileWriter(file);
        writer.write(content);
        writer.close();

        String path = file.getAbsolutePath();

        List<FileSlice> slices = fileSplitter.split(path, 667);
        Assert.assertEquals(1, slices.size());

        FileSlice slice = slices.get(0);
        InputStream is = storage.getInputStream(path, slice.getStart(), slice.getLength());
        byte[] bs = StreamUtil.read(is, (int) slice.getLength());
        Assert.assertEquals(content, new String(bs));
    }

    @Test
    public void tesSplitAllFile2() throws Exception {
        String content = "总笔数:100|总金额:300.03\r\n"
                         + "流水号|基金公司订单号|订单申请时间|普通日期|普通日期时间|普通数字|金额|年龄|长整型|布尔值|备注\r\n"
                         + "seq_0|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r"
                         + "seq_1|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\n"
                         + "seq_2|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n"
                         + "seq_3|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r"
                         + "seq_4|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n\r\r\r\n\n";

        File file = new File(tf.getRoot(), "test.txt");
        FileWriter writer = new FileWriter(file);
        writer.write(content);
        writer.close();

        String path = file.getAbsolutePath();

        List<FileSlice> slices = fileSplitter.split(path, 1000);
        Assert.assertEquals(1, slices.size());

        FileSlice slice = slices.get(0);
        InputStream is = storage.getInputStream(path, slice.getStart(), slice.getLength());
        byte[] bs = StreamUtil.read(is, (int) slice.getLength());
        Assert.assertEquals(content, new String(bs));
    }

    @Test
    public void testEmptyFile() throws Exception {
        File file = new File(tf.getRoot(), "test.txt");
        FileWriter writer = new FileWriter(file);
        writer.write("");
        writer.close();

        List<FileSlice> slices = fileSplitter.split(file.getAbsolutePath(), 100);

        Assert.assertEquals(1, slices.size());
        Assert.assertEquals(0, slices.get(0).getStart());
        Assert.assertEquals(0, slices.get(0).getEnd());
    }

    @Test
    public void testError() throws Exception {
        try {
            fileSplitter.split("filePath", 100);
            Assert.fail();
        } catch (RdfFileException e) {
            Assert.assertEquals(RdfErrorEnum.NOT_EXSIT, e.getErrorEnum());
        }
    }

    @After
    public void after() throws Exception {
        tf.delete();
    }
}
