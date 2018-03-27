package com.alipay.rdf.file.spi;

import com.alipay.rdf.file.model.FileConfig;
import com.alipay.rdf.file.model.FileDataTypeEnum;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 * 
 * 行分割器
 * 
 * @author hongwei.quhw
 * @version $Id: RowSplitSpi.java, v 0.1 2017年7月22日 下午10:25:29 hongwei.quhw Exp $
 */
public interface RdfFileRowSplitSpi {
    String[] split(SplitContext ctx);

    String getSplit(FileConfig fileConfig);

    public class SplitContext {
        private final String           line;
        private final FileConfig       fileConfig;
        private final FileDataTypeEnum rowType;

        public SplitContext(String line, FileConfig fileConfig, FileDataTypeEnum rowType) {
            super();
            this.line = line;
            this.fileConfig = fileConfig;
            this.rowType = rowType;
        }

        /**
         * Getter method for property <tt>line</tt>.
         * 
         * @return property value of line
         */
        public String getLine() {
            return line;
        }

        public FileConfig getFileConfig() {
            return fileConfig;
        }

        /**
         * Getter method for property <tt>rowType</tt>.
         * 
         * @return property value of rowType
         */
        public FileDataTypeEnum getRowType() {
            return rowType;
        }

    }
}
