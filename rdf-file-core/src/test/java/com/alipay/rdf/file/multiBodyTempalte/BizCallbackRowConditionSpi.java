package com.alipay.rdf.file.multiBodyTempalte;

import com.alipay.rdf.file.meta.FileBodyMeta;
import com.alipay.rdf.file.model.FileConfig;
import com.alipay.rdf.file.spi.RdfFileRowConditionSpi;
import com.alipay.rdf.file.util.BeanMapWrapper;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 *
 * 业务实现回调示例
 *
 * @author hongwei.quhw
 * @version $Id: BizCallbackRowConditionSpi.java, v 0.1 2018年10月11日 下午8:57:50 hongwei.quhw Exp $
 */
public class BizCallbackRowConditionSpi implements RdfFileRowConditionSpi {

    @Override
    public void init(FileBodyMeta bodyMeta) {
    }

    @Override
    public boolean serialize(FileConfig config, BeanMapWrapper row) {
        return false;
    }

    @Override
    public boolean deserialize(FileConfig fileConfig, String[] row) {
        return false;
    }

}
