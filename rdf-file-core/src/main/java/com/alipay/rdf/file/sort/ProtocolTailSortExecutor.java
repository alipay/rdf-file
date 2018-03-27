package com.alipay.rdf.file.sort;

import java.util.HashMap;
import java.util.Map;

import com.alipay.rdf.file.interfaces.FileFactory;
import com.alipay.rdf.file.interfaces.FileReader;
import com.alipay.rdf.file.interfaces.FileWriter;
import com.alipay.rdf.file.model.FileConfig;
import com.alipay.rdf.file.model.FileSlice;
import com.alipay.rdf.file.model.SortConfig;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 * 
 * 协议文件尾分片文件
 * 
 * @author hongwei.quhw
 * @version $Id: ProtocolHeadSortExecutor.java, v 0.1 2017年8月22日 下午11:54:42 hongwei.quhw Exp $
 */
public class ProtocolTailSortExecutor extends AbstractSortExecutor {
    public static final ProtocolTailSortExecutor INSTANCE = new ProtocolTailSortExecutor();

    @Override
    protected String doSort(FileConfig fileConfig, SortConfig sortConfig, FileSlice fileSlice,
                            FileReader sliceReader) {
        Map<String, Object> tail = sliceReader.readTail(HashMap.class);

        String tempTailPath = FileSortUtil.getTailFilePath(sortConfig);
        FileConfig tailConfig = fileConfig.clone();
        tailConfig.setFilePath(tempTailPath);
        tailConfig.setStorageConfig(sortConfig.getResultStorageConfig());
        FileWriter tailWriter = FileFactory.createWriter(tailConfig);
        try {
            tailWriter.writeTail(tail);
            return tempTailPath;
        } finally {
            if (null != tailWriter) {
                tailWriter.close();
            }
        }
    }

}
