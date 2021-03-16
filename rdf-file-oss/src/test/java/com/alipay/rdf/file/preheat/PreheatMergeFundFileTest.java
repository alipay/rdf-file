package com.alipay.rdf.file.preheat;

import com.alipay.rdf.file.interfaces.*;
import com.alipay.rdf.file.interfaces.FileStorage.FilePathFilter;
import com.alipay.rdf.file.model.*;
import com.alipay.rdf.file.storage.OssConfig;
import com.alipay.rdf.file.util.OssTestUtil;
import com.alipay.rdf.file.util.RdfFileUtil;
import com.alipay.rdf.file.util.TemporaryFolderUtil;
import com.alipay.rdf.file.util.TestLog;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 * 
 * 合并测试
 * 
 * @author hongwei.quhw
 * @version $Id: PreheatMergeFundFileTest.java, v 0.1 2018年4月17日 下午5:27:55 hongwei.quhw Exp $
 */
public class PreheatMergeFundFileTest {
    private TemporaryFolderUtil        temporaryFolder = new TemporaryFolderUtil();
    private static final StorageConfig storageConfig   = OssTestUtil.geStorageConfig();
    private static String              ossPathPrefix   = "rdf/rdf-file/open/PreheatMergeFundFileTest";
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
    public void testExsitFile() throws Exception {
        String ossPath = RdfFileUtil.combinePath(ossPathPrefix, "testExsitFile");
        fileStorage.upload(File.class.getResource("/preheat/fund/all/").getPath(), ossPath, false);

        String targetFilePath = RdfFileUtil.combinePath(ossPath, "existmerged.txt");

        // 目标文件删除
        try {
            fileStorage.delete(targetFilePath);
        } catch (Exception e) {
        }

        // 文件太大去掉两个
        List<String> paths = fileStorage.listAllFiles(ossPath);
        Collections.sort(paths);
        System.out.println("sortedPath = " + paths);

        FileConfig preheatConfig = new FileConfig(targetFilePath,
            "/preheat/template_batchPurchase.json", storageConfig);
        preheatConfig.setFileEncoding("GBK");
        preheatConfig.setSummaryEnable(true);
        preheatConfig.setType(FileOssToolContants.OSS_PREHEAT_PROTOCOL_READER);

        FileMerger fileMerger = FileFactory.createMerger(preheatConfig);
        MergerConfig mergerConfig = new MergerConfig();
        mergerConfig.setExistFilePaths(paths);
        fileMerger.merge(mergerConfig);

        Assert.assertTrue(fileStorage.getFileInfo(targetFilePath).isExists());

        FileConfig mergedConfig = new FileConfig(targetFilePath,
            "/preheat/template_batchPurchase.json", storageConfig);
        mergedConfig.setFileEncoding("GBK");

        FileReader mergedReader = FileFactory.createReader(mergedConfig);
        Map<String, Object> mergeHead = mergedReader.readHead(HashMap.class);
        System.out.println("mergedHead: " + mergeHead);

        Integer totalCount = 0;
        for (String path : paths) {
            System.out.println(path);
            FileConfig config = new FileConfig(path, "/preheat/template_batchPurchase.json",
                storageConfig);
            config.setFileEncoding("GBK");
            FileReader reader = FileFactory.createReader(config);
            Map<String, Object> head = reader.readHead(HashMap.class);
            totalCount += (Integer) head.get("totalCount");
            Map<String, Object> row = null;
            while (null != (row = reader.readRow(HashMap.class))) {
                Assert.assertEquals(row, mergedReader.readRow(HashMap.class));
            }

            reader.close();
        }

        Assert.assertNull(mergedReader.readRow(HashMap.class));

        Assert.assertEquals(totalCount, mergeHead.get("totalCount"));

        mergedReader.close();

        fileStorage.delete(targetFilePath);
    }

    @Test
    public void mergeSlice() throws Exception {
        String ossPath = RdfFileUtil.combinePath(ossPathPrefix, "mergeSlice");

        fileStorage.upload(File.class.getResource("/preheat/fund/slice/").getPath(), ossPath,
            false);

        String targetFilePath = RdfFileUtil.combinePath(ossPath, "mergeslice.txt");
        // 目标文件删除
        try {
            fileStorage.delete(targetFilePath);
        } catch (Exception e) {
        }

        List<String> headPath = fileStorage.listAllFiles(ossPath, new FilePathFilter() {
            @Override
            public boolean accept(String file) {
                return file.indexOf("head") > 0;
            }
        });
        System.out.println(headPath);
        List<String> bodyPath = fileStorage.listAllFiles(ossPath, new FilePathFilter() {
            @Override
            public boolean accept(String file) {
                return file.indexOf("body") > 0;
            }
        });
        Collections.sort(bodyPath);
        System.out.println(bodyPath);
        
        List<String> tailPath = fileStorage.listAllFiles(ossPath, new FilePathFilter() {
            @Override
            public boolean accept(String file) {
                return file.indexOf("tail") > 0;
            }
        });
        Collections.sort(bodyPath);
        System.out.println(bodyPath);

        FileConfig preheatConfig = new FileConfig(targetFilePath,
            "/preheat/template_batchPurchase.json", storageConfig);
        preheatConfig.setFileEncoding("GBK");
        preheatConfig.setSummaryEnable(true);
        preheatConfig.setType(FileOssToolContants.OSS_PREHEAT_PROTOCOL_READER);

        FileMerger fileMerger = FileFactory.createMerger(preheatConfig);
        MergerConfig mergerConfig = new MergerConfig();
        mergerConfig.setHeadFilePaths(headPath);
        mergerConfig.setBodyFilePaths(bodyPath);
        mergerConfig.setTailFilePaths(tailPath);
        fileMerger.merge(mergerConfig);

        Assert.assertTrue(fileStorage.getFileInfo(targetFilePath).isExists());

        Integer totalCount = 0;
        for (String path : headPath) {
            System.out.println(path);
            FileConfig config = new FileConfig(path, "/preheat/template_batchPurchase.json",
                storageConfig);
            config.setFileEncoding("GBK");
            config.setFileDataType(FileDataTypeEnum.HEAD);
            FileReader reader = FileFactory.createReader(config);
            Map<String, Object> head = reader.readHead(HashMap.class);
            totalCount += (Integer) head.get("totalCount");
            reader.close();
        }

        FileConfig mergedConfig = new FileConfig(targetFilePath,
            "/preheat/template_batchPurchase.json", storageConfig);
        mergedConfig.setFileEncoding("GBK");
        FileReader mergedReader = FileFactory.createReader(mergedConfig);
        Map<String, Object> mergeHead = mergedReader.readHead(HashMap.class);
        System.out.println("mergedHead: " + mergeHead);

        for (String path : bodyPath) {
            System.out.println(path);
            FileConfig config = new FileConfig(path, "/preheat/template_batchPurchase.json",
                storageConfig);
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

        Assert.assertEquals(totalCount, mergeHead.get("totalCount"));

        mergedReader.close();

        fileStorage.delete(targetFilePath);
    }

    @Test
    public void mergeSliceAndExsitFile() throws Exception {
        String ossPath = RdfFileUtil.combinePath(ossPathPrefix, "mergeSliceAndExsitFile");
        String ossSlicePath = RdfFileUtil.combinePath(ossPath, "slice");
        String ossAllPath = RdfFileUtil.combinePath(ossPath, "all");
        fileStorage.upload(File.class.getResource("/preheat/fund/slice/").getPath(), ossSlicePath,
            false);
        fileStorage.upload(File.class.getResource("/preheat/fund/all/").getPath(), ossAllPath,
            false);

        String targetFilePath = RdfFileUtil.combinePath(ossPath, "mergesliceAndExist.txt");
        // 目标文件删除
        try {
            fileStorage.delete(targetFilePath);
        } catch (Exception e) {
        }

        List<String> headPath = fileStorage.listAllFiles(ossSlicePath, new FilePathFilter() {
            @Override
            public boolean accept(String file) {
                return file.indexOf("head") > 0;
            }
        });
        System.out.println(headPath);
        List<String> bodyPath = fileStorage.listAllFiles(ossSlicePath, new FilePathFilter() {
            @Override
            public boolean accept(String file) {
                return file.indexOf("body") > 0;
            }
        });
        Collections.sort(bodyPath);
        System.out.println(bodyPath);

        List<String> paths = fileStorage.listAllFiles(ossAllPath);
        Collections.sort(paths);
        System.out.println("sortedPath = " + paths);

        FileConfig preheatConfig = new FileConfig(targetFilePath,
            "/preheat/template_batchPurchase.json", storageConfig);
        preheatConfig.setFileEncoding("GBK");
        preheatConfig.setSummaryEnable(true);
        preheatConfig.setType(FileOssToolContants.OSS_PREHEAT_PROTOCOL_READER);

        FileMerger fileMerger = FileFactory.createMerger(preheatConfig);
        MergerConfig mergerConfig = new MergerConfig();
        mergerConfig.setExistFilePaths(paths);
        mergerConfig.setHeadFilePaths(headPath);
        mergerConfig.setBodyFilePaths(bodyPath);
        fileMerger.merge(mergerConfig);

        Assert.assertTrue(fileStorage.getFileInfo(targetFilePath).isExists());

        Integer totalCount = 0;
        for (String path : headPath) {
            System.out.println(path);
            FileConfig config = new FileConfig(path, "/preheat/template_batchPurchase.json",
                storageConfig);
            config.setFileEncoding("GBK");
            config.setFileDataType(FileDataTypeEnum.HEAD);
            FileReader reader = FileFactory.createReader(config);
            Map<String, Object> head = reader.readHead(HashMap.class);
            totalCount += (Integer) head.get("totalCount");
            reader.close();
        }

        FileConfig mergedConfig = new FileConfig(targetFilePath,
            "/preheat/template_batchPurchase.json", storageConfig);
        mergedConfig.setFileEncoding("GBK");
        FileReader mergedReader = FileFactory.createReader(mergedConfig);
        Map<String, Object> mergeHead = mergedReader.readHead(HashMap.class);
        System.out.println("mergedHead: " + mergeHead);

        for (String path : paths) {
            System.out.println(path);
            FileConfig config = new FileConfig(path, "/preheat/template_batchPurchase.json",
                storageConfig);
            config.setFileEncoding("GBK");
            FileReader reader = FileFactory.createReader(config);
            Map<String, Object> head = reader.readHead(HashMap.class);
            totalCount += (Integer) head.get("totalCount");
            Map<String, Object> row = null;
            while (null != (row = reader.readRow(HashMap.class))) {
                Assert.assertEquals(row, mergedReader.readRow(HashMap.class));
            }

            reader.close();
        }

        for (String path : bodyPath) {
            System.out.println(path);
            FileConfig config = new FileConfig(path, "/preheat/template_batchPurchase.json",
                storageConfig);
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

        Assert.assertEquals(totalCount, mergeHead.get("totalCount"));

        mergedReader.close();
        fileStorage.delete(targetFilePath);
    }

    @Test
    public void testMergeEmpty() throws Exception {
        String ossPath = RdfFileUtil.combinePath(ossPathPrefix, "testMergeEmpty");
        fileStorage.upload(File.class.getResource("/preheat/fund/empty/").getPath(), ossPath,
            false);

        String targetFilePath = RdfFileUtil.combinePath(ossPath, "mergeempty.txt");
        // 目标文件删除
        try {
            fileStorage.delete(targetFilePath);
        } catch (Exception e) {
        }

        FileStorage fileStorage = FileFactory.createStorage(storageConfig);
        List<String> paths = fileStorage.listAllFiles(ossPath);
        Collections.sort(paths);
        System.out.println(paths);

        FileConfig preheatConfig = new FileConfig(targetFilePath,
            "/preheat/template_batchPurchase.json", storageConfig);
        preheatConfig.setFileEncoding("GBK");
        preheatConfig.setSummaryEnable(true);
        preheatConfig.setType(FileOssToolContants.OSS_PREHEAT_PROTOCOL_READER);

        FileMerger fileMerger = FileFactory.createMerger(preheatConfig);
        MergerConfig mergerConfig = new MergerConfig();
        mergerConfig.setExistFilePaths(paths);
        fileMerger.merge(mergerConfig);

        FileConfig mergedConfig = new FileConfig(targetFilePath,
            "/preheat/template_batchPurchase.json", storageConfig);
        mergedConfig.setFileEncoding("GBK");
        FileReader mergedReader = FileFactory.createReader(mergedConfig);
        Map<String, Object> head = mergedReader.readHead(HashMap.class);
        Assert.assertEquals(new Integer(0), head.get("totalCount"));

        Assert.assertNull(mergedReader.readRow(HashMap.class));

        mergedReader.close();
    }

    @Test
    public void testMergeNodata() throws Exception {
        String ossPath = RdfFileUtil.combinePath(ossPathPrefix, "testMergeNodata");

        String targetFilePath = RdfFileUtil.combinePath(ossPath, "testMergeNodata.txt");
        // 目标文件删除
        try {
            fileStorage.delete(targetFilePath);
        } catch (Exception e) {
        }

        FileStorage fileStorage = FileFactory.createStorage(storageConfig);

        FileConfig preheatConfig = new FileConfig(targetFilePath,
            "/preheat/template_batchPurchase.json", storageConfig);
        preheatConfig.setFileEncoding("GBK");
        preheatConfig.setSummaryEnable(true);
        preheatConfig.setType(FileOssToolContants.OSS_PREHEAT_PROTOCOL_READER);

        FileMerger fileMerger = FileFactory.createMerger(preheatConfig);
        MergerConfig mergerConfig = new MergerConfig();
        fileMerger.merge(mergerConfig);

        Assert.assertFalse(fileStorage.getFileInfo(targetFilePath).isExists());

    }

    @Test
    public void testMergeNodata2() throws Exception {
        String ossPath = RdfFileUtil.combinePath(ossPathPrefix, "testMergeNodata2");

        String targetFilePath = RdfFileUtil.combinePath(ossPath, "testMergeNodata2.txt");
        // 目标文件删除
        try {
            fileStorage.delete(targetFilePath);
        } catch (Exception e) {
        }

        FileStorage fileStorage = FileFactory.createStorage(storageConfig);

        FileConfig preheatConfig = new FileConfig(targetFilePath,
            "/preheat/template_batchPurchase.json", storageConfig);
        preheatConfig.setFileEncoding("GBK");
        preheatConfig.setSummaryEnable(true);
        preheatConfig.setType(FileOssToolContants.OSS_PREHEAT_PROTOCOL_READER);
        preheatConfig.setCreateEmptyFile(true);

        FileMerger fileMerger = FileFactory.createMerger(preheatConfig);
        MergerConfig mergerConfig = new MergerConfig();
        fileMerger.merge(mergerConfig);

        Assert.assertTrue(fileStorage.getFileInfo(targetFilePath).isExists());

    }

}
