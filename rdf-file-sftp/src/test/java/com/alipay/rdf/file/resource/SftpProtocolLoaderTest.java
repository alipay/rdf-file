/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
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
import com.alipay.rdf.file.sftp.SftpTestUtil;

/**
 *
 * @author haofan.whf
 * @version $Id: SftpTemplateLoaderTest.java, v 0.1 2018年10月19日 下午5:17 haofan.whf Exp $
 */
public class SftpProtocolLoaderTest {
    private static final String        SFTP_PATH      = SftpTestUtil.combineHomeDir("rdf/rdf-file/META-INF/");
    private static final StorageConfig storageConfig = SftpTestUtil.getStorageConfig();

    private FileStorage                fileStorage   = FileFactory.createStorage(storageConfig);

    @Before
    public void setUp() {
        FileDefaultConfig defaultConfig = new FileDefaultConfig();
        defaultConfig.addDefaultFleParam("sftp", storageConfig);
        defaultConfig.setRdfProcessorPath("sftp:" + SftpTestUtil.combineHomeDir("rdf/rdf-file/META-INF/auto-processor/"));
        defaultConfig.setRdfProtocolPath("sftp:" + SftpTestUtil.combineHomeDir("rdf/rdf-file/META-INF/protocol/"));
        defaultConfig.setRdfFormatPath("sftp:" + SftpTestUtil.combineHomeDir("rdf/rdf-file/META-INF/format/"));
        defaultConfig.setRdfTemplatePath("sftp:");
    }

    @Test
    public void testLoader() {
        fileStorage.upload(File.class.getResource("/resource/meta").getPath(), SFTP_PATH, true);

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
        FileDefaultConfig.RDF_PROCESSOR_PATH = "classpath*:META-INF/rdf-file/auto-processor/";
        FileDefaultConfig.RDF_PROTOCOL_PATH = "classpath*:META-INF/rdf-file/protocol/";
        FileDefaultConfig.RDF_FORMAT_PATH = "classpath*:META-INF/rdf-file/format/";
        FileDefaultConfig.RDF_TEMPLATE_PATH = "classpath:";
    }



}