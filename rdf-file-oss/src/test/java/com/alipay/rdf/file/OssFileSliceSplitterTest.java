package com.alipay.rdf.file;

import java.io.File;
import java.util.List;

import org.junit.After;
import org.junit.Test;

import com.alipay.rdf.file.interfaces.FileFactory;
import com.alipay.rdf.file.interfaces.FileReader;
import com.alipay.rdf.file.interfaces.FileSplitter;
import com.alipay.rdf.file.interfaces.FileStorage;
import com.alipay.rdf.file.model.FileConfig;
import com.alipay.rdf.file.model.FileSlice;
import com.alipay.rdf.file.model.StorageConfig;
import com.alipay.rdf.file.util.OssTestUtil;
import com.alipay.rdf.file.util.RdfProfiler;

public class OssFileSliceSplitterTest {
    private static final StorageConfig storageConfig = OssTestUtil.geStorageConfig();

    private FileStorage                fileStorage   = FileFactory.createStorage(storageConfig);

    @Test
    public void testSplit() throws Exception {
        String src = File.class.getResource("/data/data1.txt").getPath();
        String osspath = "rdf/oss/data1.txt";
        fileStorage.upload(src, osspath, true);

        //FileConfig fileConfig = new FileConfig(osspath, null, storageConfig);

        FileSplitter fileSplitter = FileFactory.createSplitter(storageConfig);
        List<FileSlice> slices = fileSplitter.split(osspath, 64);
        slices = fileSplitter.split(osspath, 64);
        //System.out.println(slices.size());

        System.out.println(RdfProfiler.dump());
        System.out.println(RdfProfiler.getDuration());

        FileConfig fileConfig = new FileConfig(osspath, null, storageConfig);
        fileConfig.setType("raw");
        for (FileSlice fs : slices) {
            fileConfig.setPartial(fs.getStart(), fs.getLength(), fs.getFileDataType());
            FileReader reader = FileFactory.createReader(fileConfig);
            String line = null;
            while (null != (line = reader.readLine())) {
                System.out.println(line);
            }
            reader.close();
            System.out.println("-------------------");
        }
    }

    @After
    public void after() {
        fileStorage.delete("rdf/oss/");
    }
}
