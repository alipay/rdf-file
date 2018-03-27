package com.alipay.rdf.file.reader;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.alipay.rdf.file.exception.RdfErrorEnum;
import com.alipay.rdf.file.exception.RdfFileException;
import com.alipay.rdf.file.interfaces.FileFactory;
import com.alipay.rdf.file.interfaces.FileReader;
import com.alipay.rdf.file.interfaces.FileSplitter;
import com.alipay.rdf.file.model.FileConfig;
import com.alipay.rdf.file.model.FileDataTypeEnum;
import com.alipay.rdf.file.model.FileDefaultConfig;
import com.alipay.rdf.file.model.FileSlice;
import com.alipay.rdf.file.model.StorageConfig;
import com.alipay.rdf.file.util.DateUtil;
import com.alipay.rdf.file.util.TemporaryFolderUtil;
import com.alipay.rdf.file.util.TestLog;

import junit.framework.Assert;

/**
 * @author hongwei.quhw
 * @version $Id: FileReaderDeTest.java, v 0.1 2017年4月7日 下午5:38:15 hongwei.quhw Exp $
 */
public class SpFileReaderTest {
    TemporaryFolderUtil tf = new TemporaryFolderUtil();

    @Before
    public void setUp() throws IOException {
        tf.create();
        new FileDefaultConfig().setCommonLog(new TestLog());
    }

    @Test
    public void testReadSpFile() throws Exception {
        String filePath = File.class.getResource("/reader/sp/data/data1.txt").getPath();

        FileConfig config = new FileConfig(filePath, "/reader/sp/template/template1.json",
            new StorageConfig("nas"));

        FileReader fileReader = FileFactory.createReader(config);

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

        fileReader.close();
    }

    /**
     * 具有文件尾
     * 
     * @throws Exception
     */
    @Test
    public void testReadSpFile2() throws Exception {
        String filePath = File.class.getResource("/reader/sp/data/data2.txt").getPath();

        FileConfig config = new FileConfig(filePath, "/reader/sp/template/template2.json",
            new StorageConfig("nas"));

        FileReader fileReader = FileFactory.createReader(config);

        Map<String, Object> tail = fileReader.readTail(HashMap.class);
        Assert.assertEquals("OFDCFEND", tail.get("fileEnd"));

        Map<String, Object> head = fileReader.readHead(HashMap.class);
        Assert.assertEquals(new Integer(100), head.get("totalCount"));
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

        tail = fileReader.readTail(HashMap.class);
        Assert.assertEquals("OFDCFEND", tail.get("fileEnd"));

        head = fileReader.readHead(HashMap.class);
        Assert.assertEquals(new Integer(100), head.get("totalCount"));
        Assert.assertEquals(new BigDecimal("300.03"), head.get("totalAmount"));

        fileReader.close();
    }

    /**
     * 具有文件尾, 含有多个变量
     * 
     * @throws Exception
     */
    @Test
    public void testReadSpFile3() throws Exception {
        String filePath = File.class.getResource("/reader/sp/data/data3.txt").getPath();

        FileConfig config = new FileConfig(filePath, "/reader/sp/template/template3.json",
            new StorageConfig("nas"));

        FileReader fileReader = FileFactory.createReader(config);

        Map<String, Object> tail = fileReader.readTail(HashMap.class);
        Assert.assertEquals("OFDCFEND", tail.get("fileEnd"));
        Assert.assertEquals("20131109", DateUtil.format((Date) tail.get("date"), "yyyyMMdd"));
        Assert.assertEquals(new BigDecimal("100"), tail.get("amount"));

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

        tail = fileReader.readTail(HashMap.class);
        Assert.assertEquals("OFDCFEND", tail.get("fileEnd"));
        Assert.assertEquals("20131109", DateUtil.format((Date) tail.get("date"), "yyyyMMdd"));
        Assert.assertEquals(new BigDecimal("100"), tail.get("amount"));

        head = fileReader.readHead(HashMap.class);
        Assert.assertEquals(new Long(100), head.get("totalCount"));
        Assert.assertEquals(new BigDecimal("300.03"), head.get("totalAmount"));

        fileReader.close();
    }

    /**
     * 具有文件尾, 含有多个变量
     * 
     * 行尾有空行
     * 
     * @throws Exception
     */
    @Test
    public void testReadSpFile4() throws Exception {
        String filePath = File.class.getResource("/reader/sp/data/data4.txt").getPath();

        FileConfig config = new FileConfig(filePath, "/reader/sp/template/template3.json",
            new StorageConfig("nas"));

        FileReader fileReader = FileFactory.createReader(config);

        Map<String, Object> tail = fileReader.readTail(HashMap.class);
        Assert.assertEquals("OFDCFEND", tail.get("fileEnd"));
        Assert.assertEquals("20131109", DateUtil.format((Date) tail.get("date"), "yyyyMMdd"));
        Assert.assertEquals(new BigDecimal("100"), tail.get("amount"));

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

        tail = fileReader.readTail(HashMap.class);
        Assert.assertEquals("OFDCFEND", tail.get("fileEnd"));
        Assert.assertEquals("20131109", DateUtil.format((Date) tail.get("date"), "yyyyMMdd"));
        Assert.assertEquals(new BigDecimal("100"), tail.get("amount"));

        head = fileReader.readHead(HashMap.class);
        Assert.assertEquals(new Long(100), head.get("totalCount"));
        Assert.assertEquals(new BigDecimal("300.03"), head.get("totalAmount"));

        fileReader.close();
    }

    /**
     * 具有文件尾, 含有多个变量
     * 
     * 行尾有空行， readline
     * 
     * @throws Exception
     */
    @Test
    public void testReadSpFile5() throws Exception {
        String filePath = File.class.getResource("/reader/sp/data/data4.txt").getPath();

        FileConfig config = new FileConfig(filePath, "/reader/sp/template/template3.json",
            new StorageConfig("nas"));

        FileReader fileReader = FileFactory.createReader(config);

        Assert.assertEquals("100|300.03", fileReader.readLine());
        Assert.assertEquals(
            "seq_0|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1",
            fileReader.readLine());
        Assert.assertEquals(
            "seq_1|inst_seq_1|2013-11-10 15:56:12|20131110|20131113 12:33:34|23.34|11.88|33|56789|false|",
            fileReader.readLine());
        Assert.assertEquals("OFDCFEND|20131109|100", fileReader.readLine());
        Assert.assertEquals("", fileReader.readLine());
        Assert.assertEquals("", fileReader.readLine());
        Assert.assertEquals("", fileReader.readLine());
        Assert.assertNull(fileReader.readLine());
        fileReader.close();
    }

    /**
     * 分片读
     * 
     * @throws Exception
     */
    @Test
    public void testReadSpFile6() throws Exception {
        String filePath = File.class.getResource("/reader/sp/data/data4.txt").getPath();

        FileConfig config = new FileConfig(filePath, "/reader/sp/template/template3.json",
            new StorageConfig("nas"));

        FileSplitter splitter = FileFactory.createSplitter(config.getStorageConfig());
        FileSlice headSlice = splitter.getHeadSlice(config);

        FileConfig headConfig = config.clone();
        headConfig.setPartial(headSlice.getStart(), headSlice.getLength(),
            headSlice.getFileDataType());
        FileReader headReader = FileFactory.createReader(headConfig);

        Assert.assertEquals("100|300.03", headReader.readLine());
        Assert.assertNull(headReader.readLine());

        headReader = FileFactory.createReader(headConfig);
        Map<String, Object> head = headReader.readHead(HashMap.class);
        Assert.assertEquals(new Long(100), head.get("totalCount"));
        Assert.assertEquals(new BigDecimal("300.03"), head.get("totalAmount"));

        FileSlice tailSlice = splitter.getTailSlice(config);
        FileConfig tailConfig = config.clone();
        tailConfig.setPartial(tailSlice.getStart(), tailSlice.getLength(),
            tailSlice.getFileDataType());
        FileReader tailReader = FileFactory.createReader(tailConfig);
        Assert.assertEquals("OFDCFEND|20131109|100", tailReader.readLine());
        Assert.assertEquals("", tailReader.readLine());
        Assert.assertEquals("", tailReader.readLine());
        Assert.assertEquals("", tailReader.readLine());
        Assert.assertNull(tailReader.readLine());

        Map<String, Object> tail = tailReader.readTail(HashMap.class);
        Assert.assertEquals("OFDCFEND", tail.get("fileEnd"));
        Assert.assertEquals("20131109", DateUtil.format((Date) tail.get("date"), "yyyyMMdd"));
        Assert.assertEquals(new BigDecimal("100"), tail.get("amount"));

        FileSlice bodySlice = splitter.getBodySlice(config);
        FileConfig bodyConfig = config.clone();
        bodyConfig.setPartial(bodySlice.getStart(), bodySlice.getLength(),
            bodySlice.getFileDataType());
        FileReader bodyReader = FileFactory.createReader(bodyConfig);
        Assert.assertEquals(
            "seq_0|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1",
            bodyReader.readLine());
        Assert.assertEquals(
            "seq_1|inst_seq_1|2013-11-10 15:56:12|20131110|20131113 12:33:34|23.34|11.88|33|56789|false|",
            bodyReader.readLine());
        Assert.assertNull(bodyReader.readLine());
        bodyReader.close();

        bodyReader = FileFactory.createReader(bodyConfig);
        Map<String, Object> row = bodyReader.readRow(HashMap.class);
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

        row = bodyReader.readRow(HashMap.class);
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
        Assert.assertNull(bodyReader.readRow(HashMap.class));
        bodyReader.close();
    }

    /**
     * 换行符测试
     * body
     * 
     * @throws Exception
     */
    @Test
    public void testReadSpFile8() throws Exception {
        String file = "seq_0|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r"
                      + "seq_1|inst_seq_1|2013-11-10 15:56:12|20131110|20131113 12:33:34|23.34|11.88|33|56789|false|\r";

        FileConfig config = new FileConfig(new ByteArrayInputStream(file.getBytes("UTF-8")),
            "/reader/sp/template/template3.json");
        config.setFileDataType(FileDataTypeEnum.BODY);
        lineBreakTestValidate(config);

        file = "seq_0|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\r\n"
               + "seq_1|inst_seq_1|2013-11-10 15:56:12|20131110|20131113 12:33:34|23.34|11.88|33|56789|false|\r\n";

        config = new FileConfig(new ByteArrayInputStream(file.getBytes("UTF-8")),
            "/reader/de/template/template3.json");
        config.setFileDataType(FileDataTypeEnum.BODY);
        lineBreakTestValidate(config);

        file = "seq_0|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\n"
               + "seq_1|inst_seq_1|2013-11-10 15:56:12|20131110|20131113 12:33:34|23.34|11.88|33|56789|false|\n";

        config = new FileConfig(new ByteArrayInputStream(file.getBytes("UTF-8")),
            "/reader/de/template/template3.json");
        config.setFileDataType(FileDataTypeEnum.BODY);
        lineBreakTestValidate(config);

        file = "seq_0|inst_seq_0|2013-11-09 12:34:56|20131109|20131112 12:23:34|23.33|10.22|22|12345|true|备注1\n"
               + "seq_1|inst_seq_1|2013-11-10 15:56:12|20131110|20131113 12:33:34|23.34|11.88|33|56789|false|\n\n";

        config = new FileConfig(new ByteArrayInputStream(file.getBytes("UTF-8")),
            "/reader/de/template/template3.json");
        config.setFileDataType(FileDataTypeEnum.BODY);
        lineBreakTestValidate(config);

    }

    private void lineBreakTestValidate(FileConfig config) throws Exception {
        FileReader fileReader = FileFactory.createReader(config);

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

        fileReader.close();
    }

    /**
     * 测试必填字段
     * 
     * @throws Exception
     */
    @Test
    public void testReadSpFile8_requried() throws Exception {
        String filePath = File.class.getResource("/reader/sp/data/data5_required.txt").getPath();
        FileConfig config = new FileConfig(filePath, "/reader/sp/template/template3.json",
            new StorageConfig("nas"));

        FileReader fileReader = FileFactory.createReader(config);
        try {
            fileReader.readHead(HashMap.class);
            Assert.fail();
        } catch (RdfFileException e) {
            Assert.assertEquals(RdfErrorEnum.VALIDATE_ERROR, e.getErrorEnum());
        } finally {
            fileReader.close();
        }

        filePath = File.class.getResource("/reader/sp/data/data5_required2.txt").getPath();
        config.setFilePath(filePath);

        fileReader = FileFactory.createReader(config);

        Map<String, Object> head = fileReader.readHead(HashMap.class);
        Assert.assertEquals(new Long(100), head.get("totalCount"));
        Assert.assertEquals(new BigDecimal("300.03"), head.get("totalAmount"));

        Map<String, Object> row = fileReader.readRow(HashMap.class);
        Assert.assertNull(row.get("seq"));
        Assert.assertEquals("inst_seq_0", row.get("instSeq"));
        fileReader.close();

        filePath = File.class.getResource("/reader/sp/data/data5_required3.txt").getPath();
        config.setFilePath(filePath);
        fileReader = FileFactory.createReader(config);
        try {
            head = fileReader.readHead(HashMap.class);
            Assert.assertEquals(new Long(100), head.get("totalCount"));
            Assert.assertEquals(new BigDecimal("300.03"), head.get("totalAmount"));

            row = fileReader.readRow(HashMap.class);
            Assert.fail();
        } catch (RdfFileException e) {
            Assert.assertEquals(RdfErrorEnum.VALIDATE_ERROR, e.getErrorEnum());
        } finally {
            fileReader.close();
        }
    }
}
