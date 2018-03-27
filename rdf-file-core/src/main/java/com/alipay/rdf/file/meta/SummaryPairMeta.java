package com.alipay.rdf.file.meta;

import com.alipay.rdf.file.model.FileDataTypeEnum;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 * 
 * 汇总字段元数据
 * 
 * @author hongwei.quhw
 * @version $Id: SummaryPairMeta.java, v 0.1 2017年8月14日 上午11:29:48 hongwei.quhw Exp $
 */
public class SummaryPairMeta {

    private final String           summaryKey;

    private final String           columnKey;

    private final FileColumnMeta   columnMeta;

    private final FileDataTypeEnum summaryDataType;

    public SummaryPairMeta(String summaryKey, String columnKey, FileColumnMeta columnMeta,
                           FileDataTypeEnum summaryDataType) {
        super();
        this.summaryKey = summaryKey;
        this.columnKey = columnKey;
        this.columnMeta = columnMeta;
        this.summaryDataType = summaryDataType;
    }

    public String getSummaryKey() {
        return summaryKey;
    }

    public String getColumnKey() {
        return columnKey;
    }

    public FileColumnMeta getColumnMeta() {
        return columnMeta;
    }

    public FileDataTypeEnum getSummaryDataType() {
        return summaryDataType;
    }

    @Override
    public String toString() {
        return "SummaryPairMeta=[summaryKey=" + summaryKey + ", columnKey=" + columnKey
               + ", summaryDataType=" + summaryDataType.name() + "]";
    }
}
