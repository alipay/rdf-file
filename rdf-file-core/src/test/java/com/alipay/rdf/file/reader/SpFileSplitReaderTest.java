package com.alipay.rdf.file.reader;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.alipay.rdf.file.interfaces.FileFactory;
import com.alipay.rdf.file.interfaces.FileReader;
import com.alipay.rdf.file.interfaces.FileSplitter;
import com.alipay.rdf.file.model.FileConfig;
import com.alipay.rdf.file.model.FileDataTypeEnum;
import com.alipay.rdf.file.model.FileSlice;
import com.alipay.rdf.file.model.StorageConfig;
import com.alipay.rdf.file.util.TemporaryFolderUtil;

/**
 * 分片读
 * 
 * @author hongwei.quhw
 * @version $Id: SpFileSplitReaderTest.java, v 0.1 2018年3月5日 下午4:50:03 hongwei.quhw Exp $
 */
public class SpFileSplitReaderTest {
    TemporaryFolderUtil tf = new TemporaryFolderUtil();

    @Before
    public void setUp() throws IOException {
        tf.create();
    }

    @Test
    public void testReadSliceFile() throws Exception {
        String filePath = File.class.getResource("/reader/sp/data/data_split.txt").getPath();

        FileConfig config = new FileConfig(filePath, "/reader/sp/template/template3.json",
            new StorageConfig("nas"));

        FileSplitter splitter = FileFactory.createSplitter(config.getStorageConfig());

        FileSlice headSlice = splitter.getHeadSlice(config);

        FileConfig headConfig = config.clone();
        headConfig.setPartial(headSlice.getStart(), headSlice.getLength(),
            headConfig.getFileDataType());
        FileReader headReader = FileFactory.createReader(headConfig);
        try {
            Map<String, Object> head = headReader.readHead(HashMap.class);
            System.out.println(head);
        } finally {
            headReader.close();
        }

        FileSlice bodySlice = splitter.getBodySlice(config);
        FileConfig bodyConfig = config.clone();
        bodyConfig.setPartial(bodySlice.getStart(), bodySlice.getLength(),
            bodySlice.getFileDataType());
        FileReader bodyReader = FileFactory.createReader(bodyConfig);
        try {
            Map<String, Object> row = null;
            while (null != (row = bodyReader.readRow(HashMap.class))) {
                System.out.println(row);
            }
        } finally {
            bodyReader.close();
        }

        FileSlice tailSlice = splitter.getTailSlice(config);
        FileConfig tailConfig = config.clone();
        tailConfig.setPartial(tailSlice.getStart(), tailSlice.getLength(),
            tailSlice.getFileDataType());
        FileReader tailReader = FileFactory.createReader(tailConfig);
        try {
            Map<String, Object> tail = tailReader.readTail(HashMap.class);
            System.out.println(tail);
        } finally {
            tailReader.close();
        }
    }

    @Test
    public void testSplitBodyBySize() {
        String filePath = File.class.getResource("/reader/sp/data/data_split.txt").getPath();

        FileConfig config = new FileConfig(filePath, "/reader/sp/template/template3.json",
            new StorageConfig("nas"));

        FileSplitter splitter = FileFactory.createSplitter(config.getStorageConfig());

        List<FileSlice> slices = splitter.getBodySlices(config, 256);
        System.out.println(slices.size());

        for (FileSlice slice : slices) {
            FileConfig sliceConfig = config.clone();
            sliceConfig.setPartial(slice.getStart(), slice.getLength(), slice.getFileDataType());
            FileReader reader = FileFactory.createReader(sliceConfig);
            try {
                Map<String, Object> row = null;
                while (null != (row = reader.readRow(HashMap.class))) {
                    System.out.println(row);
                }

            } finally {
                reader.close();
            }
        }
    }

    @Test
    public void testSplitBySize() {
        String filePath = File.class.getResource("/reader/sp/data/data_split.txt").getPath();

        FileConfig config = new FileConfig(filePath, "/reader/sp/template/template3.json",
            new StorageConfig("nas"));

        FileSplitter splitter = FileFactory.createSplitter(config.getStorageConfig());

        List<FileSlice> slices = splitter.split(filePath, 256);

        System.out.println(slices.size());
        for (FileSlice slice : slices) {
            FileConfig sliceConfig = config.clone();
            sliceConfig.setPartial(slice.getStart(), slice.getLength(), FileDataTypeEnum.UNKOWN);
            FileReader reader = FileFactory.createReader(sliceConfig);
            try {
                String line = null;
                while (null != (line = reader.readLine())) {
                    System.out.println(line);
                }

            } finally {
                reader.close();
            }
        }
    }

}
