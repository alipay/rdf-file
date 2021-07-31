package com.alipay.rdf.file.codec;

import com.alipay.rdf.file.exception.RdfFileException;
import com.alipay.rdf.file.interfaces.FileFactory;
import com.alipay.rdf.file.interfaces.FileReader;
import com.alipay.rdf.file.model.FileConfig;
import com.alipay.rdf.file.model.StorageConfig;
import com.alipay.rdf.file.util.DateUtil;
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
import java.util.Map;

/**
 * @Author: hongwei.quhw 2021/7/30 5:04 下午
 */
public class RowRelationCodecTest {
    TemporaryFolderUtil tf = new TemporaryFolderUtil();

    @Before
    public void setUp() throws IOException {
        tf.create();
    }

    @Test
    public void testReadDEFile() throws Exception {
        String filePath = File.class.getResource("/codec/relation/data/data1.txt").getPath();

        FileConfig config = new FileConfig(filePath, "/codec/relation/template/template1.json",
                new StorageConfig("nas"));
        // 设置读行兼容模式
        config.setRelationReadRowCompatibility(true);

        FileReader fileReader = FileFactory.createReader(config);

        Assert.assertNull(fileReader.readTail(HashMap.class));

        Map<String, Object> head = fileReader.readHead(HashMap.class);
        Assert.assertEquals(new Long(100), head.get("totalCount"));
        Assert.assertEquals(new BigDecimal("300.03"), head.get("totalAmount"));

        Map<String, Object> row = fileReader.readRow(HashMap.class);
        Assert.assertEquals("seq_0", row.get("seq"));
        Assert.assertEquals("inst_seq_0", row.get("instSeq"));
        Assert.assertEquals("2013-11-09 12:34:56",
                DateUtil.format((Date) row.get("gmtApply"), "yyyy-MM-dd HH:mm:ss"));
        Assert.assertEquals("20131109", DateUtil.format((Date) row.get("date"), "yyyyMMdd"));
        Assert.assertEquals("20131112 12:23:34",
                DateUtil.format((Date) row.get("dateTime"), "yyyyMMdd HH:mm:ss"));
        Assert.assertEquals(new BigDecimal("23.33"), row.get("applyNumber"));
        Assert.assertEquals(new BigDecimal("10.22"), row.get("amount"));
        Assert.assertEquals(new Integer(22), row.get("age"));
        Assert.assertEquals(new Long(12345), row.get("longN"));
        Assert.assertEquals(Boolean.TRUE, row.get("bol"));
        Assert.assertEquals("备注1", row.get("memo"));

        row = fileReader.readRow(HashMap.class);
        Assert.assertEquals("seq_1", row.get("seq"));
        Assert.assertEquals("inst_seq_1", row.get("instSeq"));
        Assert.assertEquals("2013-11-10 15:56:12",
                DateUtil.format((Date) row.get("gmtApply"), "yyyy-MM-dd HH:mm:ss"));
        Assert.assertEquals("20131110", DateUtil.format((Date) row.get("date"), "yyyyMMdd"));
        Assert.assertEquals("20131113 12:33:34",
                DateUtil.format((Date) row.get("dateTime"), "yyyyMMdd HH:mm:ss"));
        Assert.assertEquals(new BigDecimal("23.34"), row.get("applyNumber"));
        Assert.assertEquals(new BigDecimal("11.88"), row.get("amount"));
        Assert.assertEquals(new Integer(33), row.get("age"));
        Assert.assertEquals(new Long(56789), row.get("longN"));
        Assert.assertEquals(Boolean.FALSE, row.get("bol"));
        Assert.assertNull(row.get("memo"));

        row = fileReader.readRow(HashMap.class);
        Assert.assertNull(row);

        Assert.assertNull(fileReader.readTail(HashMap.class));

        fileReader.close();
    }

    @Test(expected = RdfFileException.class)
    public void testReadDEFile2() throws Exception {
        String filePath = File.class.getResource("/codec/relation/data/data1.txt").getPath();

        FileConfig config = new FileConfig(filePath, "/codec/relation/template/template2.json",
                new StorageConfig("nas"));
        // 设置读行兼容模式
        config.setRelationReadRowCompatibility(true);

        FileReader fileReader = FileFactory.createReader(config);

        Assert.assertNull(fileReader.readTail(HashMap.class));

        fileReader.readHead(HashMap.class);
    }

    // 前后分隔符  config设置true， tempalte设置false 优先级测试
    @Test
    public void testReadDEFile3() throws Exception {
        String filePath = File.class.getResource("/codec/relation/data/data2.txt").getPath();

        FileConfig config = new FileConfig(filePath, "/codec/relation/template/template3.json",
                new StorageConfig("nas"));
        // 设置读行兼容模式
        config.setRelationReadRowCompatibility(true);

        FileReader fileReader = FileFactory.createReader(config);

        Assert.assertNull(fileReader.readTail(HashMap.class));

        Map<String, Object> head = fileReader.readHead(HashMap.class);
        Assert.assertEquals(new Long(100), head.get("totalCount"));
        Assert.assertEquals(new BigDecimal("300.03"), head.get("totalAmount"));

        Map<String, Object> row = fileReader.readRow(HashMap.class);
        Assert.assertEquals("seq_0", row.get("seq"));
        Assert.assertEquals("inst_seq_0", row.get("instSeq"));
        Assert.assertEquals("2013-11-09 12:34:56",
                DateUtil.format((Date) row.get("gmtApply"), "yyyy-MM-dd HH:mm:ss"));
        Assert.assertEquals("20131109", DateUtil.format((Date) row.get("date"), "yyyyMMdd"));
        Assert.assertEquals("20131112 12:23:34",
                DateUtil.format((Date) row.get("dateTime"), "yyyyMMdd HH:mm:ss"));
        Assert.assertEquals(new BigDecimal("23.33"), row.get("applyNumber"));
        Assert.assertEquals(new BigDecimal("10.22"), row.get("amount"));
        Assert.assertEquals(new Integer(22), row.get("age"));
        Assert.assertEquals(new Long(12345), row.get("longN"));
        Assert.assertEquals(Boolean.TRUE, row.get("bol"));
        Assert.assertEquals("备注1", row.get("memo"));

        row = fileReader.readRow(HashMap.class);
        Assert.assertEquals("seq_1", row.get("seq"));
        Assert.assertEquals("inst_seq_1", row.get("instSeq"));
        Assert.assertEquals("2013-11-10 15:56:12",
                DateUtil.format((Date) row.get("gmtApply"), "yyyy-MM-dd HH:mm:ss"));
        Assert.assertEquals("20131110", DateUtil.format((Date) row.get("date"), "yyyyMMdd"));
        Assert.assertEquals("20131113 12:33:34",
                DateUtil.format((Date) row.get("dateTime"), "yyyyMMdd HH:mm:ss"));
        Assert.assertEquals(new BigDecimal("23.34"), row.get("applyNumber"));
        Assert.assertEquals(new BigDecimal("11.88"), row.get("amount"));
        Assert.assertEquals(new Integer(33), row.get("age"));
        Assert.assertEquals(new Long(56789), row.get("longN"));
        Assert.assertEquals(Boolean.FALSE, row.get("bol"));
        Assert.assertNull(row.get("memo"));

        row = fileReader.readRow(HashMap.class);
        Assert.assertNull(row);

        Assert.assertNull(fileReader.readTail(HashMap.class));

        fileReader.close();
    }

    //数据定义模板定义兼容
    @Test
    public void testReadDEFile4() throws Exception {
        String filePath = File.class.getResource("/codec/relation/data/data1.txt").getPath();

        FileConfig config = new FileConfig(filePath, "/codec/relation/template/template4.json",
                new StorageConfig("nas"));

        FileReader fileReader = FileFactory.createReader(config);

        Assert.assertNull(fileReader.readTail(HashMap.class));

        Map<String, Object> head = fileReader.readHead(HashMap.class);
        Assert.assertEquals(new Long(100), head.get("totalCount"));
        Assert.assertEquals(new BigDecimal("300.03"), head.get("totalAmount"));

        Map<String, Object> row = fileReader.readRow(HashMap.class);
        Assert.assertEquals("seq_0", row.get("seq"));
        Assert.assertEquals("inst_seq_0", row.get("instSeq"));
        Assert.assertEquals("2013-11-09 12:34:56",
                DateUtil.format((Date) row.get("gmtApply"), "yyyy-MM-dd HH:mm:ss"));
        Assert.assertEquals("20131109", DateUtil.format((Date) row.get("date"), "yyyyMMdd"));
        Assert.assertEquals("20131112 12:23:34",
                DateUtil.format((Date) row.get("dateTime"), "yyyyMMdd HH:mm:ss"));
        Assert.assertEquals(new BigDecimal("23.33"), row.get("applyNumber"));
        Assert.assertEquals(new BigDecimal("10.22"), row.get("amount"));
        Assert.assertEquals(new Integer(22), row.get("age"));
        Assert.assertEquals(new Long(12345), row.get("longN"));
        Assert.assertEquals(Boolean.TRUE, row.get("bol"));
        Assert.assertEquals("备注1", row.get("memo"));

        row = fileReader.readRow(HashMap.class);
        Assert.assertEquals("seq_1", row.get("seq"));
        Assert.assertEquals("inst_seq_1", row.get("instSeq"));
        Assert.assertEquals("2013-11-10 15:56:12",
                DateUtil.format((Date) row.get("gmtApply"), "yyyy-MM-dd HH:mm:ss"));
        Assert.assertEquals("20131110", DateUtil.format((Date) row.get("date"), "yyyyMMdd"));
        Assert.assertEquals("20131113 12:33:34",
                DateUtil.format((Date) row.get("dateTime"), "yyyyMMdd HH:mm:ss"));
        Assert.assertEquals(new BigDecimal("23.34"), row.get("applyNumber"));
        Assert.assertEquals(new BigDecimal("11.88"), row.get("amount"));
        Assert.assertEquals(new Integer(33), row.get("age"));
        Assert.assertEquals(new Long(56789), row.get("longN"));
        Assert.assertEquals(Boolean.FALSE, row.get("bol"));
        Assert.assertNull(row.get("memo"));

        row = fileReader.readRow(HashMap.class);
        Assert.assertNull(row);

        Assert.assertNull(fileReader.readTail(HashMap.class));

        fileReader.close();
    }

    @After
    public void after() {
        tf.delete();
    }
}
