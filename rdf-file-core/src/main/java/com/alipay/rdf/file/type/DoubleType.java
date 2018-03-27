package com.alipay.rdf.file.type;

import com.alipay.rdf.file.meta.FileColumnMeta;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 * 
 * @author hongwei.quhw
 * @version $Id: DoubleType.java, v 0.1 2017年8月19日 下午3:32:50 hongwei.quhw Exp $
 */
public class DoubleType extends AbstractNumberTypeCodec<Double> {

    @Override
    protected Double doDeserialize(String field, FileColumnMeta columnMeta) {
        return Double.valueOf(field);
    }

    /** 
     * @see hongwei.quhw.file.type.RdfFileColumnTypeSpi#serialize(java.lang.Object, hongwei.quhw.file.meta.FileColumnMeta)
     */
    @Override
    public String serialize(Double field, FileColumnMeta columnMeta) {
        return field.toString();
    }

    @Override
    public Double add(Double a, Double b) {
        if (null == a && null == b) {
            return new Double(0);
        }
        if (null == a) {
            return b;
        }
        if (null == b) {
            return a;
        }

        return a + b;
    }
}
