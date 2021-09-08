package com.alipay.rdf.file.summary;

import com.alipay.rdf.file.interfaces.FileFactory;
import com.alipay.rdf.file.interfaces.FileValidator;
import com.alipay.rdf.file.interfaces.FileWriter;
import com.alipay.rdf.file.model.*;
import com.alipay.rdf.file.util.TemporaryFolderUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  Kv编码解码汇总统计测试
 */
public class SummaryKVCodecTest {
    TemporaryFolderUtil tf = new TemporaryFolderUtil();

    @Before
    public void setUp() throws IOException {
        tf.create();
    }

    @Test
    public void test1() {
        String filePath = tf.getRoot().getAbsolutePath();
        filePath = "/var/folders/67/_2h9s51d17b9vddthhjbh2p80000gp/T/4430911874749143438";
        FileConfig config = new FileConfig(new File(filePath, "test.txt").getAbsolutePath(), "/summary/template3.json", new StorageConfig("nas"));
        config.setSummaryEnable(true);
        FileWriter fileWriter = FileFactory.createWriter(config);

        Map<String, Object> head = new HashMap<String, Object>();
        head.put("totalCount", new BigDecimal(4));
        head.put("totalAmount", new BigDecimal("500"));
        head.put("successAmount", new BigDecimal("200"));
        head.put("successCount", 1L);
        fileWriter.writeHead(head);

        Map<String, Object> row = new HashMap<String, Object>();
        row.put("seq", "abcd1234");
        row.put("instSeq", "instSeq1");
        row.put("amount", new BigDecimal("100"));
        row.put("age", 15);
        row.put("bol", true);
        fileWriter.writeRow(row);

        row = new HashMap<String, Object>();
        row.put("seq", "abcd1235");
        row.put("instSeq", "instSeq1");
        row.put("amount", new BigDecimal("100"));
        row.put("age", 15);
        row.put("bol", true);
        fileWriter.writeRow(row);

        row = new HashMap<String, Object>();
        row.put("seq", "abcdd236");
        row.put("instSeq", "instSeq1");
        row.put("amount", new BigDecimal("200"));
        row.put("age", 15);
        //row.put("bol", true);
        fileWriter.writeRow(row);

        row = new HashMap<String, Object>();
        row.put("seq", "eabc1235");
        row.put("instSeq", "instSeq1");
        row.put("amount", new BigDecimal("100"));
        row.put("age", 15);
        row.put("bol", true);
        fileWriter.writeRow(row);

        Map<String, Object> tail = new HashMap<String, Object>();
        tail.put("fileEnd", "fileEnd");
        tail.put("date", new Date());
        tail.put("failcount", 1);
        fileWriter.writeTail(tail);

        Summary summary = fileWriter.getSummary();
        for (SummaryPair summaryPair : summary.getSummaryPairs()) {
            System.out.println(summaryPair.summaryMsg());
        }

        for (StatisticPair pair : summary.getStatisticPairs()) {
            System.out.println(pair.staticsticMsg());
        }

        fileWriter.close();

        FileValidator fileValidator = FileFactory.createValidator(config);
        ValidateResult validate = fileValidator.validate();
        System.out.println(validate.getErrorMsg());
        Assert.assertTrue(validate.isSuccess());



    }

    @After
    public void after() {
        tf.delete();
    }
}
