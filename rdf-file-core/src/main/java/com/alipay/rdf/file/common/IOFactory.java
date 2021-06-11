package com.alipay.rdf.file.common;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import com.alipay.rdf.file.exception.RdfErrorEnum;
import com.alipay.rdf.file.exception.RdfFileException;
import com.alipay.rdf.file.interfaces.FileFactory;
import com.alipay.rdf.file.model.FileConfig;
import com.alipay.rdf.file.processor.ProcessorTypeEnum;
import com.alipay.rdf.file.spi.RdfFileProcessorSpi;
import com.alipay.rdf.file.storage.FileInnterStorage;
import com.alipay.rdf.file.util.RdfFileUtil;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 *
 * IO工厂类
 *
 * @author hongwei.quhw
 * @version $Id: IOFactory.java, v 0.1 2017年8月10日 上午10:51:15 hongwei.quhw Exp $
 */
public class IOFactory {

    public static RdfBufferedReader createReader(FileConfig fileConfig,
                                                 Map<ProcessorTypeEnum, List<RdfFileProcessorSpi>> processors) {
        InputStream is = fileConfig.getInputStream();

        if (null == is) {
            FileInnterStorage fileStorage = (FileInnterStorage) FileFactory
                .createStorage(fileConfig.getStorageConfig());

            if (fileConfig.isPartial()) {
                is = fileStorage.getInputStream(fileConfig.getFilePath(), fileConfig.getOffset(),
                    fileConfig.getLength());
            } else {
                is = fileStorage.getInputStream(fileConfig.getFilePath());
            }

        }

        try {
            return new RdfBufferedReader(is, fileConfig, processors);
        } catch (UnsupportedEncodingException e) {
            throw new RdfFileException("rdf-file#IOFactory.createReader(fileConfig=" + fileConfig
                                       + ", processors=" + processors + ") 异常",
                e, RdfErrorEnum.ENCODING_ERROR);
        }
    }

    public static RdfBufferedReader createTailReader(FileConfig fileConfig,
                                                     Map<ProcessorTypeEnum, List<RdfFileProcessorSpi>> processors) {
        InputStream is = fileConfig.getInputStream();

        if (null == is) {
            FileInnterStorage fileStorage = (FileInnterStorage) FileFactory
                .createStorage(fileConfig.getStorageConfig());
            is = fileStorage.getTailInputStream(fileConfig);

        }

        try {
            return new RdfBufferedReader(is, fileConfig, processors);
        } catch (UnsupportedEncodingException e) {
            throw new RdfFileException("rdf-file#IOFactory.createTailReader(fileConfig="
                                       + fileConfig + ", processors=" + processors + ") 异常",
                e, RdfErrorEnum.ENCODING_ERROR);
        }
    }

    public static RdfBufferedWriter createWriter(FileConfig fileConfig,
                                                 Map<ProcessorTypeEnum, List<RdfFileProcessorSpi>> processors) {
        return new RdfBufferedWriter(fileConfig, processors);
    }
}
