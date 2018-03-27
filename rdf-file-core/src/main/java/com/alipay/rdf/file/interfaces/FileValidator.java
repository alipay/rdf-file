package com.alipay.rdf.file.interfaces;

import com.alipay.rdf.file.exception.RdfFileException;
import com.alipay.rdf.file.model.ValidateResult;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 * 
 * 对文件进行整体校验
 * 
 * @author hongwei.quhw
 * @version $Id: FileValidator.java, v 0.1 2017年8月17日 上午10:52:13 hongwei.quhw Exp $
 */
public interface FileValidator {

    /**
     * 
     * 文件校验
     * <li>根据文件模板校验文件格式，包括非空字段 </li>
     * <li>根据文件模板配置的RowValidator校验每行数据</li>
     * <li>根据文件模板配置的汇总信息校验文件汇总信息和总笔数</li>
     */
    ValidateResult validate() throws RdfFileException;
}
