package com.alipay.rdf.file.writer;

import com.alipay.rdf.file.interfaces.FileFactory;
import com.alipay.rdf.file.interfaces.FileWriter;
import com.alipay.rdf.file.model.FileConfig;
import com.alipay.rdf.file.model.StorageConfig;
import com.alipay.rdf.file.util.DateUtil;
import com.alipay.rdf.file.util.TemporaryFolderUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * de 写测试
 *
 * @author hongwei.quhw
 * @version $Id: FileWriterDeTest.java, v 0.1 2017年8月10日 下午7:02:59 hongwei.quhw Exp $
 */
public class CmFileWriterTest {
    TemporaryFolderUtil tf = new TemporaryFolderUtil();

    @Before
    public void setUp() throws IOException {
        tf.create();
    }

    @Test
    public void testWriter() throws Exception {
        String filePath = tf.getRoot().getAbsolutePath();
        System.out.println(filePath);

        FileConfig config = new FileConfig(new File(filePath, "test.txt").getAbsolutePath(),
                "/writer/template/cm.json", new StorageConfig("nas"));
        config.setLineBreak("\r");
        config.setFileEncoding("UTF-8");

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
        Assert.assertEquals("总笔数|总金额", reader.readLine());
        Assert.assertEquals("2|23.22", reader.readLine());
        Assert.assertEquals("流水号|基金公司订单号|订单申请时间|普通日期|普通日期时间|普通数字|金额|年龄|长整型|布尔值|备注",
                reader.readLine());
        Assert.assertEquals(
                "seq12345|303|2017-01-03 12:22:33|20170103|20170103 12:22:33|12|1.22|33|33|true|memo1",
                reader.readLine());
        Assert.assertEquals(
                "seq234567|505|2016-02-03 12:22:33|20160203|20160203 12:22:33|12|1.09|66|125|false|memo2",
                reader.readLine());

        Assert.assertNull(reader.readLine());

        reader.close();
    }

    @Test
    public void testWriter2() throws Exception {
        String filePath = tf.getRoot().getAbsolutePath();
        System.out.println(filePath);

        FileConfig config = new FileConfig(new File(filePath, "test.txt").getAbsolutePath(),
                "/writer/template/cm2.json", new StorageConfig("nas"));
        config.setLineBreak("\r");
        config.setFileEncoding("UTF-8");

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

        fileWriter.writeTail(new HashMap<String, Object>());

        fileWriter.close();

        //校验文件
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(new File(config.getFilePath())), "UTF-8"));
        Assert.assertEquals("总笔数|总金额", reader.readLine());
        Assert.assertEquals("2|23.22", reader.readLine());
        Assert.assertEquals("流水号|基金公司订单号|订单申请时间|普通日期|普通日期时间|普通数字|金额|年龄|长整型|布尔值|备注",
                reader.readLine());
        Assert.assertEquals(
                "seq12345|303|2017-01-03 12:22:33|20170103|20170103 12:22:33|12|1.22|33|33|true|memo1",
                reader.readLine());
        Assert.assertEquals(
                "seq234567|505|2016-02-03 12:22:33|20160203|20160203 12:22:33|12|1.09|66|125|false|memo2",
                reader.readLine());

        Assert.assertEquals("文件结束A|文件结束B", reader.readLine());
        Assert.assertEquals("AEnd|BEnd", reader.readLine());
        Assert.assertNull(reader.readLine());
        reader.close();
    }

    @Test
    public void testWriter3() throws Exception {
        String filePath = tf.getRoot().getAbsolutePath();
        System.out.println(filePath);

        FileConfig config = new FileConfig(new File(filePath, "test.txt").getAbsolutePath(),
                "/writer/template/cm3.json", new StorageConfig("nas"));
        config.setLineBreak("\r");
        config.setFileEncoding("UTF-8");

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

        fileWriter.writeTail(new HashMap<String, Object>());

        fileWriter.close();

        //校验文件
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(new File(config.getFilePath())), "UTF-8"));
        Assert.assertEquals("|总笔数|总金额", reader.readLine());
        Assert.assertEquals("|2|23.22", reader.readLine());
        Assert.assertEquals("|流水号|基金公司订单号|订单申请时间|普通日期|普通日期时间|普通数字|金额|年龄|长整型|布尔值|备注",
                reader.readLine());
        Assert.assertEquals(
                "|seq12345|303|2017-01-03 12:22:33|20170103|20170103 12:22:33|12|1.22|33|33|true|memo1|",
                reader.readLine());
        Assert.assertEquals(
                "|seq234567|505|2016-02-03 12:22:33|20160203|20160203 12:22:33|12|1.09|66|125|false|memo2|",
                reader.readLine());

        Assert.assertEquals("|文件结束A|文件结束B|", reader.readLine());
        Assert.assertEquals("|AEnd|BEnd|", reader.readLine());
        Assert.assertNull(reader.readLine());
        reader.close();
    }

    @Test
    public void testWriter4() throws Exception {
        String filePath = tf.getRoot().getAbsolutePath();
        System.out.println(filePath);

        FileConfig config = new FileConfig(new File(filePath, "test.txt").getAbsolutePath(),
                "/writer/template/cm_ext.json", new StorageConfig("nas"));
        config.setLineBreak("\r");
        config.setFileEncoding("UTF-8");

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

        fileWriter.writeTail(new HashMap<String, Object>());

        fileWriter.close();

        //校验文件
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(new File(config.getFilePath())), "UTF-8"));
        Assert.assertEquals("总笔数", reader.readLine());
        Assert.assertEquals("总金额", reader.readLine());
        Assert.assertEquals("2", reader.readLine());
        Assert.assertEquals("23.22", reader.readLine());
        Assert.assertEquals("|流水号|基金公司订单号|订单申请时间|普通日期|普通日期时间|普通数字|金额|年龄|长整型|布尔值|备注",
                reader.readLine());
        Assert.assertEquals(
                "|seq12345|303|2017-01-03 12:22:33|20170103|20170103 12:22:33|12|1.22|33|33|true|memo1|",
                reader.readLine());
        Assert.assertEquals(
                "|seq234567|505|2016-02-03 12:22:33|20160203|20160203 12:22:33|12|1.09|66|125|false|memo2|",
                reader.readLine());

        Assert.assertEquals("文件结束A", reader.readLine());
        Assert.assertEquals("文件结束B", reader.readLine());
        Assert.assertEquals("AEnd", reader.readLine());
        Assert.assertEquals("BEnd", reader.readLine());
        Assert.assertNull(reader.readLine());
        reader.close();
    }

    @After
    public void after() {
        tf.delete();
    }
}
