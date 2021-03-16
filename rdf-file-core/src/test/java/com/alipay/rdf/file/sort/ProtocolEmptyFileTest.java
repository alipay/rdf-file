package com.alipay.rdf.file.sort;

import com.alipay.rdf.file.interfaces.FileCoreStorageContants;
import com.alipay.rdf.file.interfaces.FileFactory;
import com.alipay.rdf.file.interfaces.FileReader;
import com.alipay.rdf.file.interfaces.FileSorter;
import com.alipay.rdf.file.model.FileConfig;
import com.alipay.rdf.file.model.SortConfig;
import com.alipay.rdf.file.model.SortConfig.ResultFileTypeEnum;
import com.alipay.rdf.file.model.SortConfig.SortTypeEnum;
import com.alipay.rdf.file.model.SortResult;
import com.alipay.rdf.file.model.StorageConfig;
import com.alipay.rdf.file.util.TemporaryFolderUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 协议文件空文件测试
 * 
 * @author hongwei.quhw
 * @version $Id: ProtocolEmptyFileTest.java, v 0.1 2017年8月26日 下午9:39:13 hongwei.quhw Exp $
 */
public class ProtocolEmptyFileTest {
    private TemporaryFolderUtil       temporaryFolder = new TemporaryFolderUtil();
    private static ThreadPoolExecutor executor        = new ThreadPoolExecutor(2, 2, 60,
        TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(5));

    @Before
    public void setUp() throws Exception {
        temporaryFolder.create();
    }

    @Test
    public void test() throws Exception {
        String sortTempPath = temporaryFolder.getRoot().getAbsolutePath();
        System.out.println(sortTempPath);
        String sourceFilePath = File.class.getResource("/sort/data/empty/fund_empty.txt").getPath();
        FileConfig fileConfig = new FileConfig(sourceFilePath, "/sort/template/fund.cfg",
            new StorageConfig(FileCoreStorageContants.STORAGE_LOCAL));

        FileSorter sorter = FileFactory.createSorter(fileConfig);

        SortConfig sortConfig = new SortConfig(sortTempPath, SortTypeEnum.DESC, executor,
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
        String sortTempPath = temporaryFolder.getRoot().getAbsolutePath();

        String sourceFilePath = File.class.getResource("/sort/data/empty/de_empty.txt").getPath();
        FileConfig fileConfig = new FileConfig(sourceFilePath, "/sort/template/de.json",
            new StorageConfig(FileCoreStorageContants.STORAGE_LOCAL));
        fileConfig.setFileEncoding("gbk");

        FileSorter sorter = FileFactory.createSorter(fileConfig);

        SortConfig sortConfig = new SortConfig(sortTempPath, SortTypeEnum.ASC, executor,
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

    @After
    public void after() {
        temporaryFolder.delete();
    }
}
