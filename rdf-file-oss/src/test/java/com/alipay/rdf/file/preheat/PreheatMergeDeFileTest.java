package com.alipay.rdf.file.preheat;

import java.io.File;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.alipay.rdf.file.interfaces.FileFactory;
import com.alipay.rdf.file.interfaces.FileMerger;
import com.alipay.rdf.file.interfaces.FileOssToolContants;
import com.alipay.rdf.file.interfaces.FileReader;
import com.alipay.rdf.file.interfaces.FileStorage;
import com.alipay.rdf.file.interfaces.FileStorage.FilePathFilter;
import com.alipay.rdf.file.model.FileConfig;
import com.alipay.rdf.file.model.FileDataTypeEnum;
import com.alipay.rdf.file.model.FileDefaultConfig;
import com.alipay.rdf.file.model.MergerConfig;
import com.alipay.rdf.file.model.StorageConfig;
import com.alipay.rdf.file.storage.OssConfig;
import com.alipay.rdf.file.util.OssTestUtil;
import com.alipay.rdf.file.util.RdfFileUtil;
import com.alipay.rdf.file.util.TemporaryFolderUtil;
import com.alipay.rdf.file.util.TestLog;

import junit.framework.Assert;

/**
 * 验证预热读
 * 
 * @author hongwei.quhw
 * @version $Id: PreheatReadTest.java, v 0.1 2017年7月17日 下午7:06:40 hongwei.quhw Exp $
 */
public class PreheatMergeDeFileTest {
    private TemporaryFolderUtil        temporaryFolder = new TemporaryFolderUtil();
    private static final StorageConfig storageConfig   = OssTestUtil.geStorageConfig();
    private static String              ossPathPrefix   = "rdf/rdf-file/open/PreheatMergeDeFileTest";
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
        fileStorage.upload(File.class.getResource("/preheat/de/all/").getPath(), ossPath, false);

        String targetFilePath = RdfFileUtil.combinePath(ossPath, "existmerged.txt");

        // 目标文件删除
        try {
            fileStorage.delete(targetFilePath);
        } catch (Exception e) {
        }

        // 文件太大去掉两个
        List<String> paths = fileStorage.listAllFiles(ossPath);
        Collections.sort(paths);
        paths.remove(0);
        paths.remove(1);
        System.out.println("sortedPath = " + paths);

        FileConfig preheatConfig = new FileConfig(targetFilePath,
            "/preheat/template_Allocation.json", storageConfig);
        preheatConfig.setFileEncoding("GBK");
        preheatConfig.setSummaryEnable(true);
        preheatConfig.setType(FileOssToolContants.OSS_PREHEAT_PROTOCOL_READER);

        FileMerger fileMerger = FileFactory.createMerger(preheatConfig);
        MergerConfig mergerConfig = new MergerConfig();
        mergerConfig.setExistFilePaths(paths);
        fileMerger.merge(mergerConfig);

        Assert.assertTrue(fileStorage.getFileInfo(targetFilePath).isExists());

        FileConfig mergedConfig = new FileConfig(targetFilePath,
            "/preheat/template_Allocation.json", storageConfig);
        mergedConfig.setFileEncoding("GBK");

        FileReader mergedReader = FileFactory.createReader(mergedConfig);
        Map<String, Object> mergeHead = mergedReader.readHead(HashMap.class);
        System.out.println("mergedHead: " + mergeHead);

        Integer totalCount = 0;
        BigDecimal totalAmount = new BigDecimal(0);
        for (String path : paths) {
            System.out.println(path);
            FileConfig config = new FileConfig(path, "/preheat/template_Allocation.json",
                storageConfig);
            config.setFileEncoding("GBK");
            FileReader reader = FileFactory.createReader(config);
            Map<String, Object> head = reader.readHead(HashMap.class);
            totalCount += (Integer) head.get("totalCount");
            totalAmount = totalAmount.add((BigDecimal) head.get("totalAmount"));
            Map<String, Object> row = null;
            while (null != (row = reader.readRow(HashMap.class))) {
                Assert.assertEquals(row, mergedReader.readRow(HashMap.class));
            }

            reader.close();
        }

        Assert.assertNull(mergedReader.readRow(HashMap.class));

        Assert.assertEquals(totalCount, mergeHead.get("totalCount"));
        Assert.assertEquals(totalAmount, mergeHead.get("totalAmount"));

        mergedReader.close();

        fileStorage.delete(targetFilePath);
    }

    @Test
    public void mergeSlice() throws Exception {
        String ossPath = RdfFileUtil.combinePath(ossPathPrefix, "mergeSlice");

        fileStorage.upload(File.class.getResource("/preheat/de/slice/").getPath(), ossPath, false);

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

        FileConfig preheatConfig = new FileConfig(targetFilePath,
            "/preheat/template_Allocation.json", storageConfig);
        preheatConfig.setFileEncoding("GBK");
        preheatConfig.setSummaryEnable(true);
        preheatConfig.setType(FileOssToolContants.OSS_PREHEAT_PROTOCOL_READER);

        FileMerger fileMerger = FileFactory.createMerger(preheatConfig);
        MergerConfig mergerConfig = new MergerConfig();
        mergerConfig.setHeadFilePaths(headPath);
        mergerConfig.setBodyFilePaths(bodyPath);
        fileMerger.merge(mergerConfig);

        Assert.assertTrue(fileStorage.getFileInfo(targetFilePath).isExists());

        Integer totalCount = 0;
        BigDecimal totalAmount = new BigDecimal(0);
        for (String path : headPath) {
            System.out.println(path);
            FileConfig config = new FileConfig(path, "/preheat/template_Allocation.json",
                storageConfig);
            config.setFileEncoding("GBK");
            config.setFileDataType(FileDataTypeEnum.HEAD);
            FileReader reader = FileFactory.createReader(config);
            Map<String, Object> head = reader.readHead(HashMap.class);
            totalCount += (Integer) head.get("totalCount");
            totalAmount = totalAmount.add((BigDecimal) head.get("totalAmount"));
            reader.close();
        }

        FileConfig mergedConfig = new FileConfig(targetFilePath,
            "/preheat/template_Allocation.json", storageConfig);
        mergedConfig.setFileEncoding("GBK");
        FileReader mergedReader = FileFactory.createReader(mergedConfig);
        Map<String, Object> mergeHead = mergedReader.readHead(HashMap.class);
        System.out.println("mergedHead: " + mergeHead);

        for (String path : bodyPath) {
            System.out.println(path);
            FileConfig config = new FileConfig(path, "/preheat/template_Allocation.json",
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
        Assert.assertEquals(totalAmount, mergeHead.get("totalAmount"));

        mergedReader.close();

        fileStorage.delete(targetFilePath);
    }

    @Test
    public void mergeSliceAndExsitFile() throws Exception {
        String ossPath = RdfFileUtil.combinePath(ossPathPrefix, "mergeSliceAndExsitFile");
        String ossSlicePath = RdfFileUtil.combinePath(ossPath, "slice");
        String ossAllPath = RdfFileUtil.combinePath(ossPath, "all");
        fileStorage.upload(File.class.getResource("/preheat/de/slice/").getPath(), ossSlicePath,
            false);
        fileStorage.upload(File.class.getResource("/preheat/de/all/").getPath(), ossAllPath, false);

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
        paths.remove(0);
        paths.remove(0);
        paths.remove(0);
        paths.remove(0);
        System.out.println("sortedPath = " + paths);

        FileConfig preheatConfig = new FileConfig(targetFilePath,
            "/preheat/template_Allocation.json", storageConfig);
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
        BigDecimal totalAmount = new BigDecimal(0);
        for (String path : headPath) {
            System.out.println(path);
            FileConfig config = new FileConfig(path, "/preheat/template_Allocation.json",
                storageConfig);
            config.setFileEncoding("GBK");
            config.setFileDataType(FileDataTypeEnum.HEAD);
            FileReader reader = FileFactory.createReader(config);
            Map<String, Object> head = reader.readHead(HashMap.class);
            totalCount += (Integer) head.get("totalCount");
            totalAmount = totalAmount.add((BigDecimal) head.get("totalAmount"));
            reader.close();
        }

        FileConfig mergedConfig = new FileConfig(targetFilePath,
            "/preheat/template_Allocation.json", storageConfig);
        mergedConfig.setFileEncoding("GBK");
        FileReader mergedReader = FileFactory.createReader(mergedConfig);
        Map<String, Object> mergeHead = mergedReader.readHead(HashMap.class);
        System.out.println("mergedHead: " + mergeHead);

        for (String path : paths) {
            System.out.println(path);
            FileConfig config = new FileConfig(path, "/preheat/template_Allocation.json",
                storageConfig);
            config.setFileEncoding("GBK");
            FileReader reader = FileFactory.createReader(config);
            Map<String, Object> head = reader.readHead(HashMap.class);
            totalCount += (Integer) head.get("totalCount");
            totalAmount = totalAmount.add((BigDecimal) head.get("totalAmount"));
            Map<String, Object> row = null;
            while (null != (row = reader.readRow(HashMap.class))) {
                Assert.assertEquals(row, mergedReader.readRow(HashMap.class));
            }

            reader.close();
        }

        for (String path : bodyPath) {
            System.out.println(path);
            FileConfig config = new FileConfig(path, "/preheat/template_Allocation.json",
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
        Assert.assertEquals(totalAmount, mergeHead.get("totalAmount"));

        mergedReader.close();
        fileStorage.delete(targetFilePath);
    }

    @Test
    public void testMergeEmpty() throws Exception {
        String ossPath = RdfFileUtil.combinePath(ossPathPrefix, "testMergeEmpty");
        fileStorage.upload(File.class.getResource("/preheat/de/empty/").getPath(), ossPath, false);

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
            "/preheat/template_Allocation.json", storageConfig);
        preheatConfig.setFileEncoding("GBK");
        preheatConfig.setSummaryEnable(true);
        preheatConfig.setType(FileOssToolContants.OSS_PREHEAT_PROTOCOL_READER);

        FileMerger fileMerger = FileFactory.createMerger(preheatConfig);
        MergerConfig mergerConfig = new MergerConfig();
        mergerConfig.setExistFilePaths(paths);
        fileMerger.merge(mergerConfig);

        FileConfig mergedConfig = new FileConfig(targetFilePath,
            "/preheat/template_Allocation.json", storageConfig);
        mergedConfig.setFileEncoding("GBK");
        FileReader mergedReader = FileFactory.createReader(mergedConfig);
        Map<String, Object> head = mergedReader.readHead(HashMap.class);
        Assert.assertEquals(new Integer(0), head.get("totalCount"));
        Assert.assertEquals(new BigDecimal("0.00"), head.get("totalAmount"));

        Assert.assertNull(mergedReader.readRow(HashMap.class));

        mergedReader.close();
    }

    @After
    public void after() {
        temporaryFolder.delete();
    }
}
