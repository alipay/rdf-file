package com.alipay.rdf.file.sort;

import com.alipay.rdf.file.interfaces.FileFactory;
import com.alipay.rdf.file.interfaces.FileReader;
import com.alipay.rdf.file.model.FileConfig;
import com.alipay.rdf.file.model.FileSlice;
import com.alipay.rdf.file.model.SortConfig;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 * 
 * 多个文件的文件分片排序
 * 
 * @author hongwei.quhw
 * @version $Id: ProtocolMultiFileBodySortExecutor.java, v 0.1 2017年12月11日 下午5:22:36 hongwei.quhw Exp $
 */
public class ProtocolMultiFileBodySortExecutor extends ProtocolBodySortExecutor {
    public static final ProtocolMultiFileBodySortExecutor INSTANCE = new ProtocolMultiFileBodySortExecutor();

    @Override
    protected String getBodyFilePath(FileSlice fileSlice, SortConfig sortConfig) {
        return FileSortUtil.getBodyFilePath(fileSlice, sortConfig, true);
    }

    @Override
    protected FileReader createSliceReader(FileConfig fileConfig, FileSlice fileSlice) {
        FileConfig sliceConfig = fileConfig.clone();
        sliceConfig.setPartial(fileSlice.getStart(), fileSlice.getLength(),
            fileSlice.getFileDataType());
        sliceConfig.setFilePath(fileSlice.getFilePath());
        return FileFactory.createReader(sliceConfig);
    }
}
