package com.alipay.rdf.file.reader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.alipay.rdf.file.interfaces.FileFactory;
import com.alipay.rdf.file.interfaces.FileReader;
import com.alipay.rdf.file.interfaces.FileWriter;
import com.alipay.rdf.file.model.FileConfig;
import com.alipay.rdf.file.model.StorageConfig;
import com.alipay.rdf.file.util.TemporaryFolderUtil;

public class FileReaderFundTest {

    TemporaryFolderUtil tf = new TemporaryFolderUtil();

    @Before
    public void setUp() throws IOException {
        tf.create();
    }

    @Test
    public void testReadFundFile() throws Exception {
        String filePath = File.class.getResource("/reader/fund/data/data1.txt").getPath();

        FileConfig config = new FileConfig(filePath, "/reader/fund/template/data1.cfg",
            new StorageConfig("nas"));

        FileReader fileReader = FileFactory.createReader(config);

        Map<String, Object> head = fileReader.readHead(HashMap.class);
        System.out.println(head);

        List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
        Map<String, Object> row = null;
        while (null != (row = fileReader.readRow(HashMap.class))) {
            System.out.println(row);
            rows.add(row);
        }

        Map<String, Object> tail = fileReader.readTail(HashMap.class);
        System.out.println(tail);

        fileReader.close();

        File test = new File(tf.getRoot(), "fund.txt");
        System.out.println(test.getAbsolutePath());
        config = new FileConfig(test.getAbsolutePath(), "/reader/fund/template/data1.cfg",
            new StorageConfig("nas"));
        config.setFileEncoding("gbk");

        FileWriter fileWriter = FileFactory.createWriter(config);
        fileWriter.writeHead(head);

        for (Map<String, Object> ro : rows) {
            fileWriter.writeRow(ro);
        }

        fileWriter.writeTail(tail);

        fileWriter.close();

    }

    @After
    public void after() {
        tf.delete();
    }
}
