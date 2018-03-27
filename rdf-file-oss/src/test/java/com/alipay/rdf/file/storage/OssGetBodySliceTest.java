package com.alipay.rdf.file.storage;

import java.io.ByteArrayInputStream;
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
import com.alipay.rdf.file.util.OssTestUtil;
import com.alipay.rdf.file.util.OssUtil;
import com.alipay.rdf.file.util.RdfFileLogUtil;
import com.alipay.rdf.file.util.TestLog;
import com.aliyun.oss.OSSClient;

import junit.framework.Assert;

/**
 * 
 * 
 * @author hongwei.quhw
 * @version $Id: OssGetBodySliceTest.java, v 0.1 2017年7月27日 下午2:03:50 hongwei.quhw Exp $
 */
public class OssGetBodySliceTest {

    private static final String             RDF_PATH = "rdf/rdf-file/test/getbodyslice/";
    private Map<String, ProtocolDefinition> PD_CACHE = null;
    private OSSClient                       ossClient;
    private String                          bucketName;
    private StorageConfig                   storageConfig;
    private FileOssStorage                  ossStorage;
    private OssFileSliceSplitter            splitter;

    @SuppressWarnings("unchecked")
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
    public void testError() throws Exception {
        String path = RDF_PATH + "empty.txt";

        FileConfig config = new FileConfig(path, "/headslice/test.json", storageConfig);
        try {
            splitter.getBodySlice(config);
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
    public void testBodyEmpty() throws Exception {
        ProtocolDefinition pd = new ProtocolDefinition();
        pd.setName("test");
        RowDefinition hrd = new RowDefinition();
        hrd.setOutput(new ColumnFunction() {
            @Override
            public int rowsAffected(RowDefinition rd, FileMeta fileMeta) {
                return 3;
            }
        });
        List<RowDefinition> hrds = new ArrayList<RowDefinition>();
        hrds.add(hrd);
        pd.setHeads(hrds);
        RowDefinition trd = new RowDefinition();
        trd.setOutput(new ColumnFunction() {
            @Override
            public int rowsAffected(RowDefinition rd, FileMeta fileMeta) {
                return 4;
            }
        });
        List<RowDefinition> trds = new ArrayList<RowDefinition>();
        trds.add(trd);
        pd.setTails(trds);
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
        FileSlice slice = splitter.getBodySlice(config);
        Assert.assertEquals(0, slice.getLength());
    }

    @Test
    public void testBodyOneline() throws Exception {
        ProtocolDefinition pd = new ProtocolDefinition();
        pd.setName("test");
        RowDefinition hrd = new RowDefinition();
        hrd.setOutput(new ColumnFunction() {
            @Override
            public int rowsAffected(RowDefinition rd, FileMeta fileMeta) {
                return 3;
            }
        });
        List<RowDefinition> hrds = new ArrayList<RowDefinition>();
        hrds.add(hrd);
        pd.setHeads(hrds);
        RowDefinition trd = new RowDefinition();
        trd.setOutput(new ColumnFunction() {
            @Override
            public int rowsAffected(RowDefinition rd, FileMeta fileMeta) {
                return 3;
            }
        });
        List<RowDefinition> trds = new ArrayList<RowDefinition>();
        trds.add(trd);
        pd.setTails(trds);
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
        FileSlice slice = splitter.getBodySlice(config);
        InputStream is = ossStorage.getInputStream(path, slice.getStart(), slice.getLength());
        byte[] bs = OssUtil.read(is, (int) slice.getLength());
        Assert.assertEquals(
            "seq_1|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n",
            new String(bs));
    }

    @Test
    public void testBodyNoHead() throws Exception {
        ProtocolDefinition pd = new ProtocolDefinition();
        pd.setName("test");
        RowDefinition trd = new RowDefinition();
        trd.setOutput(new ColumnFunction() {
            @Override
            public int rowsAffected(RowDefinition rd, FileMeta fileMeta) {
                return 3;
            }
        });
        List<RowDefinition> trds = new ArrayList<RowDefinition>();
        trds.add(trd);
        pd.setTails(trds);
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
        FileSlice slice = splitter.getBodySlice(config);
        InputStream is = ossStorage.getInputStream(path, slice.getStart(), slice.getLength());
        byte[] bs = OssUtil.read(is, (int) slice.getLength());
        Assert.assertEquals(
            "总笔数:100|总金额:300.03\r\n" + "流水号|基金公司订单号|订单申请时间|普通日期|普通日期时间|普通数字|金额|年龄|长整型|布尔值|备注\r\n"
                            + "seq_0|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n"
                            + "seq_1|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n",
            new String(bs));
    }

    @Test
    public void testBodyNoTail() throws Exception {
        ProtocolDefinition pd = new ProtocolDefinition();
        pd.setName("test");
        RowDefinition hrd = new RowDefinition();
        hrd.setOutput(new ColumnFunction() {
            @Override
            public int rowsAffected(RowDefinition rd, FileMeta fileMeta) {
                return 3;
            }
        });
        List<RowDefinition> hrds = new ArrayList<RowDefinition>();
        hrds.add(hrd);
        pd.setHeads(hrds);
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
        FileSlice slice = splitter.getBodySlice(config);
        InputStream is = ossStorage.getInputStream(path, slice.getStart(), slice.getLength());
        byte[] bs = OssUtil.read(is, (int) slice.getLength());
        Assert.assertEquals(
            "seq_1|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n"
                            + "seq_2|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n"
                            + "seq_3|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n"
                            + "seq_4|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n",
            new String(bs));
    }

    @Test
    public void testOnlyBody() throws Exception {
        ProtocolDefinition pd = new ProtocolDefinition();
        pd.setName("test");
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
        FileSlice slice = splitter.getBodySlice(config);
        InputStream is = ossStorage.getInputStream(path, slice.getStart(), slice.getLength());
        byte[] bs = OssUtil.read(is, (int) slice.getLength());
        Assert.assertEquals(file, new String(bs));
    }

    @After
    public void after() {
        ossStorage.delete(RDF_PATH);
    }
}
