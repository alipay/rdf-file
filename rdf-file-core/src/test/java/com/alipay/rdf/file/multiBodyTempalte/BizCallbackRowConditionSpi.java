package com.alipay.rdf.file.multiBodyTempalte;

import com.alipay.rdf.file.spi.RdfFileRowConditionSpi;

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
    public boolean caculate(RowConditionContext ctx) {
        return false;
    }

}
