package com.alipay.rdf.file.summary;

import com.alipay.rdf.file.loader.ExtensionLoader;
import com.alipay.rdf.file.meta.StatisticPairMeta;
import com.alipay.rdf.file.model.FileConfig;
import com.alipay.rdf.file.model.FileDataTypeEnum;
import com.alipay.rdf.file.spi.RdfFileColumnTypeSpi;
import com.alipay.rdf.file.spi.RdfFileRowConditionSpi;
import com.alipay.rdf.file.util.BeanMapWrapper;
import com.alipay.rdf.file.util.RdfFileUtil;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 *
 * 统计处理
 *
 * @author hongwei.quhw
 * @version $Id: StatisticPair.java, v 0.1 2018年11月13日 下午6:19:12 hongwei.quhw Exp $
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class StatisticPair {
    private final StatisticPairMeta pairMeta;
    /**头统计值*/
    private Object                  headValue;
    /**尾统计值*/
    private Object                  tailValue;
    /**读写过程统计的值*/
    private Long                    staticsticValue;

    private RdfFileColumnTypeSpi    columnTypeCodec;

    public StatisticPair(StatisticPairMeta pairMeta) {
        this.pairMeta = pairMeta;

        String columnName = pairMeta.getColumnMeta().getType().getName();
        RdfFileColumnTypeSpi columnTypeCodec = ExtensionLoader
            .getExtensionLoader(RdfFileColumnTypeSpi.class).getExtension(columnName);
        RdfFileUtil.assertNotNull(columnTypeCodec,
            "rdf-file#StatisticPair 没有type=[" + columnName + "] 对应的类型codec");

    }

    /**
     * 计数累加
     */
    public void increment(FileConfig config, BeanMapWrapper bmw) {
        RdfFileRowConditionSpi rowCondition = pairMeta.getRowCondition();
        if (null != rowCondition) {
            if (rowCondition.serialize(config, bmw)) {
                return;
            }
        }

        if (null == staticsticValue) {
            staticsticValue = 1L;
        } else {
            staticsticValue++;
        }
    }

    public void addColValue(Object value) {
        if (null == value) {
            return;
        }

        String strValue = columnTypeCodec.serialize(value, pairMeta.getColumnMeta());
        staticsticValue += Long.valueOf(strValue);
    }

    public boolean isStatisticEquals() {
        if (null == headValue && null == tailValue) {
            if (null == staticsticValue) {
                return true;
            } else {
                return false;
            }
        }

        if (null == staticsticValue) {
            staticsticValue = 0L;
        }

        if (null != headValue) {
            return RdfFileUtil.compare(headValue, staticsticValue);
        } else {
            return RdfFileUtil.compare(tailValue, staticsticValue);
        }
    }

    public String staticsticMsg() {
        StringBuilder builder = new StringBuilder("统计字段信息：");
        if (FileDataTypeEnum.HEAD.equals(pairMeta.getStatisticdataType())) {
            builder.append("headKey=").append(pairMeta.getStatisticKey());
            builder.append(" ");
            builder.append("headValue=").append(null == headValue ? "为空" : headValue);
            builder.append(" ");
        }
        builder.append(" ");
        builder.append("staticsticValue=").append(staticsticValue == null ? "为空" : staticsticValue);

        if (FileDataTypeEnum.TAIL.equals(pairMeta.getStatisticdataType())) {
            builder.append(" ");
            builder.append("tailKey=").append(pairMeta.getStatisticKey());
            builder.append(" ");
            builder.append("tailValue=").append(null == tailValue ? "为空" : tailValue);

        }
        return builder.toString();
    }

    public void setHeadValue(Object headValue) {
        this.headValue = headValue;
    }

    public void setTailValue(Object tailValue) {
        this.tailValue = tailValue;
    }

    public String getHeadKey() {
        if (FileDataTypeEnum.HEAD.equals(pairMeta.getStatisticdataType())) {
            return pairMeta.getStatisticKey();
        } else {
            return null;
        }
    }

    public String getTailKey() {
        if (FileDataTypeEnum.TAIL.equals(pairMeta.getStatisticdataType())) {
            return pairMeta.getStatisticKey();
        } else {
            return null;
        }
    }

    public RdfFileRowConditionSpi getRowCondition() {
        return pairMeta.getRowCondition();
    }

    public Object getHeadValue() {
        return headValue;
    }

    public Object getTailValue() {
        return tailValue;
    }

    public Object getStaticsticValue() {
        if (null == staticsticValue) {
            return null;
        }

        return columnTypeCodec.deserialize(String.valueOf(staticsticValue),
            pairMeta.getColumnMeta());
    }

    public Long getStaticsticLongValue() {
        return staticsticValue;
    }
}
