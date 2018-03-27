package com.alipay.rdf.file.interfaces;

import com.alipay.rdf.file.model.MergerConfig;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 * 
 * 文件合并接口
 * 
 * @author hongwei.quhw
 * @version $Id: FileMerger.java, v 0.1 2017年4月20日 下午7:18:03 hongwei.quhw Exp $
 */
public interface FileMerger {

    void merge(MergerConfig config);
}
