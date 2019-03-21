package com.alipay.rdf.file.writer;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.alipay.rdf.file.exception.RdfErrorEnum;
import com.alipay.rdf.file.exception.RdfFileException;
import com.alipay.rdf.file.interfaces.FileFactory;
import com.alipay.rdf.file.interfaces.FileReader;
import com.alipay.rdf.file.interfaces.FileWriter;
import com.alipay.rdf.file.model.FileConfig;
import com.alipay.rdf.file.model.FileDefaultConfig;
import com.alipay.rdf.file.model.StorageConfig;
import com.alipay.rdf.file.util.DateUtil;
import com.alipay.rdf.file.util.TemporaryFolderUtil;
import com.alipay.rdf.file.util.TestLog;

/**
 * de 写测试
 * 
 * @author hongwei.quhw
 * @version $Id: FileWriterDeTest.java, v 0.1 2017年8月10日 下午7:02:59 hongwei.quhw Exp $
 */
public class DeSeperatorFileWriterTest {
    TemporaryFolderUtil tf = new TemporaryFolderUtil();

    @Before
    public void setUp() throws IOException {
        tf.create();
        new FileDefaultConfig().setCommonLog(new TestLog());
    }

    @Test
    public void testWriter() throws Exception {
        String filePath = tf.getRoot().getAbsolutePath();
        System.out.println(filePath);

        FileConfig config = new FileConfig(new File(filePath, "test.txt").getAbsolutePath(),
            "/writer/template/seperate_template.json", new StorageConfig("nas"));

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
        Assert.assertEquals("总笔数:2\u0001总金额:23.22", reader.readLine());
        Assert.assertEquals(
            "流水号\u0001基金公司订单号\u0001订单申请时间\u0001普通日期\u0001普通日期时间\u0001普通数字\u0001金额\u0001年龄\u0001长整型\u0001布尔值\u0001备注",
            reader.readLine());
        Assert.assertEquals(
            "seq12345\u0001303\u00012017-01-03 12:22:33\u000120170103\u000120170103 12:22:33\u000112\u00011.22\u000133\u000133\u0001true\u0001memo1",
            reader.readLine());
        Assert.assertEquals(
            "seq234567\u0001505\u00012016-02-03 12:22:33\u000120160203\u000120160203 12:22:33\u000112\u00011.09\u000166\u0001125\u0001false\u0001memo2",
            reader.readLine());

        reader.close();

        FileReader fileReader = FileFactory.createReader(config);
        Map<String, Object> validatedHead = fileReader.readHead(HashMap.class);
        assertEquals(head.get("totalCount"), validatedHead.get("totalCount"));
        assertEquals(head.get("totalAmount"), validatedHead.get("totalAmount"));

        Map<String, Object> validateRow = fileReader.readRow(HashMap.class);
        Assert.assertEquals("seq12345", validateRow.get("seq"));
        Assert.assertEquals("2017-01-03 12:22:33",
            DateUtil.format((Date) validateRow.get("dateTime"), "yyyy-MM-dd HH:mm:ss"));

        validateRow = fileReader.readRow(HashMap.class);
        Assert.assertEquals("seq234567", validateRow.get("seq"));
        Assert.assertEquals("505", validateRow.get("instSeq"));
        Assert.assertEquals("2016-02-03 12:22:33",
            DateUtil.format((Date) validateRow.get("gmtApply"), "yyyy-MM-dd HH:mm:ss"));
        Assert.assertEquals("2016-02-03",
            DateUtil.format((Date) validateRow.get("date"), "yyyy-MM-dd"));
        Assert.assertEquals("2016-02-03 12:22:33",
            DateUtil.format((Date) validateRow.get("dateTime"), "yyyy-MM-dd HH:mm:ss"));
        Assert.assertEquals(new Long(12), validateRow.get("applyNumber"));
        Assert.assertEquals(new BigDecimal("1.09"), validateRow.get("amount"));
        Assert.assertEquals(66, validateRow.get("age"));
        Assert.assertEquals(new Long(125), validateRow.get("longN"));
        Assert.assertEquals(false, validateRow.get("bol"));
        Assert.assertEquals("memo2", validateRow.get("memo"));

        fileReader.close();
    }

    @Test
    public void testWriter2() throws Exception {
        String filePath = tf.getRoot().getAbsolutePath();
        System.out.println(filePath);

        FileConfig config = new FileConfig(new File(filePath, "test.txt").getAbsolutePath(),
            "/writer/template/seperate_template2.json", new StorageConfig("nas"));

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
        Assert.assertEquals("总笔数:2 总金额:23.22", reader.readLine());
        Assert.assertEquals("流水号 基金公司订单号 订单申请时间 普通日期 普通日期时间 普通数字 金额 年龄 长整型 布尔值 备注",
            reader.readLine());
        Assert.assertEquals(
            "seq12345 303 2017-01-03 12:22:33 20170103 20170103 12:22:33 12 1.22 33 33 true memo1",
            reader.readLine());
        Assert.assertEquals(
            "seq234567 505 2016-02-03 12:22:33 20160203 20160203 12:22:33 12 1.09 66 125 false memo2",
            reader.readLine());

        reader.close();

        FileReader fileReader = FileFactory.createReader(config);
        Map<String, Object> validatedHead = fileReader.readHead(HashMap.class);
        assertEquals(head.get("totalCount"), validatedHead.get("totalCount"));
        assertEquals(head.get("totalAmount"), validatedHead.get("totalAmount"));

        // 字段内容本身存在空格
        try {
            fileReader.readRow(HashMap.class);
            Assert.fail();
        } catch (RdfFileException e) {
            Assert.assertEquals(RdfErrorEnum.DESERIALIZE_ERROR, e.getErrorEnum());
        }

        fileReader.close();
    }

    @After
    public void after() {
        tf.delete();
    }
}
