package com.alipay.rdf.file.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.alipay.rdf.file.exception.RdfErrorEnum;
import com.alipay.rdf.file.exception.RdfFileException;

/**
 * 日期工具类
 * 
 * @author hongwei.quhw
 * @version $Id: DateUtil.java, v 0.1 2017年8月8日 下午10:54:39 hongwei.quhw Exp $
 */
public class DateUtil {
    public static String format(Date date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }

    public static Date parse(String field, String format) {
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
