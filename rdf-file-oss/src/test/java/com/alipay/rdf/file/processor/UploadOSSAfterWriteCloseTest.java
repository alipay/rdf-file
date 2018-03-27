package com.alipay.rdf.file.processor;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.alipay.rdf.file.exception.RdfErrorEnum;
import com.alipay.rdf.file.exception.RdfFileException;
import com.alipay.rdf.file.interfaces.FileFactory;
import com.alipay.rdf.file.interfaces.FileStorage;
import com.alipay.rdf.file.interfaces.FileWriter;
import com.alipay.rdf.file.model.FileConfig;
import com.alipay.rdf.file.model.FileDefaultConfig;
import com.alipay.rdf.file.model.StorageConfig;
import com.alipay.rdf.file.storage.FileInnterStorage;
import com.alipay.rdf.file.storage.OssConfig;
import com.alipay.rdf.file.util.DateUtil;
import com.alipay.rdf.file.util.OssTestUtil;
import com.alipay.rdf.file.util.RdfFileUtil;
import com.alipay.rdf.file.util.TemporaryFolderUtil;
import com.alipay.rdf.file.util.TestLog;

/**
 * oss 写完自动上传到oss
 * 
 * @author hongwei.quhw
 * @version $Id: UploadOSSAfterWriteCloseTest.java, v 0.1 2017年8月23日 上午10:33:11 hongwei.quhw Exp $
 */
public class UploadOSSAfterWriteCloseTest {
    private static final StorageConfig storageConfig = OssTestUtil.geStorageConfig();
    private static final String        ossPath       = "rdf/rdf-file/osswriter";
    private static FileStorage         fileStorage   = FileFactory.createStorage(storageConfig);
    private OssConfig                  ossConfig;
    private static TemporaryFolderUtil tf            = new TemporaryFolderUtil();

    @Before
    public void setUp() throws Exception {
        FileDefaultConfig defaultConfig = new FileDefaultConfig();
        defaultConfig.setCommonLog(new TestLog());
        tf.create();

        ossConfig = (OssConfig) storageConfig.getParam(OssConfig.OSS_STORAGE_CONFIG_KEY);
        ossConfig.setOssTempRoot(tf.getRoot().getAbsolutePath());
        System.out.println(tf.getRoot().getAbsolutePath());
    }

    @Test
    public void testWriter() throws Exception {

        FileConfig config = new FileConfig(RdfFileUtil.combinePath(ossPath, "test.txt"),
            "/processor/template1.json", storageConfig);
        config.setLineBreak("\r");
        config.setFileEncoding("UTF-8");
        writeAndValide(config);

        config.setLineBreak("\n");
        writeAndValide(config);

        config.setLineBreak("\r\n");
        writeAndValide(config);

        try {
            config.setLineBreak("\\c");
            writeAndValide(config);
            Assert.fail();
        } catch (RdfFileException e) {
            Assert.assertEquals(RdfErrorEnum.UNSUPPORT_LINEBREAK, e.getErrorEnum());
        }
    }

    private void writeAndValide(FileConfig config) throws Exception {
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

        InputStream is = ((FileInnterStorage) fileStorage).getInputStream(config.getFilePath());
        //校验文件
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
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

    /**
     * oss append  大小限制5G
     * 
     * @throws Exception
     */
    @Test
    public void testAppendWriter() throws Exception {
        FileConfig config = new FileConfig(RdfFileUtil.combinePath(ossPath, "append.txt"),
            "/processor/template1.json", storageConfig);
        config.setAppend(true);
        FileWriter headWriter = FileFactory.createWriter(config);

        Map<String, Object> head = new HashMap<String, Object>();
        head.put("totalCount", 2);
        head.put("totalAmount", new BigDecimal("23.22"));
        headWriter.writeHead(head);
        headWriter.close();

        FileWriter bodyWriter = FileFactory.createWriter(config);
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
        bodyWriter.writeRow(body);
        bodyWriter.close();

        bodyWriter = FileFactory.createWriter(config);
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
        bodyWriter.writeRow(body);
        bodyWriter.close();

        InputStream is = ((FileInnterStorage) fileStorage).getInputStream(config.getFilePath());
        //校验文件
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
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

    @AfterClass
    public static void after() {
        fileStorage.delete(ossPath);
        tf.delete();
    }
}
