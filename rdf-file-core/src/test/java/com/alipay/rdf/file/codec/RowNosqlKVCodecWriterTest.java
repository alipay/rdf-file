package com.alipay.rdf.file.codec;

import com.alipay.rdf.file.exception.RdfFileException;
import com.alipay.rdf.file.interfaces.FileFactory;
import com.alipay.rdf.file.interfaces.FileReader;
import com.alipay.rdf.file.interfaces.FileWriter;
import com.alipay.rdf.file.model.FileConfig;
import com.alipay.rdf.file.model.StorageConfig;
import com.alipay.rdf.file.util.DateUtil;
import com.alipay.rdf.file.util.TemporaryFolderUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: hongwei.quhw 2021/8/1 5:00 下午
 */
public class RowNosqlKVCodecWriterTest {
    TemporaryFolderUtil tf = new TemporaryFolderUtil();

    @Before
    public void setUp() throws IOException {
        tf.create();
    }

    /**
     * de 有columnInfo的序列化与反序列化不支持
     */
    @Test(expected = RdfFileException.class)
    public void test() throws Exception {
        String filePath = tf.getRoot().getAbsolutePath();
        FileConfig config = new FileConfig(new File(filePath, "test.txt").getAbsolutePath(),
                "/codec/kv/template/template1.json", new StorageConfig("nas"));

        FileWriter fileWriter = FileFactory.createWriter(config);

        Map<String, Object> head = new HashMap<String, Object>();
        head.put("totalCount", 2);
        head.put("totalAmount", new BigDecimal("23.22"));
        fileWriter.writeHead(head);

        Map<String, Object> body = new HashMap<String, Object>();

        Date testDate = DateUtil.parse("2017-01-03 12:22:33", "yyyy-MM-dd HH:mm:ss");

        body.put("seq", "seq12345");
        body.put("instSeq", "303");
        body.put("gmtApply", testDate);
        body.put("date", testDate);
        body.put("dateTime", testDate);
        body.put("applyNumber", 12);
        body.put("amount", new BigDecimal("1.22"));
        body.put("age", new Integer(33));
        body.put("longN", new Long(33));
        body.put("bol", true);
        body.put("memo", "memo1");
        fileWriter.writeRow(body);

        testDate = DateUtil.parse("2016-02-03 12:22:33", "yyyy-MM-dd HH:mm:ss");

        body.put("seq", "seq234567");
        body.put("instSeq", "505");
        body.put("gmtApply", testDate);
        body.put("date", testDate);
        body.put("dateTime", testDate);
        body.put("applyNumber", 12);
        body.put("amount", new BigDecimal("1.09"));
        body.put("age", 66);
        body.put("longN", 125);
        body.put("bol", false);
        body.put("memo", "memo2");
        fileWriter.writeRow(body);

        fileWriter.close();

        //校验文件
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(new File(config.getFilePath())), "UTF-8"));
        Assert.assertEquals("总笔数:2|总金额:23.22", reader.readLine());
        Assert.assertEquals("流水号|基金公司订单号|订单申请时间|普通日期|普通日期时间|普通数字|金额|年龄|长整型|布尔值|备注",
                reader.readLine());
        Assert.assertEquals(
                "seq12345|303|2017-01-03 12:22:33|20170103|20170103 12:22:33|12|1.22|33|33|true|memo1",
                reader.readLine());
        Assert.assertEquals(
                "seq234567|505|2016-02-03 12:22:33|20160203|20160203 12:22:33|12|1.09|66|125|false|memo2",
                reader.readLine());

        reader.close();
    }

    @Test
    public void test2() throws Exception {
        String filePath = tf.getRoot().getAbsolutePath();
        FileConfig config = new FileConfig(new File(filePath, "test.txt").getAbsolutePath(),
                "/codec/kv/template/template2.json", new StorageConfig("nas"));

        FileWriter fileWriter = FileFactory.createWriter(config);

        Map<String, Object> head = new HashMap<String, Object>();
        head.put("totalCount", 2);
        head.put("totalAmount", new BigDecimal("23.22"));
        fileWriter.writeHead(head);

        Map<String, Object> body = new HashMap<String, Object>();

        Date testDate = DateUtil.parse("2017-01-03 12:22:33", "yyyy-MM-dd HH:mm:ss");

        body.put("seq", "");
        body.put("instSeq", "303");
        body.put("gmtApply", testDate);
        body.put("date", testDate);
        body.put("dateTime", testDate);
        body.put("applyNumber", 12);
        body.put("amount", new BigDecimal("1.22"));
        body.put("age", new Integer(33));
        body.put("longN", new Long(33));
        body.put("bol", null);
        body.put("memo", "   ");
        fileWriter.writeRow(body);

        testDate = DateUtil.parse("2016-02-03 12:22:33", "yyyy-MM-dd HH:mm:ss");

        body.put("seq", "seq234567");
        body.put("instSeq", "505");
        body.put("gmtApply", testDate);
        body.put("date", testDate);
        body.put("dateTime", testDate);
        body.put("applyNumber", 12);
        body.put("amount", new BigDecimal("1.09"));
        body.put("age", 66);
        body.put("longN", 125);
        body.put("bol", false);
        body.put("memo", "memo2");
        fileWriter.writeRow(body);

        body = new HashMap<String, Object>();
        body.put("seq", "seq234568");
        body.put("instSeq", "505");
        body.put("gmtApply", testDate);
        //body.put("date", testDate);
        body.put("dateTime", testDate);
        //body.put("applyNumber", 12);
        body.put("amount", new BigDecimal("1.09"));
        body.put("age", 66);
        body.put("longN", 125);
        //body.put("bol", false);
        //body.put("memo", "memo2");
        fileWriter.writeRow(body);

        body = new HashMap<String, Object>();
        //body.put("seq", "seq234568");
        body.put("instSeq", "999");
        body.put("gmtApply", testDate);
        //body.put("date", testDate);
        body.put("dateTime", testDate);
        //body.put("applyNumber", 12);
        body.put("amount", new BigDecimal("1.09"));
        body.put("age", 66);
        body.put("longN", 125);
        //body.put("bol", false);
        //body.put("memo", "memo2");
        fileWriter.writeRow(body);

        fileWriter.close();

        //校验文件
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(new File(config.getFilePath())), "UTF-8"));
        Assert.assertEquals("totalCount:2|totalAmount:23.22", reader.readLine());
        Assert.assertEquals(
                "seq:|instSeq:303|gmtApply:2017-01-03 12:22:33|date:20170103|dateTime:20170103 12:22:33|applyNumber:12|amount:1.22|age:33|longN:33|memo:",
                reader.readLine());
        Assert.assertEquals(
                "seq:seq234567|instSeq:505|gmtApply:2016-02-03 12:22:33|date:20160203|dateTime:20160203 12:22:33|applyNumber:12|amount:1.09|age:66|longN:125|bol:false|memo:memo2",
                reader.readLine());

        Assert.assertEquals(
                "seq:seq234568|instSeq:505|gmtApply:2016-02-03 12:22:33|dateTime:20160203 12:22:33|amount:1.09|age:66|longN:125",
                reader.readLine());
        Assert.assertEquals(
                "instSeq:999|gmtApply:2016-02-03 12:22:33|dateTime:20160203 12:22:33|amount:1.09|age:66|longN:125",
                reader.readLine());

        Assert.assertNull(reader.readLine());

        reader.close();
    }

    @Test
    public void test3() throws Exception {
        String filePath = tf.getRoot().getAbsolutePath();
        FileConfig config = new FileConfig(new File(filePath, "test.txt").getAbsolutePath(),
                "/codec/kv/template/template5.json", new StorageConfig("nas"));

        FileWriter fileWriter = FileFactory.createWriter(config);

        Map<String, Object> head = new HashMap<String, Object>();
        head.put("totalCount", 2);
        head.put("totalAmount", new BigDecimal("23.22"));
        fileWriter.writeHead(head);

        Map<String, Object> body = new HashMap<String, Object>();

        Date testDate = DateUtil.parse("2017-01-03 12:22:33", "yyyy-MM-dd HH:mm:ss");

        body.put("seq", "");
        body.put("instSeq", "303");
        body.put("gmtApply", testDate);
        body.put("date", testDate);
        body.put("dateTime", testDate);
        body.put("applyNumber", 12);
        body.put("amount", new BigDecimal("1.22"));
        body.put("age", new Integer(33));
        body.put("longN", new Long(33));
        body.put("bol", null);
        body.put("memo", "   ");
        fileWriter.writeRow(body);

        testDate = DateUtil.parse("2016-02-03 12:22:33", "yyyy-MM-dd HH:mm:ss");

        body.put("seq", "seq234567");
        body.put("instSeq", "505");
        body.put("gmtApply", testDate);
        body.put("date", testDate);
        body.put("dateTime", testDate);
        body.put("applyNumber", 12);
        body.put("amount", new BigDecimal("1.09"));
        body.put("age", 66);
        body.put("longN", 125);
        body.put("bol", false);
        body.put("memo", "memo2");
        fileWriter.writeRow(body);

        body = new HashMap<String, Object>();
        body.put("seq", "seq234568");
        body.put("instSeq", "505");
        body.put("gmtApply", testDate);
        //body.put("date", testDate);
        body.put("dateTime", testDate);
        //body.put("applyNumber", 12);
        body.put("amount", new BigDecimal("1.09"));
        body.put("age", 66);
        body.put("longN", 125);
        //body.put("bol", false);
        //body.put("memo", "memo2");
        fileWriter.writeRow(body);

        body = new HashMap<String, Object>();
        //body.put("seq", "seq234568");
        body.put("instSeq", "999");
        body.put("gmtApply", testDate);
        //body.put("date", testDate);
        body.put("dateTime", testDate);
        //body.put("applyNumber", 12);
        body.put("amount", new BigDecimal("1.09"));
        body.put("age", 66);
        body.put("longN", 125);
        //body.put("bol", false);
        //body.put("memo", "memo2");
        fileWriter.writeRow(body);

        fileWriter.writeTail(new HashMap<String, Object>());

        fileWriter.close();

        //校验文件
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(new File(config.getFilePath())), "UTF-8"));
        Assert.assertEquals("|totalCount:2|totalAmount:23.22", reader.readLine());
        Assert.assertEquals(
                "|seq:|instSeq:303|gmtApply:2017-01-03 12:22:33|date:20170103|dateTime:20170103 12:22:33|applyNumber:12|amount:1.22|age:33|longN:33|memo:|",
                reader.readLine());
        Assert.assertEquals(
                "|seq:seq234567|instSeq:505|gmtApply:2016-02-03 12:22:33|date:20160203|dateTime:20160203 12:22:33|applyNumber:12|amount:1.09|age:66|longN:125|bol:false|memo:memo2|",
                reader.readLine());

        Assert.assertEquals(
                "|seq:seq234568|instSeq:505|gmtApply:2016-02-03 12:22:33|dateTime:20160203 12:22:33|amount:1.09|age:66|longN:125|",
                reader.readLine());
        Assert.assertEquals(
                "|instSeq:999|gmtApply:2016-02-03 12:22:33|dateTime:20160203 12:22:33|amount:1.09|age:66|longN:125|",
                reader.readLine());

        Assert.assertEquals("|fileEnd:OFDCFEND|",reader.readLine());

        Assert.assertNull(reader.readLine());
        reader.close();

        FileReader fileReader = FileFactory.createReader(config);
        head = fileReader.readHead(HashMap.class);
        Assert.assertEquals(2L, head.get("totalCount"));

        body = fileReader.readRow(HashMap.class);
        Assert.assertNull(body.get("seq"));
        Assert.assertEquals("303", body.get("instSeq"));
        Assert.assertNull(body.get("memo"));

        Map<String, Object> tail = fileReader.readTail(HashMap.class);
        Assert.assertEquals("OFDCFEND", tail.get("fileEnd"));
        fileReader.close();
    }

    @After
    public void after() {
        tf.delete();
    }
}
