package com.alipay.rdf.file.sort;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alipay.rdf.file.interfaces.FileCoreToolContants;
import com.alipay.rdf.file.interfaces.FileReader;
import com.alipay.rdf.file.loader.TemplateLoader;
import com.alipay.rdf.file.meta.FileColumnMeta;
import com.alipay.rdf.file.model.FileConfig;
import com.alipay.rdf.file.model.SortConfig.SortTypeEnum;
import com.alipay.rdf.file.spi.RdfFileReaderSpi;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 * 
 * 排序行数据
 * 
 * @author hongwei.quhw
 * @version $Id: RowData.java, v 0.1 2017年8月23日 下午8:43:33 hongwei.quhw Exp $
 */
public class RowData implements Comparable<RowData> {
    private final int[]        sortIndexes;

    private final SortTypeEnum sortType;

    private final Object[]     colDatas;

    private final String       separator;

    /**对字段重新排序 如：{5,3,6,0,2,1}*/
    private final int[]        columnSort;

    /**关联的reader*/
    private RdfFileReaderSpi   fileReader;

    public RowData(int[] sortIndexes, SortTypeEnum sortType, Object[] colDatas, String separator,
                   int[] columnSort) {
        this.sortIndexes = sortIndexes;
        this.sortType = sortType;
        this.colDatas = colDatas;
        this.separator = separator;
        this.columnSort = columnSort;
    }

    /**
     * Getter method for property <tt>separator</tt>.
     * 
     * @return property value of separator
     */
    public String getSeparator() {
        return separator;
    }

    public Object[] getColDatas() {
        return colDatas;
    }

    public Object getColumnSortDatas() {
        FileConfig fileConfig = (fileReader).getFileConfig();

        if (FileCoreToolContants.PROTOCOL_SORTER.equals(fileConfig.getType())) {
            Map<String, Object> datas = new HashMap<String, Object>();
            List<FileColumnMeta> bodyColMeta = TemplateLoader.load((fileReader).getFileConfig())
                .getBodyColumns();
            if (null == columnSort || columnSort.length == 0) {
                for (int i = 0; i < colDatas.length; i++) {
                    datas.put(bodyColMeta.get(i).getName(), colDatas[i]);
                }
            } else {
                for (int i = 0; i < columnSort.length; i++) {
                    datas.put(bodyColMeta.get(columnSort[i]).getName(), colDatas[columnSort[i]]);
                }
            }
            return datas;
        } else {
            if (null == columnSort || columnSort.length == 0) {
                return this;
            } else {
                Object[] newDatas = new Object[columnSort.length];
                for (int i = 0; i < columnSort.length; i++) {
                    newDatas[i] = colDatas[columnSort[i]];
                }
                return new RowData(sortIndexes, sortType, newDatas, separator, columnSort);
            }
        }
    }

    public FileReader getFileReader() {
        return fileReader;
    }

    public void setFileReader(FileReader fileReader) {
        this.fileReader = (RdfFileReaderSpi) fileReader;
    }

    /** 
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(RowData o) {
        int keyDiff = 0;

        if (null == sortIndexes || sortIndexes.length == 0) {
            keyDiff = toString().compareTo(o.toString());
        } else {

            for (int i = 0; i < sortIndexes.length; i++) {
                keyDiff = colDatas[sortIndexes[i]].toString()
                    .compareTo(o.colDatas[sortIndexes[i]].toString());

                if (keyDiff != 0) {
                    break;
                }
            }

            //排序字段相同，这个字符串比较
            if (keyDiff == 0) {
                keyDiff = toString().compareTo(o.toString());
            }
        }

        if (SortTypeEnum.DESC.equals(sortType)) {
            keyDiff = -keyDiff;
        }

        return keyDiff;
    }

    @Override
    public String toString() {
        StringBuffer line = new StringBuffer();

        if (null == columnSort) {
            for (int i = 0; i < colDatas.length; i++) {
                if (null == separator || i == colDatas.length - 1) {
                    line.append(colDatas[i]);
                } else {
                    line.append(colDatas[i]).append(separator);
                }
            }
        } else {
            for (int i = 0; i < columnSort.length; i++) {
                if (null == separator || i == columnSort.length - 1) {
                    line.append(colDatas[columnSort[i]]);
                } else {
                    line.append(colDatas[columnSort[i]]).append(separator);
                }
            }
        }
        return line.toString();
    }
}
