package com.alipay.rdf.file.format;

import java.math.BigDecimal;
import java.math.BigInteger;

import com.alipay.rdf.file.exception.RdfErrorEnum;
import com.alipay.rdf.file.exception.RdfFileException;
import com.alipay.rdf.file.meta.FileColumnMeta;
import com.alipay.rdf.file.model.FileConfig;
import com.alipay.rdf.file.spi.RdfFileFormatSpi;
import com.alipay.rdf.file.util.RdfFileUtil;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 * 
 * 基金文件 序列化，反序列化工具, 这里并没有对数据进行校验
 * 
 * A 数字字符型，限于0—9 
 * C 字符型
 * N 数值型，其长度不包含小数点，可参与数值计算
 * 
 * 补位规则
 * a） 数字左补零右对齐，字符右补空格左对齐；
 * b） 字符不区分大小写
 * 
 * @author quhongwei
 * @version $Id: FundNColumnFormat.java, v 0.1 2017年3月22日 下午5:07:43 quhongwei Exp $
 */
public class NColumnFormat implements RdfFileFormatSpi {

    @Override
    public String serialize(String field, FileColumnMeta colMeta, FileConfig fileConfig) {
        //空补零
        if (RdfFileUtil.isBlank(field)) {
            field = RdfFileUtil.alignRight("", colMeta.getRange().getFirstAttr(), '0');
        } else {
            try {
                BigInteger value = new BigDecimal(field.toString().trim())
                    .multiply(new BigDecimal(RdfFileUtil.alignLeft("1", colMeta.getRange().getSecondAttr() + 1, "0"))).toBigInteger();

                //负数
                if (value.compareTo(new BigInteger("0")) < 0) {
                    field = RdfFileUtil.alignRight(String.valueOf(value.negate()), colMeta.getRange().getFirstAttr(), '0', true);
                } else {
                    field = RdfFileUtil.alignRight(String.valueOf(value), colMeta.getRange().getFirstAttr(), '0');
                }
            } catch (NumberFormatException e) {
                throw new RdfFileException("rdf-file#NColumnFormat field = " + field
                                           + ", columnName=" + colMeta.getName() + " 数组转换出错",
                    e, RdfErrorEnum.SERIALIZE_ERROR);
            }
        }

        return field;
    }

    @Override
    public String deserialize(String field, FileColumnMeta colMeta, FileConfig fileConfig) {
        //数值型补位为0 不可能StringUtils.isBlank(field)
        BigDecimal value = null;
        //有小数点
        if (colMeta.getRange().getSecondAttr() > 0) {
            value = new BigDecimal(field).divide(new BigDecimal(RdfFileUtil.alignLeft("1", colMeta.getRange().getSecondAttr() + 1, "0")));
        } else {
            value = new BigDecimal(field);
        }

        return value.toString();
    }

}
