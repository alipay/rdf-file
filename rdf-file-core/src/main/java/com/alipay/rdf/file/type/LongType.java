package com.alipay.rdf.file.type;

import com.alipay.rdf.file.meta.FileColumnMeta;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 * 
 * @author hongwei.quhw
 * @version $Id: LongType.java, v 0.1 2017年8月19日 下午3:33:15 hongwei.quhw Exp $
 */
public class LongType extends AbstractNumberTypeCodec<Long> {

    @Override
    protected Long doDeserialize(String field, FileColumnMeta columnMeta) {
        return Long.valueOf(field);
    }

    @Override
    public Long add(Long a, Long b) {
        if (null == a && null == b) {
            return new Long(0);
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
