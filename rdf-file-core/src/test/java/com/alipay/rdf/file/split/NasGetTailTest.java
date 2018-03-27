package com.alipay.rdf.file.split;

import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

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

import junit.framework.Assert;

/**
 * 测试获取文件尾
 * 
 * @author hongwei.quhw
 * @version $Id: NasGetTailTest.java, v 0.1 2017年7月31日 下午4:11:35 hongwei.quhw Exp $
 */
public class NasGetTailTest {
    private static final String             templatePath = "/split/tailslice/test.json";
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
     * 行尾有空行
     */
    @Test
    public void testMixLineBreak() throws Exception {
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
        pd.setTails(rds);
        PD_CACHE.put(pd.getName(), pd);

        String content = "总笔数:100|总金额:300.03\r"
                         + "流水号|基金公司订单号|订单申请时间|普通日期|普通日期时间|普通数字|金额|年龄|长整型|布尔值|备注\r\n"
                         + "seq_0|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\n"
                         + "seq_1|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n"
                         + "seq_2|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\n"
                         + "seq_3|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r"
                         + "seq_4|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n\n\r\r\r\n\n";
        File file = new File(tf.getRoot(), "test.txt");
        FileWriter writer = new FileWriter(file);
        writer.write(content);
        writer.close();

        FileConfig config = new FileConfig(file.getAbsolutePath(), templatePath,
            new StorageConfig("nas"));

        FileSlice tailSlice = fileSplitter.getTailSlice(config);
        InputStream is = storage.getInputStream(file.getAbsolutePath(), tailSlice.getStart(),
            tailSlice.getLength());
        byte[] bs = StreamUtil.read(is, (int) tailSlice.getLength());
        Assert.assertEquals(content, new String(bs));

    }

    /**
     * 行尾有空行
     */
    @Test
    public void testEndWithEmptyLine() throws Exception {
        ProtocolDefinition pd = new ProtocolDefinition();
        pd.setName("test");
        RowDefinition rd = new RowDefinition();
        rd.setOutput(new ColumnFunction() {
            @Override
            public int rowsAffected(RowDefinition rd, FileMeta fileMeta) {
                return 1;
            }
        });
        List<RowDefinition> rds = new ArrayList<RowDefinition>();
        rds.add(rd);
        pd.setTails(rds);
        PD_CACHE.put(pd.getName(), pd);

        String content = "总笔数:100|总金额:300.03\r\n"
                         + "流水号|基金公司订单号|订单申请时间|普通日期|普通日期时间|普通数字|金额|年龄|长整型|布尔值|备注\r\n"
                         + "seq_0|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n"
                         + "seq_1|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n"
                         + "seq_2|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n"
                         + "seq_3|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n"
                         + "seq_4|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n\n\r\r\r\n\n";
        File file = new File(tf.getRoot(), "test.txt");
        FileWriter writer = new FileWriter(file);
        writer.write(content);
        writer.close();

        FileConfig config = new FileConfig(file.getAbsolutePath(), templatePath,
            new StorageConfig("nas"));

        FileSlice tailSlice = fileSplitter.getTailSlice(config);
        InputStream is = storage.getInputStream(file.getAbsolutePath(), tailSlice.getStart(),
            tailSlice.getLength());
        byte[] bs = StreamUtil.read(is, (int) tailSlice.getLength());
        Assert.assertEquals(
            "seq_4|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n\n\r\r\r\n\n",
            new String(bs));

    }

    /**
     * 行尾有换行符
     */
    @Test
    public void testEndWithLineBreak() throws Exception {
        ProtocolDefinition pd = new ProtocolDefinition();
        pd.setName("test");
        RowDefinition rd = new RowDefinition();
        rd.setOutput(new ColumnFunction() {
            @Override
            public int rowsAffected(RowDefinition rd, FileMeta fileMeta) {
                return 1;
            }
        });
        List<RowDefinition> rds = new ArrayList<RowDefinition>();
        rds.add(rd);
        pd.setTails(rds);
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

        FileSlice tailSlice = fileSplitter.getTailSlice(config);
        InputStream is = storage.getInputStream(file.getAbsolutePath(), tailSlice.getStart(),
            tailSlice.getLength());
        byte[] bs = StreamUtil.read(is, (int) tailSlice.getLength());
        Assert.assertEquals(
            "seq_4|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n",
            new String(bs));

    }

    /**
     * 行尾没有换行符
     */
    @Test
    public void testNoLineBreak() throws Exception {
        ProtocolDefinition pd = new ProtocolDefinition();
        pd.setName("test");
        RowDefinition rd = new RowDefinition();
        rd.setOutput(new ColumnFunction() {
            @Override
            public int rowsAffected(RowDefinition rd, FileMeta fileMeta) {
                return 1;
            }
        });
        List<RowDefinition> rds = new ArrayList<RowDefinition>();
        rds.add(rd);
        pd.setTails(rds);
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

        FileSlice tailSlice = fileSplitter.getTailSlice(config);
        InputStream is = storage.getInputStream(file.getAbsolutePath(), tailSlice.getStart(),
            tailSlice.getLength());
        byte[] bs = StreamUtil.read(is, (int) tailSlice.getLength());
        Assert.assertEquals(
            "seq_4|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1",
            new String(bs));

    }

    /**
     * 行尾没有换行符
     * 7行包括所有数据
     */
    @Test
    public void testNoLineBreak2() throws Exception {
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
        pd.setTails(rds);
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

        FileSlice tailSlice = fileSplitter.getTailSlice(config);
        InputStream is = storage.getInputStream(file.getAbsolutePath(), tailSlice.getStart(),
            tailSlice.getLength());
        byte[] bs = StreamUtil.read(is, (int) tailSlice.getLength());
        Assert.assertEquals(content, new String(bs));

    }

    /**
     * 行尾没有换行符
     * 7行包括所有数据
     * 
     * 行头是换行符
     */
    @Test
    public void testNoLineBreak3() throws Exception {
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
        pd.setTails(rds);
        PD_CACHE.put(pd.getName(), pd);

        String content = "\r\n流水号|基金公司订单号|订单申请时间|普通日期|普通日期时间|普通数字|金额|年龄|长整型|布尔值|备注\r\n"
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

        FileSlice tailSlice = fileSplitter.getTailSlice(config);
        InputStream is = storage.getInputStream(file.getAbsolutePath(), tailSlice.getStart(),
            tailSlice.getLength());
        byte[] bs = StreamUtil.read(is, (int) tailSlice.getLength());
        Assert.assertEquals(content, new String(bs));

    }

    @Test
    public void testEmptyFile() throws Exception {
        File file = new File(tf.getRoot(), "test.txt");
        FileWriter writer = new FileWriter(file);
        writer.write("");
        writer.close();

        FileConfig config = new FileConfig(file.getAbsolutePath(), templatePath,
            new StorageConfig("nas"));

        FileSlice tailSlice = fileSplitter.getTailSlice(config);

        Assert.assertEquals(0, tailSlice.getStart());
        Assert.assertEquals(0, tailSlice.getEnd());
    }

    @Test
    public void testError() throws Exception {
        FileConfig config = new FileConfig("filePath", templatePath, new StorageConfig("nas"));
        try {
            fileSplitter.getTailSlice(config);
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
