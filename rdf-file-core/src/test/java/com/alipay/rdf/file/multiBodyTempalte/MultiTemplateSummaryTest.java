package com.alipay.rdf.file.multiBodyTempalte;

import com.alipay.rdf.file.interfaces.FileFactory;
import com.alipay.rdf.file.interfaces.FileReader;
import com.alipay.rdf.file.interfaces.FileWriter;
import com.alipay.rdf.file.model.FileConfig;
import com.alipay.rdf.file.model.StorageConfig;
import com.alipay.rdf.file.util.TemporaryFolderUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 *
 * @author hongwei.quhw
 * @version $Id: MultiTemplateSummaryTest.java, v 0.1 2018年10月15日 下午2:42:17 hongwei.quhw Exp $
 */
public class MultiTemplateSummaryTest {
    TemporaryFolderUtil tf = new TemporaryFolderUtil();

    @Before
    public void setUp() throws IOException {
        tf.create();
    }

    @Test
    public void testSummary() {
        String filePath = tf.getRoot().getAbsolutePath();
        System.out.println(filePath);

        FileConfig config = new FileConfig(new File(filePath, "test.txt").getAbsolutePath(),
            "/multiBodyTemplate/template/template3_sp_summary.json", new StorageConfig("nas"));
        config.setSummaryEnable(true);

        FileWriter fileWriter = FileFactory.createWriter(config);

        HashMap<String, Object> row = new HashMap<String, Object>();
        row.put("seq", "1");
        row.put("bol", true);
        row.put("count", 10);
        fileWriter.writeRow(row);

        row = new HashMap<String, Object>();
        row.put("seq", "2");
        row.put("bol", true);
        row.put("count", 20);
        fileWriter.writeRow(row);

        row = new HashMap<String, Object>();
        row.put("seq", "3");
        row.put("bol", false);
        row.put("amount", new BigDecimal("12"));
        fileWriter.writeRow(row);

        row = new HashMap<String, Object>();
        row.put("seq", "4");
        row.put("bol", false);
        row.put("amount", new BigDecimal("24"));
        fileWriter.writeRow(row);

        fileWriter.writeTail(fileWriter.getSummary().summaryTailToMap());

        fileWriter.close();

        FileReader fileReader = FileFactory.createReader(config);
        Map<String, Object> tail = fileReader.readTail(HashMap.class);
        Assert.assertEquals(4, tail.get("totalCount"));
        Assert.assertEquals(30, tail.get("count"));
        Assert.assertEquals(new BigDecimal("36"), tail.get("totalAmount"));

        fileReader.close();
    }

    @After
    public void after() {
        tf.delete();
    }
}
