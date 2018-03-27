package com.alipay.rdf.file.spi;

import com.alipay.rdf.file.init.RdfInit;
import com.alipay.rdf.file.interfaces.FileStorage;
import com.alipay.rdf.file.model.StorageConfig;
import com.alipay.rdf.file.storage.FileInnterStorage;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 * 
 * 标记可扩展的文件存储接口
 * 
 * @author hongwei.quhw
 * @version $Id: FileStorageSpi.java, v 0.1 2017年4月13日 下午4:59:02 hongwei.quhw Exp $
 */
public interface RdfFileStorageSpi extends FileStorage, FileInnterStorage, RdfInit<StorageConfig> {
}
