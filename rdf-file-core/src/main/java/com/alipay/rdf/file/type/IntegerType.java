package com.alipay.rdf.file.type;

import com.alipay.rdf.file.meta.FileColumnMeta;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 * 
 * @author hongwei.quhw
 * @version $Id: IntegerType.java, v 0.1 2017年8月19日 下午3:33:08 hongwei.quhw Exp $
 */
public class IntegerType extends AbstractNumberTypeCodec<Integer> {

    @Override
    protected Integer doDeserialize(String field, FileColumnMeta columnMeta) {
        return Integer.valueOf(field);
    }

    @Override
    public Integer add(Integer a, Integer b) {
        if (null == a && null == b) {
            return new Integer(0);
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
