package com.alipay.rdf.file;

import java.util.List;

import org.junit.Test;

import com.alipay.rdf.file.interfaces.FileFactory;
import com.alipay.rdf.file.interfaces.FileSplitter;
import com.alipay.rdf.file.interfaces.FileStorage;
import com.alipay.rdf.file.model.FileConfig;
import com.alipay.rdf.file.model.FileInfo;
import com.alipay.rdf.file.model.FileSlice;
import com.alipay.rdf.file.model.StorageConfig;
import com.alipay.rdf.file.util.OssTestUtil;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 *
 * 大文件先上传了， 没有就算了
 * @author hongwei.quhw
 * @version $Id: OssBizDataSplitTest.java, v 0.1 2018年3月12日 下午5:30:03 hongwei.quhw Exp $
 */
public class OssBizDataSplitTest {
    private static final StorageConfig storageConfig = OssTestUtil.geStorageConfig();

    private FileStorage                fileStorage   = FileFactory.createStorage(storageConfig);

    @Test
    public void testSplit() throws Exception {

        String osspath = "rdf/biz/finorm-99-5008.et15.alipay.com-tt.txt";
        FileInfo fileInfo = fileStorage.getFileInfo(osspath);

        if (!fileInfo.isExists()) {
            System.out.println("file not exist:" + osspath);
            return;
        }

        FileSplitter fileSplitter = FileFactory.createSplitter(storageConfig);
        FileConfig fileConfig = new FileConfig(osspath, "/bizdata/bswsno-standard03_template.txt",
            storageConfig);

        List<FileSlice> slices = fileSplitter.getBodySlices(fileConfig, 20971520);

        for (FileSlice fs : slices) {
            System.out.println(
                "start:" + fs.getStart() + ", end=" + fs.getEnd() + ", length=" + fs.getLength());
        }

        System.out.println(fileSplitter.getHeadSlice(fileConfig));
    }

}
