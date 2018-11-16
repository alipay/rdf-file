package com.alipay.rdf.file.reader;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.alipay.rdf.file.interfaces.FileFactory;
import com.alipay.rdf.file.interfaces.FileReader;
import com.alipay.rdf.file.model.FileConfig;
import com.alipay.rdf.file.model.StorageConfig;
import com.alipay.rdf.file.model.Summary;
import com.alipay.rdf.file.util.TemporaryFolderUtil;

import junit.framework.Assert;

/**
 * 汇总 & 统计读
 * 
 * @author hongwei.quhw
 * @version $Id: FileReaderDeTest.java, v 0.1 2017年4月7日 下午5:38:15 hongwei.quhw Exp $
 */
public class SpFileReaderTest2 {
    TemporaryFolderUtil tf = new TemporaryFolderUtil();

    @Before
    public void setUp() throws IOException {
        tf.create();
    }

    @Test
    public void testReadSpFile() throws Exception {
        String filePath = File.class.getResource("/reader/sp/data/data6.txt").getPath();

        FileConfig config = new FileConfig(filePath, "/reader/sp/template/template4.json",
            new StorageConfig("nas"));
        config.setSummaryEnable(true);

        FileReader fileReader = FileFactory.createReader(config);
        Map<String, Object> head = fileReader.readHead(HashMap.class);
        System.out.println(head);

        Map<String, Object> row = null;
        while (null != (row = fileReader.readRow(HashMap.class))) {
            System.out.println(row);
        }

        Map<String, Object> tail = fileReader.readTail(HashMap.class);
        System.out.println(tail);

        Summary summary = fileReader.getSummary();

        Map<String, Object> summaryHead = summary.summaryHeadToMap();
        Assert.assertEquals(head.get("failAmount"), summaryHead.get("failAmount"));
        Assert.assertEquals(head.get("typeASuccessAmount"), summaryHead.get("typeASuccessAmount"));
        Assert.assertEquals(head.get("applyFailAmount"), summaryHead.get("applyFailAmount"));
        Assert.assertEquals(head.get("applyAmount"), summaryHead.get("applyAmount"));
        Assert.assertEquals(head.get("typeBAmount"), summaryHead.get("typeBAmount"));
        Assert.assertEquals(head.get("typeBSuccessAmount"), summaryHead.get("typeBSuccessAmount"));
        Assert.assertEquals(head.get("typeAAmount"), summaryHead.get("typeAAmount"));
        Assert.assertEquals(head.get("successAmount"), summaryHead.get("successAmount"));
        Assert.assertEquals(head.get("totalAmount"), summaryHead.get("totalAmount"));

        Map<String, Object> summaryTail = summary.summaryTailToMap();
        Assert.assertEquals(tail.get("failCount"), summaryTail.get("failCount"));
        Assert.assertEquals(tail.get("totalCount"), summaryTail.get("totalCount"));
        Assert.assertEquals(tail.get("totalBCount"), summaryTail.get("totalBCount"));
        Assert.assertEquals(tail.get("totalACount"), summaryTail.get("totalACount"));
        Assert.assertEquals(tail.get("totalBFailCount"), summaryTail.get("totalBFailCount"));
        Assert.assertEquals(tail.get("totalAFailCount"), summaryTail.get("totalAFailCount"));
        Assert.assertEquals(tail.get("successCount"), summaryTail.get("successCount"));

        fileReader.close();
    }

}
