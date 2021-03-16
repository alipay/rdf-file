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

import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 获取文件头
 * 
 * @author hongwei.quhw
 * @version $Id: NasGetHeadTest.java, v 0.1 2017年7月31日 下午7:31:19 hongwei.quhw Exp $
 */
public class NasGetHeadTest {
    private static final String             templatePath = "/split/headslice/test.json";
    private Map<String, ProtocolDefinition> PD_CACHE     = null;
    private TemporaryFolderUtil             tf           = new TemporaryFolderUtil();
    private RdfFileSplitterSpi                 fileSplitter = new NasFileSliceSplitter();
    private RdfFileStorageSpi                  storage      = new FileNasStorage();

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() throws Exception {
        Field field = ProtocolLoader.class.getDeclaredField("PD_CACHE");
        field.setAccessible(true);
        PD_CACHE = (Map<String, ProtocolDefinition>) field.get(ProtocolLoader.class);
        RdfFileLogUtil.common = new TestLog();
        tf.create();
    }

    /**
     * 两个行头
     */
    @Test
    public void testTwoLines() throws Exception {
        ProtocolDefinition pd = new ProtocolDefinition();
        pd.setName("test");
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
        PD_CACHE.put(pd.getName(), pd);

        String content = "总笔数:100|总金额:300.03\r"
                         + "流水号|基金公司订单号|订单申请时间|普通日期|普通日期时间|普通数字|金额|年龄|长整型|布尔值|备注\r\n"
                         + "seq_0|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n"
                         + "seq_1|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n"
                         + "seq_2|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n"
                         + "seq_3|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n"
                         + "seq_4|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1";
        File file = new File(tf.getRoot(), "test.txt");
        FileWriter writer = new FileWriter(file);
        writer.write(content);
        writer.close();

        FileConfig config = new FileConfig(file.getAbsolutePath(), templatePath,
            new StorageConfig("nas"));

        FileSlice headSlice = fileSplitter.getHeadSlice(config);
        InputStream is = storage.getInputStream(file.getAbsolutePath(), headSlice.getStart(),
            headSlice.getLength());
        byte[] bs = StreamUtil.read(is, (int) headSlice.getLength());
        Assert.assertEquals(
            "总笔数:100|总金额:300.03\r" + "流水号|基金公司订单号|订单申请时间|普通日期|普通日期时间|普通数字|金额|年龄|长整型|布尔值|备注\r\n",
            new String(bs));

    }

    /**
     * 两个行头
     */
    @Test
    public void testTwoLines2() throws Exception {
        ProtocolDefinition pd = new ProtocolDefinition();
        pd.setName("test");
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
        PD_CACHE.put(pd.getName(), pd);

        String content = "总笔数:100|总金额:300.03\r"
                         + "流水号|基金公司订单号|订单申请时间|普通日期|普通日期时间|普通数字|金额|年龄|长整型|布尔值|备注\n"
                         + "seq_0|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n"
                         + "seq_1|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n"
                         + "seq_2|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n"
                         + "seq_3|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n"
                         + "seq_4|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1";
        File file = new File(tf.getRoot(), "test.txt");
        FileWriter writer = new FileWriter(file);
        writer.write(content);
        writer.close();

        FileConfig config = new FileConfig(file.getAbsolutePath(), templatePath,
            new StorageConfig("nas"));

        FileSlice headSlice = fileSplitter.getHeadSlice(config);
        InputStream is = storage.getInputStream(file.getAbsolutePath(), headSlice.getStart(),
            headSlice.getLength());
        byte[] bs = StreamUtil.read(is, (int) headSlice.getLength());
        Assert.assertEquals(
            "总笔数:100|总金额:300.03\r" + "流水号|基金公司订单号|订单申请时间|普通日期|普通日期时间|普通数字|金额|年龄|长整型|布尔值|备注\n",
            new String(bs));

    }

    /**
     * 所有数据都是头
     * 不是以换行符结束
     */
    @Test
    public void testAllHeadNotEndWithLineBreak() throws Exception {
        ProtocolDefinition pd = new ProtocolDefinition();
        pd.setName("test");
        RowDefinition rd = new RowDefinition();
        rd.setOutput(new ColumnFunction() {
            @Override
            public int rowsAffected(RowDefinition rd, FileMeta fileMeta) {
                return 7;
            }
        });
        List<RowDefinition> rds = new ArrayList<RowDefinition>();
        rds.add(rd);
        pd.setHeads(rds);
        PD_CACHE.put(pd.getName(), pd);

        String content = "总笔数:100|总金额:300.03\r\n"
                         + "流水号|基金公司订单号|订单申请时间|普通日期|普通日期时间|普通数字|金额|年龄|长整型|布尔值|备注\r\n"
                         + "seq_0|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n"
                         + "seq_1|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n"
                         + "seq_2|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n"
                         + "seq_3|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n"
                         + "seq_4|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1";
        File file = new File(tf.getRoot(), "test.txt");
        FileWriter writer = new FileWriter(file);
        writer.write(content);
        writer.close();

        FileConfig config = new FileConfig(file.getAbsolutePath(), templatePath,
            new StorageConfig("nas"));

        FileSlice headSlice = fileSplitter.getHeadSlice(config);
        InputStream is = storage.getInputStream(file.getAbsolutePath(), headSlice.getStart(),
            headSlice.getLength());
        byte[] bs = StreamUtil.read(is, (int) headSlice.getLength());
        Assert.assertEquals(content, new String(bs));

    }

    /**
     * 所有数据都是头
     * 以换行符结束 \r
     */
    @Test
    public void testAllHeadEndWithLineBreak() throws Exception {
        ProtocolDefinition pd = new ProtocolDefinition();
        pd.setName("test");
        RowDefinition rd = new RowDefinition();
        rd.setOutput(new ColumnFunction() {
            @Override
            public int rowsAffected(RowDefinition rd, FileMeta fileMeta) {
                return 7;
            }
        });
        List<RowDefinition> rds = new ArrayList<RowDefinition>();
        rds.add(rd);
        pd.setHeads(rds);
        PD_CACHE.put(pd.getName(), pd);

        String content = "总笔数:100|总金额:300.03\r\n"
                         + "流水号|基金公司订单号|订单申请时间|普通日期|普通日期时间|普通数字|金额|年龄|长整型|布尔值|备注\r\n"
                         + "seq_0|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n"
                         + "seq_1|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n"
                         + "seq_2|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n"
                         + "seq_3|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n"
                         + "seq_4|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r";
        File file = new File(tf.getRoot(), "test.txt");
        FileWriter writer = new FileWriter(file);
        writer.write(content);
        writer.close();

        FileConfig config = new FileConfig(file.getAbsolutePath(), templatePath,
            new StorageConfig("nas"));

        FileSlice headSlice = fileSplitter.getHeadSlice(config);
        InputStream is = storage.getInputStream(file.getAbsolutePath(), headSlice.getStart(),
            headSlice.getLength());
        byte[] bs = StreamUtil.read(is, (int) headSlice.getLength());
        Assert.assertEquals(content, new String(bs));

    }

    /**
     * 所有数据都是头
     * 以换行符结束 \n
     */
    @Test
    public void testAllHeadEndWithLineBreak2() throws Exception {
        ProtocolDefinition pd = new ProtocolDefinition();
        pd.setName("test");
        RowDefinition rd = new RowDefinition();
        rd.setOutput(new ColumnFunction() {
            @Override
            public int rowsAffected(RowDefinition rd, FileMeta fileMeta) {
                return 7;
            }
        });
        List<RowDefinition> rds = new ArrayList<RowDefinition>();
        rds.add(rd);
        pd.setHeads(rds);
        PD_CACHE.put(pd.getName(), pd);

        String content = "总笔数:100|总金额:300.03\r\n"
                         + "流水号|基金公司订单号|订单申请时间|普通日期|普通日期时间|普通数字|金额|年龄|长整型|布尔值|备注\r\n"
                         + "seq_0|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n"
                         + "seq_1|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n"
                         + "seq_2|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n"
                         + "seq_3|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n"
                         + "seq_4|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\n";
        File file = new File(tf.getRoot(), "test.txt");
        FileWriter writer = new FileWriter(file);
        writer.write(content);
        writer.close();

        FileConfig config = new FileConfig(file.getAbsolutePath(), templatePath,
            new StorageConfig("nas"));

        FileSlice headSlice = fileSplitter.getHeadSlice(config);
        InputStream is = storage.getInputStream(file.getAbsolutePath(), headSlice.getStart(),
            headSlice.getLength());
        byte[] bs = StreamUtil.read(is, (int) headSlice.getLength());
        Assert.assertEquals(content, new String(bs));
    }

    /**
     * 所有数据都是头
     * 以换行符结束 \r\n
     */
    @Test
    public void testAllHeadEndWithLineBreak3() throws Exception {
        ProtocolDefinition pd = new ProtocolDefinition();
        pd.setName("test");
        RowDefinition rd = new RowDefinition();
        rd.setOutput(new ColumnFunction() {
            @Override
            public int rowsAffected(RowDefinition rd, FileMeta fileMeta) {
                return 7;
            }
        });
        List<RowDefinition> rds = new ArrayList<RowDefinition>();
        rds.add(rd);
        pd.setHeads(rds);
        PD_CACHE.put(pd.getName(), pd);

        String content = "总笔数:100|总金额:300.03\r\n"
                         + "流水号|基金公司订单号|订单申请时间|普通日期|普通日期时间|普通数字|金额|年龄|长整型|布尔值|备注\r\n"
                         + "seq_0|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n"
                         + "seq_1|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n"
                         + "seq_2|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n"
                         + "seq_3|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n"
                         + "seq_4|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n";
        File file = new File(tf.getRoot(), "test.txt");
        FileWriter writer = new FileWriter(file);
        writer.write(content);
        writer.close();

        FileConfig config = new FileConfig(file.getAbsolutePath(), templatePath,
            new StorageConfig("nas"));

        FileSlice headSlice = fileSplitter.getHeadSlice(config);
        InputStream is = storage.getInputStream(file.getAbsolutePath(), headSlice.getStart(),
            headSlice.getLength());
        byte[] bs = StreamUtil.read(is, (int) headSlice.getLength());
        Assert.assertEquals(content, new String(bs));

    }

    /**
     * 所有数据都是头
     * 各种换行符
     */
    @Test
    public void testAllHeadMixWithLineBreak() throws Exception {
        ProtocolDefinition pd = new ProtocolDefinition();
        pd.setName("test");
        RowDefinition rd = new RowDefinition();
        rd.setOutput(new ColumnFunction() {
            @Override
            public int rowsAffected(RowDefinition rd, FileMeta fileMeta) {
                return 7;
            }
        });
        List<RowDefinition> rds = new ArrayList<RowDefinition>();
        rds.add(rd);
        pd.setHeads(rds);
        PD_CACHE.put(pd.getName(), pd);

        String content = "总笔数:100|总金额:300.03\n"
                         + "流水号|基金公司订单号|订单申请时间|普通日期|普通日期时间|普通数字|金额|年龄|长整型|布尔值|备注\r\n"
                         + "seq_0|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r"
                         + "seq_1|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n"
                         + "seq_2|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\n"
                         + "seq_3|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r"
                         + "seq_4|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\r\n\n\n\r\r";
        File file = new File(tf.getRoot(), "test.txt");
        FileWriter writer = new FileWriter(file);
        writer.write(content);
        writer.close();

        FileConfig config = new FileConfig(file.getAbsolutePath(), templatePath,
            new StorageConfig("nas"));

        FileSlice headSlice = fileSplitter.getHeadSlice(config);
        InputStream is = storage.getInputStream(file.getAbsolutePath(), headSlice.getStart(),
            headSlice.getLength());
        byte[] bs = StreamUtil.read(is, (int) headSlice.getLength());
        Assert.assertEquals(
            "总笔数:100|总金额:300.03\n" + "流水号|基金公司订单号|订单申请时间|普通日期|普通日期时间|普通数字|金额|年龄|长整型|布尔值|备注\r\n"
                            + "seq_0|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r"
                            + "seq_1|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n"
                            + "seq_2|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\n"
                            + "seq_3|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r"
                            + "seq_4|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r",
            new String(bs));

    }

    @Test
    public void testEmptyFile() throws Exception {
        File file = new File(tf.getRoot(), "test.txt");
        FileWriter writer = new FileWriter(file);
        writer.write("");
        writer.close();

        FileConfig config = new FileConfig(file.getAbsolutePath(), templatePath,
            new StorageConfig("nas"));

        FileSlice headSlice = fileSplitter.getHeadSlice(config);

        Assert.assertEquals(0, headSlice.getStart());
        Assert.assertEquals(0, headSlice.getEnd());
    }

    @Test
    public void testError() throws Exception {
        FileConfig config = new FileConfig("filePath", templatePath, new StorageConfig("nas"));
        try {
            fileSplitter.getHeadSlice(config);
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
