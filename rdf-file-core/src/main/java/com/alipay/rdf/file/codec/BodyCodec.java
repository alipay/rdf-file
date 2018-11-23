package com.alipay.rdf.file.codec;

import java.util.List;
import java.util.Map;

import com.alipay.rdf.file.common.ProtocolFileReader;
import com.alipay.rdf.file.common.ProtocolFileWriter;
import com.alipay.rdf.file.exception.RdfErrorEnum;
import com.alipay.rdf.file.exception.RdfFileException;
import com.alipay.rdf.file.interfaces.FileReader;
import com.alipay.rdf.file.interfaces.FileWriter;
import com.alipay.rdf.file.loader.ProtocolLoader;
import com.alipay.rdf.file.loader.TemplateLoader;
import com.alipay.rdf.file.meta.FileBodyMeta;
import com.alipay.rdf.file.meta.FileMeta;
import com.alipay.rdf.file.model.FileConfig;
import com.alipay.rdf.file.model.FileDataTypeEnum;
import com.alipay.rdf.file.processor.ProcessorTypeEnum;
import com.alipay.rdf.file.protocol.ProtocolDefinition;
import com.alipay.rdf.file.protocol.RowDefinition;
import com.alipay.rdf.file.spi.RdfFileProcessorSpi;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 *  
 * @author hongwei.quhw
 * @version $Id: BodyCodec.java, v 0.1 2017年4月10日 下午7:27:45 hongwei.quhw Exp $
 */
public class BodyCodec implements FileCodec {
    public static BodyCodec instance = new BodyCodec();

    /** 
     * @see hongwei.quhw.file.codec.FileCodec#serialize(Object, ProtocolFileWriter.quhw.file.common.CommonFileWriter)
     */
    @Override
    public void serialize(Object bean, FileConfig config, FileWriter writer,
                          Map<ProcessorTypeEnum, List<RdfFileProcessorSpi>> processors) {
        FileMeta fileMeta = TemplateLoader.load(config);
        List<FileBodyMeta> columnMetas = fileMeta.getBodyMetas();

        if (columnMetas.size() == 0) {
            throw new RdfFileException("rdf-file#BodyCodec.deserialize 数据模板templatePath="
                                       + config.getTemplatePath() + " 中没有配置文件体，不支持此操作",
                RdfErrorEnum.BODY_NOT_DEFINED);
        }

        ProtocolDefinition pd = ProtocolLoader.loadProtocol(fileMeta.getProtocol());
        List<RowDefinition> rds = pd.getBodys();

        if (rds.size() == 0) {
            throw new RdfFileException(
                "rdf-file#BodyCodec.deserialize 协议模板protocol=" + pd.getName() + " 中没有配置文件体，不支持此操作",
                RdfErrorEnum.BODY_NOT_DEFINED);
        }

        RowsCodec.serialize(bean, config, writer, processors, FileDataTypeEnum.BODY);
    }

    /** 
     * @see hongwei.quhw.file.codec.FileCodec#deserialize(ProtocolFileReader.quhw.file.common.CommonFileReader)
     */
    @Override
    public <T> T deserialize(Class<?> clazz, FileConfig config, FileReader reader,
                             Map<ProcessorTypeEnum, List<RdfFileProcessorSpi>> processors) {
        FileMeta fileMeta = TemplateLoader.load(config);
        List<FileBodyMeta> columnMetas = fileMeta.getBodyMetas();

        if (columnMetas.size() == 0) {
            throw new RdfFileException("rdf-file#BodyCodec.deserialize 数据模板templatePath="
                                       + config.getTemplatePath() + " 中没有配置文件体，不支持此操作",
                RdfErrorEnum.BODY_NOT_DEFINED);
        }

        ProtocolDefinition pd = ProtocolLoader.loadProtocol(fileMeta.getProtocol());
        List<RowDefinition> rds = pd.getBodys();

        if (rds.size() == 0) {
            throw new RdfFileException(
                "rdf-file#BodyCodec.deserialize 协议模板protocol=" + pd.getName() + " 中没有配置文件体，不支持此操作",
                RdfErrorEnum.BODY_NOT_DEFINED);
        }

        return RowsCodec.deserialize(clazz, config, reader, processors, FileDataTypeEnum.BODY);
    }

}
