package com.alipay.rdf.file.storage;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import com.alipay.rdf.file.interfaces.FileFactory;
import com.alipay.rdf.file.interfaces.FileReader;
import com.alipay.rdf.file.interfaces.FileStorage;
import com.alipay.rdf.file.model.FileConfig;
import com.alipay.rdf.file.model.FileDefaultConfig;
import com.alipay.rdf.file.model.StorageConfig;
import com.alipay.rdf.file.util.OssTestUtil;
import com.alipay.rdf.file.util.RdfFileUtil;
import com.alipay.rdf.file.util.TemporaryFolderUtil;
import com.alipay.rdf.file.util.TestLog;

import junit.framework.Assert;

public class OssCopyTest {
    private static final StorageConfig       storageConfig   = OssTestUtil.geStorageConfig();
    private static String                    ossPath         = "rdf/rdf-file-open/OssCopyTest";
    private static FileStorage               fileStorage     = FileFactory
        .createStorage(storageConfig);
    private OssConfig                        ossConfig;
    private static final TemporaryFolderUtil temporaryFolder = new TemporaryFolderUtil();

    @Before
    public void setUp() throws Exception {
        FileDefaultConfig defaultConfig = new FileDefaultConfig();
        TestLog log = new TestLog() {
            @Override
            public boolean isDebug() {
                return false;
            }
        };
        defaultConfig.setCommonLog(log);
        temporaryFolder.create();

        ossConfig = (OssConfig) storageConfig.getParam(OssConfig.OSS_STORAGE_CONFIG_KEY);
        ossConfig.setOssTempRoot(temporaryFolder.getRoot().getAbsolutePath());
        ossConfig.setOssBigFileSize(1024L);
        System.out.println(temporaryFolder.getRoot().getAbsolutePath());
    }

    @Test
    public void testbigfile() throws Exception {
        String sourceOssFile = RdfFileUtil.combinePath(ossPath, "sourcebigfile");
        String targetOssFile = RdfFileUtil.combinePath(ossPath, "targetbigfile");
        fileStorage.upload(File.class.getResource("/osscopy/de1.txt").getPath(), sourceOssFile,
            true);

        fileStorage.rename(sourceOssFile, targetOssFile);

        FileConfig sourceConfig = new FileConfig(sourceOssFile, "/osscopy/de.json", storageConfig);
        FileReader sourceReader = FileFactory.createReader(sourceConfig);

        FileConfig targetConfig = new FileConfig(targetOssFile, "/osscopy/de.json", storageConfig);
        FileReader targetReader = FileFactory.createReader(targetConfig);

        String line = null;

        while (null != (line = sourceReader.readLine())) {
            Assert.assertEquals(line, targetReader.readLine());
        }

        Assert.assertNull(targetReader.readLine());

        fileStorage.delete(ossPath);
    }
}
