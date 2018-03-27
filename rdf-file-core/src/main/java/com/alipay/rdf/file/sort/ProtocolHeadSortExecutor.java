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
 * 协议文件头分片文件
 * 
 * @author hongwei.quhw
 * @version $Id: ProtocolHeadSortExecutor.java, v 0.1 2017年8月22日 下午11:54:42 hongwei.quhw Exp $
 */
public class ProtocolHeadSortExecutor extends AbstractSortExecutor {
    public static final ProtocolHeadSortExecutor INSTANCE = new ProtocolHeadSortExecutor();

    @Override
    protected String doSort(FileConfig fileConfig, SortConfig sortConfig, FileSlice fileSlice,
                            FileReader sliceReader) {
        Map<String, Object> head = sliceReader.readHead(HashMap.class);

        String tempHeadPath = FileSortUtil.getHeadFilePath(sortConfig);
        FileConfig writerConfig = fileConfig.clone();
        writerConfig.setFilePath(tempHeadPath);
        writerConfig.setStorageConfig(sortConfig.getResultStorageConfig());
        FileWriter headWriter = FileFactory.createWriter(writerConfig);
        try {
            headWriter.writeHead(head);
            return tempHeadPath;
        } finally {
            if (null != headWriter) {
                headWriter.close();
            }
        }
    }

}
