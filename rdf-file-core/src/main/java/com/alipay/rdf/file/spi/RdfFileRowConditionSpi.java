package com.alipay.rdf.file.spi;

import com.alipay.rdf.file.init.RdfInit;
import com.alipay.rdf.file.meta.FileBodyMeta;
import com.alipay.rdf.file.model.FileConfig;
import com.alipay.rdf.file.util.BeanMapWrapper;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 *
 * body数据条件决策spi
 *
 * @author hongwei.quhw
 * @version $Id: RdfFileConditionSpi.java, v 0.1 2018年10月11日 下午8:38:11 hongwei.quhw Exp $
 */
public interface RdfFileRowConditionSpi extends RdfInit<FileBodyMeta> {

    boolean caculate(RowConditionContext ctx);

    public static class RowConditionContext {
        /**文件配置*/
        private final FileConfig     fileConfig;
        /**行数据*/
        private final BeanMapWrapper row;

        public RowConditionContext(FileConfig fileConfig, BeanMapWrapper row) {
            this.fileConfig = fileConfig;
            this.row = row;
        }

        public FileConfig getFileConfig() {
            return fileConfig;
        }

        public BeanMapWrapper getRow() {
            return row;
        }
    }
}
