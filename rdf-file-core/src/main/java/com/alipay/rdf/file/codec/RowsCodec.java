package com.alipay.rdf.file.codec;

import java.util.List;
import java.util.Map;

import com.alipay.rdf.file.interfaces.FileReader;
import com.alipay.rdf.file.interfaces.FileWriter;
import com.alipay.rdf.file.loader.ProtocolLoader;
import com.alipay.rdf.file.loader.TemplateLoader;
import com.alipay.rdf.file.meta.FileColumnMeta;
import com.alipay.rdf.file.meta.FileMeta;
import com.alipay.rdf.file.model.FileConfig;
import com.alipay.rdf.file.model.FileDataTypeEnum;
import com.alipay.rdf.file.processor.ProcessorTypeEnum;
import com.alipay.rdf.file.protocol.ColumnLayoutEnum;
import com.alipay.rdf.file.protocol.RowDefinition;
import com.alipay.rdf.file.spi.RdfFileFunctionSpi.CodecType;
import com.alipay.rdf.file.spi.RdfFileFunctionSpi.FuncContext;
import com.alipay.rdf.file.spi.RdfFileProcessorSpi;
import com.alipay.rdf.file.util.BeanMapWrapper;
import com.alipay.rdf.file.util.RdfFileUtil;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 * 
 * 对文件记录进行逐行编码解码
 * 
 * @author hongwei.quhw
 * @version $Id: RowsCodec.java, v 0.1 2017年8月3日 下午2:55:06 hongwei.quhw Exp $
 */
public class RowsCodec {

    public static void serialize(Object rowBean, FileConfig fileConfig, FileWriter writer,
                                 Map<ProcessorTypeEnum, List<RdfFileProcessorSpi>> processors,
                                 FileDataTypeEnum rowType) {
        FileMeta fileMeta = TemplateLoader.load(fileConfig);
        List<RowDefinition> rds = ProtocolLoader.getRowDefinitos(fileMeta.getProtocol(), rowType);
        BeanMapWrapper bmw = new BeanMapWrapper(rowBean);

        for (RowDefinition rd : rds) {
            if (rd.isColumnloop()) {
                if (ColumnLayoutEnum.horizontal.equals(rd.getColumnLayout())) {
                    writer.writeLine(RowColumnHorizontalCodec.serialize(bmw, fileConfig, rd,
                        processors, rowType));
                } else {
                    List<FileColumnMeta> columnMetas = fileMeta.getColumns(rowType);
                    for (int i = 0; i < columnMetas.size(); i++) {
                        FileColumnMeta columnMeta = columnMetas.get(i);
                        FuncContext ctx = new FuncContext();
                        ctx.codecType = CodecType.SERIALIZE;
                        ctx.field = bmw.getProperty(columnMeta.getName());
                        ctx.columnMeta = columnMeta;
                        ctx.writer = writer;
                        ctx.fileConfig = fileConfig;
                        ctx.processors = processors;
                        writer.writeLine((String) rd.getOutput().execute(ctx));
                    }
                }
            } else {
                FuncContext ctx = new FuncContext();
                ctx.protocolName = fileMeta.getProtocol();
                ctx.codecType = CodecType.SERIALIZE;
                ctx.rowBean = rowBean;
                ctx.writer = writer;
                ctx.fileConfig = fileConfig;
                ctx.columnMeta = getColumnMeta(rd.getColumnMeta(), fileMeta);
                ctx.processors = processors;
                Object ret = rd.getOutput().execute(ctx);
                if (null != ret) {
                    writer.writeLine((String) ret);
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T deserialize(Class<?> requiredType, FileConfig fileConfig, FileReader reader,
                                    Map<ProcessorTypeEnum, List<RdfFileProcessorSpi>> processors,
                                    FileDataTypeEnum rowType) {
        FileMeta fileMeta = TemplateLoader.load(fileConfig);
        List<RowDefinition> rds = ProtocolLoader.getRowDefinitos(fileMeta.getProtocol(), rowType);
        BeanMapWrapper bmw = new BeanMapWrapper(requiredType);

        for (RowDefinition rd : rds) {
            if (rd.isColumnloop()) {
                // column水平合成一行
                if (rd.getColumnLayout().equals(ColumnLayoutEnum.horizontal)) {
                    String line = reader.readLine();
                    if (RdfFileUtil.isBlank(line)) {
                        return null;
                    }

                    RowColumnHorizontalCodec.deserialize(bmw, fileConfig, line, rd, processors,
                        rowType);
                }
                // column每一个就是一行
                else if (rd.getColumnLayout().equals(ColumnLayoutEnum.vertical)) {
                    List<FileColumnMeta> columnMetas = fileMeta.getColumns(rowType);
                    for (int i = 0; i < columnMetas.size(); i++) {
                        String line = reader.readLine();
                        if (null == line) {
                            return null;
                        }

                        FileColumnMeta columnMeta = columnMetas.get(i);
                        FuncContext ctx = new FuncContext();
                        ctx.codecType = CodecType.DESERIALIZE;
                        ctx.field = line;
                        ctx.columnMeta = columnMeta;
                        ctx.reader = reader;
                        ctx.fileConfig = fileConfig;
                        ctx.processors = processors;
                        bmw.setProperty(columnMeta.getName(), rd.getOutput().execute(ctx));
                    }

                }

            } else {
                FuncContext ctx = new FuncContext();
                ctx.protocolName = fileMeta.getProtocol();
                ctx.codecType = CodecType.DESERIALIZE;
                ctx.reader = reader;
                ctx.columnMeta = getColumnMeta(rd.getColumnMeta(), fileMeta);
                ctx.rowBean = bmw.getBean();
                ctx.fileConfig = fileConfig;
                ctx.processors = processors;
                Map<String, Object> retMap = (Map<String, Object>) rd.getOutput().execute(ctx);
                if (null != retMap) {
                    bmw.setProperties(retMap);
                }
            }
        }

        return (T) bmw.getBean();
    }

    public static FileColumnMeta getColumnMeta(FileColumnMeta colMeta, FileMeta fileMeta) {
        if (null == colMeta) {
            return null;
        }

        return new FileColumnMeta(colMeta.getColIndex(), colMeta.getName(), colMeta.getDesc(),
            colMeta.getType(), colMeta.isRequired(), colMeta.getRange(), colMeta.getDefaultValue(),
            fileMeta, colMeta.getDataType());
    }
}
