package com.alipay.rdf.file.type;

import com.alipay.rdf.file.meta.FileColumnMeta;
import com.alipay.rdf.file.util.RdfFileUtil;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 * 
 * 数值类型抽象方法
 * 
 * @author hongwei.quhw
 * @version $Id: AbstractNumberTypeCodec.java, v 0.1 2017年8月19日 下午3:34:06 hongwei.quhw Exp $
 */
public abstract class AbstractNumberTypeCodec<T> extends AbstractColumnTypeCodec<T> {
    @Override
    public String serialize(T field, FileColumnMeta columnMeta) {
        String text = super.serialize(field, columnMeta);
        if (RdfFileUtil.isBlank(text)) {
            return "0";
        } else {
            return text;
        }
    }
}
