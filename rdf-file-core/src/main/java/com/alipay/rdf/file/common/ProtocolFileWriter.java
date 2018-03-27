package com.alipay.rdf.file.common;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import com.alipay.rdf.file.codec.BodyCodec;
import com.alipay.rdf.file.codec.HeaderCodec;
import com.alipay.rdf.file.codec.TailCodec;
import com.alipay.rdf.file.exception.RdfErrorEnum;
import com.alipay.rdf.file.exception.RdfFileException;
import com.alipay.rdf.file.loader.ProcessorLoader;
import com.alipay.rdf.file.loader.ProtocolLoader;
import com.alipay.rdf.file.loader.SummaryLoader;
import com.alipay.rdf.file.loader.TemplateLoader;
import com.alipay.rdf.file.meta.FileMeta;
import com.alipay.rdf.file.model.FileConfig;
import com.alipay.rdf.file.model.Summary;
import com.alipay.rdf.file.processor.ProcessExecutor;
import com.alipay.rdf.file.processor.ProcessExecutor.BizData;
import com.alipay.rdf.file.processor.ProcessorTypeEnum;
import com.alipay.rdf.file.spi.RdfFileProcessorSpi;
import com.alipay.rdf.file.spi.RdfFileWriterSpi;
import com.alipay.rdf.file.util.RdfFileLogUtil;
import com.alipay.rdf.file.util.RdfFileUtil;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 * 
 * @author hongwei.quhw
 * @version $Id: CommonFileWriter.java, v 0.1 2017年4月7日 下午2:16:59 hongwei.quhw Exp $
 */
public class ProtocolFileWriter implements RdfFileWriterSpi {
    private FileConfig                                        fileConfig;
    private FileMeta                                          fileMeta;
    private Map<ProcessorTypeEnum, List<RdfFileProcessorSpi>> processors;
    private RdfBufferedWriter                                 writer = null;
    private Summary                                           summary;

    @Override
    public void init(FileConfig fileConfig) {
        String templatePath = RdfFileUtil.assertTrimNotBlank(fileConfig.getTemplatePath());
        this.fileConfig = fileConfig;
        this.fileMeta = TemplateLoader.load(templatePath, fileConfig.getTemplateEncoding());

        if (fileConfig.isSummaryEnable()) {
            summary = SummaryLoader.getNewSummary(fileMeta);
            fileConfig.addProcessorKey("summary");
        }

        ProtocolLoader.loadProtocol(fileMeta.getProtocol());
        processors = ProcessorLoader.loadByType(fileConfig, ProcessorTypeEnum.AFTER_WRITE_HEAD,
            ProcessorTypeEnum.BEFORE_WRITE_ROW, ProcessorTypeEnum.AFTER_WRITE_ROW,
            ProcessorTypeEnum.AFTER_WRITE_TAIL, ProcessorTypeEnum.AFTER_CLOSE_WRITER,
            ProcessorTypeEnum.AFTER_WRITE_BYTES, ProcessorTypeEnum.AFTER_SERIALIZE_ROW,
            ProcessorTypeEnum.BEFORE_SERIALIZE_ROW, ProcessorTypeEnum.BEFORE_CREATE_WRITER);

        if (RdfFileLogUtil.common.isInfo()) {
            RdfFileLogUtil.common.info("rdf-file#ProtocolFileWriter(fileConfig=" + fileConfig
                                       + ")  processors=" + processors);
        }
    }

    /** 
     * @see hongwei.quhw.file.interfaces.FileWriter#writeHead(java.lang.Object)
     */
    @Override
    public void writeHead(Object headBean) {
        ensureOpen();
        HeaderCodec.instance.serialize(headBean, fileConfig, this, processors);

        ProcessExecutor.execute(ProcessorTypeEnum.AFTER_WRITE_HEAD, processors, fileConfig,
            new BizData("summary", summary), new BizData("data", headBean));
    }

    /** 
     * @see hongwei.quhw.file.interfaces.FileWriter#writeRow(java.lang.Object)
     */
    @Override
    public void writeRow(Object rowBean) {
        ProcessExecutor.execute(ProcessorTypeEnum.BEFORE_WRITE_ROW, processors, fileConfig);

        ensureOpen();

        BodyCodec.instance.serialize(rowBean, fileConfig, this, processors);

        ProcessExecutor.execute(ProcessorTypeEnum.AFTER_WRITE_ROW, processors, fileConfig,
            new BizData("summary", summary), new BizData("data", rowBean));
    }

    /** 
     * @see hongwei.quhw.file.interfaces.FileWriter#writeTail(java.lang.Object)
     */
    @Override
    public void writeTail(Object tailBean) {
        ensureOpen();
        TailCodec.instance.serialize(tailBean, fileConfig, this, processors);

        ProcessExecutor.execute(ProcessorTypeEnum.AFTER_WRITE_TAIL, processors, fileConfig,
            new BizData("summary", summary), new BizData("data", tailBean));
    }

    /** 
     * @see hongwei.quhw.file.interfaces.FileWriter#close()
     */
    @Override
    public void close() {
        if (null == writer) {
            return;
        }

        try {
            writer.close();
        } finally {
            writer = null;
        }
    }

    @Override
    public void ensureOpen() {
        if (null == writer) {
            writer = IOFactory.createWriter(fileConfig, processors);
        }
    }

    public void writeLine(String line) {
        RdfFileUtil.assertNotNull(line, "ProtocolFileWriter.writeLine(line == null)",
            RdfErrorEnum.ILLEGAL_ARGUMENT);
        ensureOpen();
        writer.write(line + RdfFileUtil.getLineBreak(fileConfig));
    }

    @Override
    public Summary geSummary() {
        if (fileConfig.isSummaryEnable()) {
            return summary;
        } else {
            throw new RdfFileException(
                "rdf-file#ProtocolFileWriter.geSummary() 请入参指定FileConfig.setSummaryEnable(true)来进行汇总参数收集",
                RdfErrorEnum.SUMMARY_DISNABLE);
        }
    }

    @Override
    public void append(InputStream in) {
        RdfFileUtil.assertNotNull(in, "ProtocolFileWriter.append(inputsream == null) ",
            RdfErrorEnum.ILLEGAL_ARGUMENT);
        writer.append(in);
    }

    @Override
    public FileConfig getFileConfig() {
        return fileConfig;
    }
}
