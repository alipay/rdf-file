package com.alipay.rdf.file.resource;

import java.io.File;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.alipay.rdf.file.interfaces.FileFactory;
import com.alipay.rdf.file.interfaces.FileReader;
import com.alipay.rdf.file.interfaces.FileStorage;
import com.alipay.rdf.file.model.FileConfig;
import com.alipay.rdf.file.model.FileDefaultConfig;
import com.alipay.rdf.file.model.StorageConfig;
import com.alipay.rdf.file.util.DateUtil;
import com.alipay.rdf.file.util.OssTestUtil;
import com.alipay.rdf.file.util.TestLog;

import junit.framework.Assert;

/**
 * @author hongwei.quhw
 * @version $Id: FileReaderDeTest.java, v 0.1 2017年4月7日 下午5:38:15 hongwei.quhw Exp $
 */
public class OssFileReaderDeTest {
    private static final String        OSS_META_PATH = "rdf/rdf-file/META-INF/";
    private static final String        OSS_FILE_PATH = "rdf/rdf-file/reader/file/";
    private static final StorageConfig storageConfig = OssTestUtil.geStorageConfig();

    private FileStorage                fileStorage   = FileFactory.createStorage(storageConfig);

    @Before
    public void setUp() {
        FileDefaultConfig defaultConfig = new FileDefaultConfig();
        defaultConfig.setCommonLog(new TestLog());
        defaultConfig.addDefaultFleParam("oss", storageConfig);
        defaultConfig.addDefaultFleParam("ossdir", storageConfig);
        defaultConfig.setRdfProcessorPath("oss:rdf/rdf-file/META-INF/auto-processor/");
        defaultConfig.setRdfProtocolPath("oss:rdf/rdf-file/META-INF/protocol/");
        defaultConfig.setRdfFormatPath("oss:rdf/rdf-file/META-INF/format/");
        defaultConfig.setRdfTemplatePath("oss:");

        fileStorage.upload(File.class.getResource("/resource/meta").getPath(), OSS_META_PATH, true);

    }

    @Test
    public void testReadDEFile() throws Exception {
        String ossFile = OSS_FILE_PATH + "test.txt";
        String ossTemplatePath = OSS_FILE_PATH + "de.json";
        String localPath = File.class.getResource("/resource/data/data1.txt").getPath();
        String localTemplate = File.class.getResource("/resource/data/de.json").getPath();
        fileStorage.upload(localPath, ossFile, true);
        fileStorage.upload(localTemplate, ossTemplatePath, true);

        FileConfig config = new FileConfig(ossFile, ossTemplatePath, storageConfig);

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

    @After
    public void after() {
        //fileStorage.delete(OSS_FILE_PATH);
        //fileStorage.delete(OSS_META_PATH);
        FileDefaultConfig.RDF_PROCESSOR_PATH = "classpath*:META-INF/rdf-file/auto-processor/";
        FileDefaultConfig.RDF_PROTOCOL_PATH = "classpath*:META-INF/rdf-file/protocol/";
        FileDefaultConfig.RDF_FORMAT_PATH = "classpath*:META-INF/rdf-file/format/";
        FileDefaultConfig.RDF_TEMPLATE_PATH = "classpath:";
    }
}
