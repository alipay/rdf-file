package com.alipay.rdf.file.sort;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.alipay.rdf.file.exception.RdfErrorEnum;
import com.alipay.rdf.file.exception.RdfFileException;
import com.alipay.rdf.file.interfaces.FileFactory;
import com.alipay.rdf.file.interfaces.FileSplitter;
import com.alipay.rdf.file.model.FileConfig;
import com.alipay.rdf.file.model.FileSlice;
import com.alipay.rdf.file.model.StorageConfig;

/**
 * 大文件放本地测试了
 * 
 * 没有忽略
 * 
 * 测试协议文件的排序
 * 
 * @author hongwei.quhw
 * @version $Id: ProtocolFileSorterTest.java, v 0.1 2017年8月23日 下午3:21:20 hongwei.quhw Exp $
 */
public class ProtocolFileSorterTest2 {

    @Test
    public void test() {
        try {

            String sorurcePath = "/Users/quhongwei/Downloads/finorm-99-5008.et15.alipay.com-tt.txt";
            FileConfig fileConfig = new FileConfig(sorurcePath,
                "/sort/template/bswsno-standard03_template.txt", new StorageConfig("nas"));
            fileConfig.setFileEncoding("gbk");
            fileConfig.setTemplateEncoding("gbk");

            FileSplitter splitter = FileFactory.createSplitter(fileConfig.getStorageConfig());
            FileSlice headSlice = splitter.getHeadSlice(fileConfig);
            System.out.println(headSlice);
            FileSlice bodySlice = splitter.getBodySlice(fileConfig);
            System.out.println(bodySlice);
            try {
                splitter.getTailSlice(fileConfig);
            } catch (RdfFileException e) {
                Assert.assertEquals(RdfErrorEnum.TAIL_NOT_DEFINED, e.getErrorEnum());
            }

            List<FileSlice> bodySlices = splitter.getBodySlices(fileConfig, 1024 * 1024);
            for (FileSlice fs : bodySlices) {
                System.out.println(fs);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

}
