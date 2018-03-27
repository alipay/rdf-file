package com.alipay.rdf.file.type;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.alipay.rdf.file.exception.RdfErrorEnum;
import com.alipay.rdf.file.exception.RdfFileException;
import com.alipay.rdf.file.meta.FileColumnMeta;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 * 
 * 日期格式
 * 
 * @author hongwei.quhw
 * @version $Id: DateType.java, v 0.1 2017年8月19日 下午3:32:40 hongwei.quhw Exp $
 */
public class DateType extends AbstractColumnTypeCodec<Object> {
    @Override
    protected String doSerialize(Object field, FileColumnMeta columnMeta) {
        if (field instanceof Date) {
            SimpleDateFormat sdf = new SimpleDateFormat(columnMeta.getType().getExtra());
            return sdf.format((Date) field);
        }

        // String 认为已经格式好了
        if (field instanceof String) {
            String format = columnMeta.getType().getExtra();

            return format(parse((String) field, format), format);
        }

        throw new RdfFileException(
            "rdf-file# DateType  " + field + " 是 " + field.getClass().getName() + "不能格式化为日期类型",
            RdfErrorEnum.DATE_FORAMT_ERROR);
    }

    @Override
    protected Date doDeserialize(String field, FileColumnMeta columnMeta) {
        String format = columnMeta.getType().getExtra();

        return parse(field, format);
    }

    private String format(Date date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }

    private Date parse(String field, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);

        try {
            return sdf.parse(field);
        } catch (ParseException e) {
            throw new RdfFileException(
                "rdf-file#DateType format" + field + "格式化成" + format + "的日期类型出错", e,
                RdfErrorEnum.DATE_FORAMT_ERROR);
        }
    }
}
