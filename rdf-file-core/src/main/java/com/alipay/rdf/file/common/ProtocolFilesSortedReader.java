package com.alipay.rdf.file.common;

import java.util.List;
import java.util.Map;

import com.alipay.rdf.file.codec.RowColumnHorizontalCodec;
import com.alipay.rdf.file.exception.RdfErrorEnum;
import com.alipay.rdf.file.exception.RdfFileException;
import com.alipay.rdf.file.interfaces.FileCoreProcessorConstants;
import com.alipay.rdf.file.interfaces.FileCoreToolContants;
import com.alipay.rdf.file.interfaces.FileFactory;
import com.alipay.rdf.file.interfaces.FileReader;
import com.alipay.rdf.file.interfaces.FileSorter;
import com.alipay.rdf.file.loader.ProcessorLoader;
import com.alipay.rdf.file.loader.ProtocolLoader;
import com.alipay.rdf.file.loader.SummaryLoader;
import com.alipay.rdf.file.loader.TemplateLoader;
import com.alipay.rdf.file.meta.FileColumnMeta;
import com.alipay.rdf.file.meta.FileMeta;
import com.alipay.rdf.file.model.FileConfig;
import com.alipay.rdf.file.model.FileDataTypeEnum;
import com.alipay.rdf.file.model.SortConfig;
import com.alipay.rdf.file.model.SortResult;
import com.alipay.rdf.file.model.Summary;
import com.alipay.rdf.file.processor.ProcessExecutor;
import com.alipay.rdf.file.processor.ProcessExecutor.BizData;
import com.alipay.rdf.file.processor.ProcessorTypeEnum;
import com.alipay.rdf.file.protocol.RowDefinition;
import com.alipay.rdf.file.sort.RowData;
import com.alipay.rdf.file.sort.SortedFileGroupReader;
import com.alipay.rdf.file.spi.RdfFileProcessorSpi;
import com.alipay.rdf.file.spi.RdfFileReaderSpi;
import com.alipay.rdf.file.util.BeanMapWrapper;
import com.alipay.rdf.file.util.RdfFileConstants;
import com.alipay.rdf.file.util.RdfFileLogUtil;
import com.alipay.rdf.file.util.RdfFileUtil;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 * 
 * 一组协议文件排序并且有序读
 * 
 * 文件格式必须一致
 * 
 * @author hongwei.quhw
 * @version $Id: ProtocolFilesSortedReader.java, v 0.1 2017年12月12日 下午2:45:34 hongwei.quhw Exp $
 */
@SuppressWarnings("unchecked")
public class ProtocolFilesSortedReader implements RdfFileReaderSpi, FileSorter {
    private Map<ProcessorTypeEnum, List<RdfFileProcessorSpi>> processors;
    private SortResult                                        sortResult;
    private FileConfig                                        fileConfig;
    private SortConfig                                        sortConfig;
    private SortedFileGroupReader                             groupReader;
    private Summary                                           summary;
    private FileMeta                                          fileMeta;
    private Object                                            headCache;
    private Object                                            tailCache;
    private LineReader                                        lineReader;

    @Override
    public void init(FileConfig fileConfig) {
        String templatePath = RdfFileUtil.assertTrimNotBlank(fileConfig.getTemplatePath());
        this.fileConfig = fileConfig;
        this.fileMeta = TemplateLoader.load(templatePath, fileConfig.getTemplateEncoding());
        if (fileConfig.isSummaryEnable()) {
            summary = SummaryLoader.getNewSummary(fileMeta);
            fileConfig.addProcessorKey(FileCoreProcessorConstants.SUMMARY);
        }

        // 加载定义的文件协议
        ProtocolLoader.loadProtocol(fileMeta.getProtocol());
        // 初始化处理器
        processors = ProcessorLoader.loadByType(fileConfig, ProcessorTypeEnum.AFTER_READ_HEAD,
            ProcessorTypeEnum.AFTER_READ_ROW, ProcessorTypeEnum.AFTER_READ_TAIL,
            ProcessorTypeEnum.AFTER_CLOSE_READER);

        if (RdfFileLogUtil.common.isInfo()) {
            RdfFileLogUtil.common.info("rdf-file#ProtocolFilesSortedReader(fileConfig=" + fileConfig
                                       + ")  processors=" + processors);
        }
    }

    @Override
    public SortResult sort(SortConfig sortConfig) {
        this.sortConfig = sortConfig;
        FileSorter sorter = FileFactory.createSorter(fileConfig);
        this.sortResult = sorter.sort(sortConfig);
        return this.sortResult;
    }

    @Override
    public <T> T readHead(Class<?> requiredType) {
        if (FileDataTypeEnum.BODY.equals(fileConfig.getFileDataType())
            || FileDataTypeEnum.TAIL.equals(fileConfig.getFileDataType())) {
            if (RdfFileLogUtil.common.isWarn()) {
                RdfFileLogUtil.common
                    .warn("rdf-file#ProtocolFilesSortedReader.readHead templatePath="
                          + fileConfig.getTemplatePath() + ", 文件类型fileDataType="
                          + fileConfig.getFileDataType().name() + " 不存在头");
            }

            return null;
        }

        if (!fileMeta.hasHead()) {
            return null;
        }

        ensureSorted();

        if (null == headCache) {
            FileReader headReader = createHeadReader();
            try {
                headCache = headReader.readHead(requiredType);

                if (null != headCache) {
                    ProcessExecutor.execute(ProcessorTypeEnum.AFTER_READ_HEAD, processors,
                        fileConfig, new BizData(RdfFileConstants.SUMMARY, summary),
                        new BizData(RdfFileConstants.DATA, headCache));
                }
            } finally {
                if (null != headReader) {
                    headReader.close();
                }
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
        ensureOpen();
        RowData rowData = groupReader.readRow();

        if (null == rowData) {
            return null;
        }

        Map<String, Object> datas = (Map<String, Object>) rowData.getColumnSortDatas();

        if (null == datas) {
            return null;
        }

        T t = null;

        if (requiredType.getName().equals(datas.getClass().getName())) {
            t = (T) datas;
        } else {
            BeanMapWrapper bmw = new BeanMapWrapper(requiredType);
            for (String key : datas.keySet()) {
                bmw.setProperty(key, bmw.getProperty(key));
            }
            t = (T) bmw.getBean();
        }

        ProcessExecutor.execute(ProcessorTypeEnum.AFTER_READ_ROW, processors, fileConfig,
            new BizData(RdfFileConstants.SUMMARY, summary), new BizData(RdfFileConstants.DATA, t));

        return t;
    }

    @Override
    public <T> T readTail(Class<?> requiredType) {
        if (FileDataTypeEnum.BODY.equals(fileConfig.getFileDataType())
            || FileDataTypeEnum.HEAD.equals(fileConfig.getFileDataType())) {
            if (RdfFileLogUtil.common.isWarn()) {
                RdfFileLogUtil.common
                    .warn("rdf-file#ProtocolFilesSortedReader.readTail templatePath="
                          + fileConfig.getTemplatePath() + ", 文件类型fileDataType="
                          + fileConfig.getFileDataType().name() + " 不存在尾");
            }

            return null;
        }

        if (!fileMeta.hasTail()) {
            return null;
        }

        ensureSorted();

        if (null == tailCache) {
            FileReader tailReader = createTailReader();
            try {
                tailCache = tailReader.readTail(requiredType);

                if (null != tailCache) {
                    ProcessExecutor.execute(ProcessorTypeEnum.AFTER_READ_TAIL, processors,
                        fileConfig, new BizData(RdfFileConstants.SUMMARY, summary),
                        new BizData(RdfFileConstants.DATA, tailCache));
                }
            } finally {
                if (null != tailReader) {
                    tailReader.close();
                }
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
        ensureLineReaderOpen();
        return lineReader.readLine();
    }

    @Override
    public String readBodyLine() {
        ensureOpen();
        RowData rowData = groupReader.readRow();

        if (null == rowData) {
            return null;
        }

        Map<String, Object> datas = (Map<String, Object>) rowData.getColumnSortDatas();

        if (null == datas) {
            return null;
        }

        BeanMapWrapper bmw = new BeanMapWrapper(datas);
        List<RowDefinition> rds = ProtocolLoader.getRowDefinitos(fileMeta.getProtocol(),
            FileDataTypeEnum.BODY);
        String line = RowColumnHorizontalCodec.serialize(bmw, fileConfig, rds.get(0), processors,
            FileDataTypeEnum.BODY);
        return line;
    }

    @Override
    public Summary getSummary() {
        ensureSorted();
        if (fileConfig.isSummaryEnable()) {
            return summary;
        } else {
            throw new RdfFileException(
                "rdf-file#ProtocolFilesSortedReader.getSummary() 请入参指定FileConfig.setSummaryEnable(true)来进行汇总参数收集",
                RdfErrorEnum.SUMMARY_DISNABLE);
        }
    }

    @Override
    public void close() {
        if (null != groupReader) {
            groupReader.close();
        }

        if (null != lineReader) {
            lineReader.close();
        }

        groupReader = null;
        summary = null;
        lineReader = null;
        headCache = null;
        tailCache = null;
        sortResult = null;
    }

    @Override
    public FileConfig getFileConfig() {
        return fileConfig;
    }

    private void ensureSorted() {
        if (null == sortResult) {
            throw new RdfFileException("rdf-file#ProtocolFilesSortedReader sorted first.",
                RdfErrorEnum.NEED_SORTED);
        }
    }

    private void ensureOpen() {
        ensureSorted();
        if (null == groupReader) {
            FileConfig bodyConfig = fileConfig.clone();
            bodyConfig.setType(FileCoreToolContants.PROTOCOL_READER);
            bodyConfig.setFileDataType(FileDataTypeEnum.BODY);
            groupReader = new SortedFileGroupReader(bodyConfig, sortConfig, sortResult);
        }
    }

    private void ensureLineReaderOpen() {
        ensureSorted();
        if (null == lineReader) {
            lineReader = new LineReader(fileMeta);
        }
    }

    private class LineReader {
        boolean    headReaded;
        boolean    bodyReaded;
        boolean    tailReaded;
        FileReader headReader = null;
        FileReader tailReader = null;

        LineReader(FileMeta fileMeta) {
            this.headReaded = !fileMeta.hasHead();
            this.tailReaded = !fileMeta.hasTail();
        }

        public String readLine() {
            String line = null;
            if (!headReaded) {
                ensureOpenHeadReader();
                line = headReader.readLine();
            }

            if (null != line) {
                return line;
            }
            headReaded = true;

            if (!bodyReaded) {
                ensureOpen();
                line = readBodyLine();
            }

            if (null != line) {
                return line;
            }

            bodyReaded = true;

            if (!tailReaded) {
                ensureOpenTailReader();
                line = tailReader.readLine();
            }

            return line;
        }

        void close() {
            if (null != headReader) {
                headReader.close();
            }

            if (null != tailReader) {
                tailReader.close();
            }
        }

        private void ensureOpenHeadReader() {
            if (headReader == null) {
                headReader = createHeadReader();
            }
        }

        private void ensureOpenTailReader() {
            if (tailReader == null) {
                tailReader = createTailReader();
            }
        }
    }

    private FileReader createHeadReader() {
        FileConfig headConfig = fileConfig.clone();
        headConfig.setType(FileCoreToolContants.PROTOCOL_READER);
        headConfig.setFileDataType(FileDataTypeEnum.HEAD);
        headConfig.setFilePath(sortResult.getHeadSlicePath());
        return FileFactory.createReader(headConfig);
    }

    private FileReader createTailReader() {
        FileConfig tailConfig = fileConfig.clone();
        tailConfig.setType(FileCoreToolContants.PROTOCOL_READER);
        tailConfig.setFileDataType(FileDataTypeEnum.TAIL);
        tailConfig.setFilePath(sortResult.getTailSlicePath());
        return FileFactory.createReader(tailConfig);
    }

    /**
     * 可以手动将排序结果注入
     * 
     * @param sortResult
     */
    public void setSortedResult(SortConfig sortConfig, SortResult sortResult) {
        this.sortResult = sortResult;
        this.sortConfig = sortConfig;
    }
}
