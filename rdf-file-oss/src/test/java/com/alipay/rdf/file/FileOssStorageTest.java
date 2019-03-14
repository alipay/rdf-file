package com.alipay.rdf.file;

import java.io.File;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.alipay.rdf.file.init.RdfInit;
import com.alipay.rdf.file.interfaces.FileFactory;
import com.alipay.rdf.file.interfaces.FileReader;
import com.alipay.rdf.file.interfaces.FileStorage;
import com.alipay.rdf.file.loader.ExtensionLoader;
import com.alipay.rdf.file.model.FileConfig;
import com.alipay.rdf.file.model.FileDefaultConfig;
import com.alipay.rdf.file.model.StorageConfig;
import com.alipay.rdf.file.spi.RdfFileStorageSpi;
import com.alipay.rdf.file.util.OssTestUtil;
import com.alipay.rdf.file.util.TestLog;

public class FileOssStorageTest {
    private static final StorageConfig storageConfig = OssTestUtil.geStorageConfig();

    private FileStorage                fileStorage   = FileFactory.createStorage(storageConfig);;

    @Before
    public void setUp() throws Exception {
        FileDefaultConfig defaultConfig = new FileDefaultConfig();
        TestLog log = new TestLog() {
            @Override
            public boolean isDebug() {
                return false;
            }
        };
        defaultConfig.setCommonLog(log);
    }

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

    @SuppressWarnings("unchecked")
    @Test
    public void testWeakHash() throws Exception {
        Map<Integer, FileStorage> cache = Collections
            .synchronizedMap(new WeakHashMap<Integer, FileStorage>());

        Integer i = 0;
        while (i < 100) {
            FileStorage fileStorage = ExtensionLoader.getExtensionLoader(RdfFileStorageSpi.class)
                .getNewExtension(storageConfig.getStorageType());
            if (fileStorage instanceof RdfInit) {
                ((RdfInit<StorageConfig>) fileStorage).init(storageConfig);
            }
            cache.put(new Integer(i++), fileStorage);
        }

        System.gc();

        Thread.sleep(1000);
    }

    @After
    public void after() {
        fileStorage.delete("rdf/oss/");
    }
}
