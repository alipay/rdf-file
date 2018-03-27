package com.alipay.rdf.file.sort;

import com.alipay.rdf.file.exception.RdfErrorEnum;
import com.alipay.rdf.file.exception.RdfFileException;
import com.alipay.rdf.file.interfaces.FileFactory;
import com.alipay.rdf.file.interfaces.FileReader;
import com.alipay.rdf.file.interfaces.FileWriter;
import com.alipay.rdf.file.model.FileConfig;
import com.alipay.rdf.file.model.FileSlice;
import com.alipay.rdf.file.model.SortConfig;
import com.alipay.rdf.file.spi.RdfFileWriterSpi;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 * 
 * 分隔符分割文件头排序
 * 
 * @author hongwei.quhw
 * @version $Id: SplitHeadSortExecutor.java, v 0.1 2017年8月24日 下午7:23:57 hongwei.quhw Exp $
 */
public class SplitHeadSortExecutor extends AbstractSortExecutor {
    public static final SplitHeadSortExecutor INSTANCE = new SplitHeadSortExecutor();

    @Override
    protected String doSort(FileConfig fileConfig, SortConfig sortConfig, FileSlice fileSlice,
                            FileReader sliceReader) {
        if (fileSlice.getStart() > 0) {
            throw new RdfFileException(
                "rdf-file#SplitHeadSortExecutor 分片必须从第一个开始 fileConfig=" + fileConfig,
                RdfErrorEnum.ILLEGAL_ARGUMENT);
        }

        String tempHeadPath = FileSortUtil.getHeadFilePath(sortConfig);
        FileConfig writerConfig = fileConfig.clone();
        writerConfig.setFilePath(tempHeadPath);
        writerConfig.setStorageConfig(sortConfig.getResultStorageConfig());
        FileWriter fileWriter = FileFactory.createWriter(writerConfig);
        // 确保文件创建
        ((RdfFileWriterSpi) fileWriter).ensureOpen();

        try {
            for (int i = 0; i < sortConfig.getHeadLines(); i++) {
                fileWriter.writeLine(sliceReader.readLine());
            }

            return tempHeadPath;
        } finally {
            if (null != fileWriter) {
                fileWriter.close();
            }
        }
    }

}
