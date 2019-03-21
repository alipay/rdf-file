package com.alipay.rdf.file.sort;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.alipay.rdf.file.interfaces.FileFactory;
import com.alipay.rdf.file.interfaces.FileReader;
import com.alipay.rdf.file.interfaces.FileWriter;
import com.alipay.rdf.file.model.FileConfig;
import com.alipay.rdf.file.model.FileSlice;
import com.alipay.rdf.file.model.SortConfig;
import com.alipay.rdf.file.spi.RdfFileWriterSpi;
import com.alipay.rdf.file.util.RdfFileUtil;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 * 
 * 分隔符分割文件体排序
 * 
 * @author hongwei.quhw
 * @version $Id: SplitBodySortExecutor.java, v 0.1 2017年8月24日 下午7:24:16 hongwei.quhw Exp $
 */
public class SplitBodySortExecutor extends AbstractSortExecutor {
    public static final SplitBodySortExecutor INSTANCE = new SplitBodySortExecutor();

    @Override
    protected String doSort(FileConfig fileConfig, SortConfig sortConfig, FileSlice fileSlice,
                            FileReader sliceReader) {
        //第一个分片且有文件头, 忽略掉
        if (fileSlice.getStart() == 0 && sortConfig.getHeadLines() > 0) {
            for (int i = 0; i < sortConfig.getHeadLines(); i++) {
                sliceReader.readLine();
            }
        }

        FileWriter fileWriter = null;
        try {
            List<RowData> rowDatas = new ArrayList<RowData>();

            String[] cols = null;
            while (null != (cols = sliceReader.readRow(String[].class))) {
                RowData rowData = new RowData(sortConfig.getSortIndexes(), sortConfig.getSortType(),
                    cols, RdfFileUtil.getRowSplit(fileConfig),
                    sortConfig.getColumnRearrangeIndex());

                if (!rowFiler(rowData, sortConfig)) {
                    rowDatas.add(rowData);
                }
            }

            // 排序所有行
            Collections.sort(rowDatas);

            String tempBodyFilePath = FileSortUtil.getBodyFilePath(fileSlice, sortConfig);
            FileConfig writerConfig = fileConfig.clone();
            writerConfig.setFilePath(tempBodyFilePath);
            writerConfig.setStorageConfig(sortConfig.getResultStorageConfig());
            fileWriter = FileFactory.createWriter(writerConfig);
            // 确保文件创建
            ((RdfFileWriterSpi) fileWriter).ensureOpen();

            // 写入临时文件
            for (RowData rowData : rowDatas) {
                fileWriter.writeLine(rowData.toString());
            }

            return tempBodyFilePath;
        } finally {
            if (null != fileWriter) {
                fileWriter.close();
            }
        }
    }

}
