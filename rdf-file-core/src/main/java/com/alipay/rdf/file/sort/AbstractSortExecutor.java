package com.alipay.rdf.file.sort;

import com.alipay.rdf.file.interfaces.FileFactory;
import com.alipay.rdf.file.interfaces.FileReader;
import com.alipay.rdf.file.model.FileConfig;
import com.alipay.rdf.file.model.FileSlice;
import com.alipay.rdf.file.model.RowFilter;
import com.alipay.rdf.file.model.SortConfig;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 * 
 * 排序执行器抽象类
 * 
 * @author hongwei.quhw
 * @version $Id: AbstractSortExecutor.java, v 0.1 2017年8月22日 下午10:02:09 hongwei.quhw Exp $
 */
public abstract class AbstractSortExecutor implements SortExecutor {

    @Override
    public String sort(FileConfig fileConfig, SortConfig sortConfig, FileSlice fileSlice) {
        FileReader sliceReader = createSliceReader(fileConfig, fileSlice);

        try {
            return doSort(fileConfig, sortConfig, fileSlice, sliceReader);
        } finally {
            if (null != sliceReader) {
                sliceReader.close();
            }
        }
    }

    protected FileReader createSliceReader(FileConfig fileConfig, FileSlice fileSlice) {
        FileConfig sliceConfig = fileConfig.clone();
        sliceConfig.setPartial(fileSlice.getStart(), fileSlice.getLength(),
            fileSlice.getFileDataType());
        return FileFactory.createReader(sliceConfig);
    }

    protected abstract String doSort(FileConfig fileConfig, SortConfig sortConfig,
                                     FileSlice fileSlice, FileReader sliceReader);

    protected boolean rowFiler(RowData rowData, SortConfig sortConfig) {
        for (RowFilter rowFilter : sortConfig.getRowFilters()) {
            if (rowFilter.filter(rowData)) {
                return true;
            }
        }

        return false;
    }
}
