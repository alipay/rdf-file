package com.alipay.rdf.file.codec;

import com.alipay.rdf.file.condition.RowConditionExecutor;
import com.alipay.rdf.file.loader.ExtensionLoader;
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
import com.alipay.rdf.file.spi.RdfFileProcessorSpi;
import com.alipay.rdf.file.spi.RdfFileRowCodecSpi;
import com.alipay.rdf.file.spi.RdfFileRowCodecSpi.RowCodecContext;
import com.alipay.rdf.file.spi.RdfFileRowSplitSpi.SplitContext;
import com.alipay.rdf.file.util.BeanMapWrapper;
import com.alipay.rdf.file.util.RdfFileConstants;
import com.alipay.rdf.file.util.RdfFileUtil;

import java.util.List;
import java.util.Map;

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
        // 条件模板处理
        List<FileColumnMeta> columnMetas = RowConditionExecutor.serializeRow(fileConfig, bmw, rowType);

        RowCodecContext ctx = new RowCodecContext(bmw, fileConfig, columnMetas, rd);
        RdfFileRowCodecSpi rowCodec = ExtensionLoader.getExtensionLoader(RdfFileRowCodecSpi.class).getExtension(RdfFileUtil.getRowCodecMode(fileConfig));
        // 字段编码
        String line = rowCodec.serialize(ctx);
        // 行编码后置处理
        line = rowCodec.postSerialize(line, ctx);
        // 行整体格式化处理
        line = RowFormatCodec.serialize(fileConfig, line, rowType);

        ProcessExecutor.execute(ProcessorTypeEnum.AFTER_SERIALIZE_ROW, processors, fileConfig,
                new BizData(RdfFileConstants.DATA, line),
                new BizData(RdfFileConstants.ROW_TYPE, rowType));

        return line;
    }

    public static <T> T deserialize(BeanMapWrapper bmw, FileConfig fileConfig, String line,
                                    RowDefinition rd,
                                    Map<ProcessorTypeEnum, List<RdfFileProcessorSpi>> processors,
                                    FileDataTypeEnum rowType) {
        ProcessExecutor.execute(ProcessorTypeEnum.BEFORE_DESERIALIZE_ROW, processors, fileConfig,
                new BizData(RdfFileConstants.DATA, line),
                new BizData(RdfFileConstants.ROW_TYPE, rowType));
        // 行格式化处理
        line = RowFormatCodec.deserialize(fileConfig, line, rowType);

        FileMeta fileMeta = TemplateLoader.load(fileConfig);
        RowCodecContext ctx = new RowCodecContext(bmw, fileConfig, null, rd, null);
        RdfFileRowCodecSpi rowCodec = ExtensionLoader.getExtensionLoader(RdfFileRowCodecSpi.class).getExtension(RdfFileUtil.getRowCodecMode(fileConfig));
        // 行前置处理
        line =rowCodec.preDeserialize(line, ctx);

        String[] columnValues = ProtocolLoader.loadProtocol(fileMeta.getProtocol()).getRowSplit().split(new SplitContext(line, fileConfig, rowType));
        // 条件模板处理
        List<FileColumnMeta> columnMetas = RowConditionExecutor.deserializeRow(fileConfig, columnValues, rowType, line);
        ctx.columnMetas = columnMetas;
        ctx.columnValues = columnValues;
        // 行字段解析
        rowCodec.deserialize(line, ctx);

        ProcessExecutor.execute(ProcessorTypeEnum.AFTER_DESERIALIZE_ROW, processors, fileConfig,
                new BizData(RdfFileConstants.DATA, bmw),
                new BizData(RdfFileConstants.ROW_TYPE, rowType));

        return (T) bmw.getBean();

    }

}
