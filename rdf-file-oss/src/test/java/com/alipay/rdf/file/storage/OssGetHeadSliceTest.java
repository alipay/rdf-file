package com.alipay.rdf.file.storage;

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
import com.alipay.rdf.file.util.OssTestUtil;
import com.alipay.rdf.file.util.OssUtil;
import com.alipay.rdf.file.util.RdfFileLogUtil;
import com.alipay.rdf.file.util.TestLog;
import com.aliyun.oss.OSSClient;
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
 * oss获取头分片测试
 * 
 * @author hongwei.quhw
 * @version $Id: OssGetHeadSliceTest.java, v 0.1 2017年7月26日 下午8:29:18 hongwei.quhw Exp $
 */
@SuppressWarnings("unchecked")
public class OssGetHeadSliceTest {

    private static final String             RDF_PATH = "rdf/rdf-file/test/getheadslice/";
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
    public void testError() throws IOException {
        ProtocolDefinition pd = new ProtocolDefinition();
        pd.setName("test");
        RowDefinition rd = new RowDefinition();
        rd.setOutput(new ColumnFunction());
        List<RowDefinition> rds = new ArrayList<RowDefinition>();
        rds.add(rd);
        pd.setHeads(rds);
        PD_CACHE.put(pd.getName(), pd);

        String path = RDF_PATH + "emptyline.txt";

        FileConfig config = new FileConfig(path, "/headslice/test.json", storageConfig);
        try {
            splitter.getHeadSlice(config);
            Assert.fail();
        } catch (RdfFileException e) {
            Assert.assertEquals(RdfErrorEnum.NOT_EXSIT, e.getErrorEnum());
        }

        String file = "";
        ossClient.putObject(bucketName, path, new ByteArrayInputStream(file.getBytes()));
        FileSlice slice = splitter.getHeadSlice(config);
        Assert.assertEquals(0, slice.getStart());
        Assert.assertEquals(0, slice.getEnd());
    }

    @Test
    public void testOneLine() throws Exception {
        ProtocolDefinition pd = new ProtocolDefinition();
        pd.setName("test");
        RowDefinition rd = new RowDefinition();
        rd.setOutput(new ColumnFunction());
        List<RowDefinition> rds = new ArrayList<RowDefinition>();
        rds.add(rd);
        pd.setHeads(rds);
        PD_CACHE.put(pd.getName(), pd);
        String path = RDF_PATH + "oneline.txt";
        String file = "总笔数:100|总金额:300.03\r\n"
                      + "流水号|基金公司订单号|订单申请时间|普通日期|普通日期时间|普通数字|金额|年龄|长整型|布尔值|备注\r\n";
        ossClient.putObject(bucketName, path, new ByteArrayInputStream(file.getBytes()));

        FileConfig config = new FileConfig(path, "/headslice/test.json", storageConfig);

        FileSlice slice = splitter.getHeadSlice(config);
        InputStream is = ossStorage.getInputStream(path, slice.getStart(), slice.getLength());
        byte[] bs = OssUtil.read(is, (int) slice.getLength());
        Assert.assertEquals("总笔数:100|总金额:300.03\r\n", new String(bs));
    }

    public void testOneLine2() throws Exception {
        ProtocolDefinition pd = new ProtocolDefinition();
        pd.setName("test");
        RowDefinition rd = new RowDefinition();
        rd.setOutput(new ColumnFunction());
        List<RowDefinition> rds = new ArrayList<RowDefinition>();
        rds.add(rd);
        pd.setHeads(rds);
        PD_CACHE.put(pd.getName(), pd);
        String path = RDF_PATH + "oneline.txt";
        String file = "总笔数:100|总金额:300.03\n"
                      + "流水号|基金公司订单号|订单申请时间|普通日期|普通日期时间|普通数字|金额|年龄|长整型|布尔值|备注\n";
        ossClient.putObject(bucketName, path, new ByteArrayInputStream(file.getBytes()));

        FileConfig config = new FileConfig(path, "/headslice/test.json", storageConfig);

        FileSlice slice = splitter.getHeadSlice(config);
        InputStream is = ossStorage.getInputStream(path, slice.getStart(), slice.getLength());
        byte[] bs = OssUtil.read(is, (int) slice.getLength());
        Assert.assertEquals("总笔数:100|总金额:300.03\n", new String(bs));
    }

    @Test
    public void testOneLine3() throws Exception {
        ProtocolDefinition pd = new ProtocolDefinition();
        pd.setName("test");
        RowDefinition rd = new RowDefinition();
        rd.setOutput(new ColumnFunction());
        List<RowDefinition> rds = new ArrayList<RowDefinition>();
        rds.add(rd);
        pd.setHeads(rds);
        PD_CACHE.put(pd.getName(), pd);
        String path = RDF_PATH + "oneline.txt";
        String file = "总笔数:100|总金额:300.03\r\n";
        ossClient.putObject(bucketName, path, new ByteArrayInputStream(file.getBytes()));

        FileConfig config = new FileConfig(path, "/headslice/test.json", storageConfig);

        FileSlice slice = splitter.getHeadSlice(config);
        InputStream is = ossStorage.getInputStream(path, slice.getStart(), slice.getLength());
        byte[] bs = OssUtil.read(is, (int) slice.getLength());
        Assert.assertEquals("总笔数:100|总金额:300.03\r\n", new String(bs));
    }

    @Test
    public void testOneLine4() throws Exception {
        ProtocolDefinition pd = new ProtocolDefinition();
        pd.setName("test");
        RowDefinition rd = new RowDefinition();
        rd.setOutput(new ColumnFunction());
        List<RowDefinition> rds = new ArrayList<RowDefinition>();
        rds.add(rd);
        pd.setHeads(rds);
        PD_CACHE.put(pd.getName(), pd);
        String path = RDF_PATH + "oneline.txt";
        String file = "总笔数:100|总金额:300.03\n";
        ossClient.putObject(bucketName, path, new ByteArrayInputStream(file.getBytes()));

        FileConfig config = new FileConfig(path, "/headslice/test.json", storageConfig);

        FileSlice slice = splitter.getHeadSlice(config);
        InputStream is = ossStorage.getInputStream(path, slice.getStart(), slice.getLength());
        byte[] bs = OssUtil.read(is, (int) slice.getLength());
        Assert.assertEquals("总笔数:100|总金额:300.03\n", new String(bs));
    }

    @Test
    public void testOneLine5() throws Exception {
        ProtocolDefinition pd = new ProtocolDefinition();
        pd.setName("test");
        RowDefinition rd = new RowDefinition();
        rd.setOutput(new ColumnFunction());
        List<RowDefinition> rds = new ArrayList<RowDefinition>();
        rds.add(rd);
        pd.setHeads(rds);
        PD_CACHE.put(pd.getName(), pd);
        String path = RDF_PATH + "oneline.txt";
        String file = "总笔数:100|总金额:300.03";
        ossClient.putObject(bucketName, path, new ByteArrayInputStream(file.getBytes()));

        FileConfig config = new FileConfig(path, "/headslice/test.json", storageConfig);

        FileSlice slice = splitter.getHeadSlice(config);
        InputStream is = ossStorage.getInputStream(path, slice.getStart(), slice.getLength());
        byte[] bs = OssUtil.read(is, (int) slice.getLength());
        Assert.assertEquals("总笔数:100|总金额:300.03", new String(bs));
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
        pd.setHeads(rds);
        PD_CACHE.put(pd.getName(), pd);
        String path = RDF_PATH + "threeline.txt";
        String file = "总笔数:100|总金额:300.03\r\n"
                      + "流水号|基金公司订单号|订单申请时间|普通日期|普通日期时间|普通数字|金额|年龄|长整型|布尔值|备注\r\n"
                      + "seq_0|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n"
                      + "seq_1|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n"
                      + "seq_2|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n"
                      + "seq_3|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n"
                      + "seq_4|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n";

        ossClient.putObject(bucketName, path, new ByteArrayInputStream(file.getBytes()));

        FileConfig config = new FileConfig(path, "/headslice/test.json", storageConfig);

        FileSlice slice = splitter.getHeadSlice(config);
        InputStream is = ossStorage.getInputStream(path, slice.getStart(), slice.getLength());
        byte[] bs = OssUtil.read(is, (int) slice.getLength());
        Assert.assertEquals(
            "总笔数:100|总金额:300.03\r\n" + "流水号|基金公司订单号|订单申请时间|普通日期|普通日期时间|普通数字|金额|年龄|长整型|布尔值|备注\r\n"
                            + "seq_0|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n",
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
        pd.setHeads(rds);
        PD_CACHE.put(pd.getName(), pd);
        String path = RDF_PATH + "threeline.txt";
        String file = "总笔数:100|总金额:300.03\r\n"
                      + "流水号|基金公司订单号|订单申请时间|普通日期|普通日期时间|普通数字|金额|年龄|长整型|布尔值|备注\r\n"
                      + "seq_0|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1";

        ossClient.putObject(bucketName, path, new ByteArrayInputStream(file.getBytes()));

        FileConfig config = new FileConfig(path, "/headslice/test.json", storageConfig);

        FileSlice slice = splitter.getHeadSlice(config);
        InputStream is = ossStorage.getInputStream(path, slice.getStart(), slice.getLength());
        byte[] bs = OssUtil.read(is, (int) slice.getLength());
        Assert.assertEquals(
            "总笔数:100|总金额:300.03\r\n" + "流水号|基金公司订单号|订单申请时间|普通日期|普通日期时间|普通数字|金额|年龄|长整型|布尔值|备注\r\n"
                            + "seq_0|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1",
            new String(bs));
    }

    @Test
    public void testFund() throws Exception {
        ProtocolDefinition pd = new ProtocolDefinition();
        pd.setName("test");
        RowDefinition rd = new RowDefinition();
        rd.setOutput(new ColumnFunction() {
            @Override
            public int rowsAffected(RowDefinition rd, FileMeta fileMeta) {
                return 85;
            }
        });
        List<RowDefinition> rds = new ArrayList<RowDefinition>();
        rds.add(rd);
        pd.setHeads(rds);
        PD_CACHE.put(pd.getName(), pd);
        String path = RDF_PATH + "fund.txt";
        String srcFilePath = File.class.getResource("/headslice/fund.txt").getPath();

        ossStorage.upload(srcFilePath, path, true);

        FileConfig config = new FileConfig(path, "/headslice/test.json", storageConfig);

        FileSlice slice = splitter.getHeadSlice(config);
        InputStream is = ossStorage.getInputStream(path, slice.getStart(), slice.getLength());
        BufferedReader srcReader = new BufferedReader(
            new InputStreamReader(new FileInputStream(new File(srcFilePath)), "gbk"));

        BufferedReader headReader = new BufferedReader(new InputStreamReader(is));

        for (int i = 0; i < 85; i++) {
            Assert.assertEquals(srcReader.readLine(), headReader.readLine());
        }

        Assert.assertNull(headReader.readLine());

        Assert.assertEquals(
            "432017042600000000023002000198 2017042714010091704220067000002996      00000000000000000000000000001001022220067000002000002088502583965931       156                                         10000000                                                        00000                 0000000000                              0000000000000000基金赎回|天弘沪深300|000961                                       0000000000000000         0         0                                          0                                                00                                                          00000 0000000000000000    00000                        0000000000000000000000000",
            srcReader.readLine());

        srcReader.close();
        headReader.close();
    }

    @Test
    public void testMacLineBreak() throws Exception {
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
        String path = RDF_PATH + "threeline.txt";
        String file = "总笔数:100|总金额:300.03\r"
                      + "流水号|基金公司订单号|订单申请时间|普通日期|普通日期时间|普通数字|金额|年龄|长整型|布尔值|备注\r"
                      + "seq_0|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r"
                      + "seq_1|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r"
                      + "seq_2|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r"
                      + "seq_3|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r"
                      + "seq_4|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r";

        ossClient.putObject(bucketName, path, new ByteArrayInputStream(file.getBytes()));

        FileConfig config = new FileConfig(path, "/headslice/test.json", storageConfig);

        FileSlice slice = splitter.getHeadSlice(config);
        InputStream is = ossStorage.getInputStream(path, slice.getStart(), slice.getLength());
        byte[] bs = OssUtil.read(is, (int) slice.getLength());
        Assert.assertEquals(
            "总笔数:100|总金额:300.03\r" + "流水号|基金公司订单号|订单申请时间|普通日期|普通日期时间|普通数字|金额|年龄|长整型|布尔值|备注\r",
            new String(bs));
    }

    @Test
    public void testMacLineBreak2() throws Exception {
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
        String path = RDF_PATH + "threeline.txt";
        String file = "总笔数:100|总金额:300.03\r"
                      + "流水号|基金公司订单号|订单申请时间|普通日期|普通日期时间|普通数字|金额|年龄|长整型|布尔值|备注\r"
                      + "seq_0|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r"
                      + "seq_1|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r"
                      + "seq_2|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r"
                      + "seq_3|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r"
                      + "seq_4|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\r\r\r\r\r";

        ossClient.putObject(bucketName, path, new ByteArrayInputStream(file.getBytes()));

        FileConfig config = new FileConfig(path, "/headslice/test.json", storageConfig);

        FileSlice slice = splitter.getHeadSlice(config);
        InputStream is = ossStorage.getInputStream(path, slice.getStart(), slice.getLength());
        byte[] bs = OssUtil.read(is, (int) slice.getLength());
        Assert.assertEquals(
            "总笔数:100|总金额:300.03\r" + "流水号|基金公司订单号|订单申请时间|普通日期|普通日期时间|普通数字|金额|年龄|长整型|布尔值|备注\r"
                            + "seq_0|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r"
                            + "seq_1|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r"
                            + "seq_2|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r"
                            + "seq_3|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r"
                            + "seq_4|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r",
            new String(bs));
    }

    @Test
    public void testMacLineBreak3() throws Exception {
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
        String path = RDF_PATH + "threeline.txt";
        String file = "总笔数:100|总金额:300.03\r"
                      + "流水号|基金公司订单号|订单申请时间|普通日期|普通日期时间|普通数字|金额|年龄|长整型|布尔值|备注\r"
                      + "seq_0|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r"
                      + "seq_1|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r"
                      + "seq_2|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r"
                      + "seq_3|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r"
                      + "seq_4|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1";

        ossClient.putObject(bucketName, path, new ByteArrayInputStream(file.getBytes()));

        FileConfig config = new FileConfig(path, "/headslice/test.json", storageConfig);

        FileSlice slice = splitter.getHeadSlice(config);
        InputStream is = ossStorage.getInputStream(path, slice.getStart(), slice.getLength());
        byte[] bs = OssUtil.read(is, (int) slice.getLength());
        Assert.assertEquals(
            "总笔数:100|总金额:300.03\r" + "流水号|基金公司订单号|订单申请时间|普通日期|普通日期时间|普通数字|金额|年龄|长整型|布尔值|备注\r"
                            + "seq_0|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r"
                            + "seq_1|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r"
                            + "seq_2|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r"
                            + "seq_3|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r"
                            + "seq_4|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1",
            new String(bs));
    }

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
        pd.setHeads(rds);
        PD_CACHE.put(pd.getName(), pd);
        String path = RDF_PATH + "threeline.txt";
        String file = "总笔数:100|总金额:300.03\r"
                      + "流水号|基金公司订单号|订单申请时间|普通日期|普通日期时间|普通数字|金额|年龄|长整型|布尔值|备注\r\n"
                      + "seq_0|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r"
                      + "seq_1|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r"
                      + "seq_2|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n"
                      + "seq_3|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r"
                      + "seq_4|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\n\r\n\n\n";

        ossClient.putObject(bucketName, path, new ByteArrayInputStream(file.getBytes()));

        FileConfig config = new FileConfig(path, "/headslice/test.json", storageConfig);

        FileSlice slice = splitter.getHeadSlice(config);
        InputStream is = ossStorage.getInputStream(path, slice.getStart(), slice.getLength());
        byte[] bs = OssUtil.read(is, (int) slice.getLength());
        Assert.assertEquals(
            "总笔数:100|总金额:300.03\r" + "流水号|基金公司订单号|订单申请时间|普通日期|普通日期时间|普通数字|金额|年龄|长整型|布尔值|备注\r\n"
                            + "seq_0|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r"
                            + "seq_1|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r"
                            + "seq_2|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n"
                            + "seq_3|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r"
                            + "seq_4|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\n",
            new String(bs));
    }

    @After
    public void after() {
        ossStorage.delete(RDF_PATH);
    }
}
