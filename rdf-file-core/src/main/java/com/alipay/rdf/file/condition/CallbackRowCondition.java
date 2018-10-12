package com.alipay.rdf.file.condition;

import com.alipay.rdf.file.spi.RdfFileRowConditionSpi;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 *
 * 回调业务实现,进行行条件校验
 *
 * @author hongwei.quhw
 * @version $Id: CallbackRowCondition.java, v 0.1 2018年10月11日 下午8:49:34 hongwei.quhw Exp $
 */
public class CallbackRowCondition implements RdfFileRowConditionSpi {

    @Override
    public boolean caculate(RowConditionContext ctx) {
        return false;
    }

}
