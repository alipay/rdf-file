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
 * @Author: hongwei.quhw 2021/9/10 2:14 下午
 */
public class RowNosqlIndexTest {
    TemporaryFolderUtil tf = new TemporaryFolderUtil();

    @Before
    public void setUp() throws IOException {
        tf.create();
    }

    // 参数指定
    @Test
    public void test1() throws IOException {
        String filePath = tf.getRoot().getAbsolutePath();
        FileConfig config = new FileConfig(new File(filePath, "test.txt").getAbsolutePath(),
                "/codec/index/template/template1.json", new StorageConfig("nas"));
        config.setRowCodecMode("index");

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
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(config.getFilePath())), "UTF-8"));
        Assert.assertEquals("总笔数:2|总金额:23.22|colMeta(idx:0,1)", reader.readLine());
        Assert.assertEquals("流水号|基金公司订单号|订单申请时间|普通日期|普通日期时间|普通数字|金额|年龄|长整型|布尔值|备注", reader.readLine());
        Assert.assertEquals("seq12345|303|2017-01-03 12:22:33|20170103|20170103 12:22:33|12|1.22|33|33|true|memo1|colMeta(idx:0,1,2,3,4,5,6,7,8,9,10)", reader.readLine());
        Assert.assertEquals("seq234567|505|2016-02-03 12:22:33|20160203|20160203 12:22:33|12|66|125|false|memo2|colMeta(idx:0,1,2,3,4,5,7,8,9,10)", reader.readLine());
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

    // 模板指定codec， 模板配置索引位置  空但是存在默认值
    @Test
    public void test2() throws IOException {
        String filePath = tf.getRoot().getAbsolutePath();
        FileConfig config = new FileConfig(new File(filePath, "test.txt").getAbsolutePath(),
                "/codec/index/template/template2.json", new StorageConfig("nas"));

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
        //body.put("memo", "memo1");
        fileWriter.writeRow(body);

        testDate = DateUtil.parse("2016-02-03 12:22:33", "yyyy-MM-dd HH:mm:ss");
        body = new HashMap<String, Object>();
        body.put("seq", "");
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
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(config.getFilePath())), "UTF-8"));
        Assert.assertEquals("colMeta(idx:0,1)|总笔数:2|总金额:23.22", reader.readLine());
        Assert.assertEquals("流水号|基金公司订单号|订单申请时间|普通日期|普通日期时间|普通数字|金额|年龄|长整型|布尔值|备注", reader.readLine());
        Assert.assertEquals("colMeta(idx:0,1,2,3,4,5,6,7,8,9,10)|seq12345|303|2017-01-03 12:22:33|20170103|20170103 12:22:33|12|1.22|33|33|true|defaultMemo", reader.readLine());
        Assert.assertEquals("colMeta(idx:0,1,2,3,4,5,7,8,9,10)||505|2016-02-03 12:22:33|20160203|20160203 12:22:33|12|66|125|false|memo2", reader.readLine());
        reader.close();

        FileReader fileReader = FileFactory.createReader(config);
        head = fileReader.readHead(HashMap.class);
        Assert.assertEquals(2L, head.get("totalCount"));
        Assert.assertEquals(new BigDecimal("23.22"), head.get("totalAmount"));

        body = fileReader.readRow(HashMap.class);
        Assert.assertEquals("defaultMemo", body.get("memo"));
        Assert.assertEquals(new BigDecimal("1.22"), body.get("amount"));

        body = fileReader.readRow(HashMap.class);
        Assert.assertEquals(null, body.get("seq"));
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

    @After
    public void after() {
        tf.delete();
    }
}
