package com.alipay.rdf.file.interfaces;

import com.alipay.rdf.file.model.Summary;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 * 
 * 文件读取接口
 * 
 * @author hongwei.quhw
 * @version $Id: FileReader.java, v 0.1 2016-12-20 下午4:46:49 hongwei.quhw Exp $
 */
public interface FileReader {
    /**
     * 读取头信息， 返回指定对象
     */
    <T> T readHead(Class<?> requiredType);

    /**
     * 读取文件行记录 ， 返回指定对象
     */
    <T> T readRow(Class<?> requiredType);

    /**
     * 读取文件尾
     */
    <T> T readTail(Class<?> requiredType);

    /**
     * 读取一行记录
     */
    String readLine();

    /**
     * 获取汇总字段 
     */
    Summary getSummary();

    /**
     * 关闭流
     */
    void close();
}
