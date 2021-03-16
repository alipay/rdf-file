package com.alipay.rdf.file.sort;

import com.alipay.rdf.file.interfaces.FileFactory;
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

import java.io.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 对字段进行重新排列
 * 
 * @author hongwei.quhw
 * @version $Id: SeparatorColumnSortTest.java, v 0.1 2017年6月29日 下午5:21:01 hongwei.quhw Exp $
 */
public class ProtocolColumnRearrangeTest {

    private TemporaryFolderUtil       temporaryFolder = new TemporaryFolderUtil();
    private static ThreadPoolExecutor executor        = new ThreadPoolExecutor(2, 2, 60,
        TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(5));

    @Before
    public void setUp() throws Exception {
        temporaryFolder.create();
    }

    //@Test
    public void testASC() throws IOException {
        String sortTempPath = temporaryFolder.getRoot().getAbsolutePath();
        System.out.println(sortTempPath);
        String sourceFilePath = File.class.getResource("/sort/data/rearrange/source").getPath();
        FileConfig fileConfig = new FileConfig(sourceFilePath, "/sort/template/de.json",
            new StorageConfig("nas"));
        fileConfig.setFileEncoding("gbk");

        FileSorter sorter = FileFactory.createSorter(fileConfig);

        SortConfig sortConfig = new SortConfig(sortTempPath, SortTypeEnum.ASC, executor,
            ResultFileTypeEnum.FULL_FILE_PATH);
        sortConfig.setResultFileName("testSort");
        sortConfig.setSliceSize(1024);
        sortConfig.setSortIndexes(new int[] { 0, 1 });
        sortConfig.setColumnRearrangeIndex(new int[] { 1, 0, 10, 6 });

        SortResult sortResult = sorter.sort(sortConfig);

        BufferedReader reader = new BufferedReader(new InputStreamReader(
            new FileInputStream(new File(sortResult.getFullFilePath())), "GBK"));
        BufferedReader expectedReader = new BufferedReader(new InputStreamReader(
            new FileInputStream(new File(File.class.getResource("/sort/data/rearrange/").getPath(),
                new File(sortResult.getFullFilePath()).getName())),
            "GBK"));
        String line = null;
        while (null != (line = reader.readLine())) {
            Assert.assertEquals(expectedReader.readLine(), line);
        }
        Assert.assertNull(expectedReader.readLine());
        reader.close();
        expectedReader.close();

        Assert.assertNull(line);

    }

    @After
    public void after() {
        temporaryFolder.delete();
    }

}
