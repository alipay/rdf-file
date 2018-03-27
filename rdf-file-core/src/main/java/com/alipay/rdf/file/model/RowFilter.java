package com.alipay.rdf.file.model;

import com.alipay.rdf.file.sort.RowData;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 * 
 * 行过滤器
 * 
 * @author hongwei.quhw
 * @version $Id: RowFilter.java, v 0.1 2017年6月23日 下午2:09:59 hongwei.quhw Exp $
 */
public interface RowFilter {

    /**
     * 需要过滤数据调返回true，  保留数据返回false
     * 
     * @param rowData
     * @return
     */
    boolean filter(RowData rowData);
}
