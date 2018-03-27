package com.alipay.rdf.file.protocol;

import com.alipay.rdf.file.exception.RdfErrorEnum;
import com.alipay.rdf.file.exception.RdfFileException;
import com.alipay.rdf.file.util.RdfFileUtil;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 * 
 * 字段输出形式
 * 
 * @author hongwei.quhw
 * @version $Id: ColumnLayoutEnum.java, v 0.1 2017年7月22日 下午9:03:44 hongwei.quhw Exp $
 */
public enum ColumnLayoutEnum {
                              /**垂直输出*/
                              vertical,

                              /**水平输出*/
                              horizontal;

    public static ColumnLayoutEnum getColumnLayoutByName(String name) {
        for (ColumnLayoutEnum columnLayout : values()) {
            if (RdfFileUtil.equals(columnLayout.name(), name)) {
                return columnLayout;
            }
        }

        throw new RdfFileException("不存在name=[" + name + "]的枚举ColumnLayoutEnum",
            RdfErrorEnum.PROTOCOL_DEFINE_ERROR);
    }
}
