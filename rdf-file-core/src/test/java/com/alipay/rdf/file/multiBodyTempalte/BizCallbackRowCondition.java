package com.alipay.rdf.file.multiBodyTempalte;

import com.alipay.rdf.file.model.FileConfig;
import com.alipay.rdf.file.model.RowCondition;
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
public class BizCallbackRowCondition implements RdfFileRowConditionSpi {

    @Override
    public void init(RowCondition conditon) {
    }

    @Override
    public boolean serialize(FileConfig config, BeanMapWrapper row) {
        long longN = (Long) row.getProperty("longN");
        int age = (Integer) row.getProperty("age");
        return age + longN == 100;
    }

    @Override
    public boolean deserialize(FileConfig fileConfig, String[] row) {
        return row[7].equals("33") && row[8].equals("67");
    }

}
