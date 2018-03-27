package com.alipay.rdf.file.split;

import com.alipay.rdf.file.loader.TemplateLoader;
import com.alipay.rdf.file.meta.FileMeta;
import com.alipay.rdf.file.model.FileConfig;
import com.alipay.rdf.file.spi.RdfFileRowSplitSpi;
import com.alipay.rdf.file.util.RdfFileUtil;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 * 
 * 分隔符分割器
 * 
 * @author hongwei.quhw
 * @version $Id: RowSplitBySeparator.java, v 0.1 2017年8月8日 上午10:55:13 hongwei.quhw Exp $
 */
public class RowSplitBySeparator implements RdfFileRowSplitSpi {

    /** 
     * @see hongwei.quhw.file.split.RdfFileRowSplitSpi#split(java.lang.String, hongwei.quhw.file.meta.FileMeta)
     */
    @Override
    public String[] split(SplitContext ctx) {
        FileMeta fileMeta = TemplateLoader.load(ctx.getFileConfig());
        return RdfFileUtil.split(ctx.getLine(), fileMeta.getColumnSplit());
    }

    @Override
    public String getSplit(FileConfig fileConfig) {
        return TemplateLoader.load(fileConfig).getColumnSplit();
    }
}
