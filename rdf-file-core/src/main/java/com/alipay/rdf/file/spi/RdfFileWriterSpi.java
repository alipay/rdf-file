package com.alipay.rdf.file.spi;

import java.io.InputStream;

import com.alipay.rdf.file.init.RdfInit;
import com.alipay.rdf.file.interfaces.FileWriter;
import com.alipay.rdf.file.model.FileConfig;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 * 
 * 组件写文件扩展标记接口
 * 
 * @author hongwei.quhw
 * @version $Id: RdfFileWriterSpi.java, v 0.1 2017年8月10日 下午11:03:37 hongwei.quhw Exp $
 */
public interface RdfFileWriterSpi extends FileWriter, RdfInit<FileConfig> {
    /**像文件输入流写*/
    void append(InputStream in);

    /**保证文件创建*/
    void ensureOpen();

    /**返回配置对象*/
    FileConfig getFileConfig();
}
