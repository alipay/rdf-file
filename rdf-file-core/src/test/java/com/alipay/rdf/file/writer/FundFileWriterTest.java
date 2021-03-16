package com.alipay.rdf.file.writer;

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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * 写测试
 * 
 * @author hongwei.quhw
 * @version $Id: FundFileWriterTest.java, v 0.1 2016-12-29 下午3:25:33 hongwei.quhw Exp $
 */
public class FundFileWriterTest {
    private TemporaryFolderUtil temporaryFolder = new TemporaryFolderUtil();

    @Before
    public void setUp() throws Exception {
        temporaryFolder.create();
    }

    @Test
    public void testWriter() throws Exception {
        String filePath = temporaryFolder.getRoot().getAbsolutePath();
        System.out.println(filePath);

        FileConfig config = new FileConfig(new File(filePath, "test.txt").getAbsolutePath(),
            "/writer/template/fund.cfg", new StorageConfig("nas"));
        FileWriter fileWriter = FileFactory.createWriter(config);

        Map<String, Object> head = new HashMap<String, Object>();
        head.put("msgRecipient", "xxx");
        head.put("sendDate", DateUtil.parse("20151204", "yyyyMMdd"));
        head.put("summaryTableNo", "aa");
        head.put("fileTypeCode", "bb");
        head.put("recipient", "ll");
        head.put("totalCount", 1);

        fileWriter.writeHead(head);

        Map<String, Object> row = new HashMap<String, Object>();
        row.put("TransactionCfmDate", DateUtil.parse("20151204", "yyyyMMdd"));
        row.put("FundCode", "中国1");
        row.put("AvailableVol", 42.11);
        fileWriter.writeRow(row);

        fileWriter.writeTail(new HashMap<String, Object>());

        fileWriter.close();

        //校验文件
        BufferedReader reader = new BufferedReader(
            new InputStreamReader(new FileInputStream(new File(filePath, "test.txt")), "UTF-8"));
        Assert.assertEquals("OFDCFDAT", reader.readLine());
        Assert.assertEquals("20  ", reader.readLine());
        Assert.assertEquals("H0       ", reader.readLine());
        Assert.assertEquals("xxx      ", reader.readLine());
        Assert.assertEquals("20151204", reader.readLine());
        Assert.assertEquals("aa ", reader.readLine());
        Assert.assertEquals("bb", reader.readLine());
        Assert.assertEquals("H0      ", reader.readLine());
        Assert.assertEquals("ll      ", reader.readLine());
        Assert.assertEquals("003", reader.readLine());
        Assert.assertEquals("TransactionCfmDate", reader.readLine());
        Assert.assertEquals("FundCode", reader.readLine());
        Assert.assertEquals("AvailableVol", reader.readLine());
        Assert.assertEquals("00000001", reader.readLine());

        Assert.assertEquals("20151204中国1 004211", reader.readLine());

        Assert.assertEquals("OFDCFEND", reader.readLine());

        reader.close();
    }

    @Test
    public void testWriterNegative() throws Exception {
        String filePath = temporaryFolder.getRoot().getAbsolutePath();
        System.out.println(filePath);

        FileConfig config = new FileConfig(new File(filePath, "test.txt").getAbsolutePath(),
                "/writer/template/fund2.cfg", new StorageConfig("nas"));
        FileWriter fileWriter = FileFactory.createWriter(config);

        Map<String, Object> head = new HashMap<String, Object>();
        head.put("msgRecipient", "xxx");
        head.put("sendDate", DateUtil.parse("20151204", "yyyyMMdd"));
        head.put("summaryTableNo", "aa");
        head.put("fileTypeCode", "bb");
        head.put("recipient", "ll");
        head.put("totalCount", 1);

        fileWriter.writeHead(head);

        Map<String, Object> row = new HashMap<String, Object>();
        row.put("TransactionCfmDate", DateUtil.parse("20151204", "yyyyMMdd"));
        row.put("FundCode", "中国1");
        row.put("AvailableVol", -42.11);
        fileWriter.writeRow(row);

        fileWriter.writeTail(new HashMap<String, Object>());

        fileWriter.close();

        FileReader fileReader = FileFactory.createReader(config);
        row = fileReader.readRow(HashMap.class);
        Assert.assertEquals(-42.11, row.get("AvailableVol"));

        //校验文件
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(new File(filePath, "test.txt")), "UTF-8"));
        Assert.assertEquals("OFDCFDAT", reader.readLine());
        Assert.assertEquals("20  ", reader.readLine());
        Assert.assertEquals("H0       ", reader.readLine());
        Assert.assertEquals("xxx      ", reader.readLine());
        Assert.assertEquals("20151204", reader.readLine());
        Assert.assertEquals("aa ", reader.readLine());
        Assert.assertEquals("bb", reader.readLine());
        Assert.assertEquals("H0      ", reader.readLine());
        Assert.assertEquals("ll      ", reader.readLine());
        Assert.assertEquals("003", reader.readLine());
        Assert.assertEquals("TransactionCfmDate", reader.readLine());
        Assert.assertEquals("FundCode", reader.readLine());
        Assert.assertEquals("AvailableVol", reader.readLine());
        Assert.assertEquals("00000001", reader.readLine());

        Assert.assertEquals("20151204中国1 -04211", reader.readLine());

        Assert.assertEquals("OFDCFEND", reader.readLine());

        reader.close();
    }

    @After
    public void after() {
        temporaryFolder.delete();
    }
}
