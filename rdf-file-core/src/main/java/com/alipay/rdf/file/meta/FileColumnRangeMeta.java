package com.alipay.rdf.file.meta;

import com.alipay.rdf.file.exception.RdfErrorEnum;
import com.alipay.rdf.file.exception.RdfFileException;
import com.alipay.rdf.file.util.RdfFileUtil;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 * 
 * 数据范围
 * 
 * 格式[20, 2]  长度20 小数位数2位
 * 
 * 如有需要可以扩展定义含义
 * 
 * @author hongwei.quhw
 * @version $Id: FileColumnRangeMeta.java, v 0.1 2016-12-20 下午4:21:20 hongwei.quhw Exp $
 */
public class FileColumnRangeMeta {
    private final int firstAttr;
    private final int secondAttr;

    /**
     * @param firstAttr
     * @param secondAttr
     */
    public FileColumnRangeMeta(int firstAttr, int secondAttr) {
        this.firstAttr = firstAttr;
        this.secondAttr = secondAttr;
    }

    /**
     * 
     * 
     * @param field
     * @param extra
     * @return
     */
    public static FileColumnRangeMeta tryValueOf(String field, String extra) {
        if (field.startsWith("[") && field.endsWith("]")) {
            String tmp = field.substring(1, field.length() - 1);

            tmp = RdfFileUtil.assertTrimNotBlank(tmp, "范围属性配置空！ 请 检查配置=" + field);

            String[] items = tmp.split(",");
            if (items.length > 2) {
                throw new RdfFileException("范围属性多于2项目前不支持请检查！ 字段=" + field,
                    RdfErrorEnum.TEMPLATE_ERROR);
            }

            int firstAttr = Integer.parseInt(items[0].trim());
            int secondAttr = items.length > 1 ? Integer.parseInt(items[1].trim()) : 0;
            return new FileColumnRangeMeta(firstAttr, secondAttr);

        }

        return null;
    }

    /**
     * Getter method for property <tt>firstAttr</tt>.
     * 
     * @return property value of firstAttr
     */
    public int getFirstAttr() {
        return firstAttr;
    }

    /**
     * Getter method for property <tt>secondAttr</tt>.
     * 
     * @return property value of secondAttr
     */
    public int getSecondAttr() {
        return secondAttr;
    }

    @Override
    public String toString() {
        return "FileColumnRangeMeta[firstAttr=" + firstAttr + ", secondAttr=" + secondAttr + "]";
    }
}
