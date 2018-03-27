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
 * 多个de文件进行排序
 * 
 * @author hongwei.quhw
 * @version $Id: MultiDeFilesSortTest.java, v 0.1 2017年12月12日 下午4:31:07 hongwei.quhw Exp $
 */
public class MultiDeFilesSortTestBody {
    private TemporaryFolderUtil       temporaryFolder = new TemporaryFolderUtil();
    private static ThreadPoolExecutor executor        = new ThreadPoolExecutor(2, 2, 60,
        TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(5));

    @Before
    public void setUp() throws Exception {
        temporaryFolder.create();
    }

    /**
     * 3个文件排序
     * 
     * @throws Exception
     */
    @Test
    public void testSortMultiFiles() throws Exception {
        String sortTempPath = temporaryFolder.getRoot().getAbsolutePath();
        FileConfig fileConfig = new FileConfig("/multiFilesSort/de/de3.json",
            new StorageConfig("nas"));
        // 设置排序
        fileConfig.setType(FileCoreToolContants.PROTOCOL_MULTI_FILE_SORTER);

        FileSorter fileSorter = FileFactory.createSorter(fileConfig);
        SortConfig sortConfig = new SortConfig(sortTempPath, SortTypeEnum.ASC, executor,
            ResultFileTypeEnum.FULL_FILE_PATH);
        sortConfig.setResultFileName("testSort");
        sortConfig.setSliceSize(1024);
        sortConfig.setSortIndexes(new int[] { 0, 1 });
        String[] sourceFilePaths = new String[3];
        sourceFilePaths[0] = File.class.getResource("/multiFilesSort/de/data3/de1.txt").getPath();
        sourceFilePaths[1] = File.class.getResource("/multiFilesSort/de/data3/de2.txt").getPath();
        sourceFilePaths[2] = File.class.getResource("/multiFilesSort/de/data3/de3.txt").getPath();
        sortConfig.setSourceFilePaths(sourceFilePaths);

        SortResult sortResult = fileSorter.sort(sortConfig);

        BufferedReader reader = new BufferedReader(new InputStreamReader(
            new FileInputStream(new File(sortResult.getFullFilePath())), "UTF-8"));
        BufferedReader expectedReader = new BufferedReader(new InputStreamReader(
            new FileInputStream(new File(
                File.class.getResource("/multiFilesSort/de/data3/testSort3Files").getPath())),
            "UTF-8"));
        String line = null;
        int i = 0;
        while (null != (line = reader.readLine())) {
            Assert.assertEquals(expectedReader.readLine(), line);
            i++;
        }
        Assert.assertEquals(139, i);
        Assert.assertNull(expectedReader.readLine());
        reader.close();
        expectedReader.close();
    }

    /**
     * 文件无数据排序
     * 
     * @throws Exception
     */
    @Test
    public void testSortMultiFiles2() throws Exception {
        String sortTempPath = temporaryFolder.getRoot().getAbsolutePath();
        FileConfig fileConfig = new FileConfig("/multiFilesSort/de/de3.json",
            new StorageConfig("nas"));
        // 设置排序
        fileConfig.setType(FileCoreToolContants.PROTOCOL_MULTI_FILE_SORTER);

        FileSorter fileSorter = FileFactory.createSorter(fileConfig);
        SortConfig sortConfig = new SortConfig(sortTempPath, SortTypeEnum.ASC, executor,
            ResultFileTypeEnum.FULL_FILE_PATH);
        sortConfig.setResultFileName("testSort");
        sortConfig.setSliceSize(1024);
        sortConfig.setSortIndexes(new int[] { 0, 1 });
        String[] sourceFilePaths = new String[2];
        sourceFilePaths[0] = File.class.getResource("/multiFilesSort/de/data3/de4_nodata.txt")
            .getPath();
        sourceFilePaths[1] = File.class.getResource("/multiFilesSort/de/data3/de5_nodata.txt")
            .getPath();
        sortConfig.setSourceFilePaths(sourceFilePaths);

        SortResult sortResult = fileSorter.sort(sortConfig);

        BufferedReader reader = new BufferedReader(new InputStreamReader(
            new FileInputStream(new File(sortResult.getFullFilePath())), "UTF-8"));
        BufferedReader expectedReader = new BufferedReader(new InputStreamReader(
            new FileInputStream(new File(
                File.class.getResource("/multiFilesSort/de/data3/testSortNoData").getPath())),
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
    }

    @After
    public void after() {
        temporaryFolder.delete();
    }
}
