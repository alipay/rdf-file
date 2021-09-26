package com.alipay.rdf.file.common;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import com.alipay.rdf.file.exception.RdfErrorEnum;
import com.alipay.rdf.file.exception.RdfFileException;
import com.alipay.rdf.file.loader.ProcessorLoader;
import com.alipay.rdf.file.model.FileConfig;
import com.alipay.rdf.file.model.Summary;
import com.alipay.rdf.file.processor.ProcessorTypeEnum;
import com.alipay.rdf.file.spi.RdfFileProcessorSpi;
import com.alipay.rdf.file.spi.RdfFileWriterSpi;
import com.alipay.rdf.file.util.RdfFileUtil;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 *
 * @author hongwei.quhw
 * @version $Id: RawFileWriter.java, v 0.1 2018年3月12日 下午4:16:24 hongwei.quhw Exp $
 */
public class RawFileWriter implements RdfFileWriterSpi {
    private RdfBufferedWriter                                 writer = null;
    private FileConfig                                        fileConfig;
    private Map<ProcessorTypeEnum, List<RdfFileProcessorSpi>> processors;
    // 该writer是否已经写过数据
    private boolean                                           hasWritten = false;

    @Override
    public void init(FileConfig fileConfig) {
        this.fileConfig = fileConfig;
        processors = ProcessorLoader.loadByType(fileConfig, ProcessorTypeEnum.AFTER_CLOSE_WRITER,
            ProcessorTypeEnum.AFTER_WRITE_BYTES, ProcessorTypeEnum.BEFORE_CREATE_WRITER);
    }

    @Override
    public void writeHead(Object headBean) {
        throw new RdfFileException("rdf-file#RawFileWriter.readRow 不支持操作",
            RdfErrorEnum.UNSUPPORTED_OPERATION);
    }

    @Override
    public void writeRow(Object rowBean) {
        writeLine(rowBean.toString());
    }

    @Override
    public void writeTail(Object tailBean) {
        throw new RdfFileException("rdf-file#RawFileWriter.writeTail 不支持操作",
            RdfErrorEnum.UNSUPPORTED_OPERATION);
    }

    @Override
    public void writeLine(String line) {
        ensureOpen();
        String lineWithLB = RdfFileUtil.processLineBreak(line
                , RdfFileUtil.getLineBreak(fileConfig)
                , fileConfig.isAppendLinebreakAtLast()
                , !hasWritten);
        writer.write(lineWithLB);
        hasWritten = true;
    }

    @Override
    public void close() {
        close(true);
    }

    @Override
    public void close(boolean hasError) {
        if (null == writer) {
            return;
        }

        try {
            writer.close(hasError);
        } finally {
            writer = null;
        }
    }

    @Override
    public Summary getSummary() {
        throw new RdfFileException("rdf-file#RawFileWriter.geSummary 不支持操作",
            RdfErrorEnum.UNSUPPORTED_OPERATION);
    }

    @Override
    public void append(InputStream in) {
        ensureOpen();
        writer.append(in);
    }

    @Override
    public void ensureOpen() {
        if (null == writer) {
            writer = IOFactory.createWriter(fileConfig, processors);
        }
    }

    @Override
    public FileConfig getFileConfig() {
        return fileConfig;
    }
}
