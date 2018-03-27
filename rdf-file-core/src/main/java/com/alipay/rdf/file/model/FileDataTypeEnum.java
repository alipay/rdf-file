package com.alipay.rdf.file.model;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 * 
 * 文件数据类型
 * 
 * @author hongwei.quhw
 * @version $Id: FileDataTypeEnum.java, v 0.1 2017年8月1日 下午3:38:30 hongwei.quhw Exp $
 */
public enum FileDataTypeEnum {
                              /**文件头数据*/
                              HEAD,
                              /**文件体数据*/
                              BODY,
                              /**文件尾数据*/
                              TAIL,
                              /**包含所有数据*/
                              ALL,
                              /**不确定*/
                              UNKOWN;
}
