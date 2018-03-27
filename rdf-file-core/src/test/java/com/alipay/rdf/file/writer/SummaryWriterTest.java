package com.alipay.rdf.file.writer;

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

import com.alipay.rdf.file.interfaces.FileFactory;
import com.alipay.rdf.file.interfaces.FileWriter;
import com.alipay.rdf.file.model.FileConfig;
import com.alipay.rdf.file.model.StorageConfig;
import com.alipay.rdf.file.util.DateUtil;
import com.alipay.rdf.file.util.TemporaryFolderUtil;

/**
 * 汇总写入
 * 
 * @author hongwei.quhw
 * @version $Id: SummaryWriterTest.java, v 0.1 2018年3月6日 上午11:36:11 hongwei.quhw Exp $
 */
public class SummaryWriterTest {
    TemporaryFolderUtil tf = new TemporaryFolderUtil();

    @Before
    public void setUp() throws IOException {
        tf.create();
    }

    @Test
    public void test() throws Exception {
        String filePath = tf.getRoot().getAbsolutePath();
        System.out.println(filePath);
        FileConfig config = new FileConfig(new File(filePath, "test.txt").getAbsolutePath(),
            "/writer/template/sp_summary.json", new StorageConfig("nas"));
        config.setSummaryEnable(true);

        FileWriter fileWriter = FileFactory.createWriter(config);

        Map<String, Object> head = new HashMap<String, Object>();
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

        fileWriter.writeTail(fileWriter.geSummary().summaryTailToMap());

        fileWriter.close();

        //校验文件
        BufferedReader reader = new BufferedReader(
            new InputStreamReader(new FileInputStream(new File(config.getFilePath())), "UTF-8"));
        Assert.assertEquals("汇总文件测试", reader.readLine());
        Assert.assertEquals(
            "seq12345|303|2017-01-03 12:22:33|20170103|20170103 12:22:33|12|1.22|33|33|true|memo1",
            reader.readLine());
        Assert.assertEquals(
            "seq234567|505|2016-02-03 12:22:33|20160203|20160203 12:22:33|12|1.09|66|125|false|memo2",
            reader.readLine());
        Assert.assertEquals("2|2.31", reader.readLine());
        Assert.assertNull(reader.readLine());

        reader.close();

    }

    @After
    public void after() {
        tf.delete();
    }
}
