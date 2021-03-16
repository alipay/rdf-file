package com.alipay.rdf.file.reader;

import com.alipay.rdf.file.exception.RdfErrorEnum;
import com.alipay.rdf.file.exception.RdfFileException;
import com.alipay.rdf.file.interfaces.FileCoreToolContants;
import com.alipay.rdf.file.interfaces.FileFactory;
import com.alipay.rdf.file.interfaces.FileReader;
import com.alipay.rdf.file.model.FileConfig;
import com.alipay.rdf.file.model.StorageConfig;
import com.alipay.rdf.file.util.TemporaryFolderUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;


/**
 * 原生文件读取测试
 * 
 * @author hongwei.quhw
 * @version $Id: RawFileReaderTest.java, v 0.1 2018年3月5日 下午7:45:42 hongwei.quhw Exp $
 */
public class RawFileReaderTest {
    TemporaryFolderUtil tf = new TemporaryFolderUtil();

    @Before
    public void setUp() throws IOException {
        tf.create();
    }

    @Test
    public void testRead() {
        String filePath = File.class.getResource("/reader/de/data/data1.txt").getPath();

        FileConfig config = new FileConfig(filePath, "/reader/de/template/template1.json",
            new StorageConfig("nas"));
        config.setType(FileCoreToolContants.RAW_READER);

        FileReader fileReader = FileFactory.createReader(config);

        try {
            Map<String, Object> head = fileReader.readHead(HashMap.class);
            Assert.assertEquals(new Long(100), head.get("totalCount"));
            Assert.assertEquals(new BigDecimal("300.03"), head.get("totalAmount"));
            Assert.fail();
        } catch (RdfFileException e) {
            Assert.assertEquals(RdfErrorEnum.UNSUPPORTED_OPERATION, e.getErrorEnum());
        }

        try {
            fileReader.readRow(HashMap.class);
            Assert.fail();
        } catch (RdfFileException e) {
            Assert.assertEquals(RdfErrorEnum.ILLEGAL_ARGUMENT, e.getErrorEnum());
        }

        String[] row = null;
        while (null != (row = fileReader.readRow(String[].class))) {
            for (String col : row) {
                System.out.print(col);
                System.out.print("|");
            }
            System.out.println();
        }

        fileReader.close();

        fileReader = FileFactory.createReader(config);
        String line = null;
        while (null != (line = fileReader.readLine())) {
            System.out.println(line);
        }

        fileReader.close();
    }

    @After
    public void after() {
        tf.delete();
    }
}
