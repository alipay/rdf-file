package com.alipay.rdf.file.split;

import com.alipay.rdf.file.exception.RdfErrorEnum;
import com.alipay.rdf.file.exception.RdfFileException;
import com.alipay.rdf.file.function.ColumnFunction;
import com.alipay.rdf.file.loader.ProtocolLoader;
import com.alipay.rdf.file.meta.FileMeta;
import com.alipay.rdf.file.model.FileConfig;
import com.alipay.rdf.file.model.FileSlice;
import com.alipay.rdf.file.model.StorageConfig;
import com.alipay.rdf.file.protocol.ProtocolDefinition;
import com.alipay.rdf.file.protocol.RowDefinition;
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

import java.io.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 对文件体进行分片
 * 
 * @author hongwei.quhw
 * @version $Id: NasSplitBodyTest.java, v 0.1 2017年8月1日 上午11:31:24 hongwei.quhw Exp $
 */
public class NasSplitBodyTest {
    private static final String             templatePath = "/split/headslice/test.json";
    private Map<String, ProtocolDefinition> PD_CACHE     = null;
    private TemporaryFolderUtil             tf           = new TemporaryFolderUtil();
    private RdfFileSplitterSpi              fileSplitter = new NasFileSliceSplitter();
    private RdfFileStorageSpi               storage      = new FileNasStorage();

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() throws Exception {
        Field field = ProtocolLoader.class.getDeclaredField("PD_CACHE");
        field.setAccessible(true);
        PD_CACHE = (Map<String, ProtocolDefinition>) field.get(ProtocolLoader.class);
        RdfFileLogUtil.common = new TestLog();
        tf.create();
    }

    @Test
    public void testGetBodyEmptyContent() throws Exception {
        ProtocolDefinition pd = new ProtocolDefinition();

        RowDefinition hrd = new RowDefinition();
        hrd.setOutput(new ColumnFunction() {
            @Override
            public int rowsAffected(RowDefinition rd, FileMeta fileMeta) {
                return 5;
            }
        });
        List<RowDefinition> hrds = new ArrayList<RowDefinition>();
        hrds.add(hrd);
        pd.setHeads(hrds);

        RowDefinition trd = new RowDefinition();
        trd.setOutput(new ColumnFunction() {
            @Override
            public int rowsAffected(RowDefinition rd, FileMeta fileMeta) {
                return 2;
            }
        });
        List<RowDefinition> trds = new ArrayList<RowDefinition>();
        trds.add(trd);
        pd.setTails(trds);
        pd.setName("test");
        PD_CACHE.put(pd.getName(), pd);

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

        FileConfig config = new FileConfig(path, templatePath, new StorageConfig("nas"));
        List<FileSlice> slices = fileSplitter.getBodySlices(config, 300);

        Assert.assertEquals(1, slices.size());

        Assert.assertEquals(0, slices.get(0).getLength());
    }

    @Test
    public void testGetBody() throws Exception {
        ProtocolDefinition pd = new ProtocolDefinition();

        RowDefinition hrd = new RowDefinition();
        hrd.setOutput(new ColumnFunction() {
            @Override
            public int rowsAffected(RowDefinition rd, FileMeta fileMeta) {
                return 1;
            }
        });
        List<RowDefinition> hrds = new ArrayList<RowDefinition>();
        hrds.add(hrd);
        pd.setHeads(hrds);

        RowDefinition trd = new RowDefinition();
        trd.setOutput(new ColumnFunction() {
            @Override
            public int rowsAffected(RowDefinition rd, FileMeta fileMeta) {
                return 2;
            }
        });
        List<RowDefinition> trds = new ArrayList<RowDefinition>();
        trds.add(trd);
        pd.setTails(trds);
        pd.setName("test");
        PD_CACHE.put(pd.getName(), pd);

        String content = "总笔数:100|总金额:300.03\r\n"
                         + "流水号|基金公司订单号|订单申请时间|普通日期|普通日期时间|普通数字|金额|年龄|长整型|布尔值|备注\r\n"
                         + "seq_0|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r"
                         + "seq_1|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\n"
                         + "seq_2|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n"
                         + "seq_3|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r"
                         + "seq_4|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n\r\r\r\n\n";

        File file = new File(tf.getRoot(), "test.txt");
        OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
        writer.write(content);
        writer.close();

        String path = file.getAbsolutePath();

        FileConfig config = new FileConfig(path, templatePath, new StorageConfig("nas"));
        List<FileSlice> slices = fileSplitter.getBodySlices(config, 300);

        Assert.assertEquals(2, slices.size());
        FileSlice slice = slices.get(0);
        InputStream is = storage.getInputStream(path, slice.getStart(), slice.getLength());
        byte[] bs = StreamUtil.read(is, (int) slice.getLength());
        System.out.println(new String(bs));
        Assert.assertEquals("流水号|基金公司订单号|订单申请时间|普通日期|普通日期时间|普通数字|金额|年龄|长整型|布尔值|备注\r\n"
                            + "seq_0|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r"
                            + "seq_1|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\n",
            new String(bs, "UTF-8"));

        slice = slices.get(1);
        is = storage.getInputStream(path, slice.getStart(), slice.getLength());
        bs = StreamUtil.read(is, (int) slice.getLength());
        Assert.assertEquals(
            "seq_2|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n",
            new String(bs, "UTF-8"));
    }

    @Test
    public void testNoHead() throws Exception {
        ProtocolDefinition pd = new ProtocolDefinition();
        RowDefinition rd = new RowDefinition();
        rd.setOutput(new ColumnFunction() {
            @Override
            public int rowsAffected(RowDefinition rd, FileMeta fileMeta) {
                return 2;
            }
        });
        List<RowDefinition> rds = new ArrayList<RowDefinition>();
        rds.add(rd);
        pd.setTails(rds);
        pd.setName("test");
        PD_CACHE.put(pd.getName(), pd);

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

        FileConfig config = new FileConfig(path, templatePath, new StorageConfig("nas"));
        List<FileSlice> slices = fileSplitter.getBodySlices(config, 300);

        FileSlice slice = slices.get(0);
        InputStream is = storage.getInputStream(path, slice.getStart(), slice.getLength());
        byte[] bs = StreamUtil.read(is, (int) slice.getLength());
        System.out.println(new String(bs));
        Assert.assertEquals(
            "总笔数:100|总金额:300.03\r\n" + "流水号|基金公司订单号|订单申请时间|普通日期|普通日期时间|普通数字|金额|年龄|长整型|布尔值|备注\r\n"
                            + "seq_0|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r"
                            + "seq_1|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\n",
            new String(bs));

        slice = slices.get(1);
        is = storage.getInputStream(path, slice.getStart(), slice.getLength());
        bs = StreamUtil.read(is, (int) slice.getLength());
        Assert.assertEquals(
            "seq_2|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n",
            new String(bs));
    }

    @Test
    public void testNotail() throws Exception {
        ProtocolDefinition pd = new ProtocolDefinition();
        RowDefinition rd = new RowDefinition();
        rd.setOutput(new ColumnFunction() {
            @Override
            public int rowsAffected(RowDefinition rd, FileMeta fileMeta) {
                return 2;
            }
        });
        List<RowDefinition> rds = new ArrayList<RowDefinition>();
        rds.add(rd);
        pd.setHeads(rds);
        pd.setName("test");
        PD_CACHE.put(pd.getName(), pd);

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

        FileConfig config = new FileConfig(path, templatePath, new StorageConfig("nas"));
        List<FileSlice> slices = fileSplitter.getBodySlices(config, 300);

        FileSlice slice = slices.get(0);
        InputStream is = storage.getInputStream(path, slice.getStart(), slice.getLength());
        byte[] bs = StreamUtil.read(is, (int) slice.getLength());
        System.out.println(new String(bs));
        Assert.assertEquals(
            "seq_0|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r"
                            + "seq_1|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\n"
                            + "seq_2|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n"
                            + "seq_3|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r",
            new String(bs));

        slice = slices.get(1);
        is = storage.getInputStream(path, slice.getStart(), slice.getLength());
        bs = StreamUtil.read(is, (int) slice.getLength());
        Assert.assertEquals(
            "seq_4|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n\r\r\r\n\n",
            new String(bs));
    }

    @Test
    public void testAllFileBody() throws Exception {
        ProtocolDefinition pd = new ProtocolDefinition();
        pd.setName("test");
        PD_CACHE.put(pd.getName(), pd);

        String content = "总笔数:100|总金额:300.03\r\n"
                         + "流水号|基金公司订单号|订单申请时间|普通日期|普通日期时间|普通数字|金额|年龄|长整型|布尔值|备注\r\n"
                         + "seq_0|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r"
                         + "seq_1|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\n"
                         + "seq_2|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n"
                         + "seq_3|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r"
                         + "seq_4|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n\r\r\r\n\n";

        File file = new File(tf.getRoot(), "test.txt");
        OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
        writer.write(content);
        writer.close();

        String path = file.getAbsolutePath();

        FileConfig config = new FileConfig(path, templatePath, new StorageConfig("nas"));
        List<FileSlice> slices = fileSplitter.getBodySlices(config, 300);

        Assert.assertEquals(2, slices.size());
        FileSlice slice = slices.get(0);
        InputStream is = storage.getInputStream(path, slice.getStart(), slice.getLength());
        byte[] bs = StreamUtil.read(is, (int) slice.getLength());
        Assert.assertEquals(
            "总笔数:100|总金额:300.03\r\n" + "流水号|基金公司订单号|订单申请时间|普通日期|普通日期时间|普通数字|金额|年龄|长整型|布尔值|备注\r\n"
                            + "seq_0|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r"
                            + "seq_1|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\n",
            new String(bs, "UTF-8"));

        slice = slices.get(1);
        is = storage.getInputStream(path, slice.getStart(), slice.getLength());
        bs = StreamUtil.read(is, (int) slice.getLength());
        Assert.assertEquals(
            "seq_2|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n"
                            + "seq_3|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r"
                            + "seq_4|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n\r\r\r\n\n",
            new String(bs, "UTF-8"));
    }

    @Test
    public void testEmptyFile() throws Exception {
        File file = new File(tf.getRoot(), "test.txt");
        FileWriter writer = new FileWriter(file);
        writer.write("");
        writer.close();

        FileConfig config = new FileConfig(file.getAbsolutePath(), templatePath,
            new StorageConfig("nas"));

        List<FileSlice> slices = fileSplitter.getBodySlices(config, 100);

        Assert.assertEquals(0, slices.get(0).getStart());
        Assert.assertEquals(0, slices.get(0).getEnd());
    }

    @Test
    public void testError() throws Exception {
        FileConfig config = new FileConfig("filePath", templatePath, new StorageConfig("nas"));
        try {
            fileSplitter.getBodySlices(config, 0);
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
