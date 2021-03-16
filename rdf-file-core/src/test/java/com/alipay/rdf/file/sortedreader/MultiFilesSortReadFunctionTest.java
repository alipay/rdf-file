package com.alipay.rdf.file.sortedreader;

import com.alipay.rdf.file.common.ProtocolFilesSortedReader;
import com.alipay.rdf.file.interfaces.FileCoreToolContants;
import com.alipay.rdf.file.interfaces.FileFactory;
import com.alipay.rdf.file.interfaces.FileSorter;
import com.alipay.rdf.file.model.*;
import com.alipay.rdf.file.model.SortConfig.ResultFileTypeEnum;
import com.alipay.rdf.file.model.SortConfig.SortTypeEnum;
import com.alipay.rdf.file.util.TemporaryFolderUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 功能测试
 * 
 * @author hongwei.quhw
 * @version $Id: MultiDeFilesSortTest.java, v 0.1 2017年12月12日 下午4:31:07 hongwei.quhw Exp $
 */
@SuppressWarnings("rawtypes")
public class MultiFilesSortReadFunctionTest {
    private TemporaryFolderUtil       temporaryFolder = new TemporaryFolderUtil();
    private static ThreadPoolExecutor executor        = new ThreadPoolExecutor(2, 2, 60,
        TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(5));

    @Before
    public void setUp() throws Exception {
        temporaryFolder.create();
    }

    /**
     * 文件先排序后读取， 并进行类型转换
     */
    @Test
    public void testSortMultiFiles() throws Exception {
        String sortTempPath = temporaryFolder.getRoot().getAbsolutePath();
        FileConfig fileConfig = new FileConfig("/sortedreader/de/de2.json",
            new StorageConfig("nas"));
        // 多文件排序类型设置
        fileConfig.setType(FileCoreToolContants.PROTOCOL_MULTI_FILE_SORTER);

        ProtocolFilesSortedReader reader = (ProtocolFilesSortedReader) FileFactory
            .createReader(fileConfig);

        FileSorter fileSorter = (FileSorter) reader;

        // 分片不合并
        SortConfig sortConfig = new SortConfig(sortTempPath, SortTypeEnum.ASC, executor,
            ResultFileTypeEnum.SLICE_FILE_PATH);
        sortConfig.setResultFileName("testSort");
        sortConfig.setSliceSize(1024);
        sortConfig.setSortIndexes(new int[] { 0, 1 });
        String[] sourceFilePaths = new String[3];
        sourceFilePaths[0] = File.class.getResource("/sortedreader/de/data2/de1.txt").getPath();
        sourceFilePaths[1] = File.class.getResource("/sortedreader/de/data2/de2.txt").getPath();
        sourceFilePaths[2] = File.class.getResource("/sortedreader/de/data2/de3.txt").getPath();
        // 跟一个文件排序不同， 多文件排序这里需要设置分片来源
        sortConfig.setSourceFilePaths(sourceFilePaths);

        // 1. 先排序
        fileSorter.sort(sortConfig);

        // 读取数据
        Map<String, Object> head = reader.readHead(HashMap.class);
        Assert.assertEquals(2, head.size());
        Assert.assertEquals(new BigDecimal(5), head.get("totalCount"));
        Assert.assertEquals(new BigDecimal(132), head.get("totalNumber"));

        Map<String, Object> tail = reader.readTail(HashMap.class);
        Assert.assertEquals(1, tail.size());
        Assert.assertEquals(new BigDecimal("156.64"), tail.get("totalAmount"));

        Map<String, Object> row = reader.readRow(HashMap.class);
        Assert.assertEquals("seq_1", row.get("seq"));
        Assert.assertEquals(new BigDecimal("23.33"), row.get("amount"));

        row = reader.readRow(HashMap.class);
        Assert.assertEquals("seq_12", row.get("seq"));
        Assert.assertEquals(new BigDecimal("22.00"), row.get("amount"));

        row = reader.readRow(HashMap.class);
        Assert.assertEquals("seq_17", row.get("seq"));
        Assert.assertEquals(new BigDecimal("11.00"), row.get("amount"));

        row = reader.readRow(HashMap.class);
        Assert.assertEquals("seq_23", row.get("seq"));
        Assert.assertEquals(new BigDecimal("00.10"), row.get("amount"));

        row = reader.readRow(HashMap.class);
        Assert.assertEquals("seq_33", row.get("seq"));
        Assert.assertEquals(new BigDecimal("100.21"), row.get("amount"));

        row = reader.readRow(HashMap.class);
        Assert.assertNull(row);
    }

    /**
     * 汇总字段
     */
    @SuppressWarnings("unused")
    @Test
    public void testSortMultiFiles2() throws Exception {
        String sortTempPath = temporaryFolder.getRoot().getAbsolutePath();
        FileConfig fileConfig = new FileConfig("/sortedreader/de/de2.json",
            new StorageConfig("nas"));
        fileConfig.setSummaryEnable(true);
        // 多文件排序类型设置
        fileConfig.setType(FileCoreToolContants.PROTOCOL_MULTI_FILE_SORTER);

        ProtocolFilesSortedReader reader = (ProtocolFilesSortedReader) FileFactory
            .createReader(fileConfig);

        FileSorter fileSorter = (FileSorter) reader;

        // 分片不合并
        SortConfig sortConfig = new SortConfig(sortTempPath, SortTypeEnum.ASC, executor,
            ResultFileTypeEnum.SLICE_FILE_PATH);
        sortConfig.setResultFileName("testSort");
        sortConfig.setSliceSize(1024);
        sortConfig.setSortIndexes(new int[] { 0, 1 });
        String[] sourceFilePaths = new String[3];
        sourceFilePaths[0] = File.class.getResource("/sortedreader/de/data2/de1.txt").getPath();
        sourceFilePaths[1] = File.class.getResource("/sortedreader/de/data2/de2.txt").getPath();
        sourceFilePaths[2] = File.class.getResource("/sortedreader/de/data2/de3.txt").getPath();
        // 跟一个文件排序不同， 多文件排序这里需要设置分片来源
        sortConfig.setSourceFilePaths(sourceFilePaths);

        // 1. 先排序
        fileSorter.sort(sortConfig);

        Map<String, Object> row = null;
        while (null != (row = reader.readRow(HashMap.class))) {
        }

        reader.readHead(HashMap.class);
        reader.readTail(HashMap.class);

        // 读取数据
        Summary summary = reader.getSummary();
        Assert.assertEquals(5, summary.getTotalCount());

        SummaryPair headPiar = summary.getHeadSummaryPairs().get(0);
        Assert.assertTrue(headPiar.isSummaryEquals());
        Assert.assertEquals(new BigDecimal("132"), headPiar.getHeadValue());

        SummaryPair tailPair = summary.getTailSummaryPairs().get(0);
        Assert.assertTrue(tailPair.isSummaryEquals());
        Assert.assertEquals(new BigDecimal("156.64"), tailPair.getTailValue());

        Assert.assertEquals(2, summary.getSummaryPairs().size());
    }

    @After
    public void after() {
        temporaryFolder.delete();
    }
}
