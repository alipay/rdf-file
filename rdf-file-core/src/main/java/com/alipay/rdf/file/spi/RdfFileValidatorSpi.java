package com.alipay.rdf.file.spi;

import com.alipay.rdf.file.init.RdfInit;
import com.alipay.rdf.file.interfaces.FileValidator;
import com.alipay.rdf.file.model.FileConfig;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 * 
 * 文件校验可扩展标记接口
 * 
 * @author hongwei.quhw
 * @version $Id: FileValidatorSpi.java, v 0.1 2017年8月17日 上午11:08:24 hongwei.quhw Exp $
 */
public interface RdfFileValidatorSpi extends FileValidator, RdfInit<FileConfig> {

}
