package com.alipay.rdf.file.codec;

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
     * de 有columnInfo的序列化与反序列化
     */
    @Test
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
        body.put("applyNumber", new BigDecimal("12"));
        body.put("amount", new BigDecimal("1.22"));
        body.put("age", new Integer(33));
        body.put("longN", new Long(33));
        body.put("bol", true);
        body.put("memo", "memo1");
        fileWriter.writeRow(body);

        testDate = DateUtil.parse("2016-02-03 12:22:33", "yyyy-MM-dd HH:mm:ss");
        body = new HashMap<String, Object>();
        body.put("seq", "seq234567");
        body.put("instSeq", "505");
        body.put("gmtApply", testDate);
        body.put("date", testDate);
        body.put("dateTime", testDate);
        body.put("applyNumber", new BigDecimal("12"));
        //body.put("amount", new BigDecimal("1.09"));
        body.put("age", 66);
        body.put("longN", 125);
        body.put("bol", false);
        body.put("memo", "memo2");
        fileWriter.writeRow(body);

        fileWriter.close();

        //校验文件
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(new File(config.getFilePath())), "UTF-8"));
        Assert.assertEquals("totalCount:总笔数:2|totalAmount:总金额:23.22", reader.readLine());
        Assert.assertEquals("流水号|基金公司订单号|订单申请时间|普通日期|普通日期时间|普通数字|金额|年龄|长整型|布尔值|备注",
                reader.readLine());
        Assert.assertEquals(
                "seq:seq12345|instSeq:303|gmtApply:2017-01-03 12:22:33|date:20170103|dateTime:20170103 12:22:33|applyNumber:12|amount:1.22|age:33|longN:33|bol:true|memo:memo1",
                reader.readLine());
        Assert.assertEquals(
                "seq:seq234567|instSeq:505|gmtApply:2016-02-03 12:22:33|date:20160203|dateTime:20160203 12:22:33|applyNumber:12|age:66|longN:125|bol:false|memo:memo2",
                reader.readLine());

        reader.close();

        FileReader fileReader = FileFactory.createReader(config);
        head = fileReader.readHead(HashMap.class);
        Assert.assertEquals(2L, head.get("totalCount"));
        Assert.assertEquals(new BigDecimal("23.22"), head.get("totalAmount"));

        body = fileReader.readRow(HashMap.class);
        body = fileReader.readRow(HashMap.class);

        Assert.assertEquals("seq234567", body.get("seq"));
        Assert.assertEquals("505", body.get("instSeq"));
        Assert.assertEquals("2016-02-03 12:22:33", DateUtil.format((Date) body.get("gmtApply"), "yyyy-MM-dd HH:mm:ss"));
        Assert.assertEquals(new BigDecimal("12"), body.get("applyNumber"));
        Assert.assertEquals(null, body.get("amount"));
        Assert.assertEquals(66, body.get("age"));
        Assert.assertEquals(125L, body.get("longN"));
        Assert.assertEquals(false, body.get("bol"));
        Assert.assertEquals("memo2", body.get("memo"));

        fileReader.close();
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

        Assert.assertEquals("|fileEnd:OFDCFEND|", reader.readLine());

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

    @Test
    public void testKVSplit() throws Exception {
        String filePath = tf.getRoot().getAbsolutePath();
        FileConfig config = new FileConfig(new File(filePath, "test.txt").getAbsolutePath(),
                "/codec/kv/template/template6.json", new StorageConfig("nas"));

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

        fileWriter.writeTail(new HashMap<String, Object>());

        fileWriter.close();

        //校验文件
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(new File(config.getFilePath())), "UTF-8"));
        Assert.assertEquals("|totalCount_@_2|totalAmount_@_23.22", reader.readLine());
        Assert.assertEquals(
                "|seq_@_|instSeq_@_303|gmtApply_@_2017-01-03 12:22:33|date_@_20170103|dateTime_@_20170103 12:22:33|applyNumber_@_12|amount_@_1.22|age_@_33|longN_@_33|memo_@_|",
                reader.readLine());

        Assert.assertEquals(
                "|seq_@_seq234568|instSeq_@_505|gmtApply_@_2016-02-03 12:22:33|dateTime_@_20160203 12:22:33|amount_@_1.09|age_@_66|longN_@_125|",
                reader.readLine());

        Assert.assertEquals("|fileEnd_@_OFDCFEND|", reader.readLine());

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

    // 多模板测试
    @Test
    public void testMultiTemlate() throws Exception {
        String filePath = tf.getRoot().getAbsolutePath();
        FileConfig config = new FileConfig(new File(filePath, "test.txt").getAbsolutePath(),
                "/codec/kv/template/template7.json", new StorageConfig("nas"));

        FileWriter fileWriter = FileFactory.createWriter(config);

        Map<String, Object> head = new HashMap<String, Object>();
        head.put("totalCount", 2L);
        head.put("totalAmount", new BigDecimal("23.22"));
        fileWriter.writeHead(head);

        Map<String, Object> body = new HashMap<String, Object>();
        body.put("seq", "seq12345");
        body.put("instSeq", "303");
        body.put("bol", true);
        body.put("memo", "memo1");
        fileWriter.writeRow(body);

        body = new HashMap<String, Object>();
        body.put("seq", "seq14345");
        body.put("amount", new BigDecimal("1.09"));
        body.put("age", 33);
        body.put("longN", 125L);
        body.put("bol", false);
        body.put("memo", "memo2");
        fileWriter.writeRow(body);

        body = new HashMap<String, Object>();
        body.put("seq", "seq4521");
        body.put("instSeq", "505");
        body.put("gmtApply", DateUtil.parse("2016-02-03 12:22:33", "yyyy-MM-dd HH:mm:ss"));
        body.put("date", DateUtil.parse("20160203", "yyyyMMdd"));
        body.put("dateTime", DateUtil.parse("20160203 12:22:33", "yyyyMMdd HH:mm:ss"));
        body.put("applyNumber", 12);
        body.put("amount", new BigDecimal("1.09"));
        body.put("age", 33);
        body.put("longN", 67L);
        body.put("bol", false);
        body.put("memo", "memo2");
        fileWriter.writeRow(body);

        fileWriter.close();

        //校验文件
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(new File(config.getFilePath())), "UTF-8"));
        Assert.assertEquals("totalCount:2|totalAmount:23.22", reader.readLine());
        Assert.assertEquals("seq:seq12345|instSeq:303|bol:true|memo:memo1", reader.readLine());
        Assert.assertEquals("seq:seq14345|amount:1.09|age:33|longN:125|bol:false|memo:memo2", reader.readLine());
        Assert.assertEquals("seq:seq4521|instSeq:505|gmtApply:2016-02-03 12:22:33|date:20160203|dateTime:20160203 12:22:33|applyNumber:12|amount:1.09|age:33|longN:67|bol:false|memo:memo2", reader.readLine());
        Assert.assertNull(reader.readLine());

        try {

            FileReader fileReader = FileFactory.createReader(config);

            head = fileReader.readHead(HashMap.class);
            Assert.assertEquals(new Long(2), head.get("totalCount"));
            Assert.assertEquals(new BigDecimal("23.22"), head.get("totalAmount"));

            Map<String, Object> row = fileReader.readRow(HashMap.class);
            Assert.assertEquals("seq12345", row.get("seq"));
            Assert.assertEquals("303", row.get("instSeq"));
            Assert.assertEquals(true, row.get("bol"));
            Assert.assertEquals("memo1", row.get("memo"));

            row = fileReader.readRow(HashMap.class);
            Assert.assertEquals("seq14345", row.get("seq"));
            Assert.assertEquals(new BigDecimal("1.09"), row.get("amount"));
            Assert.assertEquals(33, row.get("age"));
            Assert.assertEquals(new Long(125), row.get("longN"));
            Assert.assertEquals(false, row.get("bol"));
            Assert.assertEquals("memo2", row.get("memo"));

            row = fileReader.readRow(HashMap.class);
            Assert.assertEquals("seq4521", row.get("seq"));
            Assert.assertEquals("505", row.get("instSeq"));
            Assert.assertEquals("2016-02-03 12:22:33",
                    DateUtil.format((Date) row.get("gmtApply"), "yyyy-MM-dd HH:mm:ss"));
            Assert.assertEquals("20160203", DateUtil.format((Date) row.get("date"), "yyyyMMdd"));
            Assert.assertEquals("20160203 12:22:33",
                    DateUtil.format((Date) row.get("dateTime"), "yyyyMMdd HH:mm:ss"));
            Assert.assertEquals(12, row.get("applyNumber"));
            Assert.assertEquals(new BigDecimal("1.09"), row.get("amount"));
            Assert.assertEquals(33, row.get("age"));
            Assert.assertEquals(new Long(67), row.get("longN"));
            Assert.assertEquals(false, row.get("bol"));
            Assert.assertEquals("memo2", row.get("memo"));

            Assert.assertNull(fileReader.readRow(HashMap.class));

            fileReader.close();
            Assert.fail();
        } catch (ArrayIndexOutOfBoundsException e) {
            // 多模板不应该使用nosql模型（kv）, 要支持也可以， RdfFileRowConditionSpi 行路由要自己实现， 目前内置MatchRowCondition， 不支持
        }
    }

    @After
    public void after() {
        tf.delete();
    }
}
