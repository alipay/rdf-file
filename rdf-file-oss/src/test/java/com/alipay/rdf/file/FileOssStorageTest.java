package com.alipay.rdf.file;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

import com.alipay.rdf.file.exception.RdfErrorEnum;
import com.alipay.rdf.file.exception.RdfFileException;
import com.alipay.rdf.file.util.RdfFileUtil;
import com.alipay.rdf.file.util.TemporaryFolderUtil;
import org.junit.After;
import org.junit.Assert;
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

    @Test
    public void testDownload_file() throws IOException {
        // file exists
        String src = File.class.getResource("/data/data1.txt").getPath();
        String osspath = "rdf/oss/data1.txt";

        fileStorage.upload(src, osspath, true);
        TemporaryFolderUtil tf = new TemporaryFolderUtil();
        tf.create();
        String localFilePath = RdfFileUtil.combinePath(tf.getRoot().getAbsolutePath(), "download_data1.txt");
        fileStorage.download(osspath, localFilePath);
        File localFile = new File(localFilePath);
        Assert.assertTrue(localFile.exists());
        tf.delete();

        // file not exists
        try {
            fileStorage.download("FileNotExists", localFilePath);
        }catch (RdfFileException e){
            Assert.assertTrue(e.getErrorEnum() == RdfErrorEnum.NOT_EXSIT);
        }
    }

    @Test
    public void testDownload_files() throws IOException {
        // dir exists
        String src = File.class.getResource("/downloadtestdir").getPath();
        String osspath = "rdf/oss/downloadtestdir";

        fileStorage.upload(src, osspath, true);
        TemporaryFolderUtil tf = new TemporaryFolderUtil();
        tf.create();
        String localFilePath = tf.getRoot().getAbsolutePath();
        fileStorage.download(osspath, localFilePath);
        File data1 = new File(RdfFileUtil.combinePath(localFilePath, "data1.txt"));
        File data2 = new File(RdfFileUtil.combinePath(localFilePath, "data1.txt"));
        Assert.assertTrue(data1.exists());
        Assert.assertTrue(data2.exists());
        tf.delete();

        // dir not exists
        try {
            fileStorage.download("DirNotExists", localFilePath);
        }catch (RdfFileException e){
            Assert.assertTrue(e.getErrorEnum() == RdfErrorEnum.NOT_EXSIT);
        }
    }

    @After
    public void after() {
        fileStorage.delete("rdf/oss/");
    }
}
