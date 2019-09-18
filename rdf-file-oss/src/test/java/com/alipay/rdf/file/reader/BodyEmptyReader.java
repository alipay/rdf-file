package com.alipay.rdf.file.reader;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.alipay.rdf.file.interfaces.FileFactory;
import com.alipay.rdf.file.interfaces.FileReader;
import com.alipay.rdf.file.interfaces.FileStorage;
import com.alipay.rdf.file.model.FileConfig;
import com.alipay.rdf.file.model.StorageConfig;
import com.alipay.rdf.file.storage.OssConfig;
import com.alipay.rdf.file.util.OssTestUtil;
import com.alipay.rdf.file.util.RdfFileUtil;
import com.alipay.rdf.file.util.TemporaryFolderUtil;

import junit.framework.Assert;

public class BodyEmptyReader {
    private TemporaryFolderUtil        temporaryFolder = new TemporaryFolderUtil();
    private static final StorageConfig storageConfig   = OssTestUtil.geStorageConfig();
    private static String              ossPathPrifx    = "rdf/rdf-file/open/BodyEmptyReader";
    private static FileStorage         fileStorage     = FileFactory.createStorage(storageConfig);
    private OssConfig                  ossConfig;

    @Before
    public void setUp() throws Exception {
        temporaryFolder.create();
        ossConfig = (OssConfig) storageConfig.getParam(OssConfig.OSS_STORAGE_CONFIG_KEY);
        ossConfig.setOssTempRoot(temporaryFolder.getRoot().getAbsolutePath());
        System.out.println(temporaryFolder.getRoot().getAbsolutePath());
    }

    @Test
    public void testRead() {
        String ossFilePath = RdfFileUtil.combinePath(ossPathPrifx, "test.txt");
        fileStorage.upload(File.class.getResource("/reader/test.txt").getPath(), ossFilePath, true);

        FileConfig normalConfig = new FileConfig(ossFilePath, "/reader/template_Allocation.json",
            storageConfig);
        FileReader reader = FileFactory.createReader(normalConfig);

        Map<String, Object> row = null;
        while (null != (row = reader.readRow(HashMap.class))) {

        }

        Assert.assertNull(row);
    }
}
