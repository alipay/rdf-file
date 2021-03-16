package com.alipay.rdf.file.sort;

import com.alipay.rdf.file.interfaces.FileFactory;
import com.alipay.rdf.file.interfaces.FileSorter;
import com.alipay.rdf.file.interfaces.FileStorage;
import com.alipay.rdf.file.model.*;
import com.alipay.rdf.file.model.SortConfig.ResultFileTypeEnum;
import com.alipay.rdf.file.model.SortConfig.SortTypeEnum;
import com.alipay.rdf.file.spi.RdfFileStorageSpi;
import com.alipay.rdf.file.storage.OssConfig;
import com.alipay.rdf.file.util.*;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 测试协议文件的排序
 * 
 * @author hongwei.quhw
 * @version $Id: ProtocolFileSorterTest.java, v 0.1 2017年8月23日 下午3:21:20 hongwei.quhw Exp $
 */
public class ProtocolOssFileSorterTest {
    private static ThreadPoolExecutor        executor      = new ThreadPoolExecutor(2, 2, 60,
        TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(5));

    private static final StorageConfig       storageConfig = OssTestUtil.geStorageConfig();
    private static String                    ossPath       = "rdf/rdf-file/osssortde";
    private static FileStorage               fileStorage   = FileFactory
        .createStorage(storageConfig);
    private OssConfig                        ossConfig;
    private static final TemporaryFolderUtil tf            = new TemporaryFolderUtil();

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
        tf.create();

        ossConfig = (OssConfig) storageConfig.getParam(OssConfig.OSS_STORAGE_CONFIG_KEY);
        ossConfig.setOssTempRoot(tf.getRoot().getAbsolutePath());
        System.out.println(tf.getRoot().getAbsolutePath());
    }

    @Test
    public void test() throws Exception {
        RdfProfiler.start("testsort");
        ossPath = ossPath + "/test";
        String sorurcePath = File.class.getResource("/sort/data/de/de.txt").getPath();
        String ossFilePath = RdfFileUtil.combinePath(ossPath, "de.txt");
        fileStorage.upload(sorurcePath, ossFilePath, true);

        FileConfig fileConfig = new FileConfig(ossFilePath, "/sort/template/de.json",
            storageConfig);

        FileSorter fileSorter = FileFactory.createSorter(fileConfig);
        SortConfig sortConfig = new SortConfig(ossPath, SortTypeEnum.ASC, executor,
            ResultFileTypeEnum.FULL_FILE_PATH);
        sortConfig.setResultFileName("testSort");
        sortConfig.setSliceSize(1024);
        sortConfig.setSortIndexes(new int[] { 0, 1 });

        SortResult sortResult = fileSorter.sort(sortConfig);

        System.out.println(sortResult.getFullFilePath());
        InputStream is = ((RdfFileStorageSpi) fileStorage)
            .getInputStream(sortResult.getFullFilePath());
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        BufferedReader expectedReader = new BufferedReader(
            new InputStreamReader(
                new FileInputStream(
                    new File(File.class.getResource("/sort/data/de/testSort").getPath())),
                "UTF-8"));
        String line = null;
        while (null != (line = reader.readLine())) {
            Assert.assertEquals(expectedReader.readLine(), line);
        }
        Assert.assertNull(expectedReader.readLine());
        reader.close();
        expectedReader.close();

        RdfProfiler.release();
        System.out.println(RdfProfiler.dump());
        RdfProfiler.reset();
    }

    @Test
    public void testGold() throws Exception {
        ossPath = ossPath + "/testGold";
        String sorurcePath = File.class.getResource("/sort/data/gold/48.txt").getPath();
        String ossFilePath = RdfFileUtil.combinePath(ossPath, "48.txt");
        fileStorage.upload(sorurcePath, ossFilePath, true);

        FileConfig fileConfig = new FileConfig(ossFilePath,
            "/sort/template/ccbgold_balance_new_template.json", storageConfig);

        FileSorter fileSorter = FileFactory.createSorter(fileConfig);
        SortConfig sortConfig = new SortConfig(ossPath, SortTypeEnum.ASC, executor,
            ResultFileTypeEnum.FULL_FILE_PATH);
        sortConfig.setResultFileName("testSort");
        sortConfig.setSliceSize(1024);
        sortConfig.setSortIndexes(new int[] { 0, 1 });
        sortConfig.setResultStorageConfig(storageConfig);

        SortResult sortResult = fileSorter.sort(sortConfig);

        System.out.println(sortResult.getFullFilePath());
        InputStream is = ((RdfFileStorageSpi) fileStorage)
            .getInputStream(sortResult.getFullFilePath());
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        BufferedReader expectedReader = new BufferedReader(new InputStreamReader(
            new FileInputStream(
                new File(File.class.getResource("/sort/data/gold/sorted.txt").getPath())),
            "UTF-8"));
        String line = null;
        while (null != (line = reader.readLine())) {
            Assert.assertEquals(expectedReader.readLine(), line);
            System.out.println(line);
        }
        Assert.assertNull(expectedReader.readLine());
        reader.close();
        expectedReader.close();
    }

    @AfterClass
    public static void after() {
        tf.delete();
        fileStorage.delete(ossPath);
    }
}
