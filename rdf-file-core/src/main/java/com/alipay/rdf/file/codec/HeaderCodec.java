package com.alipay.rdf.file.codec;

import java.util.List;
import java.util.Map;

import com.alipay.rdf.file.exception.RdfErrorEnum;
import com.alipay.rdf.file.exception.RdfFileException;
import com.alipay.rdf.file.interfaces.FileReader;
import com.alipay.rdf.file.interfaces.FileWriter;
import com.alipay.rdf.file.loader.ProtocolLoader;
import com.alipay.rdf.file.loader.TemplateLoader;
import com.alipay.rdf.file.meta.FileColumnMeta;
import com.alipay.rdf.file.meta.FileMeta;
import com.alipay.rdf.file.model.FileConfig;
import com.alipay.rdf.file.model.FileDataTypeEnum;
import com.alipay.rdf.file.processor.ProcessorTypeEnum;
import com.alipay.rdf.file.protocol.ProtocolDefinition;
import com.alipay.rdf.file.protocol.RowDefinition;
import com.alipay.rdf.file.spi.RdfFileProcessorSpi;
import com.alipay.rdf.file.util.RdfFileUtil;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 *
 * @author hongwei.quhw
 * @version $Id: HeaderCodec.java, v 0.1 2018年3月12日 下午4:13:53 hongwei.quhw Exp $
 */
public class HeaderCodec implements FileCodec {
    public static HeaderCodec instance = new HeaderCodec();

    @Override
    public void serialize(Object bean, FileConfig config, FileWriter writer,
                          Map<ProcessorTypeEnum, List<RdfFileProcessorSpi>> processors) {
        FileMeta fileMeta = TemplateLoader.load(config);
        List<FileColumnMeta> columnMetas = fileMeta.getHeadColumns();

        if (columnMetas.size() == 0) {
            throw new RdfFileException("rdf-file#HeaderCodec.serialize 模板tempaltePath="
                                       + config.getTemplatePath() + " 中没有配置头，不支持此操作",
                RdfErrorEnum.HEAD_NOT_DEFINED);
        }

        ProtocolDefinition pd = ProtocolLoader.loadProtocol(fileMeta.getProtocol());
        List<RowDefinition> rds = pd.getHeads();

        if (rds.size() == 0) {
            throw new RdfFileException(
                "rdf-file#HeaderCodec.serialize协议protocol=" + pd.getName() + " 中没有定义头",
                RdfErrorEnum.HEAD_NOT_DEFINED);
        }

        RowsCodec.serialize(bean, config, writer, processors, FileDataTypeEnum.HEAD);
    }

    @Override
    public <T> T deserialize(Class<?> clazz, FileConfig config, FileReader reader,
                             Map<ProcessorTypeEnum, List<RdfFileProcessorSpi>> processors) {
        FileMeta fileMeta = TemplateLoader.load(config);
        List<FileColumnMeta> columnMetas = fileMeta.getHeadColumns();

        if (columnMetas.size() == 0) {
            return null;
        }

        ProtocolDefinition pd = ProtocolLoader.loadProtocol(fileMeta.getProtocol());
        List<RowDefinition> rds = pd.getHeads();

        if (rds.size() == 0) {
            return null;
        }

        T t = RowsCodec.deserialize(clazz, config, reader, processors, FileDataTypeEnum.HEAD);

        RdfFileUtil.assertNotNull(t, "文件读取头部，格式错误");

        return t;
    }

}
