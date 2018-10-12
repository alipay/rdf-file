package com.alipay.rdf.file.condition;

import com.alipay.rdf.file.spi.RdfFileRowConditionSpi;

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
    public boolean caculate(RowConditionContext ctx) {
        return false;
    }

}
