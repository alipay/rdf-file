package com.alipay.rdf.file.spi;

import com.alipay.rdf.file.meta.FileColumnMeta;
import com.alipay.rdf.file.model.FileConfig;
import com.alipay.rdf.file.model.FileDataTypeEnum;
import com.alipay.rdf.file.protocol.RowDefinition;
import com.alipay.rdf.file.util.BeanMapWrapper;

import java.util.List;

/**
 *
 * 用于对行记录的序列/反序列化扩展
 *
 * @Author: hongwei.quhw 2021/6/27 2:51 下午
 */
public interface RdfFileRowCodecSpi {

    String serialize(RowCodecContext ctx);

    void deserialize(String line, RowCodecContext ctx);

    public static class RowCodecContext {
        public final BeanMapWrapper bmw;
        public final FileConfig fileConfig;
        public final List<FileColumnMeta> columnMetas;
        public final RowDefinition rd;
        public final String[] columnValues;

        public RowCodecContext(BeanMapWrapper bmw, FileConfig fileConfig, List<FileColumnMeta> columnMetas, RowDefinition rd) {
            this.bmw = bmw;
            this.fileConfig = fileConfig;
            this.columnMetas = columnMetas;
            this.rd = rd;
            this.columnValues = null;
        }

        public RowCodecContext(BeanMapWrapper bmw, FileConfig fileConfig, List<FileColumnMeta> columnMetas, RowDefinition rd, String[] columnValues) {
            this.bmw = bmw;
            this.fileConfig = fileConfig;
            this.columnMetas = columnMetas;
            this.rd = rd;
            this.columnValues = columnValues;
        }
    }
}
