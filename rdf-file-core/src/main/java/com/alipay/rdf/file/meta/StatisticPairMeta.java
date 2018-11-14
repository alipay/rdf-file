package com.alipay.rdf.file.meta;

import com.alipay.rdf.file.model.FileDataTypeEnum;
import com.alipay.rdf.file.spi.RdfFileRowConditionSpi;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 *
 * 统计配置元数据
 *
 * @author hongwei.quhw
 * @version $Id: StatisticPairMeta.java, v 0.1 2018年11月13日 下午2:30:12 hongwei.quhw Exp $
 */
public class StatisticPairMeta {
    private final String           statisticKey;

    private final FileColumnMeta   columnMeta;

    private final FileDataTypeEnum statisticdataType;

    private RdfFileRowConditionSpi rowCondition;

    public StatisticPairMeta(String statisticKey, FileColumnMeta columnMeta,
                             FileDataTypeEnum statisticdataType) {
        super();
        this.statisticKey = statisticKey;
        this.columnMeta = columnMeta;
        this.statisticdataType = statisticdataType;
    }

    public RdfFileRowConditionSpi getRowCondition() {
        return rowCondition;
    }

    public void setRowCondition(RdfFileRowConditionSpi rowCondition) {
        this.rowCondition = rowCondition;
    }

    public String getStatisticKey() {
        return statisticKey;
    }

    public FileColumnMeta getColumnMeta() {
        return columnMeta;
    }

    public FileDataTypeEnum getStatisticdataType() {
        return statisticdataType;
    }
}
