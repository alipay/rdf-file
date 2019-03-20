package com.alipay.rdf.file.sort;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.alipay.rdf.file.interfaces.FileCoreToolContants;
import com.alipay.rdf.file.interfaces.FileFactory;
import com.alipay.rdf.file.interfaces.FileReader;
import com.alipay.rdf.file.loader.TemplateLoader;
import com.alipay.rdf.file.meta.FileColumnMeta;
import com.alipay.rdf.file.model.FileConfig;
import com.alipay.rdf.file.model.FileDataTypeEnum;
import com.alipay.rdf.file.model.SortConfig;
import com.alipay.rdf.file.model.SortResult;
import com.alipay.rdf.file.util.RdfFileUtil;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 * 
 * 对一组文件排序读
 * 
 * @author hongwei.quhw
 * @version $Id: SortedFileGroupReader.java, v 0.1 2017年6月27日 下午3:25:58 hongwei.quhw Exp $
 */
public class SortedFileGroupReader {
    private final SortConfig                sortConfig;
    private List<FileReader>                readers         = null;
    private TreeMap<RowData, List<RowData>> bufferefRowData = new TreeMap<RowData, List<RowData>>();
    private final FileConfig                fileConfig;

    public SortedFileGroupReader(FileConfig fileConfig, SortConfig sortConfig,
                                 SortResult sortResult) {
        // int[] idx = sortConfig.getColumnRearrangeIndex();
        this.fileConfig = fileConfig;
        this.sortConfig = sortConfig.clone();
        this.sortConfig.setColumnRearrangeIndex(null);

        readers = new ArrayList<FileReader>(sortResult.getBodySlicePath().size());
        for (String sliePath : sortResult.getBodySlicePath()) {
            FileConfig sliceConfig = fileConfig.clone();
            sliceConfig.setFilePath(sliePath);
            sliceConfig.setStorageConfig(sortConfig.getResultStorageConfig());
            sliceConfig.setFileDataType(FileDataTypeEnum.BODY);
            readers.add(FileFactory.createReader(sliceConfig));
        }
    }

    public RowData readRow() {
        //若缓存记录为空，从每个数据源中读取一行，并排序
        if (bufferefRowData.isEmpty()) {
            prepareDatas();
        }

        //取第一条
        RowData rowData = getFirstData();

        if (null == rowData) {
            return null;
        }

        // 删除补一条数据，并排序
        popAndReFillBuffer(rowData);

        return rowData;
    }

    private void popAndReFillBuffer(RowData rowData) {
        // 删除记录
        removeBuffer(rowData);

        // 补充记录
        RowData newRowData = getRowData(rowData.getFileReader());

        if (null == newRowData) {
            return;
        }

        // 加入缓存
        putBuffer(newRowData);
    }

    private void putBuffer(RowData rowData) {
        List<RowData> rowDatas = bufferefRowData.get(rowData);
        if (null == rowDatas) {
            rowDatas = new ArrayList<RowData>();
            bufferefRowData.put(rowData, rowDatas);
        }

        rowDatas.add(rowData);
    }

    private void removeBuffer(RowData rowData) {
        List<RowData> rowDatas = bufferefRowData.get(rowData);
        if (null == rowDatas || rowDatas.isEmpty()) {
            return;
        }

        rowDatas.remove(0);

        if (rowDatas.isEmpty()) {
            bufferefRowData.remove(rowData);
        }
    }

    private RowData getFirstData() {
        if (bufferefRowData.isEmpty()) {
            return null;
        }

        List<RowData> rowDatas = bufferefRowData.get(bufferefRowData.firstKey());
        if (null == rowDatas || rowDatas.isEmpty()) {
            return null;
        }

        return rowDatas.get(0);
    }

    private void prepareDatas() {
        for (FileReader reader : readers) {
            RowData rowData = getRowData(reader);

            if (null == rowData) {
                continue;
            }

            List<RowData> rowDatas = bufferefRowData.get(rowData);
            if (null == rowDatas) {
                rowDatas = new ArrayList<RowData>();
                bufferefRowData.put(rowData, rowDatas);
            }
            rowDatas.add(rowData);
        }
    }

    private RowData getRowData(FileReader reader) {

        if (FileCoreToolContants.RAW_SORTER.equals(fileConfig.getType())) {
            String[] cols = reader.readRow(String[].class);
            if (null == cols) {
                return null;
            }

            RowData rowData = new RowData(sortConfig.getSortIndexes(), sortConfig.getSortType(),
                cols, RdfFileUtil.getRowSplit(fileConfig), sortConfig.getColumnRearrangeIndex());
            rowData.setFileReader(reader);
            return rowData;
        } else {
            List<FileColumnMeta> colMetas = TemplateLoader.load(fileConfig).getBodyColumns();
            Object[] data = new Object[colMetas.size()];
            Map<String, Object> row = reader.readRow(HashMap.class);

            if (null == row) {
                return null;
            }

            for (int i = 0; i < colMetas.size(); i++) {
                data[i] = row.get(colMetas.get(i).getName());
            }

            RowData rowData = new RowData(sortConfig.getSortIndexes(), sortConfig.getSortType(),
                data, RdfFileUtil.getRowSplit(fileConfig), sortConfig.getColumnRearrangeIndex());
            rowData.setFileReader(reader);
            return rowData;
        }
    }

    public void close() {
        for (FileReader reader : readers) {
            reader.close();
        }
    }
}
