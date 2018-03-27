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

/**
 * 测试fund文件排序
 * 
 * @author hongwei.quhw
 * @version $Id: ProtocolFundFileSortTest.java, v 0.1 2017年8月24日 下午11:42:54 hongwei.quhw Exp $
 */
public class ProtocolOssFundFileSortTest {
    private static ThreadPoolExecutor        executor        = new ThreadPoolExecutor(2, 2, 60,
        TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(5));

    private static final StorageConfig       storageConfig   = OssTestUtil.geStorageConfig();
    private static String                    ossPath         = "rdf/rdf-file/osssortfund/";
    private FileStorage                      fileStorage     = FileFactory
        .createStorage(storageConfig);
    private OssConfig                        ossConfig;
    private static final TemporaryFolderUtil temporaryFolder = new TemporaryFolderUtil();

    @Before
    public void setUp() throws Exception {
        FileDefaultConfig defaultConfig = new FileDefaultConfig();
        TestLog test = new TestLog() {
            @Override
            public boolean isDebug() {
                return false;
            }
        };

        defaultConfig.setCommonLog(test);
        temporaryFolder.create();

        ossConfig = (OssConfig) storageConfig.getParam(OssConfig.OSS_STORAGE_CONFIG_KEY);
        ossConfig.setOssTempRoot(temporaryFolder.getRoot().getAbsolutePath());
        //ossConfig.setOssTempRoot(
        //    "/var/folders/pd/t0ck64755qb57z2_46lxz28c0000gn/T/-6325345980415775955/");
        System.out.println(temporaryFolder.getRoot().getAbsolutePath());
    }

    @Test
    public void test() throws Exception {
        String sorurcePath = File.class.getResource("/sort/data/fund/data.txt").getPath();
        System.out.println(sorurcePath);
        String ossFilePath = RdfFileUtil.combinePath(ossPath, "fund.txt");
        fileStorage.upload(sorurcePath, ossFilePath, true);

        FileConfig fileConfig = new FileConfig(ossFilePath, "/sort/template/fund.cfg",
            storageConfig);
        fileConfig.setFileEncoding("gbk");

        FileSorter sorter = FileFactory.createSorter(fileConfig);
        SortConfig sortConfig = new SortConfig(ossPath, SortTypeEnum.DESC, executor,
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

        Assert.assertNull(reader.readLine());

        reader.close();
        expectedReader.close();
    }

    @After
    public void after() {
        //temporaryFolder.delete();
        fileStorage.delete(ossPath);
    }
}
