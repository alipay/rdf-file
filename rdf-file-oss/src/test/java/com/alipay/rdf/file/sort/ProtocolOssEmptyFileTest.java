package com.alipay.rdf.file.sort;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

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
 * 协议文件空文件测试
 * 
 * @author hongwei.quhw
 * @version $Id: ProtocolEmptyFileTest.java, v 0.1 2017年8月26日 下午9:39:13 hongwei.quhw Exp $
 */
public class ProtocolOssEmptyFileTest {
    private static ThreadPoolExecutor        executor      = new ThreadPoolExecutor(2, 2, 60,
        TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(5));

    private static final StorageConfig       storageConfig = OssTestUtil.geStorageConfig();
    private static String                    ossPath       = "rdf/rdf-file/osssortempty";
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
        String sourceFilePath = File.class.getResource("/sort/data/empty/fund_empty.txt").getPath();
        String ossFilePath = RdfFileUtil.combinePath(ossPath, "fund.txt");
        fileStorage.upload(sourceFilePath, ossFilePath, true);

        FileConfig fileConfig = new FileConfig(ossFilePath, "/sort/template/fund.cfg",
            storageConfig);

        FileSorter sorter = FileFactory.createSorter(fileConfig);

        SortConfig sortConfig = new SortConfig(ossFilePath, SortTypeEnum.DESC, executor,
            ResultFileTypeEnum.FULL_FILE_PATH);
        sortConfig.setResultFileName("testSort");
        sortConfig.setSliceSize(1024);
        sortConfig.setSortIndexes(new int[] { 0, 1 });

        SortResult sortResult = sorter.sort(sortConfig);

        BufferedReader expectedReader = new BufferedReader(new InputStreamReader(
            new FileInputStream(
                new File(File.class.getResource("/sort/data/empty/fund_empty.txt").getPath())),
            "GBK"));

        fileConfig.setFilePath(sortResult.getFullFilePath());
        FileReader reader = FileFactory.createReader(fileConfig);

        String line = null;
        while (null != (line = expectedReader.readLine())) {
            Assert.assertEquals(line, reader.readLine());
        }

        Assert.assertNull(expectedReader.readLine());
        Assert.assertNull(reader.readLine());

        expectedReader.close();
    }

    @Test
    public void test2() throws Exception {

        String sourceFilePath = File.class.getResource("/sort/data/empty/de_empty.txt").getPath();
        String ossFilePath = RdfFileUtil.combinePath(ossPath, "de.txt");
        fileStorage.upload(sourceFilePath, ossFilePath, true);

        FileConfig fileConfig = new FileConfig(ossFilePath, "/sort/template/de.json",
            storageConfig);
        fileConfig.setFileEncoding("gbk");

        FileSorter sorter = FileFactory.createSorter(fileConfig);

        SortConfig sortConfig = new SortConfig(ossFilePath, SortTypeEnum.ASC, executor,
            ResultFileTypeEnum.FULL_FILE_PATH);
        sortConfig.setResultFileName("testSort");
        sortConfig.setSliceSize(1024);
        sortConfig.setSortIndexes(new int[] { 0, 1 });

        SortResult sortResult = sorter.sort(sortConfig);

        BufferedReader expectedReader = new BufferedReader(new InputStreamReader(
            new FileInputStream(
                new File(File.class.getResource("/sort/data/empty/de_empty.txt").getPath())),
            "GBK"));

        fileConfig.setFilePath(sortResult.getFullFilePath());
        FileReader reader = FileFactory.createReader(fileConfig);

        String line = null;
        while (null != (line = expectedReader.readLine())) {
            Assert.assertEquals(line, reader.readLine());
        }

        Assert.assertNull(expectedReader.readLine());
        Assert.assertNull(reader.readLine());

        expectedReader.close();
    }

    @AfterClass
    public static void after() {
        fileStorage.delete(ossPath);
    }
}
