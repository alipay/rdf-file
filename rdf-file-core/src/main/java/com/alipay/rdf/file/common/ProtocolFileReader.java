package com.alipay.rdf.file.common;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.alipay.rdf.file.codec.BodyCodec;
import com.alipay.rdf.file.codec.HeaderCodec;
import com.alipay.rdf.file.codec.TailCodec;
import com.alipay.rdf.file.exception.RdfErrorEnum;
import com.alipay.rdf.file.exception.RdfFileException;
import com.alipay.rdf.file.interfaces.FileCoreProcessorConstants;
import com.alipay.rdf.file.interfaces.FileFactory;
import com.alipay.rdf.file.interfaces.FileSplitter;
import com.alipay.rdf.file.loader.ProcessorLoader;
import com.alipay.rdf.file.loader.ProtocolLoader;
import com.alipay.rdf.file.loader.SummaryLoader;
import com.alipay.rdf.file.loader.TemplateLoader;
import com.alipay.rdf.file.meta.FileColumnMeta;
import com.alipay.rdf.file.meta.FileMeta;
import com.alipay.rdf.file.model.FileConfig;
import com.alipay.rdf.file.model.FileDataTypeEnum;
import com.alipay.rdf.file.model.FileSlice;
import com.alipay.rdf.file.model.Summary;
import com.alipay.rdf.file.processor.ProcessExecutor;
import com.alipay.rdf.file.processor.ProcessExecutor.BizData;
import com.alipay.rdf.file.processor.ProcessorTypeEnum;
import com.alipay.rdf.file.spi.RdfFileProcessorSpi;
import com.alipay.rdf.file.spi.RdfFileReaderSpi;
import com.alipay.rdf.file.util.BeanMapWrapper;
import com.alipay.rdf.file.util.RdfFileConstants;
import com.alipay.rdf.file.util.RdfFileLogUtil;
import com.alipay.rdf.file.util.RdfFileUtil;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 * 
 * @author hongwei.quhw
 * @version $Id: CommonFileReader.java, v 0.1 2017年4月7日 下午2:04:21 hongwei.quhw Exp $
 */
@SuppressWarnings("unchecked")
public class ProtocolFileReader implements RdfFileReaderSpi {
    private FileMeta                                          fileMeta;
    private FileConfig                                        fileConfig;
    private FileConfig                                        bodyConfig;
    private RdfBufferedReader                                 reader;
    private Object                                            headCache;
    private Object                                            tailCache;
    private Map<ProcessorTypeEnum, List<RdfFileProcessorSpi>> processors;
    private Summary                                           summary;

    @Override
    public void init(FileConfig fileConfig) {
        String templatePath = RdfFileUtil.assertTrimNotBlank(fileConfig.getTemplatePath());
        this.fileMeta = TemplateLoader.load(templatePath, fileConfig.getTemplateEncoding());
        this.fileConfig = fileConfig;
        if (fileConfig.isSummaryEnable()) {
            summary = SummaryLoader.getNewSummary(fileMeta);
            fileConfig.addProcessorKey(FileCoreProcessorConstants.SUMMARY);
        }

        // 加载定义的文件协议
        ProtocolLoader.loadProtocol(fileMeta.getProtocol());
        // 初始化处理器
        processors = ProcessorLoader.loadByType(fileConfig, ProcessorTypeEnum.AFTER_READ_HEAD,
            ProcessorTypeEnum.BEFORE_READ_ROW, ProcessorTypeEnum.AFTER_READ_ROW,
            ProcessorTypeEnum.AFTER_READ_TAIL, ProcessorTypeEnum.AFTER_CLOSE_READER,
            ProcessorTypeEnum.AFTER_READ_BYTES, ProcessorTypeEnum.AFTER_DESERIALIZE_ROW,
            ProcessorTypeEnum.BEFORE_DESERIALIZE_ROW);

        if (RdfFileLogUtil.common.isInfo()) {
            RdfFileLogUtil.common.info("rdf-file#ProtocolFileReader(fileConfig=" + fileConfig
                                       + ")  processors=" + processors);
        }
    }

    @Override
    public <T> T readHead(Class<?> requiredType) {
        if (FileDataTypeEnum.BODY.equals(fileConfig.getFileDataType())
            || FileDataTypeEnum.TAIL.equals(fileConfig.getFileDataType())) {
            if (RdfFileLogUtil.common.isWarn()) {
                RdfFileLogUtil.common
                    .warn(
                        "rdf-file#ProtocolFileReader.readHead filePath=" + fileConfig.getFilePath()
                          + ", 文件类型fileDataType=" + fileConfig.getFileDataType().name() + " 不存在头");
            }

            return null;
        }

        if (null == headCache) {
            if (fileMeta.getHeadColumns().isEmpty()) {
                if (RdfFileLogUtil.common.isWarn()) {
                    RdfFileLogUtil.common
                        .warn("rdf-file#ProtocolFileReader.readHead 数据定义模板没有定义头 filePath="
                              + fileConfig.getFilePath() + ", tempaltePath="
                              + fileConfig.getTemplateEncoding());
                }
                return null;
            }

            try {
                ensureHeadOpen();
                headCache = HeaderCodec.instance.deserialize(requiredType, fileConfig, this,
                    processors);

                if (null != headCache) {
                    ProcessExecutor.execute(ProcessorTypeEnum.AFTER_READ_HEAD, processors,
                        fileConfig, new BizData(RdfFileConstants.SUMMARY, summary),
                        new BizData(RdfFileConstants.DATA, headCache));
                }
            } finally {
                closeReader();
            }

        } else if (!headCache.getClass().getName().equals(requiredType.getName())) {
            List<FileColumnMeta> headColMetas = fileMeta.getHeadColumns();
            BeanMapWrapper headCacheWrapper = new BeanMapWrapper(headCache);
            BeanMapWrapper targetWrapper = new BeanMapWrapper(requiredType);
            for (FileColumnMeta columnMeta : headColMetas) {
                targetWrapper.setProperty(columnMeta.getName(),
                    headCacheWrapper.getProperty(columnMeta.getName()));
            }

            headCache = targetWrapper.getBean();
        }

        return (T) headCache;
    }

    @Override
    public <T> T readRow(Class<?> requiredType) {
        if (FileDataTypeEnum.HEAD.equals(fileConfig.getFileDataType())
            || FileDataTypeEnum.TAIL.equals(fileConfig.getFileDataType())) {
            if (RdfFileLogUtil.common.isWarn()) {
                RdfFileLogUtil.common.warn("rdf-file#ProtocolFileReader.readRow filePath="
                                           + fileConfig.getFilePath() + ", 文件类型fileDataType="
                                           + fileConfig.getFileDataType().name() + " 不存在body数据");
            }

            return null;
        }

        if (null == bodyConfig) {
            bodyConfig = fileConfig;
            if (null == bodyConfig.getInputStream()
                && FileDataTypeEnum.ALL.equals(bodyConfig.getFileDataType())) {
                FileSplitter splitter = FileFactory.createSplitter(bodyConfig.getStorageConfig());
                FileSlice bodySlice = splitter.getBodySlice(bodyConfig);
                bodyConfig = bodyConfig.clone();
                bodyConfig.setPartial(bodySlice.getStart(), bodySlice.getLength(),
                    bodySlice.getFileDataType());
            }
        }

        // 返回空
        if (!ProcessExecutor.execute(ProcessorTypeEnum.BEFORE_READ_ROW, processors, bodyConfig)) {
            return null;
        }

        ensureOpen(bodyConfig);

        T t = BodyCodec.instance.deserialize(requiredType, bodyConfig, this, processors);

        ProcessExecutor.execute(ProcessorTypeEnum.AFTER_READ_ROW, processors, bodyConfig,
            new BizData(RdfFileConstants.SUMMARY, summary), new BizData(RdfFileConstants.DATA, t));

        return t;
    }

    @Override
    public <T> T readTail(Class<?> requiredType) {
        if (FileDataTypeEnum.BODY.equals(fileConfig.getFileDataType())
            || FileDataTypeEnum.HEAD.equals(fileConfig.getFileDataType())) {
            if (RdfFileLogUtil.common.isWarn()) {
                RdfFileLogUtil.common
                    .warn(
                        "rdf-file#ProtocolFileReader.readTail filePath=" + fileConfig.getFilePath()
                          + ", 文件类型fileDataType=" + fileConfig.getFileDataType().name() + " 不存在尾");
            }

            return null;
        }

        if (tailCache == null) {
            if (fileMeta.getTailColumns().isEmpty()) {
                if (RdfFileLogUtil.common.isWarn()) {
                    RdfFileLogUtil.common
                        .warn("rdf-file#ProtocolFileReader.readTail 数据定义模板没有定义尾 filePath="
                              + fileConfig.getFilePath() + ", tempaltePath="
                              + fileConfig.getTemplateEncoding());
                }
                return null;
            }

            try {
                ensureTailOpen();
                tailCache = TailCodec.instance.deserialize(requiredType, fileConfig, this,
                    processors);

                if (null != tailCache) {
                    ProcessExecutor.execute(ProcessorTypeEnum.AFTER_READ_TAIL, processors,
                        fileConfig, new BizData(RdfFileConstants.SUMMARY, summary),
                        new BizData(RdfFileConstants.DATA, tailCache));
                }

            } finally {
                closeReader();
            }
        } else if (!tailCache.getClass().getName().equals(requiredType.getName())) {
            List<FileColumnMeta> tailColMetas = fileMeta.getTailColumns();
            BeanMapWrapper tailCacheWrapper = new BeanMapWrapper(tailCache);
            BeanMapWrapper targetWrapper = new BeanMapWrapper(requiredType);
            for (FileColumnMeta columnMeta : tailColMetas) {
                targetWrapper.setProperty(columnMeta.getName(),
                    tailCacheWrapper.getProperty(columnMeta.getName()));
            }

            tailCache = targetWrapper.getBean();
        }

        return (T) tailCache;
    }

    @Override
    public String readLine() {
        ensureOpen(fileConfig);
        try {
            return reader.readLine();
        } catch (IOException e) {
            throw new RdfFileException("rdf-file#ProtocolFileReader.readLine 异常", e,
                RdfErrorEnum.IO_ERROR);
        }
    }

    @Override
    public String readBodyLine() {
        FileConfig bodyConfig = fileConfig;
        if (FileDataTypeEnum.ALL.equals(bodyConfig.getFileDataType())) {
            FileSplitter splitter = FileFactory.createSplitter(bodyConfig.getStorageConfig());
            FileSlice bodySlice = splitter.getBodySlice(bodyConfig);
            bodyConfig = bodyConfig.clone();
            bodyConfig.setPartial(bodySlice.getStart(), bodySlice.getLength(),
                bodySlice.getFileDataType());
        }

        ensureOpen(bodyConfig);
        try {
            return reader.readLine();
        } catch (IOException e) {
            throw new RdfFileException("rdf-file#ProtocolFileReader.readBodyLine 异常", e,
                RdfErrorEnum.IO_ERROR);
        }
    }

    @Override
    public void close() {
        closeReader();

        reader = null;
        headCache = null;
        summary = null;

        ProcessExecutor.execute(ProcessorTypeEnum.AFTER_CLOSE_READER, processors, fileConfig);
    }

    private void closeReader() {
        if (null != reader) {
            try {
                reader.close();
            } catch (IOException e) {
                RdfFileLogUtil.common.error("rdf-file#ProtocolFileReader.closeReader 异常", e);
            }
        }
        reader = null;
    }

    @Override
    public Summary getSummary() {
        if (fileConfig.isSummaryEnable()) {
            return summary;
        } else {
            throw new RdfFileException(
                "rdf-file#ProtocolFileReader.getSummary() 请入参指定FileConfig.setSummaryEnable(true)来进行汇总参数收集",
                RdfErrorEnum.SUMMARY_DISNABLE);
        }
    }

    @Override
    public FileConfig getFileConfig() {
        return fileConfig;
    }

    private void ensureOpen(FileConfig fileConfig) {
        if (null == reader) {
            reader = IOFactory.createReader(fileConfig, processors);
        }
    }

    private void ensureTailOpen() {
        closeReader();
        reader = IOFactory.createTailReader(fileConfig, processors);
    }

    private void ensureHeadOpen() {
        closeReader();
        reader = IOFactory.createReader(fileConfig, processors);
    }
}
