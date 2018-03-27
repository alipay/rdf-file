package com.alipay.rdf.file.sort;

import com.alipay.rdf.file.model.FileConfig;
import com.alipay.rdf.file.model.FileSlice;
import com.alipay.rdf.file.model.SortConfig;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 * 
 * 分片排序接口
 * 
 * @author hongwei.quhw
 * @version $Id: SortExecutor.java, v 0.1 2017年8月22日 下午10:00:24 hongwei.quhw Exp $
 */
public interface SortExecutor {

    /**
     * @param fileConfig
     * @param sortConfig
     * @param fileSlice
     * @return 排序后分片地址
     */
    String sort(FileConfig fileConfig, SortConfig sortConfig, FileSlice fileSlice);
}
