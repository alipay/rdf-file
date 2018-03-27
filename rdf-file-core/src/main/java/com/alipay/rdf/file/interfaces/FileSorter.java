package com.alipay.rdf.file.interfaces;

import com.alipay.rdf.file.model.SortConfig;
import com.alipay.rdf.file.model.SortResult;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 * 
 * 文件排序接口
 * 
 * @author hongwei.quhw
 * @version $Id: FileSorter.java, v 0.1 2017年8月22日 下午9:04:19 hongwei.quhw Exp $
 */
public interface FileSorter {

    /**
     * 文件排序
     * 
     * @param sortConfig
     * @return
     */
    SortResult sort(SortConfig sortConfig);
}
