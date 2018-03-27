package com.alipay.rdf.file.sort;

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

import com.alipay.rdf.file.interfaces.FileFactory;
import com.alipay.rdf.file.interfaces.FileSorter;
import com.alipay.rdf.file.model.FileConfig;
import com.alipay.rdf.file.model.SortConfig;
import com.alipay.rdf.file.model.SortConfig.ResultFileTypeEnum;
import com.alipay.rdf.file.model.SortConfig.SortTypeEnum;
import com.alipay.rdf.file.model.SortResult;
import com.alipay.rdf.file.model.StorageConfig;
import com.alipay.rdf.file.util.TemporaryFolderUtil;

import junit.framework.Assert;

/**
 * 测试协议文件的排序
 * 
 * @author hongwei.quhw
 * @version $Id: ProtocolFileSorterTest.java, v 0.1 2017年8月23日 下午3:21:20 hongwei.quhw Exp $
 */
public class ProtocolFileSorterTest {
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
        //sortTempPath = "/var/folders/pd/t0ck64755qb57z2_46lxz28c0000gn/T/4169271386463704859/";
        String sorurcePath = File.class.getResource("/sort/data/de/de.txt").getPath();
        FileConfig fileConfig = new FileConfig(sorurcePath, "/sort/template/de.json",
            new StorageConfig("nas"));

        FileSorter fileSorter = FileFactory.createSorter(fileConfig);
        SortConfig sortConfig = new SortConfig(sortTempPath, SortTypeEnum.ASC, executor,
            ResultFileTypeEnum.FULL_FILE_PATH);
        sortConfig.setResultFileName("testSort");
        sortConfig.setSliceSize(1024);
        sortConfig.setSortIndexes(new int[] { 0, 1 });

        SortResult sortResult = fileSorter.sort(sortConfig);

        System.out.println(sortResult.getFullFilePath());
        BufferedReader reader = new BufferedReader(new InputStreamReader(
            new FileInputStream(new File(sortResult.getFullFilePath())), "UTF-8"));
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
    }

    @Test
    public void test2() throws Exception {
        String sortTempPath = temporaryFolder.getRoot().getAbsolutePath();

        FileConfig config = new FileConfig(
            File.class.getResource("/sort/data/gold/48.txt").getPath(),
            "/sort/template/ccbgold_balance_new_template.json", new StorageConfig("nas"));

        FileSorter fileSorter = FileFactory.createSorter(config);
        SortConfig sortConfig = new SortConfig(sortTempPath, SortTypeEnum.ASC, executor,
            ResultFileTypeEnum.FULL_FILE_PATH);
        sortConfig.setResultFileName("testSort");
        sortConfig.setSliceSize(1024);
        sortConfig.setSortIndexes(new int[] { 0, 1 });

        SortResult sortResult = fileSorter.sort(sortConfig);

        BufferedReader reader = new BufferedReader(new InputStreamReader(
            new FileInputStream(new File(sortResult.getFullFilePath())), "UTF-8"));
        BufferedReader expectedReader = new BufferedReader(new InputStreamReader(
            new FileInputStream(
                new File(File.class.getResource("/sort/data/gold/sorted.txt").getPath())),
            "UTF-8"));
        String line = null;
        while (null != (line = reader.readLine())) {
            Assert.assertEquals(expectedReader.readLine(), line);
        }
        Assert.assertNull(expectedReader.readLine());
        reader.close();
        expectedReader.close();
    }

    @After
    public void after() {
        temporaryFolder.delete();
    }
}
