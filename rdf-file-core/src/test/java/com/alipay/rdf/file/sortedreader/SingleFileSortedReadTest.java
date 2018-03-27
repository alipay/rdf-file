package com.alipay.rdf.file.sortedreader;

import java.io.File;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.alipay.rdf.file.common.ProtocolFilesSortedReader;
import com.alipay.rdf.file.interfaces.FileCoreToolContants;
import com.alipay.rdf.file.interfaces.FileFactory;
import com.alipay.rdf.file.interfaces.FileSorter;
import com.alipay.rdf.file.model.FileConfig;
import com.alipay.rdf.file.model.SortConfig;
import com.alipay.rdf.file.model.SortConfig.ResultFileTypeEnum;
import com.alipay.rdf.file.model.SortConfig.SortTypeEnum;
import com.alipay.rdf.file.model.StorageConfig;
import com.alipay.rdf.file.util.TemporaryFolderUtil;

import junit.framework.Assert;

/**
 * 
 * 
 * @author hongwei.quhw
 * @version $Id: SingleFileSortedReadTest.java, v 0.1 2018年3月5日 下午5:54:45 hongwei.quhw Exp $
 */
public class SingleFileSortedReadTest {
    private TemporaryFolderUtil       temporaryFolder = new TemporaryFolderUtil();

    private static ThreadPoolExecutor executor        = new ThreadPoolExecutor(2, 2, 60,
        TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(5));

    @Before
    public void setUp() throws Exception {
        temporaryFolder.create();
    }

    @Test
    public void testSortedRead() {
        String sortTempPath = temporaryFolder.getRoot().getAbsolutePath();
        FileConfig fileConfig = new FileConfig(
            File.class.getResource("/sortedreader/de/data/de2.txt").getPath(),
            "/sortedreader/de/de.json", new StorageConfig("nas"));
        // 多文件排序类型设置
        fileConfig.setType(FileCoreToolContants.PROTOCOL_MULTI_FILE_SORTER);

        ProtocolFilesSortedReader reader = (ProtocolFilesSortedReader) FileFactory
            .createReader(fileConfig);

        FileSorter fileSorter = (FileSorter) reader;

        // 分片不合并
        SortConfig sortConfig = new SortConfig(sortTempPath, SortTypeEnum.ASC, executor,
            ResultFileTypeEnum.SLICE_FILE_PATH);
        sortConfig.setSliceSize(256);
        sortConfig.setSortIndexes(new int[] { 0, 1 });

        // 1. 先排序
        fileSorter.sort(sortConfig);

        // 读取数据
        Map<String, Object> head = reader.readHead(HashMap.class);
        System.out.println(head);
        Assert.assertEquals(new BigDecimal("300.03"), head.get("totalAmount"));

        String[] sortedSeq = new String[] { "seq_12", "seq_17", "seq_23", "seq_33", "seq_55",
                                            "seq_56", "seq_7", "seq_77", "seq_80" };

        Map<String, Object> row = null;
        int i = 0;
        while (null != (row = reader.readRow(HashMap.class))) {
            Assert.assertEquals(sortedSeq[i++], row.get("seq"));
        }
    }

    @After
    public void after() {
        temporaryFolder.delete();
    }
}
