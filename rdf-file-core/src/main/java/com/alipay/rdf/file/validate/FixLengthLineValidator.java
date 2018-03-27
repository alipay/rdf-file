package com.alipay.rdf.file.validate;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import com.alipay.rdf.file.exception.RdfErrorEnum;
import com.alipay.rdf.file.exception.RdfFileException;
import com.alipay.rdf.file.loader.TemplateLoader;
import com.alipay.rdf.file.model.FileConfig;
import com.alipay.rdf.file.model.FileDataTypeEnum;
import com.alipay.rdf.file.processor.ProcessCotnext;
import com.alipay.rdf.file.processor.ProcessorTypeEnum;
import com.alipay.rdf.file.spi.RdfFileProcessorSpi;
import com.alipay.rdf.file.util.RdfFileConstants;
import com.alipay.rdf.file.util.RdfFileUtil;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 * 
 * 对于定长协议格式，进行数据的长度校验
 * 
 * @author hongwei.quhw
 * @version $Id: FixLengthLineValidator.java, v 0.1 2017年8月5日 下午11:48:06 hongwei.quhw Exp $
 */
public class FixLengthLineValidator implements RdfFileProcessorSpi {

    @Override
    public List<ProcessorTypeEnum> supportedTypes() {
        List<ProcessorTypeEnum> types = new ArrayList<ProcessorTypeEnum>();
        types.add(ProcessorTypeEnum.AFTER_SERIALIZE_ROW);
        types.add(ProcessorTypeEnum.BEFORE_DESERIALIZE_ROW);
        return types;
    }

    @Override
    public void process(ProcessCotnext pc) {
        if (!FileDataTypeEnum.BODY.equals(pc.getBizData(RdfFileConstants.ROW_TYPE))) {
            return;
        }

        FileConfig fileConfig = pc.getFileConfig();
        String line = (String) pc.getBizData(RdfFileConstants.DATA);

        int lineLength = 0;

        try {
            lineLength = line.getBytes(RdfFileUtil.getFileEncoding(fileConfig)).length;
        } catch (UnsupportedEncodingException e) {
            throw new RdfFileException("rdf-file#FixLengthLineValidator line=" + line
                                       + ", fileEncoding=" + RdfFileUtil.getFileEncoding(fileConfig),
                e, RdfErrorEnum.ENCODING_ERROR);
        }

        int rowDefinedLength = TemplateLoader.getRowLength(fileConfig);

        if (lineLength != rowDefinedLength) {
            throw new RdfFileException(
                "rdf-file#FixLengthLineValidator line = " + line + ", 定义row的长度=" + rowDefinedLength
                                       + ", 实际row的长度" + lineLength + ", fileConfig=" + fileConfig,
                RdfErrorEnum.VALIDATE_ERROR);
        }
    }

    @Override
    public int getOrder() {
        return 0;
    }

}
