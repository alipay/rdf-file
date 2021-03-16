package com.alipay.rdf.file.preheat;

import com.alipay.rdf.file.interfaces.*;
import com.alipay.rdf.file.interfaces.FileStorage.FilePathFilter;
import com.alipay.rdf.file.model.*;
import com.alipay.rdf.file.storage.OssConfig;
import com.alipay.rdf.file.util.OssTestUtil;
import com.alipay.rdf.file.util.RdfFileUtil;
import com.alipay.rdf.file.util.TemporaryFolderUtil;
import com.alipay.rdf.file.util.TestLog;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 只合并body文件， 验证没有头，预热读合并的一个bug
 *
 * @author hongwei.quhw
 * @version $Id: PreheatReadTest.java, v 0.1 2020年7月10日 下午7:06:40 hongwei.quhw Exp $
 */
public class PreheatMergeBodyDeFileTest {
    private TemporaryFolderUtil        temporaryFolder = new TemporaryFolderUtil();
    private static final StorageConfig storageConfig   = OssTestUtil.geStorageConfig();
    private static String              ossPathPrefix   = "rdf/rdf-file/open/PreheatMergeBodyDeFileTest";
    private static FileStorage         fileStorage     = FileFactory.createStorage(storageConfig);
    private OssConfig                  ossConfig;

    @Before
    public void setUp() throws Exception {
        FileDefaultConfig defaultConfig = new FileDefaultConfig();
        TestLog log = new TestLog() {
            public boolean isDebug() {
                return false;
            }
        };
        defaultConfig.setCommonLog(log);
        temporaryFolder.create();
        ossConfig = (OssConfig) storageConfig.getParam(OssConfig.OSS_STORAGE_CONFIG_KEY);
        ossConfig.setOssTempRoot(temporaryFolder.getRoot().getAbsolutePath());
        System.out.println(temporaryFolder.getRoot().getAbsolutePath());
    }



    @Test
    public void mergeSlice() throws Exception {
        String ossPath = RdfFileUtil.combinePath(ossPathPrefix, "mergeSlice");

        fileStorage.upload(File.class.getResource("/preheat/de/body/").getPath(), ossPath, false);

        String targetFilePath = RdfFileUtil.combinePath(ossPath, "mergebody.txt");
        // 目标文件删除
        try {
            fileStorage.delete(targetFilePath);
        } catch (Exception e) {
        }


        List<String> bodyPath = fileStorage.listAllFiles(ossPath, new FilePathFilter() {
            @Override
            public boolean accept(String file) {
                return file.indexOf("body") > 0;
            }
        });
        Collections.sort(bodyPath);
        System.out.println(bodyPath);

        FileConfig preheatConfig = new FileConfig(targetFilePath,
            "/preheat/template_body.json", storageConfig);
        preheatConfig.setFileEncoding("GBK");
        preheatConfig.setSummaryEnable(true);
        preheatConfig.setType(FileOssToolContants.OSS_PREHEAT_PROTOCOL_READER);

        FileMerger fileMerger = FileFactory.createMerger(preheatConfig);
        MergerConfig mergerConfig = new MergerConfig();
        mergerConfig.setBodyFilePaths(bodyPath);
        fileMerger.merge(mergerConfig);

        Assert.assertTrue(fileStorage.getFileInfo(targetFilePath).isExists());



        FileConfig mergedConfig = new FileConfig(targetFilePath, "/preheat/template_body.json", storageConfig);
        mergedConfig.setFileEncoding("GBK");
        FileReader mergedReader = FileFactory.createReader(mergedConfig);

        for (String path : bodyPath) {
            System.out.println(path);
            FileConfig config = new FileConfig(path, "/preheat/template_body.json", storageConfig);
            config.setFileEncoding("GBK");
            config.setFileDataType(FileDataTypeEnum.BODY);
            FileReader reader = FileFactory.createReader(config);
            Map<String, Object> row = null;
            while (null != (row = reader.readRow(HashMap.class))) {
                Assert.assertEquals(row, mergedReader.readRow(HashMap.class));
            }

            reader.close();
        }

        Assert.assertNull(mergedReader.readRow(HashMap.class));

        mergedReader.close();

        fileStorage.delete(targetFilePath);
    }

    @After
    public void after() {
        temporaryFolder.delete();
    }
}
