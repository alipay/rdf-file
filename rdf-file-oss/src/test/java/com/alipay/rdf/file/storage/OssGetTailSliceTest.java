package com.alipay.rdf.file.storage;

import com.alipay.rdf.file.function.ColumnFunction;
import com.alipay.rdf.file.loader.ProtocolLoader;
import com.alipay.rdf.file.meta.FileMeta;
import com.alipay.rdf.file.model.FileConfig;
import com.alipay.rdf.file.model.FileSlice;
import com.alipay.rdf.file.model.StorageConfig;
import com.alipay.rdf.file.protocol.ProtocolDefinition;
import com.alipay.rdf.file.protocol.RowDefinition;
import com.alipay.rdf.file.util.OssTestUtil;
import com.alipay.rdf.file.util.OssUtil;
import com.alipay.rdf.file.util.RdfFileLogUtil;
import com.alipay.rdf.file.util.TestLog;
import com.aliyun.oss.OSSClient;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 
 * 
 * @author hongwei.quhw
 * @version $Id: OssGetTailSliceTest.java, v 0.1 2017年7月23日 下午7:44:32 hongwei.quhw Exp $
 */
@SuppressWarnings("unchecked")
public class OssGetTailSliceTest {
    private static final String             RDF_PATH = "rdf/rdf-file/test/gettailslice/";
    private Map<String, ProtocolDefinition> PD_CACHE = null;
    private OSSClient                       ossClient;
    private String                          bucketName;
    private StorageConfig                   storageConfig;
    private FileOssStorage                  ossStorage;
    private OssFileSliceSplitter            splitter;

    @Before
    public void setUp() throws Exception {
        Field field = ProtocolLoader.class.getDeclaredField("PD_CACHE");
        field.setAccessible(true);
        PD_CACHE = (Map<String, ProtocolDefinition>) field.get(ProtocolLoader.class);
        storageConfig = OssTestUtil.geStorageConfig();
        OssConfig config = (OssConfig) storageConfig.getParam(OssConfig.OSS_STORAGE_CONFIG_KEY);
        ossClient = new OSSClient(config.getEndpoint(), config.getAccessKeyId(),
            config.getAccessKeySecret());
        bucketName = config.getBucketName();

        ossStorage = new FileOssStorage();
        ossStorage.init(storageConfig);

        splitter = new OssFileSliceSplitter();
        splitter.init(ossStorage);

        RdfFileLogUtil.common = new TestLog();
    }

    @Test
    public void testOneLine() throws Exception {
        ProtocolDefinition pd = new ProtocolDefinition();
        pd.setName("test");
        RowDefinition rd = new RowDefinition();
        rd.setOutput(new ColumnFunction());
        List<RowDefinition> rds = new ArrayList<RowDefinition>();
        rds.add(rd);
        pd.setTails(rds);
        PD_CACHE.put(pd.getName(), pd);

        String file = "总笔数:100|总金额:300.03\r\n"
                      + "流水号|基金公司订单号|订单申请时间|普通日期|普通日期时间|普通数字|金额|年龄|长整型|布尔值|备注\r\n"
                      + "seq_0|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n"
                      + "seq_1|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n"
                      + "seq_2|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n"
                      + "seq_3|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n"
                      + "seq_4|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n";
        String path = RDF_PATH + "emptyline.txt";
        ossClient.putObject(bucketName, path, new ByteArrayInputStream(file.getBytes()));

        FileConfig config = new FileConfig(path, "/tailslice/test.json", storageConfig);
        FileSlice slice = splitter.getTailSlice(config);

        InputStream is = ossStorage.getInputStream(path, slice.getStart(), slice.getLength());
        byte[] bs = OssUtil.read(is, (int) slice.getLength());
        Assert.assertEquals(
            "seq_4|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n",
            new String(bs));

        //设置buffer 32
        System.out.println("----------------设置buffer 32-----------");
        OssUtil.OSS_READ_TAIL_BUFFER = 32;
        slice = splitter.getTailSlice(config);
        is = ossStorage.getInputStream(path, slice.getStart(), slice.getLength());
        bs = OssUtil.read(is, (int) slice.getLength());
        Assert.assertEquals(
            "seq_4|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n",
            new String(bs));

    }

    @Test
    public void testOneLine2() throws Exception {
        ProtocolDefinition pd = new ProtocolDefinition();
        pd.setName("test");
        RowDefinition rd = new RowDefinition();
        rd.setOutput(new ColumnFunction());
        List<RowDefinition> rds = new ArrayList<RowDefinition>();
        rds.add(rd);
        pd.setTails(rds);
        PD_CACHE.put(pd.getName(), pd);

        String file = "seq_4|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n";
        String path = RDF_PATH + "emptyline.txt";
        ossClient.putObject(bucketName, path, new ByteArrayInputStream(file.getBytes()));

        FileConfig config = new FileConfig(path, "/tailslice/test.json", storageConfig);
        FileSlice slice = splitter.getTailSlice(config);

        InputStream is = ossStorage.getInputStream(path, slice.getStart(), slice.getLength());
        byte[] bs = OssUtil.read(is, (int) slice.getLength());
        Assert.assertEquals(
            "seq_4|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n",
            new String(bs));

        System.out.println("----------------设置buffer 64-----------");
        OssUtil.OSS_READ_TAIL_BUFFER = 64;
        slice = splitter.getTailSlice(config);
        is = ossStorage.getInputStream(path, slice.getStart(), slice.getLength());
        bs = OssUtil.read(is, (int) slice.getLength());
        Assert.assertEquals(
            "seq_4|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n",
            new String(bs));
    }

    @Test
    public void testOneLine3() throws Exception {
        ProtocolDefinition pd = new ProtocolDefinition();
        pd.setName("test");
        RowDefinition rd = new RowDefinition();
        rd.setOutput(new ColumnFunction());
        List<RowDefinition> rds = new ArrayList<RowDefinition>();
        rds.add(rd);
        pd.setTails(rds);
        PD_CACHE.put(pd.getName(), pd);

        String file = "\r\n";
        String path = RDF_PATH + "emptyline.txt";
        ossClient.putObject(bucketName, path, new ByteArrayInputStream(file.getBytes()));

        FileConfig config = new FileConfig(path, "/tailslice/test.json", storageConfig);
        FileSlice slice = splitter.getTailSlice(config);

        InputStream is = ossStorage.getInputStream(path, slice.getStart(), slice.getLength());
        byte[] bs = OssUtil.read(is, (int) slice.getLength());
        Assert.assertEquals("\r\n", new String(bs));
    }

    @Test
    public void testEmpty() throws Exception {
        ProtocolDefinition pd = new ProtocolDefinition();
        pd.setName("test");
        RowDefinition rd = new RowDefinition();
        rd.setOutput(new ColumnFunction());
        List<RowDefinition> rds = new ArrayList<RowDefinition>();
        rds.add(rd);
        pd.setTails(rds);
        PD_CACHE.put(pd.getName(), pd);

        String file = "";
        String path = RDF_PATH + "emptyline.txt";
        ossClient.putObject(bucketName, path, new ByteArrayInputStream(file.getBytes()));

        FileConfig config = new FileConfig(path, "/tailslice/test.json", storageConfig);
        FileSlice slice = splitter.getTailSlice(config);
        Assert.assertEquals(0, slice.getStart());
        Assert.assertEquals(0, slice.getEnd());
    }

    @Test
    public void testThreeLine() throws Exception {
        ProtocolDefinition pd = new ProtocolDefinition();
        pd.setName("test");
        RowDefinition rd = new RowDefinition();
        rd.setOutput(new ColumnFunction() {
            @Override
            public int rowsAffected(RowDefinition rd, FileMeta fileMeta) {
                return 3;
            }
        });
        List<RowDefinition> rds = new ArrayList<RowDefinition>();
        rds.add(rd);
        pd.setTails(rds);
        PD_CACHE.put(pd.getName(), pd);

        String file = "总笔数:100|总金额:300.03\r\n"
                      + "流水号|基金公司订单号|订单申请时间|普通日期|普通日期时间|普通数字|金额|年龄|长整型|布尔值|备注\r\n"
                      + "seq_0|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n"
                      + "seq_1|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n"
                      + "seq_2|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n"
                      + "seq_3|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n"
                      + "seq_4|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n";
        String path = RDF_PATH + "emptyline.txt";
        ossClient.putObject(bucketName, path, new ByteArrayInputStream(file.getBytes()));

        FileConfig config = new FileConfig(path, "/tailslice/test.json", storageConfig);
        FileSlice slice = splitter.getTailSlice(config);

        InputStream is = ossStorage.getInputStream(path, slice.getStart(), slice.getLength());
        byte[] bs = OssUtil.read(is, (int) slice.getLength());
        Assert.assertEquals(
            "seq_2|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n"
                            + "seq_3|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n"
                            + "seq_4|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n",
            new String(bs));

        System.out.println("----------------设置buffer 100-----------");
        OssUtil.OSS_READ_TAIL_BUFFER = 100;
        slice = splitter.getTailSlice(config);
        is = ossStorage.getInputStream(path, slice.getStart(), slice.getLength());
        bs = OssUtil.read(is, (int) slice.getLength());
        Assert.assertEquals(
            "seq_2|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n"
                            + "seq_3|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n"
                            + "seq_4|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n",
            new String(bs));

        System.out.println("----------------设置buffer 101-----------");
        OssUtil.OSS_READ_TAIL_BUFFER = 101;
        slice = splitter.getTailSlice(config);
        is = ossStorage.getInputStream(path, slice.getStart(), slice.getLength());
        bs = OssUtil.read(is, (int) slice.getLength());
        Assert.assertEquals(
            "seq_2|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n"
                            + "seq_3|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n"
                            + "seq_4|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n",
            new String(bs));

    }

    @Test
    public void testThreeLine2() throws Exception {
        ProtocolDefinition pd = new ProtocolDefinition();
        pd.setName("test");
        RowDefinition rd = new RowDefinition();
        rd.setOutput(new ColumnFunction() {
            @Override
            public int rowsAffected(RowDefinition rd, FileMeta fileMeta) {
                return 3;
            }
        });
        List<RowDefinition> rds = new ArrayList<RowDefinition>();
        rds.add(rd);
        pd.setTails(rds);
        PD_CACHE.put(pd.getName(), pd);

        String file = "seq_2|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n"
                      + "seq_3|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n"
                      + "seq_4|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n";
        String path = RDF_PATH + "emptyline.txt";
        ossClient.putObject(bucketName, path, new ByteArrayInputStream(file.getBytes()));

        FileConfig config = new FileConfig(path, "/tailslice/test.json", storageConfig);
        FileSlice slice = splitter.getTailSlice(config);

        InputStream is = ossStorage.getInputStream(path, slice.getStart(), slice.getLength());
        byte[] bs = OssUtil.read(is, (int) slice.getLength());
        Assert.assertEquals(
            "seq_2|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n"
                            + "seq_3|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n"
                            + "seq_4|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n",
            new String(bs));

        System.out.println("----------------设置buffer 100-----------");
        OssUtil.OSS_READ_TAIL_BUFFER = 100;
        slice = splitter.getTailSlice(config);
        is = ossStorage.getInputStream(path, slice.getStart(), slice.getLength());
        bs = OssUtil.read(is, (int) slice.getLength());
        Assert.assertEquals(
            "seq_2|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n"
                            + "seq_3|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n"
                            + "seq_4|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n",
            new String(bs));

        System.out.println("----------------设置buffer 101-----------");
        OssUtil.OSS_READ_TAIL_BUFFER = 101;
        slice = splitter.getTailSlice(config);
        is = ossStorage.getInputStream(path, slice.getStart(), slice.getLength());
        bs = OssUtil.read(is, (int) slice.getLength());
        Assert.assertEquals(
            "seq_2|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n"
                            + "seq_3|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n"
                            + "seq_4|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n",
            new String(bs));

    }

    @Test
    public void testThreeLine3() throws Exception {
        ProtocolDefinition pd = new ProtocolDefinition();
        pd.setName("test");
        RowDefinition rd = new RowDefinition();
        rd.setOutput(new ColumnFunction() {
            @Override
            public int rowsAffected(RowDefinition rd, FileMeta fileMeta) {
                return 3;
            }
        });
        List<RowDefinition> rds = new ArrayList<RowDefinition>();
        rds.add(rd);
        pd.setTails(rds);
        PD_CACHE.put(pd.getName(), pd);

        String file = "seq_2|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\n"
                      + "seq_3|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n"
                      + "seq_4|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\n";
        String path = RDF_PATH + "emptyline.txt";
        ossClient.putObject(bucketName, path, new ByteArrayInputStream(file.getBytes()));

        FileConfig config = new FileConfig(path, "/tailslice/test.json", storageConfig);
        FileSlice slice = splitter.getTailSlice(config);

        InputStream is = ossStorage.getInputStream(path, slice.getStart(), slice.getLength());
        byte[] bs = OssUtil.read(is, (int) slice.getLength());
        Assert.assertEquals(
            "seq_2|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\n"
                            + "seq_3|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n"
                            + "seq_4|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\n",
            new String(bs));

        System.out.println("----------------设置buffer 99-----------");
        OssUtil.OSS_READ_TAIL_BUFFER = 99;
        slice = splitter.getTailSlice(config);
        is = ossStorage.getInputStream(path, slice.getStart(), slice.getLength());
        bs = OssUtil.read(is, (int) slice.getLength());
        Assert.assertEquals(
            "seq_2|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\n"
                            + "seq_3|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n"
                            + "seq_4|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\n",
            new String(bs));

    }

    @Test
    public void testThreeLine4() throws Exception {
        ProtocolDefinition pd = new ProtocolDefinition();
        pd.setName("test");
        RowDefinition rd = new RowDefinition();
        rd.setOutput(new ColumnFunction() {
            @Override
            public int rowsAffected(RowDefinition rd, FileMeta fileMeta) {
                return 3;
            }
        });
        List<RowDefinition> rds = new ArrayList<RowDefinition>();
        rds.add(rd);
        pd.setTails(rds);
        PD_CACHE.put(pd.getName(), pd);

        String file = "seq_2|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r"
                      + "seq_3|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n"
                      + "seq_4|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r";
        String path = RDF_PATH + "emptyline.txt";
        ossClient.putObject(bucketName, path, new ByteArrayInputStream(file.getBytes()));

        FileConfig config = new FileConfig(path, "/tailslice/test.json", storageConfig);
        FileSlice slice = splitter.getTailSlice(config);

        InputStream is = ossStorage.getInputStream(path, slice.getStart(), slice.getLength());
        byte[] bs = OssUtil.read(is, (int) slice.getLength());
        Assert.assertEquals(
            "seq_2|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r"
                            + "seq_3|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n"
                            + "seq_4|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r",
            new String(bs));

        System.out.println("----------------设置buffer 99-----------");
        OssUtil.OSS_READ_TAIL_BUFFER = 99;
        slice = splitter.getTailSlice(config);
        is = ossStorage.getInputStream(path, slice.getStart(), slice.getLength());
        bs = OssUtil.read(is, (int) slice.getLength());
        Assert.assertEquals(
            "seq_2|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r"
                            + "seq_3|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n"
                            + "seq_4|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r",
            new String(bs));

    }

    @Test
    public void testThreeLine5() throws Exception {
        ProtocolDefinition pd = new ProtocolDefinition();
        pd.setName("test");
        RowDefinition rd = new RowDefinition();
        rd.setOutput(new ColumnFunction() {
            @Override
            public int rowsAffected(RowDefinition rd, FileMeta fileMeta) {
                return 3;
            }
        });
        List<RowDefinition> rds = new ArrayList<RowDefinition>();
        rds.add(rd);
        pd.setTails(rds);
        PD_CACHE.put(pd.getName(), pd);

        String file = "seq_2|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r"
                      + "seq_3|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r"
                      + "seq_4|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1";
        String path = RDF_PATH + "emptyline.txt";
        ossClient.putObject(bucketName, path, new ByteArrayInputStream(file.getBytes()));

        FileConfig config = new FileConfig(path, "/tailslice/test.json", storageConfig);
        FileSlice slice = splitter.getTailSlice(config);

        InputStream is = ossStorage.getInputStream(path, slice.getStart(), slice.getLength());
        byte[] bs = OssUtil.read(is, (int) slice.getLength());
        Assert.assertEquals(
            "seq_2|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r"
                            + "seq_3|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r"
                            + "seq_4|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1",
            new String(bs));

        System.out.println("----------------设置buffer 99-----------");
        OssUtil.OSS_READ_TAIL_BUFFER = 99;
        slice = splitter.getTailSlice(config);
        is = ossStorage.getInputStream(path, slice.getStart(), slice.getLength());
        bs = OssUtil.read(is, (int) slice.getLength());
        Assert.assertEquals(
            "seq_2|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r"
                            + "seq_3|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r"
                            + "seq_4|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1",
            new String(bs));

    }

    @Test
    public void testEmptyLine() throws Exception {
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
        pd.setTails(rds);
        PD_CACHE.put(pd.getName(), pd);

        String file = "seq_2|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n"
                      + "seq_3|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n"
                      + "seq_4|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1";
        String path = RDF_PATH + "emptyline.txt";
        ossClient.putObject(bucketName, path, new ByteArrayInputStream(file.getBytes()));

        FileConfig config = new FileConfig(path, "/tailslice/test.json", storageConfig);
        FileSlice slice = splitter.getTailSlice(config);

        InputStream is = ossStorage.getInputStream(path, slice.getStart(), slice.getLength());
        byte[] bs = OssUtil.read(is, (int) slice.getLength());
        Assert.assertEquals(
            "seq_3|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n"
                            + "seq_4|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1",
            new String(bs));

        System.out.println("----------------设置buffer 100-----------");
        OssUtil.OSS_READ_TAIL_BUFFER = 100;
        slice = splitter.getTailSlice(config);
        is = ossStorage.getInputStream(path, slice.getStart(), slice.getLength());
        bs = OssUtil.read(is, (int) slice.getLength());
        Assert.assertEquals(
            "seq_3|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n"
                            + "seq_4|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1",
            new String(bs));

        System.out.println("----------------设置buffer 101-----------");
        OssUtil.OSS_READ_TAIL_BUFFER = 101;
        slice = splitter.getTailSlice(config);
        is = ossStorage.getInputStream(path, slice.getStart(), slice.getLength());
        bs = OssUtil.read(is, (int) slice.getLength());
        Assert.assertEquals(
            "seq_3|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n"
                            + "seq_4|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1",
            new String(bs));

    }

    @Test
    public void testEmptyLineMore() throws Exception {
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
        pd.setTails(rds);
        PD_CACHE.put(pd.getName(), pd);

        String file = "seq_2|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n"
                      + "seq_3|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n"
                      + "seq_4|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n\r\n\r\n\r\n\r\n";
        String path = RDF_PATH + "emptyline.txt";
        ossClient.putObject(bucketName, path, new ByteArrayInputStream(file.getBytes()));

        FileConfig config = new FileConfig(path, "/tailslice/test.json", storageConfig);
        FileSlice slice = splitter.getTailSlice(config);

        InputStream is = ossStorage.getInputStream(path, slice.getStart(), slice.getLength());
        byte[] bs = OssUtil.read(is, (int) slice.getLength());
        Assert.assertEquals(
            "seq_3|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n"
                            + "seq_4|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n\r\n\r\n\r\n\r\n",
            new String(bs));

        System.out.println("----------------设置buffer 100-----------");
        OssUtil.OSS_READ_TAIL_BUFFER = 100;
        slice = splitter.getTailSlice(config);
        is = ossStorage.getInputStream(path, slice.getStart(), slice.getLength());
        bs = OssUtil.read(is, (int) slice.getLength());
        Assert.assertEquals(
            "seq_3|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n"
                            + "seq_4|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n\r\n\r\n\r\n\r\n",
            new String(bs));

        System.out.println("----------------设置buffer 101-----------");
        OssUtil.OSS_READ_TAIL_BUFFER = 101;
        slice = splitter.getTailSlice(config);
        is = ossStorage.getInputStream(path, slice.getStart(), slice.getLength());
        bs = OssUtil.read(is, (int) slice.getLength());
        Assert.assertEquals(
            "seq_3|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n"
                            + "seq_4|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n\r\n\r\n\r\n\r\n",
            new String(bs));

    }

    @Test
    public void testEmptyLineMore2() throws Exception {
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
        pd.setTails(rds);
        PD_CACHE.put(pd.getName(), pd);

        String file = "seq_2|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n"
                      + "seq_3|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\n"
                      + "seq_4|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\n\n\n\n";
        String path = RDF_PATH + "emptyline.txt";
        ossClient.putObject(bucketName, path, new ByteArrayInputStream(file.getBytes()));

        FileConfig config = new FileConfig(path, "/tailslice/test.json", storageConfig);
        FileSlice slice = splitter.getTailSlice(config);

        InputStream is = ossStorage.getInputStream(path, slice.getStart(), slice.getLength());
        byte[] bs = OssUtil.read(is, (int) slice.getLength());
        Assert.assertEquals(
            "seq_3|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\n"
                            + "seq_4|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\n\n\n\n",
            new String(bs));

        System.out.println("----------------设置buffer 100-----------");
        OssUtil.OSS_READ_TAIL_BUFFER = 100;
        slice = splitter.getTailSlice(config);
        is = ossStorage.getInputStream(path, slice.getStart(), slice.getLength());
        bs = OssUtil.read(is, (int) slice.getLength());
        Assert.assertEquals(
            "seq_3|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\n"
                            + "seq_4|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\n\n\n\n",
            new String(bs));

        System.out.println("----------------设置buffer 101-----------");
        OssUtil.OSS_READ_TAIL_BUFFER = 101;
        slice = splitter.getTailSlice(config);
        is = ossStorage.getInputStream(path, slice.getStart(), slice.getLength());
        bs = OssUtil.read(is, (int) slice.getLength());
        Assert.assertEquals(
            "seq_3|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\n"
                            + "seq_4|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\n\n\n\n",
            new String(bs));

    }

    @Test
    public void testEmptyLineMore3() throws Exception {
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
        pd.setTails(rds);
        PD_CACHE.put(pd.getName(), pd);

        String file = "seq_2|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n"
                      + "seq_3|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r"
                      + "seq_4|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\r\r\r";
        String path = RDF_PATH + "emptyline.txt";
        ossClient.putObject(bucketName, path, new ByteArrayInputStream(file.getBytes()));

        FileConfig config = new FileConfig(path, "/tailslice/test.json", storageConfig);
        FileSlice slice = splitter.getTailSlice(config);

        InputStream is = ossStorage.getInputStream(path, slice.getStart(), slice.getLength());
        byte[] bs = OssUtil.read(is, (int) slice.getLength());
        Assert.assertEquals(
            "seq_3|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r"
                            + "seq_4|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\r\r\r",
            new String(bs));

        System.out.println("----------------设置buffer 100-----------");
        OssUtil.OSS_READ_TAIL_BUFFER = 100;
        slice = splitter.getTailSlice(config);
        is = ossStorage.getInputStream(path, slice.getStart(), slice.getLength());
        bs = OssUtil.read(is, (int) slice.getLength());
        Assert.assertEquals(
            "seq_3|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r"
                            + "seq_4|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\r\r\r",
            new String(bs));

        System.out.println("----------------设置buffer 101-----------");
        OssUtil.OSS_READ_TAIL_BUFFER = 101;
        slice = splitter.getTailSlice(config);
        is = ossStorage.getInputStream(path, slice.getStart(), slice.getLength());
        bs = OssUtil.read(is, (int) slice.getLength());
        Assert.assertEquals(
            "seq_3|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r"
                            + "seq_4|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\r\r\r",
            new String(bs));

    }
}
