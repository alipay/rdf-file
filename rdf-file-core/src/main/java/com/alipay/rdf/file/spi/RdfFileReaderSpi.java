package com.alipay.rdf.file.spi;

import com.alipay.rdf.file.init.RdfInit;
import com.alipay.rdf.file.interfaces.FileReader;
import com.alipay.rdf.file.model.FileConfig;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 * 
 * 组件读文件扩展标记接口
 * 
 * @author hongwei.quhw
 * @version $Id: RdfFileReaderSpi.java, v 0.1 2017年8月10日 下午8:19:12 hongwei.quhw Exp $
 */
public interface RdfFileReaderSpi extends FileReader, RdfInit<FileConfig> {
    /**
     * 读取一行记录
     */
    String readBodyLine();

    /**
     * 返回配置对象
     * 
     * @return
     */
    FileConfig getFileConfig();
}
