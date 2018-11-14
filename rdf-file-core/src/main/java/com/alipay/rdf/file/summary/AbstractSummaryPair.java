package com.alipay.rdf.file.summary;

import com.alipay.rdf.file.spi.RdfFileRowConditionSpi;
import com.alipay.rdf.file.spi.RdfFileSummaryPairSpi;
import com.alipay.rdf.file.util.RdfFileUtil;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 *
 * @author hongwei.quhw
 * @version $Id: AbstractSummaryPair.java, v 0.1 2018年3月12日 下午4:27:24 hongwei.quhw Exp $
 */
public abstract class AbstractSummaryPair<T> implements RdfFileSummaryPairSpi<T> {
    protected String               headKey;
    protected String               columnKey;
    protected String               tailKey;

    protected T                    headValue;
    protected T                    summaryValue;
    protected T                    tailValue;

    private RdfFileRowConditionSpi conditon;

    @Override
    public String summaryMsg() {
        StringBuilder builder = new StringBuilder("汇总字段信息：");
        if (RdfFileUtil.isNotBlank(headKey)) {
            builder.append("headKey=").append(headKey);
            builder.append(" ");
            builder.append("headValue=").append(null == headValue ? "为空" : headValue);
            builder.append(" ");
        }
        builder.append("columnKey=").append(columnKey);
        builder.append(" ");
        builder.append("summaryValue=").append(summaryValue == null ? "为空" : summaryValue);

        if (RdfFileUtil.isNotBlank(tailKey)) {
            builder.append(" ");
            builder.append("tailKey=").append(tailKey);
            builder.append(" ");
            builder.append("tailValue=").append(null == tailValue ? "为空" : tailValue);

        }
        return builder.toString();
    }

    @Override
    public void setHeadValue(T headValue) {
        this.headValue = headValue;
    }

    @Override
    public String getHeadKey() {
        return headKey;
    }

    @Override
    public String getColumnKey() {
        return columnKey;
    }

    @Override
    public T getSummaryValue() {
        return summaryValue;
    }

    @Override
    public String getTailKey() {
        return tailKey;
    }

    @Override
    public T getTailValue() {
        return tailValue;
    }

    @Override
    public void setHeadKey(String headKey) {
        this.headKey = headKey;
    }

    @Override
    public void setTailKey(String tailKey) {
        this.tailKey = tailKey;
    }

    @Override
    public void setTailValue(T tailValue) {
        this.tailValue = tailValue;
    }

    @Override
    public void setColumnKey(String columnKey) {
        this.columnKey = columnKey;
    }

    @Override
    public void addColValue(T columnValue) {
        if (null == columnValue) {
            return;
        }

        if (null == summaryValue) {
            summaryValue = columnValue;
        } else {
            doAddColValue(columnValue);
        }
    }

    @Override
    public T getHeadValue() {
        return headValue;
    }

    @Override
    public boolean isSummaryEquals() {
        if (null == headValue && null == tailValue) {
            if (null == summaryValue) {
                return true;
            } else {
                return false;
            }
        }

        if (null == summaryValue) {
            summaryValue = initDefaultColumnValue();
        }

        if (null != headValue) {
            return RdfFileUtil.compare(headValue, summaryValue);
        } else {
            return RdfFileUtil.compare(tailValue, summaryValue);
        }

    }

    protected abstract void doAddColValue(T columnValue);

    /**
     * body数据为空时初始化默认值
     * 
     * @return
     */
    protected abstract T initDefaultColumnValue();

    public RdfFileRowConditionSpi getRowCondition() {
        return conditon;
    }

    public void setRowCondition(RdfFileRowConditionSpi conditon) {
        this.conditon = conditon;
    }
    
    
}
