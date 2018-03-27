package com.alipay.rdf.file.processor;

import com.alipay.rdf.file.interfaces.FileOssStorageContants;
import com.alipay.rdf.file.spi.RdfFileProcessorSpi;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 * 
 * oss存储处理通用逻辑
 * 
 * @author hongwei.quhw
 * @version $Id: AbstractOssProcessor.java, v 0.1 2017年8月24日 上午11:17:20 hongwei.quhw Exp $
 */
public abstract class AbstractOssProcessor implements RdfFileProcessorSpi {
    @Override
    public final void process(ProcessCotnext pc) {
        if (!FileOssStorageContants.STORAGE_OSS
            .equals(pc.getFileConfig().getStorageConfig().getStorageType())) {
            return;
        }

        doProcess(pc);
    }

    protected abstract void doProcess(ProcessCotnext pc);
}
