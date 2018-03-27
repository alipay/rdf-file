package com.alipay.rdf.file.resource;

import java.io.File;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.alipay.rdf.file.format.AColumnFormat;
import com.alipay.rdf.file.format.RawFormat;
import com.alipay.rdf.file.interfaces.FileFactory;
import com.alipay.rdf.file.interfaces.FileStorage;
import com.alipay.rdf.file.loader.FormatLoader;
import com.alipay.rdf.file.loader.ProtocolLoader;
import com.alipay.rdf.file.model.FileDefaultConfig;
import com.alipay.rdf.file.model.StorageConfig;
import com.alipay.rdf.file.util.OssTestUtil;
import com.alipay.rdf.file.util.TestLog;

/**
 * 协议定义放在oss测试
 * 
 * @author hongwei.quhw
 * @version $Id: OssProtocolLoaderTest.java, v 0.1 2017年8月22日 下午4:10:10 hongwei.quhw Exp $
 */
public class OssProtocolLoaderTest {
    private static final String        OSS_PATH      = "rdf/rdf-file/META-INF/";
    private static final StorageConfig storageConfig = OssTestUtil.geStorageConfig();

    private FileStorage                fileStorage   = FileFactory.createStorage(storageConfig);

    @Before
    public void setUp() {
        FileDefaultConfig defaultConfig = new FileDefaultConfig();
        defaultConfig.setCommonLog(new TestLog());
        defaultConfig.addDefaultFleParam("oss", storageConfig);
        defaultConfig.setRdfProcessorPath("oss:rdf/rdf-file/META-INF/auto-processor/");
        defaultConfig.setRdfProtocolPath("oss:rdf/rdf-file/META-INF/protocol/");
        defaultConfig.setRdfFormatPath("oss:rdf/rdf-file/META-INF/format/");
        defaultConfig.setRdfTemplatePath("oss:");
    }

    @Test
    public void testLoader() {
        fileStorage.upload(File.class.getResource("/resource/meta").getPath(), OSS_PATH, true);

        Assert.assertNotNull(ProtocolLoader.loadProtocol("fund"));
        Assert.assertNotNull(ProtocolLoader.loadProtocol("de"));
        Assert.assertNotNull(ProtocolLoader.loadProtocol("sp"));
        Assert.assertNotNull(ProtocolLoader.loadProtocol("fund_index"));

        Assert.assertTrue(
            FormatLoader.getColumnFormt("fund", "DigitalChar") instanceof AColumnFormat);
        Assert.assertTrue(FormatLoader.getColumnFormt("de", "DigitalChar") instanceof RawFormat);
    }

    @After
    public void after() {
        fileStorage.delete(OSS_PATH);
        FileDefaultConfig.RDF_PROCESSOR_PATH = "classpath*:META-INF/rdf-file/auto-processor/";
        FileDefaultConfig.RDF_PROTOCOL_PATH = "classpath*:META-INF/rdf-file/protocol/";
        FileDefaultConfig.RDF_FORMAT_PATH = "classpath*:META-INF/rdf-file/format/";
        FileDefaultConfig.RDF_TEMPLATE_PATH = "classpath:";
    }

}
