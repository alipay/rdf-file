package com.alipay.rdf.file.common;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.alipay.rdf.file.exception.RdfErrorEnum;
import com.alipay.rdf.file.exception.RdfFileException;
import com.alipay.rdf.file.loader.ProcessorLoader;
import com.alipay.rdf.file.loader.TemplateLoader;
import com.alipay.rdf.file.model.FileConfig;
import com.alipay.rdf.file.model.Summary;
import com.alipay.rdf.file.processor.ProcessorTypeEnum;
import com.alipay.rdf.file.spi.RdfFileProcessorSpi;
import com.alipay.rdf.file.spi.RdfFileReaderSpi;
import com.alipay.rdf.file.util.RdfFileLogUtil;
import com.alipay.rdf.file.util.RdfFileUtil;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 * 
 * 原生文件读取
 * 
 * @author hongwei.quhw
 * @version $Id: RawFileReader.java, v 0.1 2017年8月24日 下午5:31:18 hongwei.quhw Exp $
 */
public class RawFileReader implements RdfFileReaderSpi {
    private FileConfig                                        fileConfig;
    private RdfBufferedReader                                 reader;
    private Map<ProcessorTypeEnum, List<RdfFileProcessorSpi>> processors;

    @Override
    public void init(FileConfig fileConfig) {
        this.fileConfig = fileConfig;
        processors = ProcessorLoader.loadByType(fileConfig, ProcessorTypeEnum.AFTER_CLOSE_READER,
            ProcessorTypeEnum.AFTER_READ_BYTES);
    }

    @Override
    public <T> T readHead(Class<?> requiredType) {
        throw new RdfFileException("rdf-file#RawFileWriter.readHead 不支持操作",
            RdfErrorEnum.UNSUPPORTED_OPERATION);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T readRow(Class<?> requiredType) {
        if (!requiredType.getName().equals(String[].class.getName())) {
            throw new RdfFileException(
                "rdf-file#RawFileReader.readRow 只支持返回String[] 实际requiredType="
                                       + requiredType.getName(),
                RdfErrorEnum.ILLEGAL_ARGUMENT);
        }

        RdfFileUtil.assertNotNull(RdfFileUtil.getRowSplit(fileConfig),
            "rdf-file#RawFileWriter.readRow 没有配置分隔符 不支持操作， fileConfig=" + fileConfig,
            RdfErrorEnum.UNSUPPORTED_OPERATION);

        String line = readLine();
        if (RdfFileUtil.isBlank(line)) {
            return null;
        } else {
            return (T) RdfFileUtil.split(line, RdfFileUtil.getRowSplit(fileConfig));
        }
    }

    @Override
    public <T> T readTail(Class<?> requiredType) {
        throw new RdfFileException("rdf-file#RawFileWriter.readTail 不支持操作",
            RdfErrorEnum.UNSUPPORTED_OPERATION);
    }

    @Override
    public String readBodyLine() {
        throw new RdfFileException("rdf-file#RawFileWriter.readBodyLine 不支持操作",
            RdfErrorEnum.UNSUPPORTED_OPERATION);
    }

    @Override
    public String readLine() {
        ensureOpen();
        try {
            return reader.readLine();
        } catch (IOException e) {
            throw new RdfFileException("rdf-file#RawFileWriter.writereadLineLine error", e,
                RdfErrorEnum.IO_ERROR);
        }
    }

    @Override
    public Summary getSummary() {
        return null;
    }

    @Override
    public void close() {
        if (null != reader) {
            try {
                reader.close();
            } catch (IOException e) {
                if (RdfFileLogUtil.common.isWarn()) {
                    RdfFileLogUtil.common.warn("rdf-file#RawFileWriter.close() error", e);
                }
            }
        }

        reader = null;
    }

    private void ensureOpen() {
        if (null == reader) {
            reader = IOFactory.createReader(fileConfig, processors);
        }
    }

    @Override
    public FileConfig getFileConfig() {
        return fileConfig;
    }
}
