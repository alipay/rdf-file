package com.alipay.rdf.file.sort;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

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

/**
 * 测试fund文件排序
 * 
 * @author hongwei.quhw
 * @version $Id: ProtocolFundFileSortTest.java, v 0.1 2017年8月24日 下午11:42:54 hongwei.quhw Exp $
 */
public class ProtocolFundFileSortTest {
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
        String sourceFilePath = File.class.getResource("/sort/data/fund/data.txt").getPath();
        System.out.println(sourceFilePath);

        FileConfig fileConfig = new FileConfig(sourceFilePath, "/sort/template/fund.cfg",
            new StorageConfig(FileCoreStorageContants.STORAGE_NAS));
        fileConfig.setFileEncoding("gbk");

        FileSorter sorter = FileFactory.createSorter(fileConfig);
        SortConfig sortConfig = new SortConfig(sortTempPath, SortTypeEnum.DESC, executor,
            ResultFileTypeEnum.FULL_FILE_PATH);
        sortConfig.setResultFileName("testSort");
        sortConfig.setSliceSize(1024);
        sortConfig.setSortIndexes(new int[] { 0, 1 });

        SortResult sortResult = sorter.sort(sortConfig);
        fileConfig.setFilePath(sortResult.getFullFilePath());
        FileReader reader = FileFactory.createReader(fileConfig);
        String testSort = File.class.getResource("/sort/data/fund/testSort").getPath();
        BufferedReader expectedReader = new BufferedReader(
            new InputStreamReader(new FileInputStream(new File(testSort)), "gbk"));
        String line = null;
        while (null != (line = expectedReader.readLine())) {
            System.out.println(line);
            Assert.assertEquals(line, reader.readLine());
        }

        reader.close();
        expectedReader.close();
    }

    @After
    public void after() {
        temporaryFolder.delete();
    }
}
