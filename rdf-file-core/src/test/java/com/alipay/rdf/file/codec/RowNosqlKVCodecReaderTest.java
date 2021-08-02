package com.alipay.rdf.file.codec;

import com.alipay.rdf.file.exception.RdfFileException;
import com.alipay.rdf.file.interfaces.FileFactory;
import com.alipay.rdf.file.interfaces.FileReader;
import com.alipay.rdf.file.model.FileConfig;
import com.alipay.rdf.file.model.StorageConfig;
import com.alipay.rdf.file.util.TemporaryFolderUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

/**
 * @Author: hongwei.quhw 2021/8/2 7:34 下午
 */
public class RowNosqlKVCodecReaderTest {
    TemporaryFolderUtil tf = new TemporaryFolderUtil();

    @Before
    public void setUp() throws IOException {
        tf.create();
    }

    @Test(expected = RdfFileException.class)
    public void test1() throws Exception {
        String filePath = File.class.getResource("/codec/kv/data/data1.txt").getPath();

        FileConfig config = new FileConfig(filePath, "/codec/kv/template/template1.json", new StorageConfig("nas"));

        FileReader fileReader = FileFactory.createReader(config);

        fileReader.readHead(HashMap.class);
    }

    @After
    public void after() {
        tf.delete();
    }
}
