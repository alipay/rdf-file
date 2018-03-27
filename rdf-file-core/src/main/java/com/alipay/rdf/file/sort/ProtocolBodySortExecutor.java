package com.alipay.rdf.file.sort;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alipay.rdf.file.interfaces.FileFactory;
import com.alipay.rdf.file.interfaces.FileReader;
import com.alipay.rdf.file.interfaces.FileWriter;
import com.alipay.rdf.file.loader.TemplateLoader;
import com.alipay.rdf.file.meta.FileColumnMeta;
import com.alipay.rdf.file.model.FileConfig;
import com.alipay.rdf.file.model.FileSlice;
import com.alipay.rdf.file.model.SortConfig;
import com.alipay.rdf.file.spi.RdfFileWriterSpi;
import com.alipay.rdf.file.util.RdfFileUtil;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 * 
 * 协议文件body分片文件排序
 * 
 * @author hongwei.quhw
 * @version $Id: ProtocolBodySortExecutor.java, v 0.1 2017年8月22日 下午11:57:38 hongwei.quhw Exp $
 */
public class ProtocolBodySortExecutor extends AbstractSortExecutor {
    public static final ProtocolBodySortExecutor INSTANCE = new ProtocolBodySortExecutor();

    @Override
    protected String doSort(FileConfig fileConfig, SortConfig sortConfig, FileSlice fileSlice,
                            FileReader sliceReader) {
        String tempBodyFilePath = getBodyFilePath(fileSlice, sortConfig);
        FileConfig writerConfig = fileConfig.clone();
        writerConfig.setFilePath(tempBodyFilePath);
        writerConfig.setStorageConfig(sortConfig.getResultStorageConfig());
        FileWriter fileWriter = FileFactory.createWriter(writerConfig);
        try {
            List<RowData> rowDatas = new ArrayList<RowData>();
            Map<String, Object> row = null;

            while (null != (row = sliceReader.readRow(HashMap.class))) {
                List<FileColumnMeta> colMetas = TemplateLoader.load(fileConfig).getBodyColumns();
                Object[] data = new Object[colMetas.size()];
                for (int i = 0; i < colMetas.size(); i++) {
                    data[i] = row.get(colMetas.get(i).getName());
                }

                RowData rowData = new RowData(sortConfig.getSortIndexes(), sortConfig.getSortType(),
                    data, RdfFileUtil.getColumnSplit(writerConfig),
                    sortConfig.getColumnRearrangeIndex());
                rowData.setFileReader(sliceReader);

                if (!rowFiler(rowData, sortConfig)) {
                    rowDatas.add(rowData);
                }
            }

            // 排序所有行
            Collections.sort(rowDatas);

            // 确保文件创建
            ((RdfFileWriterSpi) fileWriter).ensureOpen();

            // 写入临时文件
            for (RowData rowData : rowDatas) {
                fileWriter.writeRow(rowData.getColumnSortDatas());
            }

            return tempBodyFilePath;
        } finally {
            if (null != fileWriter) {
                fileWriter.close();
            }
        }
    }

    /**
     * 获取分片路径
     * 
     * @param fileSlice
     * @param sortConfig
     * @return
     */
    protected String getBodyFilePath(FileSlice fileSlice, SortConfig sortConfig) {
        return FileSortUtil.getBodyFilePath(fileSlice, sortConfig);
    }
}
