package com.alipay.rdf.file.codec;

import java.util.List;
import java.util.Map;

import com.alipay.rdf.file.condition.RowConditionExecutor;
import com.alipay.rdf.file.exception.RdfErrorEnum;
import com.alipay.rdf.file.exception.RdfFileException;
import com.alipay.rdf.file.loader.ProtocolLoader;
import com.alipay.rdf.file.loader.TemplateLoader;
import com.alipay.rdf.file.meta.FileColumnMeta;
import com.alipay.rdf.file.meta.FileMeta;
import com.alipay.rdf.file.model.FileConfig;
import com.alipay.rdf.file.model.FileDataTypeEnum;
import com.alipay.rdf.file.processor.ProcessExecutor;
import com.alipay.rdf.file.processor.ProcessExecutor.BizData;
import com.alipay.rdf.file.processor.ProcessorTypeEnum;
import com.alipay.rdf.file.protocol.RowDefinition;
import com.alipay.rdf.file.spi.RdfFileFunctionSpi.CodecType;
import com.alipay.rdf.file.spi.RdfFileFunctionSpi.FuncContext;
import com.alipay.rdf.file.spi.RdfFileProcessorSpi;
import com.alipay.rdf.file.spi.RdfFileRowSplitSpi.SplitContext;
import com.alipay.rdf.file.util.BeanMapWrapper;
import com.alipay.rdf.file.util.RdfFileConstants;
import com.alipay.rdf.file.util.RdfFileUtil;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 * 
 * 针对字段水平codec
 * 
 * 抽离的目的在于方便，对单行数据进行编码解码， 特别是在测试或者排查问题的时候
 * 
 * @author hongwei.quhw
 * @version $Id: RowColumnHorizontalCodec.java, v 0.1 2017年8月1日 下午8:49:41 hongwei.quhw Exp $
 */
@SuppressWarnings("unchecked")
public class RowColumnHorizontalCodec {

    public static String serialize(BeanMapWrapper bmw, FileConfig fileConfig, RowDefinition rd,
                                   Map<ProcessorTypeEnum, List<RdfFileProcessorSpi>> processors,
                                   FileDataTypeEnum rowType) {
        ProcessExecutor.execute(ProcessorTypeEnum.BEFORE_SERIALIZE_ROW, processors, fileConfig,
            new BizData(RdfFileConstants.DATA, bmw),
            new BizData(RdfFileConstants.ROW_TYPE, rowType));

        FileMeta fileMeta = TemplateLoader.load(fileConfig);
        List<FileColumnMeta> columnMetas = RowConditionExecutor.serializeRow(fileConfig, bmw,
            rowType);
        StringBuffer line = new StringBuffer();
        String split = ProtocolLoader.loadProtocol(fileMeta.getProtocol()).getRowSplit()
            .getSplit(fileConfig);

        if (RdfFileUtil.isNotBlank(split) && fileMeta.isStartWithSplit(rowType)) {
            line.append(split);
        }

        for (int i = 0; i < columnMetas.size(); i++) {
            try {
                FileColumnMeta columnMeta = columnMetas.get(i);
                FuncContext ctx = new FuncContext();
                ctx.codecType = CodecType.SERIALIZE;
                ctx.field = bmw.getProperty(columnMeta.getName());
                ctx.columnMeta = columnMeta;
                ctx.fileConfig = fileConfig;
                line.append(rd.getOutput().execute(ctx));

                if (RdfFileUtil.isNotBlank(split)
                    && (i < columnMetas.size() - 1 || fileMeta.isEndWithSplit(rowType))) {
                    line.append(split);
                }
            } catch (RdfFileException e) {
                throw new RdfFileException(
                    "rdf-file#RowColumnHorizontalCodec.serialize serialize row=" + bmw.getBean()
                                           + ", fileConfig=" + fileConfig + ", 将数据反序列到对象出错."
                                           + e.getMessage(),
                    e, e.getErrorEnum());
            } catch (Throwable e) {
                throw new RdfFileException(
                    "rdf-file#RowColumnHorizontalCodec.serialize row=" + bmw.getBean()
                                           + ", fileConfig=" + fileConfig + ", 将数据序列到文件出错.",
                    e, RdfErrorEnum.SERIALIZE_ERROR);
            }
        }

        String data = line.toString();

        ProcessExecutor.execute(ProcessorTypeEnum.AFTER_SERIALIZE_ROW, processors, fileConfig,
            new BizData(RdfFileConstants.DATA, data),
            new BizData(RdfFileConstants.ROW_TYPE, rowType));

        return data;
    }

    public static <T> T deserialize(BeanMapWrapper bmw, FileConfig fileConfig, String line,
                                    RowDefinition rd,
                                    Map<ProcessorTypeEnum, List<RdfFileProcessorSpi>> processors,
                                    FileDataTypeEnum rowType) {
        ProcessExecutor.execute(ProcessorTypeEnum.BEFORE_DESERIALIZE_ROW, processors, fileConfig,
            new BizData(RdfFileConstants.DATA, line),
            new BizData(RdfFileConstants.ROW_TYPE, rowType));

        FileMeta fileMeta = TemplateLoader.load(fileConfig);

        boolean startWithSplit = fileMeta.isStartWithSplit(rowType);
        boolean endWithSplit = fileMeta.isEndWithSplit(rowType);

        String[] column = ProtocolLoader.loadProtocol(fileMeta.getProtocol()).getRowSplit()
            .split(new SplitContext(line, fileConfig, rowType));

        List<FileColumnMeta> columnMetas = RowConditionExecutor.deserializeRow(fileConfig, column,
            rowType, line);

        int splitLength = startWithSplit ? columnMetas.size() + 1 : columnMetas.size();
        splitLength = endWithSplit ? splitLength + 1 : splitLength;

        if (column.length != splitLength) {
            throw new RdfFileException(
                "rdf-file#RowColumnHorizontalCodec.deserialize fileConfig=" + fileConfig + ", line="
                                       + line + "模板定义列数=" + column.length + ", 实际列数=" + splitLength,
                RdfErrorEnum.DESERIALIZE_ERROR);
        }

        int statIndex = fileMeta.isStartWithSplit(rowType) ? 1 : 0;
        int endIndex = fileMeta.isEndWithSplit(rowType) ? column.length - 1 : column.length;

        for (int i = statIndex; i < endIndex; i++) {
            try {
                FileColumnMeta columnMeta = columnMetas.get(i - statIndex);
                FuncContext ctx = new FuncContext();
                ctx.codecType = CodecType.DESERIALIZE;
                ctx.field = column[i];
                ctx.columnMeta = columnMeta;
                ctx.fileConfig = fileConfig;
                bmw.setProperty(columnMeta.getName(), rd.getOutput().execute(ctx));
            } catch (RdfFileException e) {
                throw new RdfFileException(
                    "rdf-file#RowColumnHorizontalCodec.deserialize line=" + line + ", fileConfig="
                                           + fileConfig + ", 将数据反序列到对象出错. " + e.getMessage(),
                    e, e.getErrorEnum());
            } catch (Throwable e) {
                throw new RdfFileException("rdf-file#RowColumnHorizontalCodec.deserialize line="
                                           + line + ", fileConfig=" + fileConfig + ", 将数据反序列到对象出错.",
                    e, RdfErrorEnum.DESERIALIZE_ERROR);
            }
        }

        ProcessExecutor.execute(ProcessorTypeEnum.AFTER_DESERIALIZE_ROW, processors, fileConfig,
            new BizData(RdfFileConstants.DATA, bmw),
            new BizData(RdfFileConstants.ROW_TYPE, rowType));

        return (T) bmw.getBean();

    }

}
