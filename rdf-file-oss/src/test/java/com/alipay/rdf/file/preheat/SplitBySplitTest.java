package com.alipay.rdf.file.preheat;

import java.io.File;
import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.alipay.rdf.file.interfaces.FileFactory;
import com.alipay.rdf.file.interfaces.FileSplitter;
import com.alipay.rdf.file.interfaces.FileStorage;
import com.alipay.rdf.file.model.FileConfig;
import com.alipay.rdf.file.model.FileSlice;
import com.alipay.rdf.file.model.StorageConfig;
import com.alipay.rdf.file.storage.OssConfig;
import com.alipay.rdf.file.util.OssTestUtil;
import com.alipay.rdf.file.util.RdfFileUtil;
import com.alipay.rdf.file.util.TemporaryFolderUtil;

@Ignore
public class SplitBySplitTest {
    private TemporaryFolderUtil        temporaryFolder = new TemporaryFolderUtil();
    private static final StorageConfig storageConfig   = OssTestUtil.geStorageConfig();
    private static String              ossPathPrefix   = "rdf/rdf-file/open/SplitBySplitTest";
    private static FileStorage         fileStorage     = FileFactory.createStorage(storageConfig);
    private OssConfig                  ossConfig;

    @Before
    public void setUp() throws Exception {
        temporaryFolder.create();
        ossConfig = (OssConfig) storageConfig.getParam(OssConfig.OSS_STORAGE_CONFIG_KEY);
        ossConfig.setOssTempRoot(temporaryFolder.getRoot().getAbsolutePath());
        System.out.println(temporaryFolder.getRoot().getAbsolutePath());
    }

    @Test
    public void split() {
        String ossFilePath = RdfFileUtil.combinePath(ossPathPrefix, "OFD_305_H0_20170717_03.txt");
        fileStorage.upload(
            File.class.getResource("/preheat/fund/all/OFD_305_H0_20170717_03.txt").getPath(),
            ossFilePath, false);

        FileConfig config = new FileConfig(ossFilePath, "/preheat/template_batchPurchase.json",
            storageConfig);
        config.setFileEncoding("GBK");

        int sliceSize = 1024 * 1024 * 10;

        FileSplitter splitter = FileFactory.createSplitter(storageConfig);
        List<FileSlice> slices = splitter.getBodySlices(config, sliceSize);

        for (FileSlice slice : slices) {
            System.out.println("外层分片： slice=" + slice);
            FileConfig sliceConfig = config.clone();
            sliceConfig.setPartial(slice.getStart(), slice.getLength(), slice.getFileDataType());
            List<FileSlice> sliceSlices = splitter.getBodySlices(sliceConfig, 1024 * 1024);
            for (FileSlice sliceSlice : sliceSlices) {
                System.out.println("\t内层分片： sliceSlice=" + sliceSlice);
            }
        }

    }
}
