package com.alipay.rdf.file.condition;

import com.alipay.rdf.file.model.FileConfig;
import com.alipay.rdf.file.model.RowCondition;
import com.alipay.rdf.file.spi.RdfFileRowConditionSpi;
import com.alipay.rdf.file.util.BeanMapWrapper;
import com.alipay.rdf.file.util.RdfFileUtil;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 *
 * 回调业务实现,进行行条件校验
 *
 * @author hongwei.quhw
 * @version $Id: CallbackRowCondition.java, v 0.1 2018年10月11日 下午8:49:34 hongwei.quhw Exp $
 */
public class CallbackRowCondition implements RdfFileRowConditionSpi {
    private RdfFileRowConditionSpi callbackRowCondition;

    @Override
    public void init(RowCondition rowCondition) {
        this.callbackRowCondition = (RdfFileRowConditionSpi) RdfFileUtil
            .newInstance(rowCondition.getConditionParam());
    }

    @Override
    public boolean serialize(FileConfig config, BeanMapWrapper row) {
        return callbackRowCondition.serialize(config, row);
    }

    @Override
    public boolean deserialize(FileConfig fileConfig, String[] row) {
        return callbackRowCondition.deserialize(fileConfig, row);
    }

}
