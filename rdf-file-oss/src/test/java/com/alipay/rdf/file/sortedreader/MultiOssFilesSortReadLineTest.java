package com.alipay.rdf.file.sortedreader;

import java.io.File;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.alipay.rdf.file.common.ProtocolFilesSortedReader;
import com.alipay.rdf.file.interfaces.FileCoreToolContants;
import com.alipay.rdf.file.interfaces.FileFactory;
import com.alipay.rdf.file.interfaces.FileReader;
import com.alipay.rdf.file.interfaces.FileSorter;
import com.alipay.rdf.file.interfaces.FileStorage;
import com.alipay.rdf.file.model.FileConfig;
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
public class MultiOssFilesSortReadLineTest {
    private static ThreadPoolExecutor        executor        = new ThreadPoolExecutor(2, 2, 60,
        TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(5));

    private static final StorageConfig       storageConfig   = OssTestUtil.geStorageConfig();
    private static String                    ossPath         = "rdf/rdf-file/MultiDeFilesSortReadLineTest";
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
     * 文件先排序后读取
     */
    @Test
    public void testSortMultiFiles() throws Exception {
        String ossFilePath = RdfFileUtil.combinePath(ossPath, "testSortMultiFiles");
        fileStorage.upload(File.class.getResource("/sortedreader/de/data/").getPath(), ossFilePath,
            true);
        FileConfig fileConfig = new FileConfig("/sortedreader/de/de.json", storageConfig);
        // 多文件排序类型设置
        fileConfig.setType(FileCoreToolContants.PROTOCOL_MULTI_FILE_SORTER);

        ProtocolFilesSortedReader reader = (ProtocolFilesSortedReader) FileFactory
            .createReader(fileConfig);

        FileSorter fileSorter = (FileSorter) reader;

        // 分片不合并
        SortConfig sortConfig = new SortConfig(ossFilePath, SortTypeEnum.ASC, executor,
            ResultFileTypeEnum.SLICE_FILE_PATH);
        sortConfig.setResultFileName("testSort");
        sortConfig.setSliceSize(1024);
        sortConfig.setSortIndexes(new int[] { 0, 1 });
        String[] sourceFilePaths = new String[3];
        sourceFilePaths[0] = RdfFileUtil.combinePath(ossFilePath, "de1.txt");
        sourceFilePaths[1] = RdfFileUtil.combinePath(ossFilePath, "de2.txt");
        sourceFilePaths[2] = RdfFileUtil.combinePath(ossFilePath, "de3.txt");
        // 跟一个文件排序不同， 多文件排序这里需要设置分片来源
        sortConfig.setSourceFilePaths(sourceFilePaths);

        // 1. 先排序
        fileSorter.sort(sortConfig);

        FileConfig sortedFileConfig = new FileConfig(
            File.class.getResource("/sortedreader/de/data/testSort3Files").getPath(),
            "/sortedreader/de/de.json", new StorageConfig("nas"));
        FileReader sortedFileReader = FileFactory.createReader(sortedFileConfig);
        String line = null;

        // 2. 有序读取
        while (null != (line = reader.readLine())) {
            Assert.assertEquals(line, sortedFileReader.readLine());
        }

        Assert.assertNull(sortedFileReader.readLine());

        fileStorage.delete(ossFilePath);
    }

    /**
     * 给定排好序的文件，有序读取
     */
    @Test
    public void testSortMultiFiles2() throws Exception {
        String ossFilePath = RdfFileUtil.combinePath(ossPath, "testSortMultiFiles2");
        fileStorage.upload(File.class.getResource("/sortedreader/de/data/").getPath(), ossFilePath,
            true);
        FileConfig fileConfig = new FileConfig("/sortedreader/de/de.json", storageConfig);
        // 多文件排序类型设置
        fileConfig.setType(FileCoreToolContants.PROTOCOL_MULTI_FILE_SORTER);

        FileSorter fileSorter = FileFactory.createSorter(fileConfig);

        // 分片不合并
        SortConfig sortConfig = new SortConfig(ossFilePath, SortTypeEnum.ASC, executor,
            ResultFileTypeEnum.SLICE_FILE_PATH);
        sortConfig.setResultFileName("testSort");
        sortConfig.setSliceSize(1024);
        sortConfig.setSortIndexes(new int[] { 0, 1 });
        String[] sourceFilePaths = new String[3];
        sourceFilePaths[0] = RdfFileUtil.combinePath(ossFilePath, "de1.txt");
        sourceFilePaths[1] = RdfFileUtil.combinePath(ossFilePath, "de2.txt");
        sourceFilePaths[2] = RdfFileUtil.combinePath(ossFilePath, "de3.txt");
        // 跟一个文件排序不同， 多文件排序这里需要设置分片来源
        sortConfig.setSourceFilePaths(sourceFilePaths);

        // 1. 先排序
        SortResult sortResult = fileSorter.sort(sortConfig);

        ProtocolFilesSortedReader reader = (ProtocolFilesSortedReader) FileFactory
            .createReader(fileConfig);
        // 2. 排序好序的文件设置到reader中
        reader.setSortedResult(sortConfig, sortResult);

        FileConfig sortedFileConfig = new FileConfig(
            File.class.getResource("/sortedreader/de/data/testSort3Files").getPath(),
            "/sortedreader/de/de.json", new StorageConfig("nas"));
        FileReader sortedFileReader = FileFactory.createReader(sortedFileConfig);
        String line = null;

        // 2. 有序读取
        while (null != (line = reader.readLine())) {
            Assert.assertEquals(line, sortedFileReader.readLine());
        }

        Assert.assertNull(sortedFileReader.readLine());
        fileStorage.delete(ossFilePath);
    }

    @After
    public void after() {
        temporaryFolder.delete();
    }
}
