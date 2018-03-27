package com.alipay.rdf.file.spi;

import com.alipay.rdf.file.init.RdfInit;
import com.alipay.rdf.file.interfaces.FileMerger;
import com.alipay.rdf.file.model.FileConfig;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 * 
 * 组件文件合并扩展标记接口
 * 
 * @author hongwei.quhw
 * @version $Id: RdfFileMergerSpi.java, v 0.1 2017年8月11日 下午10:09:43 hongwei.quhw Exp $
 */
public interface RdfFileMergerSpi extends FileMerger, RdfInit<FileConfig> {
}
