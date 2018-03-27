package com.alipay.rdf.file.meta;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 * 
 * 接受配置参数
 * 
 * @author hongwei.quhw
 * @version $Id: TemplateConfig.java, v 0.1 2017年5月9日 上午10:26:52 hongwei.quhw Exp $
 */
public class TemplateConfig {
    /** 协议*/
    private String       protocol;
    /** 文件编码*/
    private String       fileEncoding;
    /** 字段分割*/
    private String       columnSplit        = "|";
    /** 换行符*/
    private String       lineBreak          = "\r\n";
    /** 文件以分隔符开始   head|body|tail*/
    private String       startWithSplit;
    /** 文件以分隔符结束   head|body|tail*/
    private String       endWithSplit;
    /** 头信息定义*/
    private List<String> head               = new ArrayList<String>();
    /** 文件体信息定义*/
    private List<String> body               = new ArrayList<String>();
    /** 文件尾部信息定义*/
    private List<String> tail               = new ArrayList<String>();
    /** 配置汇总字段*/
    private List<String> summaryColumnPairs = new ArrayList<String>();
    /** 行校验器*/
    private List<String> rowValidators      = new ArrayList<String>();

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getFileEncoding() {
        return fileEncoding;
    }

    public void setFileEncoding(String fileEncoding) {
        this.fileEncoding = fileEncoding;
    }

    public String getColumnSplit() {
        return columnSplit;
    }

    public void setColumnSplit(String columnSplit) {
        this.columnSplit = columnSplit;
    }

    public String getLineBreak() {
        return lineBreak;
    }

    public void setLineBreak(String lineBreak) {
        this.lineBreak = lineBreak;
    }

    public List<String> getHead() {
        return head;
    }

    public void setHead(List<String> head) {
        this.head = head;
    }

    public List<String> getBody() {
        return body;
    }

    public void setBody(List<String> body) {
        this.body = body;
    }

    public List<String> getTail() {
        return tail;
    }

    public void setTail(List<String> tail) {
        this.tail = tail;
    }

    public List<String> getSummaryColumnPairs() {
        return summaryColumnPairs;
    }

    public void setSummaryColumnPairs(List<String> summaryColumnPairs) {
        this.summaryColumnPairs = summaryColumnPairs;
    }

    public List<String> getRowValidators() {
        return rowValidators;
    }

    public void setRowValidators(List<String> rowValidators) {
        this.rowValidators = rowValidators;
    }

    public String getStartWithSplit() {
        return startWithSplit;
    }

    public void setStartWithSplit(String startWithSplit) {
        this.startWithSplit = startWithSplit;
    }

    public String getEndWithSplit() {
        return endWithSplit;
    }

    public void setEndWithSplit(String endWithSplit) {
        this.endWithSplit = endWithSplit;
    }

}
