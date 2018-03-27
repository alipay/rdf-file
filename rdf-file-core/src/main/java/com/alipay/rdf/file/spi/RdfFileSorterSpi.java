package com.alipay.rdf.file.spi;

import com.alipay.rdf.file.init.RdfInit;
import com.alipay.rdf.file.interfaces.FileSorter;
import com.alipay.rdf.file.model.FileConfig;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 * 
 * 文件排序标记接口
 * 
 * @author hongwei.quhw
 * @version $Id: RdfFileSorterSpi.java, v 0.1 2017年8月23日 上午12:40:45 hongwei.quhw Exp $
 */
public interface RdfFileSorterSpi extends FileSorter, RdfInit<FileConfig> {

}
