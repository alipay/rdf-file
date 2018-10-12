package com.alipay.rdf.file.condition;

import com.alipay.rdf.file.meta.FileBodyMeta;
import com.alipay.rdf.file.model.FileConfig;
import com.alipay.rdf.file.spi.RdfFileRowConditionSpi;
import com.alipay.rdf.file.util.BeanMapWrapper;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 *
 * 基于表达式的行条件计算器
 *
 * @author hongwei.quhw
 * @version $Id: ExpressionRowCondition.java, v 0.1 2018年10月11日 下午8:48:10 hongwei.quhw Exp $
 */
public class ExpressionRowCondition implements RdfFileRowConditionSpi {

    @Override
    public void init(FileBodyMeta bodyMeta) {
        String param = bodyMeta.getRowConditionParam();
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
