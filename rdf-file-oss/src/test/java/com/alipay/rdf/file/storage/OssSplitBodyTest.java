package com.alipay.rdf.file.storage;

import com.alipay.rdf.file.exception.RdfErrorEnum;
import com.alipay.rdf.file.exception.RdfFileException;
import com.alipay.rdf.file.function.ColumnFunction;
import com.alipay.rdf.file.meta.FileMeta;
import com.alipay.rdf.file.model.FileConfig;
import com.alipay.rdf.file.model.FileSlice;
import com.alipay.rdf.file.protocol.ProtocolDefinition;
import com.alipay.rdf.file.protocol.RowDefinition;
import com.alipay.rdf.file.util.OssUtil;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * oss分片测试
 * 
 * @author hongwei.quhw
 * @version $Id: OssSplitTest.java, v 0.1 2017年7月27日 下午7:21:11 hongwei.quhw Exp $
 */
public class OssSplitBodyTest extends AbstractOssSliceTest {

    @Test
    public void testError() throws Exception {
        String path = RDF_PATH + "split.txt";
        FileConfig config = new FileConfig(path, "/headslice/test.json", storageConfig);
        try {
            splitter.getBodySlices(config, 100);
            Assert.fail();
        } catch (RdfFileException e) {
            Assert.assertEquals(RdfErrorEnum.NOT_EXSIT, e.getErrorEnum());
        }

        String file = "";
        ossClient.putObject(bucketName, path, new ByteArrayInputStream(file.getBytes()));
        List<FileSlice> slices = splitter.split(path, 10);
        Assert.assertEquals(1, slices.size());
        Assert.assertEquals(0, slices.get(0).getStart());
        Assert.assertEquals(0, slices.get(0).getEnd());

        try {
            splitter.getBodySlices(config, 0);
            Assert.fail();
        } catch (RdfFileException e) {
            Assert.assertEquals(RdfErrorEnum.ILLEGAL_ARGUMENT, e.getErrorEnum());
        }
    }

    @Test
    public void testSplit() throws Exception {
        ProtocolDefinition pd = new ProtocolDefinition();
        pd.setName("test");
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
                return 1;
            }
        });
        List<RowDefinition> trds = new ArrayList<RowDefinition>();
        trds.add(trd);
        pd.setTails(trds);
        PD_CACHE.put(pd.getName(), pd);
        String path = RDF_PATH + "split.txt";
        String file = "总笔数:100|总金额:300.03\r\n"
                      + "流水号|基金公司订单号|订单申请时间|普通日期|普通日期时间|普通数字|金额|年龄|长整型|布尔值|备注\r\n"
                      + "seq_0|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n"
                      + "seq_1|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n"
                      + "seq_2|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n"
                      + "seq_3|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n"
                      + "seq_4|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n";

        ossClient.putObject(bucketName, path, new ByteArrayInputStream(file.getBytes()));

        FileConfig config = new FileConfig(path, "/headslice/test.json", storageConfig);
        List<FileSlice> slices = splitter.getBodySlices(config, 10);
        Assert.assertEquals(5, slices.size());

        FileSlice slice = slices.get(0);
        InputStream is = ossStorage.getInputStream(path, slice.getStart(), slice.getLength());
        byte[] bs = OssUtil.read(is, (int) slice.getLength());
        Assert.assertEquals("流水号|基金公司订单号|订单申请时间|普通日期|普通日期时间|普通数字|金额|年龄|长整型|布尔值|备注\r\n",
            new String(bs));

        slice = slices.get(1);
        is = ossStorage.getInputStream(path, slice.getStart(), slice.getLength());
        bs = OssUtil.read(is, (int) slice.getLength());
        Assert.assertEquals(
            "seq_0|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n",
            new String(bs));

        slice = slices.get(2);
        is = ossStorage.getInputStream(path, slice.getStart(), slice.getLength());
        bs = OssUtil.read(is, (int) slice.getLength());
        Assert.assertEquals(
            "seq_1|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n",
            new String(bs));

        slice = slices.get(3);
        is = ossStorage.getInputStream(path, slice.getStart(), slice.getLength());
        bs = OssUtil.read(is, (int) slice.getLength());
        Assert.assertEquals(
            "seq_2|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n",
            new String(bs));

        slice = slices.get(4);
        is = ossStorage.getInputStream(path, slice.getStart(), slice.getLength());
        bs = OssUtil.read(is, (int) slice.getLength());
        Assert.assertEquals(
            "seq_3|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n",
            new String(bs));
    }

    @Test
    public void testSplit2() throws Exception {
        ProtocolDefinition pd = new ProtocolDefinition();
        pd.setName("test");
        RowDefinition hrd = new RowDefinition();
        hrd.setOutput(new ColumnFunction() {
            @Override
            public int rowsAffected(RowDefinition rd, FileMeta fileMeta) {
                return 2;
            }
        });
        List<RowDefinition> hrds = new ArrayList<RowDefinition>();
        hrds.add(hrd);
        pd.setHeads(hrds);
        PD_CACHE.put(pd.getName(), pd);
        String path = RDF_PATH + "split.txt";
        String file = "总笔数:100|总金额:300.03\r\n"
                      + "流水号|基金公司订单号|订单申请时间|普通日期|普通日期时间|普通数字|金额|年龄|长整型|布尔值|备注\r\n"
                      + "seq_0|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n"
                      + "seq_1|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n"
                      + "seq_2|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n"
                      + "seq_3|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n"
                      + "seq_4|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n";

        ossClient.putObject(bucketName, path, new ByteArrayInputStream(file.getBytes()));

        FileConfig config = new FileConfig(path, "/headslice/test.json", storageConfig);
        List<FileSlice> slices = splitter.getBodySlices(config, 10);
        Assert.assertEquals(5, slices.size());

        FileSlice slice = slices.get(0);
        InputStream is = ossStorage.getInputStream(path, slice.getStart(), slice.getLength());
        byte[] bs = OssUtil.read(is, (int) slice.getLength());
        Assert.assertEquals(
            "seq_0|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n",
            new String(bs));

        slice = slices.get(1);
        is = ossStorage.getInputStream(path, slice.getStart(), slice.getLength());
        bs = OssUtil.read(is, (int) slice.getLength());
        Assert.assertEquals(
            "seq_1|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n",
            new String(bs));

        slice = slices.get(2);
        is = ossStorage.getInputStream(path, slice.getStart(), slice.getLength());
        bs = OssUtil.read(is, (int) slice.getLength());
        Assert.assertEquals(
            "seq_2|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n",
            new String(bs));

        slice = slices.get(3);
        is = ossStorage.getInputStream(path, slice.getStart(), slice.getLength());
        bs = OssUtil.read(is, (int) slice.getLength());
        Assert.assertEquals(
            "seq_3|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n",
            new String(bs));
        slice = slices.get(4);
        is = ossStorage.getInputStream(path, slice.getStart(), slice.getLength());
        bs = OssUtil.read(is, (int) slice.getLength());
        Assert.assertEquals(
            "seq_4|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n",
            new String(bs));
    }

    @Test
    public void testSplit3() throws Exception {
        ProtocolDefinition pd = new ProtocolDefinition();
        pd.setName("test");
        RowDefinition hrd = new RowDefinition();
        hrd.setOutput(new ColumnFunction() {
            @Override
            public int rowsAffected(RowDefinition rd, FileMeta fileMeta) {
                return 2;
            }
        });
        List<RowDefinition> hrds = new ArrayList<RowDefinition>();
        hrds.add(hrd);
        pd.setHeads(hrds);
        RowDefinition trd = new RowDefinition();
        trd.setOutput(new ColumnFunction() {
            @Override
            public int rowsAffected(RowDefinition rd, FileMeta fileMeta) {
                return 5;
            }
        });
        List<RowDefinition> trds = new ArrayList<RowDefinition>();
        trds.add(trd);
        pd.setTails(trds);
        PD_CACHE.put(pd.getName(), pd);
        String path = RDF_PATH + "split.txt";
        String file = "总笔数:100|总金额:300.03\r\n"
                      + "流水号|基金公司订单号|订单申请时间|普通日期|普通日期时间|普通数字|金额|年龄|长整型|布尔值|备注\r\n"
                      + "seq_0|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n"
                      + "seq_1|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n"
                      + "seq_2|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n"
                      + "seq_3|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n"
                      + "seq_4|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n";

        ossClient.putObject(bucketName, path, new ByteArrayInputStream(file.getBytes()));

        FileConfig config = new FileConfig(path, "/headslice/test.json", storageConfig);
        List<FileSlice> slices = splitter.getBodySlices(config, 10);
        Assert.assertEquals(1, slices.size());
        Assert.assertEquals(0, slices.get(0).getLength());
    }

    @Test
    public void testSplit4() throws Exception {
        ProtocolDefinition pd = new ProtocolDefinition();
        pd.setName("test");
        PD_CACHE.put(pd.getName(), pd);
        String path = RDF_PATH + "split.txt";
        String file = "总笔数:100|总金额:300.03\r\n"
                      + "流水号|基金公司订单号|订单申请时间|普通日期|普通日期时间|普通数字|金额|年龄|长整型|布尔值|备注\r\n"
                      + "seq_0|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n"
                      + "seq_1|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n"
                      + "seq_2|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n"
                      + "seq_3|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n"
                      + "seq_4|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n";

        ossClient.putObject(bucketName, path, new ByteArrayInputStream(file.getBytes()));

        FileConfig config = new FileConfig(path, "/headslice/test.json", storageConfig);
        List<FileSlice> slices = splitter.getBodySlices(config, 10);
        Assert.assertEquals(7, slices.size());

        FileSlice slice = slices.get(2);
        InputStream is = ossStorage.getInputStream(path, slice.getStart(), slice.getLength());
        byte[] bs = OssUtil.read(is, (int) slice.getLength());
        Assert.assertEquals(
            "seq_0|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n",
            new String(bs));

        slice = slices.get(3);
        is = ossStorage.getInputStream(path, slice.getStart(), slice.getLength());
        bs = OssUtil.read(is, (int) slice.getLength());
        Assert.assertEquals(
            "seq_1|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n",
            new String(bs));

        slice = slices.get(4);
        is = ossStorage.getInputStream(path, slice.getStart(), slice.getLength());
        bs = OssUtil.read(is, (int) slice.getLength());
        Assert.assertEquals(
            "seq_2|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n",
            new String(bs));

        slice = slices.get(5);
        is = ossStorage.getInputStream(path, slice.getStart(), slice.getLength());
        bs = OssUtil.read(is, (int) slice.getLength());
        Assert.assertEquals(
            "seq_3|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n",
            new String(bs));
        slice = slices.get(6);
        is = ossStorage.getInputStream(path, slice.getStart(), slice.getLength());
        bs = OssUtil.read(is, (int) slice.getLength());
        Assert.assertEquals(
            "seq_4|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n",
            new String(bs));
    }

    @Test
    public void testSplit5() throws Exception {
        ProtocolDefinition pd = new ProtocolDefinition();
        pd.setName("test");
        RowDefinition hrd = new RowDefinition();
        hrd.setOutput(new ColumnFunction() {
            @Override
            public int rowsAffected(RowDefinition rd, FileMeta fileMeta) {
                return 2;
            }
        });
        List<RowDefinition> hrds = new ArrayList<RowDefinition>();
        hrds.add(hrd);
        pd.setHeads(hrds);
        RowDefinition trd = new RowDefinition();
        trd.setOutput(new ColumnFunction() {
            @Override
            public int rowsAffected(RowDefinition rd, FileMeta fileMeta) {
                return 1;
            }
        });
        List<RowDefinition> trds = new ArrayList<RowDefinition>();
        trds.add(trd);
        pd.setTails(trds);
        PD_CACHE.put(pd.getName(), pd);
        String path = RDF_PATH + "split.txt";
        String file = "总笔数:100|总金额:300.03\r\n"
                      + "流水号|基金公司订单号|订单申请时间|普通日期|普通日期时间|普通数字|金额|年龄|长整型|布尔值|备注\r\n"
                      + "seq_0|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n"
                      + "seq_1|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n"
                      + "seq_2|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n"
                      + "seq_3|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n"
                      + "seq_4|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n";

        ossClient.putObject(bucketName, path, new ByteArrayInputStream(file.getBytes()));

        FileConfig config = new FileConfig(path, "/headslice/test.json", storageConfig);
        List<FileSlice> slices = splitter.getBodySlices(config, 665);
        Assert.assertEquals(1, slices.size());

        FileSlice slice = slices.get(0);
        InputStream is = ossStorage.getInputStream(path, slice.getStart(), slice.getLength());
        byte[] bs = OssUtil.read(is, (int) slice.getLength());
        Assert.assertEquals(
            "seq_0|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n"
                            + "seq_1|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n"
                            + "seq_2|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n"
                            + "seq_3|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n",
            new String(bs));

    }

    @Test
    public void testSplit6() throws Exception {
        ProtocolDefinition pd = new ProtocolDefinition();
        pd.setName("test");
        RowDefinition hrd = new RowDefinition();
        hrd.setOutput(new ColumnFunction() {
            @Override
            public int rowsAffected(RowDefinition rd, FileMeta fileMeta) {
                return 2;
            }
        });
        List<RowDefinition> hrds = new ArrayList<RowDefinition>();
        hrds.add(hrd);
        pd.setHeads(hrds);
        RowDefinition trd = new RowDefinition();
        trd.setOutput(new ColumnFunction() {
            @Override
            public int rowsAffected(RowDefinition rd, FileMeta fileMeta) {
                return 1;
            }
        });
        List<RowDefinition> trds = new ArrayList<RowDefinition>();
        trds.add(trd);
        pd.setTails(trds);
        PD_CACHE.put(pd.getName(), pd);
        String path = RDF_PATH + "split.txt";
        String file = "总笔数:100|总金额:300.03\r\n"
                      + "流水号|基金公司订单号|订单申请时间|普通日期|普通日期时间|普通数字|金额|年龄|长整型|布尔值|备注\r\n"
                      + "seq_0|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n"
                      + "seq_1|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n"
                      + "seq_2|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n"
                      + "seq_3|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n"
                      + "seq_4|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n";

        ossClient.putObject(bucketName, path, new ByteArrayInputStream(file.getBytes()));

        FileConfig config = new FileConfig(path, "/headslice/test.json", storageConfig);
        List<FileSlice> slices = splitter.getBodySlices(config, 200);
        Assert.assertEquals(2, slices.size());

        FileSlice slice = slices.get(0);
        InputStream is = ossStorage.getInputStream(path, slice.getStart(), slice.getLength());
        byte[] bs = OssUtil.read(is, (int) slice.getLength());
        Assert.assertEquals(
            "seq_0|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n"
                            + "seq_1|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n"
                            + "seq_2|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n",
            new String(bs));

        slice = slices.get(1);
        is = ossStorage.getInputStream(path, slice.getStart(), slice.getLength());
        bs = OssUtil.read(is, (int) slice.getLength());
        Assert.assertEquals(
            "seq_3|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n",
            new String(bs));

    }
}
