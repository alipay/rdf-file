package com.alipay.rdf.file.protocol;

import java.util.ArrayList;
import java.util.List;

import com.alipay.rdf.file.spi.RdfFileRowSplitSpi;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 * 
 * @author hongwei.quhw
 * @version $Id: ProtocolDefinition.java, v 0.1 2017年4月1日 上午10:51:18 hongwei.quhw Exp $
 */
public class ProtocolDefinition {
    private String              name;

    private RdfFileRowSplitSpi         rowSplit;

    private List<RowDefinition> heads = new ArrayList<RowDefinition>();

    private List<RowDefinition> bodys = new ArrayList<RowDefinition>();

    private List<RowDefinition> tails = new ArrayList<RowDefinition>();

    /**
     * Getter method for property <tt>name</tt>.
     * 
     * @return property value of name
     */
    public String getName() {
        return name;
    }

    /**
     * Setter method for property <tt>name</tt>.
     * 
     * @param name value to be assigned to property name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Getter method for property <tt>rowSplit</tt>.
     * 
     * @return property value of rowSplit
     */
    public RdfFileRowSplitSpi getRowSplit() {
        return rowSplit;
    }

    /**
     * Setter method for property <tt>rowSplit</tt>.
     * 
     * @param rowSplit value to be assigned to property rowSplit
     */
    public void setRowSplit(RdfFileRowSplitSpi rowSplit) {
        this.rowSplit = rowSplit;
    }

    /**
     * Getter method for property <tt>heads</tt>.
     * 
     * @return property value of heads
     */
    public List<RowDefinition> getHeads() {
        return heads;
    }

    /**
     * Setter method for property <tt>heads</tt>.
     * 
     * @param heads value to be assigned to property heads
     */
    public void setHeads(List<RowDefinition> heads) {
        this.heads = heads;
    }

    /**
     * Getter method for property <tt>bodys</tt>.
     * 
     * @return property value of bodys
     */
    public List<RowDefinition> getBodys() {
        return bodys;
    }

    /**
     * Setter method for property <tt>bodys</tt>.
     * 
     * @param bodys value to be assigned to property bodys
     */
    public void setBodys(List<RowDefinition> bodys) {
        this.bodys = bodys;
    }

    /**
     * Getter method for property <tt>tails</tt>.
     * 
     * @return property value of tails
     */
    public List<RowDefinition> getTails() {
        return tails;
    }

    /**
     * Setter method for property <tt>tails</tt>.
     * 
     * @param tails value to be assigned to property tails
     */
    public void setTails(List<RowDefinition> tails) {
        this.tails = tails;
    }

    @Override
    public String toString() {
        return "protocol=" + name;
    }
}
