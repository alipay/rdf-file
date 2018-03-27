package com.alipay.rdf.file.codec;

import java.util.List;
import java.util.Map;

import com.alipay.rdf.file.interfaces.FileReader;
import com.alipay.rdf.file.interfaces.FileWriter;
import com.alipay.rdf.file.model.FileConfig;
import com.alipay.rdf.file.processor.ProcessorTypeEnum;
import com.alipay.rdf.file.spi.RdfFileProcessorSpi;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 *
 * @author hongwei.quhw
 * @version $Id: FileCodec.java, v 0.1 2018年3月12日 下午4:13:38 hongwei.quhw Exp $
 */
public interface FileCodec {
    /**
     * 序列化一行数据到文件
     */
    void serialize(Object bean, FileConfig config, FileWriter writer,
                   Map<ProcessorTypeEnum, List<RdfFileProcessorSpi>> processors);

    /**
     * 从文件反序列化一条数据 
     */
    <T> T deserialize(Class<?> clazz, FileConfig config, FileReader reader,
                      Map<ProcessorTypeEnum, List<RdfFileProcessorSpi>> processors);
}
