package com.alipay.rdf.file.split;

import java.io.File;
import java.util.HashMap;

import org.junit.Test;

import com.alipay.rdf.file.interfaces.FileFactory;
import com.alipay.rdf.file.interfaces.FileReader;
import com.alipay.rdf.file.model.FileConfig;
import com.alipay.rdf.file.model.FileSlice;
import com.alipay.rdf.file.model.StorageConfig;
import com.alipay.rdf.file.spi.RdfFileSplitterSpi;

import junit.framework.Assert;

public class NasFileSliceSplitterTest {

    @Test
    public void testTailSilce() throws Exception {
        String filePath = File.class.getResource("/split/data1.txt").getPath();
        StorageConfig storageConfig = new StorageConfig("nas");
        FileConfig fileConfig = new FileConfig(filePath, "/split/data1.cfg", storageConfig);
        RdfFileSplitterSpi fileSplitter = (RdfFileSplitterSpi) FileFactory
            .createSplitter(storageConfig);
        FileSlice fileSlice = fileSplitter.getTailSlice(fileConfig);
        fileConfig.setPartial(fileSlice.getStart(), fileSlice.getLength(),
            fileSlice.getFileDataType());

        FileReader fileReader = FileFactory.createReader(fileConfig);
        Assert.assertEquals("OFDCFEND", fileReader.readLine());
    }

    @Test
    public void testNasTailSlice() throws Exception {
        String filePath = File.class.getResource("/split/data1.txt").getPath();
        StorageConfig storageConfig = new StorageConfig("nas");
        FileConfig fileConfig = new FileConfig(filePath, "/split/data1.cfg", storageConfig);
        FileReader fileReader = FileFactory.createReader(fileConfig);
        HashMap<String, Object> tail = fileReader.readTail(HashMap.class);
        Assert.assertEquals("OFDCFEND", tail.get("fileEnd"));
    }
}
