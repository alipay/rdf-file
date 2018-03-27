package com.alipay.rdf.file.type;

import com.alipay.rdf.file.exception.RdfErrorEnum;
import com.alipay.rdf.file.exception.RdfFileException;
import com.alipay.rdf.file.meta.FileColumnMeta;
import com.alipay.rdf.file.spi.RdfFileColumnTypeSpi;
import com.alipay.rdf.file.util.RdfFileUtil;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 * 
 * 抽象方法默认实现
 * 
 * @author hongwei.quhw
 * @version $Id: AbstractColumnTypeCodec.java, v 0.1 2017年8月19日 下午3:34:24 hongwei.quhw Exp $
 */
public abstract class AbstractColumnTypeCodec<T> implements RdfFileColumnTypeSpi<T> {
    @Override
    public String serialize(T field, FileColumnMeta columnMeta) {
        String colValue = "";

        if (null != field) {
            colValue = doSerialize(field, columnMeta);
        }

        if (RdfFileUtil.isBlank(colValue) && RdfFileUtil.isNotBlank(columnMeta.getDefaultValue())) {
            colValue = columnMeta.getDefaultValue();
        }

        if (RdfFileUtil.isBlank(colValue) && columnMeta.isRequired()) {
            throw new RdfFileException(
                "rdf-file#serialize 必填字段 name=" + columnMeta.getName() + ", 值为空",
                RdfErrorEnum.VALIDATE_ERROR);
        }

        return colValue;
    }

    protected String doSerialize(T field, FileColumnMeta columnMeta) {
        return field.toString().trim();
    }

    @Override
    public T deserialize(String field, FileColumnMeta columnMeta) {
        if (RdfFileUtil.isBlank(field)) {
            if (columnMeta.isRequired()) {
                throw new RdfFileException(
                    "rdf-file#serialize 必填字段 name=" + columnMeta.getName() + ", 值为空",
                    RdfErrorEnum.VALIDATE_ERROR);
            }
            return null;
        }

        field = field.trim();

        return doDeserialize(field, columnMeta);
    }

    protected abstract T doDeserialize(String field, FileColumnMeta columnMeta);

    @Override
    public T add(T a, T b) {
        throw new RdfFileException("rdf-file#" + getClass().getName() + " 不支持add操作!",
            RdfErrorEnum.UNSUPPORTED_OPERATION);
    }
}
