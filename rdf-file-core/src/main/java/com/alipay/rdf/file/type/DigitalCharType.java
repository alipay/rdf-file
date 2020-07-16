package com.alipay.rdf.file.type;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.alipay.rdf.file.exception.RdfErrorEnum;
import com.alipay.rdf.file.exception.RdfFileException;
import com.alipay.rdf.file.meta.FileColumnMeta;
import com.alipay.rdf.file.spi.RdfFileColumnTypeSpi;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 * 
 * 由数字组成的字符串
 * 
 * @author hongwei.quhw
 * @version $Id: DigitalCharType.java, v 0.1 2017年8月19日 下午3:31:17 hongwei.quhw Exp $
 */
public class DigitalCharType extends AbstractColumnTypeCodec<String> {
    private static final Pattern DIGITAL_REG = Pattern.compile("^[0-9]*$");

    @Override
    protected String doDeserialize(String field, FileColumnMeta columnMeta) {
        check(field, columnMeta);
        return field;
    }

   @Override
    public String serialize(String field, FileColumnMeta columnMeta) {
        String value = super.serialize(field, columnMeta);
        check(value, columnMeta);
        return value;
    }

    private void check(String field, FileColumnMeta columnMeta) {
        Matcher match = DIGITAL_REG.matcher(field);
        if (!match.matches()) {
            throw new RdfFileException(
                "rdf-fiel#字段" + columnMeta.getDesc() + "只能是数字字符类型, 实际是" + field,
                RdfErrorEnum.VALIDATE_ERROR);
        }
    }
}