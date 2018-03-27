package com.alipay.rdf.file.spi;

import com.alipay.rdf.file.init.RdfInit;
import com.alipay.rdf.file.interfaces.FileSplitter;
import com.alipay.rdf.file.interfaces.FileStorage;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 * 
 * 标记可扩展接口
 * 
 * @author hongwei.quhw
 * @version $Id: FileSplitterSpi.java, v 0.1 2017年4月13日 下午4:55:35 hongwei.quhw Exp $
 */
public interface RdfFileSplitterSpi extends FileSplitter, RdfInit<FileStorage> {
}
