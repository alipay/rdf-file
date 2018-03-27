package com.alipay.rdf.file.multifilessort;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.alipay.rdf.file.interfaces.FileCoreToolContants;
import com.alipay.rdf.file.interfaces.FileFactory;
import com.alipay.rdf.file.interfaces.FileReader;
import com.alipay.rdf.file.interfaces.FileSorter;
import com.alipay.rdf.file.interfaces.FileStorage;
import com.alipay.rdf.file.model.FileConfig;
import com.alipay.rdf.file.model.FileDataTypeEnum;
import com.alipay.rdf.file.model.FileDefaultConfig;
import com.alipay.rdf.file.model.SortConfig;
import com.alipay.rdf.file.model.SortConfig.ResultFileTypeEnum;
import com.alipay.rdf.file.model.SortConfig.SortTypeEnum;
import com.alipay.rdf.file.model.SortResult;
import com.alipay.rdf.file.model.StorageConfig;
import com.alipay.rdf.file.storage.OssConfig;
import com.alipay.rdf.file.util.OssTestUtil;
import com.alipay.rdf.file.util.RdfFileUtil;
import com.alipay.rdf.file.util.TemporaryFolderUtil;
import com.alipay.rdf.file.util.TestLog;

import junit.framework.Assert;

/**
 * 多个de文件进行排序
 * 
 * @author hongwei.quhw
 * @version $Id: MultiDeFilesSortTest.java, v 0.1 2017年12月12日 下午4:31:07 hongwei.quhw Exp $
 */
public class MultiOssFilesSortTestWithTail {
    private static ThreadPoolExecutor        executor        = new ThreadPoolExecutor(2, 2, 60,
        TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(5));

    private static final StorageConfig       storageConfig   = OssTestUtil.geStorageConfig();
    private static String                    ossPath         = "rdf/rdf-file/MultiOssFilesSortTestWithTail";
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
        System.out.println(temporaryFolder.getRoot().getAbsolutePath());
    }

    /**
     * 3个文件排序
     * 
     * @throws Exception
     */
    @Test
    public void testSortMultiFiles() throws Exception {
        String ossFilePath = RdfFileUtil.combinePath(ossPath, "testSortMultiFiles");
        fileStorage.upload(File.class.getResource("/multiFilesSort/de/data2/").getPath(),
            ossFilePath, true);
        FileConfig fileConfig = new FileConfig("/multiFilesSort/de/de2.json", storageConfig);
        // 设置排序
        fileConfig.setType(FileCoreToolContants.PROTOCOL_MULTI_FILE_SORTER);

        FileSorter fileSorter = FileFactory.createSorter(fileConfig);
        SortConfig sortConfig = new SortConfig(ossFilePath, SortTypeEnum.ASC, executor,
            ResultFileTypeEnum.FULL_FILE_PATH);
        sortConfig.setResultFileName("testSort");
        sortConfig.setSliceSize(1024);
        sortConfig.setSortIndexes(new int[] { 0, 1 });
        String[] sourceFilePaths = new String[3];
        sourceFilePaths[0] = RdfFileUtil.combinePath(ossFilePath, "de1.txt");
        sourceFilePaths[1] = RdfFileUtil.combinePath(ossFilePath, "de2.txt");
        sourceFilePaths[2] = RdfFileUtil.combinePath(ossFilePath, "de3.txt");
        sortConfig.setSourceFilePaths(sourceFilePaths);

        SortResult sortResult = fileSorter.sort(sortConfig);

        fileConfig.setType("protocol");
        fileConfig.setFilePath(sortResult.getFullFilePath());
        FileReader reader = FileFactory.createReader(fileConfig);
        BufferedReader expectedReader = new BufferedReader(new InputStreamReader(
            new FileInputStream(new File(
                File.class.getResource("/multiFilesSort/de/data2/testSort3Files").getPath())),
            "UTF-8"));
        String line = null;
        int i = 0;
        while (null != (line = reader.readLine())) {
            Assert.assertEquals(expectedReader.readLine(), line);
            i++;
        }
        Assert.assertEquals(142, i);
        Assert.assertNull(expectedReader.readLine());
        reader.close();
        expectedReader.close();
        fileStorage.delete(ossFilePath);
    }

    /**
     * 文件无数据排序
     * 
     * @throws Exception
     */
    @Test
    public void testSortMultiFiles2() throws Exception {
        String ossFilePath = RdfFileUtil.combinePath(ossPath, "testSortMultiFiles2");
        fileStorage.upload(File.class.getResource("/multiFilesSort/de/data2/").getPath(),
            ossFilePath, true);
        FileConfig fileConfig = new FileConfig("/multiFilesSort/de/de2.json", storageConfig);
        // 设置排序
        fileConfig.setType(FileCoreToolContants.PROTOCOL_MULTI_FILE_SORTER);

        FileSorter fileSorter = FileFactory.createSorter(fileConfig);
        SortConfig sortConfig = new SortConfig(ossFilePath, SortTypeEnum.ASC, executor,
            ResultFileTypeEnum.FULL_FILE_PATH);
        sortConfig.setResultFileName("testSort");
        sortConfig.setSliceSize(1024);
        sortConfig.setSortIndexes(new int[] { 0, 1 });
        String[] sourceFilePaths = new String[2];
        sourceFilePaths[0] = RdfFileUtil.combinePath(ossFilePath, "de4_nodata.txt");
        sourceFilePaths[1] = RdfFileUtil.combinePath(ossFilePath, "de5_nodata.txt");
        sortConfig.setSourceFilePaths(sourceFilePaths);

        SortResult sortResult = fileSorter.sort(sortConfig);

        fileConfig.setType("protocol");
        fileConfig.setFilePath(sortResult.getFullFilePath());
        FileReader reader = FileFactory.createReader(fileConfig);
        BufferedReader expectedReader = new BufferedReader(new InputStreamReader(
            new FileInputStream(new File(
                File.class.getResource("/multiFilesSort/de/data2/testSortNoData").getPath())),
            "UTF-8"));
        String line = null;
        int i = 0;
        while (null != (line = reader.readLine())) {
            Assert.assertEquals(expectedReader.readLine(), line);
            i++;
        }
        Assert.assertEquals(3, i);
        Assert.assertNull(expectedReader.readLine());
        reader.close();
        expectedReader.close();
        fileStorage.delete(ossFilePath);
    }

    /**
     * 空文件排序
     * 
     * @throws Exception
     */
    @Test
    public void testSortMultiFiles3() throws Exception {
        String ossFilePath = RdfFileUtil.combinePath(ossPath, "testSortMultiFiles3");
        fileStorage.upload(File.class.getResource("/multiFilesSort/de/data2/").getPath(),
            ossFilePath, true);
        FileConfig fileConfig = new FileConfig("/multiFilesSort/de/de2.json", storageConfig);
        // 设置排序
        fileConfig.setType(FileCoreToolContants.PROTOCOL_MULTI_FILE_SORTER);
        //body 文件无数据
        fileConfig.setFileDataType(FileDataTypeEnum.BODY);

        FileSorter fileSorter = FileFactory.createSorter(fileConfig);
        SortConfig sortConfig = new SortConfig(ossFilePath, SortTypeEnum.ASC, executor,
            ResultFileTypeEnum.FULL_FILE_PATH);
        sortConfig.setResultFileName("testSort");
        sortConfig.setSliceSize(1024);
        sortConfig.setSortIndexes(new int[] { 0, 1 });
        String[] sourceFilePaths = new String[2];
        sourceFilePaths[0] = RdfFileUtil.combinePath(ossFilePath, "de6_empty.txt");
        sourceFilePaths[1] = RdfFileUtil.combinePath(ossFilePath, "de7_empty.txt");
        sortConfig.setSourceFilePaths(sourceFilePaths);

        SortResult sortResult = fileSorter.sort(sortConfig);

        fileConfig.setType("protocol");
        fileConfig.setFilePath(sortResult.getFullFilePath());
        FileReader reader = FileFactory.createReader(fileConfig);
        BufferedReader expectedReader = new BufferedReader(new InputStreamReader(
            new FileInputStream(new File(
                File.class.getResource("/multiFilesSort/de/data2/testSortEmpty").getPath())),
            "UTF-8"));
        String line = null;
        int i = 0;
        while (null != (line = reader.readLine())) {
            Assert.assertEquals(expectedReader.readLine(), line);
            i++;
        }
        Assert.assertEquals(0, i);
        Assert.assertNull(expectedReader.readLine());
        reader.close();
        expectedReader.close();
        fileStorage.delete(ossFilePath);
    }

    @After
    public void after() {
        temporaryFolder.delete();
    }
}
