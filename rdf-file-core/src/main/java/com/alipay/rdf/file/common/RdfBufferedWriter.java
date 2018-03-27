package com.alipay.rdf.file.common;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.alipay.rdf.file.exception.RdfErrorEnum;
import com.alipay.rdf.file.exception.RdfFileException;
import com.alipay.rdf.file.model.FileConfig;
import com.alipay.rdf.file.processor.ProcessExecutor;
import com.alipay.rdf.file.processor.ProcessExecutor.BizData;
import com.alipay.rdf.file.processor.ProcessorTypeEnum;
import com.alipay.rdf.file.spi.RdfFileProcessorSpi;
import com.alipay.rdf.file.util.RdfFileLogUtil;
import com.alipay.rdf.file.util.RdfFileUtil;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 * 
 * @author hongwei.quhw
 * @version $Id: RdfBufferedWriter.java, v 0.1 2017年8月16日 上午11:37:28 hongwei.quhw Exp $
 */
class RdfBufferedWriter {
    /** 扩展写buffer大小 */
    private static final int                                  BUFFER_SIZE = 1024 * 64; // 64K

    private BufferedOutputStream                              outStream;
    private FileConfig                                        fileConfig;
    private Map<ProcessorTypeEnum, List<RdfFileProcessorSpi>> processors;

    public RdfBufferedWriter(FileConfig fileConfig,
                             Map<ProcessorTypeEnum, List<RdfFileProcessorSpi>> processors) {
        this.processors = processors;
        this.fileConfig = fileConfig;
        FileConfig createConfig = fileConfig.clone();
        ProcessExecutor.execute(ProcessorTypeEnum.BEFORE_CREATE_WRITER, processors, createConfig);
        File parent = new File(createConfig.getFilePath()).getParentFile();
        if (!parent.exists() && parent.mkdirs() && !parent.exists()) {
            throw new RdfFileException(
                "rdf-fle#RdfBufferedWriter 创建目录失败 path=" + parent.getAbsolutePath(),
                RdfErrorEnum.IO_ERROR);
        }

        try {

            FileOutputStream outputStream = new FileOutputStream(
                new File(createConfig.getFilePath()), createConfig.isAppend());
            this.outStream = new BufferedOutputStream(outputStream);
        } catch (FileNotFoundException e) {
            throw new RdfFileException(
                "rdf-file#RdfBufferedWriter(fileConfig=" + createConfig + ") 文件不存在", e,
                RdfErrorEnum.NOT_EXSIT);
        }
    }

    /**
     *  写入数据
     * 
     * @param line
     */
    public void write(String line) {
        RdfFileUtil.assertNotNull(line, "rdf-file#RdfBufferedWriter.write(line= null)",
            RdfErrorEnum.ILLEGAL_ARGUMENT);

        try {
            byte[] bs = line.getBytes(RdfFileUtil.getFileEncoding(fileConfig));
            outStream.write(bs);

            ProcessExecutor.execute(ProcessorTypeEnum.AFTER_WRITE_BYTES, processors, fileConfig,
                new BizData("inputByte", bs));
        } catch (UnsupportedEncodingException e) {
            throw new RdfFileException("rdf-file#RdfBufferedWriter.write(line=" + line + ") 编码问题",
                e, RdfErrorEnum.ENCODING_ERROR);
        } catch (IOException e) {
            throw new RdfFileException("rdf-file#RdfBufferedWriter.write(line=" + line + ") IO问题",
                e, RdfErrorEnum.IO_ERROR);
        }

    }

    /**
     * 关闭流
     */
    public void close() {
        try {
            outStream.close();
        } catch (IOException e) {
            if (RdfFileLogUtil.common.isWarn()) {
                RdfFileLogUtil.common
                    .warn("RdfBufferedWriter.close filePath=" + fileConfig.getFileEncoding(), e);
            }
        }

        ProcessExecutor.execute(ProcessorTypeEnum.AFTER_CLOSE_WRITER, processors, fileConfig);
    }

    /**
     * 输入流写入文件尾
     * 
     * @param in
     */
    public void append(InputStream in) {
        byte[] buffer = new byte[BUFFER_SIZE];
        int len;
        try {
            while ((len = in.read(buffer)) != -1) {
                outStream.write(buffer, 0, len);
                byte[] buf;
                if (len == BUFFER_SIZE) {
                    buf = buffer;
                } else {
                    buf = Arrays.copyOf(buffer, len);
                }

                ProcessExecutor.execute(ProcessorTypeEnum.AFTER_WRITE_BYTES, processors, fileConfig,
                    new BizData("inputByte", buf));
            }
        } catch (IOException e) {
            throw new RdfFileException("rdf-file#RdfBufferedWriter.append stream error", e,
                RdfErrorEnum.IO_ERROR);
        }
    }
}
