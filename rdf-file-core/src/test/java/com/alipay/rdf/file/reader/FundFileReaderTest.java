package com.alipay.rdf.file.reader;

import com.alipay.rdf.file.interfaces.FileFactory;
import com.alipay.rdf.file.interfaces.FileReader;
import com.alipay.rdf.file.model.FileConfig;
import com.alipay.rdf.file.model.StorageConfig;
import com.alipay.rdf.file.util.DateUtil;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * 
 * @author hongwei.quhw
 * @version $Id: FundFileReaderTest.java, v 0.1 2016-12-29 下午2:26:17 hongwei.quhw Exp $
 */
public class FundFileReaderTest {

    @Test
    public void testRead() throws IOException {
        FileConfig config = new FileConfig(
            File.class.getResource("/reader/fund/data/data1.txt").getPath(),
            "/reader/fund/template/data1.cfg", new StorageConfig("nas"));

        FileReader fileReader = FileFactory.createReader(config);

        Map<String, Object> head = fileReader.readHead(HashMap.class);
        Assert.assertEquals("OFDCFDAT", head.get("identity"));
        Assert.assertEquals("20", head.get("version"));
        Assert.assertEquals("H0", head.get("msgCreator"));
        Assert.assertEquals("xxx", head.get("msgRecipient"));
        Assert.assertEquals("20151204", DateUtil.format((Date) head.get("sendDate"), "yyyyMMdd"));
        Assert.assertEquals("aa", head.get("summaryTableNo"));
        Assert.assertEquals("bb", head.get("fileTypeCode"));
        Assert.assertEquals("H0", head.get("sender"));
        Assert.assertEquals("ll", head.get("recipient"));
        Assert.assertEquals(10, head.get("totalCount"));

        int count = (Integer) head.get("totalCount");

        Map<String, Object> row = null;
        for (int i = 0; i < count; i++) {
            row = fileReader.readRow(HashMap.class);
        }

        Assert.assertEquals("20151204",
            DateUtil.format((Date) row.get("TransactionCfmDate"), "yyyyMMdd"));
        Assert.assertEquals("中国9", row.get("FundCode"));
        Assert.assertEquals(new Integer(9), row.get("AvailableVol"));

        fileReader.close();
    }

    @Test
    public void testRead2() throws IOException {
        FileConfig config = new FileConfig(
            File.class.getResource("/reader/fund/data/data2.txt").getPath(),
            "/reader/fund/template/data1.cfg", new StorageConfig("nas"));

        FileReader fileReader = FileFactory.createReader(config);

        Map<String, Object> head = fileReader.readHead(HashMap.class);
        Assert.assertEquals("OFDCFDAT", head.get("identity"));
        Assert.assertEquals("20", head.get("version"));
        Assert.assertEquals("H0", head.get("msgCreator"));
        Assert.assertNull(head.get("msgRecipient"));
        Assert.assertEquals("20151204", DateUtil.format((Date) head.get("sendDate"), "yyyyMMdd"));
        Assert.assertNull(head.get("summaryTableNo"));
        Assert.assertEquals("bb", head.get("fileTypeCode"));
        Assert.assertEquals("H0", head.get("sender"));
        Assert.assertEquals("ll", head.get("recipient"));
        Assert.assertEquals(10, head.get("totalCount"));

        int count = (Integer) head.get("totalCount");

        Map<String, Object> row = null;
        for (int i = 0; i < count; i++) {
            row = fileReader.readRow(HashMap.class);
        }

        Assert.assertEquals("20151204",
            DateUtil.format((Date) row.get("TransactionCfmDate"), "yyyyMMdd"));
        Assert.assertEquals("中国9", row.get("FundCode"));
        Assert.assertEquals(new Integer(9), row.get("AvailableVol"));

        fileReader.close();
    }

    @Test
    public void readTail() throws IOException {
        FileConfig config = new FileConfig(
            File.class.getResource("/reader/fund/data/data1.txt").getPath(),
            "/reader/fund/template/data1.cfg", new StorageConfig("nas"));

        FileReader fileReader = FileFactory.createReader(config);

        Map<String, Object> tail = fileReader.readTail(HashMap.class);
        Assert.assertEquals("OFDCFEND", tail.get("fileEnd"));
    }
}
