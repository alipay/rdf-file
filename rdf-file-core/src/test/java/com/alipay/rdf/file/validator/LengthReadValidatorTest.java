package com.alipay.rdf.file.validator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.alipay.rdf.file.interfaces.FileFactory;
import com.alipay.rdf.file.interfaces.FileReader;
import com.alipay.rdf.file.model.FileConfig;
import com.alipay.rdf.file.model.StorageConfig;
import com.alipay.rdf.file.util.TemporaryFolderUtil;

/**
 * 长度校验
 * 
 * @author hongwei.quhw
 * @version $Id: LengthReadValidatorTest.java, v 0.1 2017年8月9日 下午2:56:01 hongwei.quhw Exp $
 */
public class LengthReadValidatorTest {
    TemporaryFolderUtil tf = new TemporaryFolderUtil();

    @Before
    public void setUp() throws IOException {
        tf.create();
    }

    @Test
    public void testReadDEFile() throws Exception {
        String filePath = File.class.getResource("/reader/de/data/data1.txt").getPath();

        FileConfig config = new FileConfig(filePath, "/reader/de/template/template1.json",
            new StorageConfig("nas"));

        config.addProcessorKey("lengthReadValidator");

        FileReader fileReader = FileFactory.createReader(config);

        Map<String, Object> head = fileReader.readHead(HashMap.class);
        System.out.println(head);

        List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
        Map<String, Object> row = null;
        while (null != (row = fileReader.readRow(HashMap.class))) {
            System.out.println(row);
            rows.add(row);
        }

        fileReader.close();
    }

}
