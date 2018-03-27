package com.alipay.rdf.file.protocol;

import com.alipay.rdf.file.meta.FileColumnMeta;
import com.alipay.rdf.file.spi.RdfFileFunctionSpi;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 * 
 * @author hongwei.quhw
 * @version $Id: RowDefinition.java, v 0.1 2017年4月1日 上午10:51:36 hongwei.quhw Exp $
 */
public class RowDefinition {
    private boolean          columnloop;
    private String           defaultValue;
    private ColumnLayoutEnum columnLayout;
    private String           columnSplit;
    private RdfFileFunctionSpi   output;
    private FileColumnMeta   columnMeta;

    /**
     * Getter method for property <tt>columnloop</tt>.
     * 
     * @return property value of columnloop
     */
    public boolean isColumnloop() {
        return columnloop;
    }

    /**
     * Setter method for property <tt>columnloop</tt>.
     * 
     * @param columnloop value to be assigned to property columnloop
     */
    public void setColumnloop(boolean columnloop) {
        this.columnloop = columnloop;
    }

    /**
     * Getter method for property <tt>defaultValue</tt>.
     * 
     * @return property value of defaultValue
     */
    public String getDefaultValue() {
        return defaultValue;
    }

    /**
     * Setter method for property <tt>defaultValue</tt>.
     * 
     * @param defaultValue value to be assigned to property defaultValue
     */
    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    /**
     * Getter method for property <tt>columnLayout</tt>.
     * 
     * @return property value of columnLayout
     */
    public ColumnLayoutEnum getColumnLayout() {
        return columnLayout;
    }

    /**
     * Setter method for property <tt>columnLayout</tt>.
     * 
     * @param columnLayout value to be assigned to property columnLayout
     */
    public void setColumnLayout(ColumnLayoutEnum columnLayout) {
        this.columnLayout = columnLayout;
    }

    /**
     * Getter method for property <tt>columnSplit</tt>.
     * 
     * @return property value of columnSplit
     */
    public String getColumnSplit() {
        return columnSplit;
    }

    /**
     * Setter method for property <tt>columnSplit</tt>.
     * 
     * @param columnSplit value to be assigned to property columnSplit
     */
    public void setColumnSplit(String columnSplit) {
        this.columnSplit = columnSplit;
    }

    /**
     * Getter method for property <tt>output</tt>.
     * 
     * @return property value of output
     */
    public RdfFileFunctionSpi getOutput() {
        return output;
    }

    /**
     * Setter method for property <tt>output</tt>.
     * 
     * @param output value to be assigned to property output
     */
    public void setOutput(RdfFileFunctionSpi output) {
        this.output = output;
    }

    public FileColumnMeta getColumnMeta() {
        return columnMeta;
    }

    public void setColumnMeta(FileColumnMeta columnMeta) {
        this.columnMeta = columnMeta;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer("RowDefinition[");
        sb.append("columnloop=" + columnloop);
        sb.append(",defaultValue=" + defaultValue);
        sb.append(",columnLayout=" + columnLayout.name());
        sb.append(",columnSplit=" + columnSplit);
        sb.append(",output=" + RdfFileFunctionSpi.class.getSimpleName());
        sb.append(",columnMeta=" + columnMeta);
        sb.append("]");
        return super.toString();
    }
}
