package com.alipay.rdf.file.interfaces;

import com.alipay.rdf.file.model.FileConfig;
import com.alipay.rdf.file.model.ValidateResult;
import com.alipay.rdf.file.util.BeanMapWrapper;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 * 
 * 行记录验证器接口
 * 
 * @author hongwei.quhw
 * @version $Id: RowValidator.java, v 0.1 2017年8月3日 下午2:48:18 hongwei.quhw Exp $
 */
public interface RowValidator {
    /**
     * 校验行
     * 
     * @param context
     * @return
     */
    ValidateResult validateRow(RowValidatorContext context);

    public static class RowValidatorContext {
        /**文件信息*/
        private final FileConfig     fileConfig;
        private final BeanMapWrapper row;

        /**
         * @param fileConfig
         * @param row
         */
        public RowValidatorContext(FileConfig fileConfig, BeanMapWrapper row) {
            super();
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
