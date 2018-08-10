package com.alipay.rdf.file.preheat;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.alipay.rdf.file.interfaces.FileFactory;
import com.alipay.rdf.file.interfaces.FileOssToolContants;
import com.alipay.rdf.file.interfaces.FileReader;
import com.alipay.rdf.file.interfaces.FileStorage;
import com.alipay.rdf.file.model.FileConfig;
import com.alipay.rdf.file.model.FileDefaultConfig;
import com.alipay.rdf.file.model.StorageConfig;
import com.alipay.rdf.file.spi.RdfFileSummaryPairSpi;
import com.alipay.rdf.file.storage.OssConfig;
import com.alipay.rdf.file.util.OssTestUtil;
import com.alipay.rdf.file.util.RdfFileUtil;
import com.alipay.rdf.file.util.TemporaryFolderUtil;
import com.alipay.rdf.file.util.TestLog;

import junit.framework.Assert;

/**
 * 验证预热读
 * 
 * @author hongwei.quhw
 * @version $Id: PreheatReadTest.java, v 0.1 2017年7月17日 下午7:06:40 hongwei.quhw Exp $
 */
public class PreheatReadDeFileTest {
    private static final ThreadPoolExecutor executor        = new ThreadPoolExecutor(2, 4, 60,
        TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(10));
    private TemporaryFolderUtil             temporaryFolder = new TemporaryFolderUtil();
    private static final StorageConfig      storageConfig   = OssTestUtil.geStorageConfig();
    private static String                   ossPathPrifx    = "rdf/rdf-file/open/PreheatReadDeFileTest";
    private static FileStorage              fileStorage     = FileFactory
        .createStorage(storageConfig);
    private OssConfig                       ossConfig;

    @Before
    public void setUp() throws Exception {
        FileDefaultConfig defaultConfig = new FileDefaultConfig();
        TestLog log = new TestLog() {
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

    @Test
    public void testPreheatWithNormalRead() throws Exception {
        String ossFilePath = RdfFileUtil.combinePath(ossPathPrifx, "0_41943100");
        fileStorage.upload(File.class.getResource("/preheat/de/all/0_41943100").getPath(),
            ossFilePath, false);

        FileConfig normalConfig = new FileConfig(ossFilePath, "/preheat/template_Allocation.json",
            storageConfig);
        normalConfig.setFileEncoding("GBK");
        normalConfig.setSummaryEnable(true);
        FileReader normalReader = FileFactory.createReader(normalConfig);

        FileConfig preheatConfig = new FileConfig(ossFilePath, "/preheat/template_Allocation.json",
            storageConfig);
        preheatConfig.setFileEncoding("GBK");
        preheatConfig.setSummaryEnable(true);
        preheatConfig.setType(FileOssToolContants.OSS_PREHEAT_PROTOCOL_READER);
        OssPreheatReaderConfig preheatReaderConfig = new OssPreheatReaderConfig();
        preheatReaderConfig.setExecutor(executor);
        preheatReaderConfig.setSliceBlockSize(512 * 1024);
        //preheatReaderConfig.setMonitorThreadPool(true);
        preheatConfig.addParam(OssPreheatReaderConfig.OSS_PREHEAT_READER_CONFIG_KEY,
            preheatReaderConfig);

        FileReader preheatReader = FileFactory.createReader(preheatConfig);

        Assert.assertEquals(normalReader.readHead(HashMap.class),
            preheatReader.readHead(HashMap.class));

        Map<String, Object> row = null;
        while (null != (row = normalReader.readRow(HashMap.class))) {
            Assert.assertEquals(row, preheatReader.readRow(HashMap.class));
        }

        Assert.assertNull(preheatReader.readRow(HashMap.class));

        Assert.assertEquals(normalReader.getSummary().getSummaryPairs().get(0).getSummaryValue(),
            preheatReader.getSummary().getSummaryPairs().get(0).getSummaryValue());
        Assert.assertTrue(preheatReader.getSummary().getSummaryPairs().get(0).isSummaryEquals());

        normalReader.close();
        preheatReader.close();
        //fileStorage.delete(ossFilePath);
    }

    @Test
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void testPreheatWithNormalRead2() throws Exception {
        String ossPath = RdfFileUtil.combinePath(ossPathPrifx, "testPreheatWithNormalRead2");
        fileStorage.upload(File.class.getResource("/preheat/de/all/").getPath(), ossPath, false);

        List<String> paths = fileStorage.listAllFiles(ossPath);
        Collections.sort(paths);
        System.out.println(paths);

        FileConfig preheatConfig = new FileConfig("/preheat/template_Allocation.json",
            storageConfig);
        preheatConfig.setFileEncoding("GBK");
        preheatConfig.setSummaryEnable(true);
        preheatConfig.setType(FileOssToolContants.OSS_PREHEAT_PROTOCOL_READER);
        OssPreheatReaderConfig preheatReaderConfig = new OssPreheatReaderConfig();
        // 使用内置线程池
        //preheatReaderConfig.setExecutor(executor);
        preheatReaderConfig.setPaths(paths);
        preheatReaderConfig.setSliceBlockSize(512 * 1024);
        //preheatReaderConfig.setMonitorThreadPool(true);
        preheatConfig.addParam(OssPreheatReaderConfig.OSS_PREHEAT_READER_CONFIG_KEY,
            preheatReaderConfig);

        FileReader preheatReader = FileFactory.createReader(preheatConfig);

        RdfFileSummaryPairSpi summaryPair = null;

        for (String path : paths) {
            System.out.println("normal read path=" + path);
            FileConfig normalConfig = new FileConfig(path, "/preheat/template_Allocation.json",
                storageConfig);
            normalConfig.setFileEncoding("GBK");
            normalConfig.setSummaryEnable(true);
            FileReader normalReader = FileFactory.createReader(normalConfig);
            Map<String, Object> row = null;
            while (null != (row = normalReader.readRow(HashMap.class))) {

                Assert.assertEquals(row, preheatReader.readRow(HashMap.class));
            }

            if (null == summaryPair) {
                summaryPair = (RdfFileSummaryPairSpi) normalReader.getSummary().getSummaryPairs()
                    .get(0);
            } else {
                summaryPair.addColValue(
                    normalReader.getSummary().getSummaryPairs().get(0).getSummaryValue());
            }

            normalReader.close();
        }

        Assert.assertEquals(summaryPair.getSummaryValue(),
            preheatReader.getSummary().getSummaryPairs().get(0).getSummaryValue());
        Assert.assertNull(preheatReader.readRow(HashMap.class));

        System.out.println(summaryPair.getSummaryValue());

        preheatReader.close();

    }

    @Test
    public void testPreheatWithNormalEmpty() throws Exception {
        String ossPath = RdfFileUtil.combinePath(ossPathPrifx, "0_41empty943100");
        fileStorage.upload(File.class.getResource("/preheat/de/empty/").getPath(), ossPath, false);

        List<String> paths = fileStorage.listAllFiles(ossPath);
        Collections.sort(paths);
        System.out.println(paths);

        FileConfig preheatConfig = new FileConfig("/preheat/template_Allocation.json",
            storageConfig);
        preheatConfig.setFileEncoding("GBK");
        preheatConfig.setSummaryEnable(true);
        preheatConfig.setType(FileOssToolContants.OSS_PREHEAT_PROTOCOL_READER);
        OssPreheatReaderConfig preheatReaderConfig = new OssPreheatReaderConfig();
        preheatReaderConfig.setPaths(paths);
        preheatReaderConfig.setSliceBlockSize(512 * 1024);
        preheatConfig.addParam(OssPreheatReaderConfig.OSS_PREHEAT_READER_CONFIG_KEY,
            preheatReaderConfig);

        FileReader preheatReader = FileFactory.createReader(preheatConfig);

        Assert.assertNull(preheatReader.readRow(HashMap.class));

        preheatReader.close();

    }

    @After
    public void after() {
        temporaryFolder.delete();
    }
}
