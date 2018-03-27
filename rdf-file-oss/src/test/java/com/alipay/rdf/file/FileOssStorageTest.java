package com.alipay.rdf.file;

import java.io.File;

import org.junit.After;
import org.junit.Test;

import com.alipay.rdf.file.interfaces.FileFactory;
import com.alipay.rdf.file.interfaces.FileReader;
import com.alipay.rdf.file.interfaces.FileStorage;
import com.alipay.rdf.file.model.FileConfig;
import com.alipay.rdf.file.model.StorageConfig;
import com.alipay.rdf.file.util.OssTestUtil;

public class FileOssStorageTest {
    private static final StorageConfig storageConfig = OssTestUtil.geStorageConfig();

    private FileStorage                fileStorage   = FileFactory.createStorage(storageConfig);;

    @Test
    public void test() throws Exception {
        String src = File.class.getResource("/data/data1.txt").getPath();
        String osspath = "rdf/oss/data1.txt";

        fileStorage.upload(src, osspath, true);

        FileConfig fileConfig = new FileConfig(osspath, null, storageConfig);
        fileConfig.setType("raw");

        FileReader fileReader = FileFactory.createReader(fileConfig);

        String line = null;

        while (null != (line = fileReader.readLine())) {
            System.out.println(line);
        }
    }

    @After
    public void after() {
        fileStorage.delete("rdf/oss/");
    }
}
