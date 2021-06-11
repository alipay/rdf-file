package com.alipay.rdf.file.util;

import com.alipay.rdf.file.protocol.ColumnLayoutEnum;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 *
 * rdf组件常量
 *
 * @author hongwei.quhw
 * @version $Id: RdfConstants.java, v 0.1 2017年7月22日 下午6:02:41 hongwei.quhw Exp $
 */
public class RdfFileConstants {
    /**默认行分割器*/
    public static final String           DEFAULT_ROW_SPLIT     = "rowSplitBySeparator";

    /**默认变量所有字段*/
    public static final boolean          DEFAULT_COLUMN_LOOP   = true;

    /**默认水平输出*/
    public static final ColumnLayoutEnum DEFAULT_COLUMN_LAYOUT = ColumnLayoutEnum.horizontal;

    //========================processor 传递参数的一些key==========================
    public static final String           DATA                  = "data";
    public static final String           ROW_TYPE              = "rowType";
    public static final String           SUMMARY               = "summary";
    public static final String           INPUT_STREAM          = "inputStream";
}
