package com.alipay.rdf.file.interfaces;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 * 
 * 内核组件processor key
 *
 * @author hongwei.quhw
 * @version $Id: FileCoreProcessorConstants.java, v 0.1 2018年4月11日 上午10:46:44 hongwei.quhw Exp $
 */
public interface FileCoreProcessorConstants {
    /**读长度校验器key， 组件非默认*/
    public static final String LENGTH_READ_VALIDATOR     = "lengthReadValidator";
    /**写长度校验器key， 组件非默认*/
    public static final String LENGTH_WRITE_VALIDATOR    = "lengthWriteValidator";
    /**汇总字段回调处理， com.alipay.rdf.file.model.FileConfig.summaryEnable=true时自动加入*/
    public static final String SUMMARY                   = "summary";
    /**行定长校验器， fund（格式）协议 自动绑定*/
    public static final String FIX_LENGTH_LINE_VALIDATOR = "fixLengthLineValidator";
    /**行校验器回调处理器 组件默认绑定*/
    public static final String BODY_ROW_VALIDTOR         = "bodyRowValidtor";

}
