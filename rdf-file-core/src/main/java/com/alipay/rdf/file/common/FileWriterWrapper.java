package com.alipay.rdf.file.common;

import java.io.InputStream;

import com.alipay.rdf.file.exception.RdfErrorEnum;
import com.alipay.rdf.file.exception.RdfFileException;
import com.alipay.rdf.file.model.FileConfig;
import com.alipay.rdf.file.model.Summary;
import com.alipay.rdf.file.spi.RdfFileWriterSpi;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 *
 * 写文件包装器
 *
 * @author hongwei.quhw
 * @version $Id: FileWriterWrapper.java, v 0.1 2018年10月23日 下午5:35:41 hongwei.quhw Exp $
 */
public class FileWriterWrapper implements RdfFileWriterSpi {
    private final RdfFileWriterSpi writer;

    /**文件写过程中出错了*/
    private boolean                hasError;

    public FileWriterWrapper(RdfFileWriterSpi writer) {
        this.writer = writer;
    }

    @Override
    public void writeHead(Object headBean) {
        try {
            writer.writeHead(headBean);
        } catch (RuntimeException e) {
            hasError = true;
            throw e;
        } catch (Exception e) {
            throw new RdfFileException("rdf-file#FileWriterWrapper writeHead error filePath=["
                                       + getFileConfig().getFilePath() + "], head=[" + headBean
                                       + "]",
                e, RdfErrorEnum.UNKOWN);
        }
    }

    @Override
    public void writeRow(Object rowBean) {
        try {
            writer.writeRow(rowBean);
        } catch (RuntimeException e) {
            hasError = true;
            throw e;
        } catch (Exception e) {
            throw new RdfFileException("rdf-file#FileWriterWrapper writeRow error filePath=["
                                       + getFileConfig().getFilePath() + "], row=[" + rowBean + "]",
                e, RdfErrorEnum.UNKOWN);
        }
    }

    @Override
    public void writeTail(Object tailBean) {
        try {
            writer.writeTail(tailBean);
        } catch (RuntimeException e) {
            hasError = true;
            throw e;
        } catch (Exception e) {
            throw new RdfFileException("rdf-file#FileWriterWrapper writeTail error filePath=["
                                       + getFileConfig().getFilePath() + "], tail=[" + tailBean
                                       + "]",
                e, RdfErrorEnum.UNKOWN);
        }
    }

    @Override
    public void writeLine(String line) {
        try {
            writer.writeLine(line);
        } catch (RuntimeException e) {
            hasError = true;
            throw e;
        } catch (Exception e) {
            throw new RdfFileException("rdf-file#FileWriterWrapper writeLine error filePath=["
                                       + getFileConfig().getFilePath() + "], line=[" + line + "]",
                e, RdfErrorEnum.UNKOWN);
        }
    }

    @Override
    public Summary getSummary() {
        return writer.getSummary();
    }

    @Override
    public void close() {
        close(hasError);
    }

    @Override
    public void close(boolean hasError) {
        writer.close(hasError);
    }

    @Override
    public void init(FileConfig config) {
        writer.init(config);
    }

    @Override
    public void append(InputStream in) {
        try {
            writer.append(in);
        } catch (RuntimeException e) {
            hasError = true;
            throw e;
        } catch (Exception e) {
            throw new RdfFileException("rdf-file#FileWriterWrapper append error filePath=["
                                       + getFileConfig().getFilePath() + "]",
                e, RdfErrorEnum.UNKOWN);
        }
    }

    @Override
    public void ensureOpen() {
        try {
            writer.ensureOpen();
        } catch (RuntimeException e) {
            hasError = true;
            throw e;
        } catch (Exception e) {
            throw new RdfFileException("rdf-file#FileWriterWrapper ensureOpen error filePath=["
                                       + getFileConfig().getFilePath() + "]",
                e, RdfErrorEnum.UNKOWN);
        }
    }

    @Override
    public FileConfig getFileConfig() {
        return writer.getFileConfig();
    }

}
