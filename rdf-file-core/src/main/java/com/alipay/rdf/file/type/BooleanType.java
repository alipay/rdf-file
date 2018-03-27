package com.alipay.rdf.file.type;

import com.alipay.rdf.file.meta.FileColumnMeta;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 * 
 * @author hongwei.quhw
 * @version $Id: BooleanType.java, v 0.1 2017年8月19日 下午3:33:30 hongwei.quhw Exp $
 */
public class BooleanType extends AbstractColumnTypeCodec<Boolean> {
    @Override
    protected Boolean doDeserialize(String field, FileColumnMeta columnMeta) {
        return Boolean.valueOf(field);
    }

}
