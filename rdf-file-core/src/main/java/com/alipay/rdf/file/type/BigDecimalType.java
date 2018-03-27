package com.alipay.rdf.file.type;

import java.math.BigDecimal;

import com.alipay.rdf.file.meta.FileColumnMeta;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 * 
 * @author hongwei.quhw
 * @version $Id: BigDecimalType.java, v 0.1 2017年8月19日 下午3:33:54 hongwei.quhw Exp $
 */
public class BigDecimalType extends AbstractNumberTypeCodec<BigDecimal> {
    @Override
    protected String doSerialize(BigDecimal field, FileColumnMeta columnMeta) {
        return field.toPlainString();
    }

    @Override
    protected BigDecimal doDeserialize(String field, FileColumnMeta columnMeta) {
        return new BigDecimal(field);
    }

    @Override
    public BigDecimal add(BigDecimal a, BigDecimal b) {
        if (null == a && null == b) {
            return new BigDecimal(0);
        }
        if (null == a) {
            return b;
        }
        if (null == b) {
            return a;
        }

        return a.add(b);
    }
}
