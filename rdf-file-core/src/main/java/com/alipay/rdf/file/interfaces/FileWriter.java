package com.alipay.rdf.file.interfaces;

import com.alipay.rdf.file.model.Summary;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 * 
 * 文件写入接口
 * 
 * @author hongwei.quhw
 * @version $Id: FileWriter.java, v 0.1 2016-12-20 下午4:48:11 hongwei.quhw Exp $
 */
public interface FileWriter {

    /**
     *  写入头部信息,  传入一个javabean对象
     * 
     * @param headBean
     */
    void writeHead(Object headBean);

    /**
     * 写入一行记录,  传入一个javabean对象
     * 
     * @param summary 汇总字段
     * @param rowBean 
     */
    void writeRow(Object rowBean);

    /**
     * 写入尾部信息,  传入一个javabean对象
     * 
     * @param tailBean      含尾部变量的模型
     */
    void writeTail(Object tailBean);

    /**
     * 写入一行
     * 
     * @param line
     */
    void writeLine(String line);

    Summary getSummary();

    /**
     * 关闭流
     */
    void close();
}
