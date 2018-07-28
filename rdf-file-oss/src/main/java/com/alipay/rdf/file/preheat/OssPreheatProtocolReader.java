package com.alipay.rdf.file.preheat;

import java.io.ByteArrayInputStream;
import java.util.List;

import com.alipay.rdf.file.common.ProtocolFileReader;
import com.alipay.rdf.file.interfaces.FileCoreProcessorConstants;
import com.alipay.rdf.file.interfaces.FileCoreToolContants;
import com.alipay.rdf.file.interfaces.FileFactory;
import com.alipay.rdf.file.interfaces.FileReader;
import com.alipay.rdf.file.loader.SummaryLoader;
import com.alipay.rdf.file.loader.TemplateLoader;
import com.alipay.rdf.file.meta.FileMeta;
import com.alipay.rdf.file.model.FileConfig;
import com.alipay.rdf.file.model.FileDataTypeEnum;
import com.alipay.rdf.file.model.FileSlice;
import com.alipay.rdf.file.model.Summary;
import com.alipay.rdf.file.preheat.OssPreheatProtocolReaderWrapper.DataHolder;
import com.alipay.rdf.file.spi.RdfFileReaderSpi;
import com.alipay.rdf.file.util.RdfFileUtil;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 *
 * Oss预热协议读
 *  
 * @author hongwei.quhw
 * @version $Id: OssPrheatProtocolReader.java, v 0.1 2018年4月4日 下午3:47:26 hongwei.quhw Exp $
 */
@SuppressWarnings("unchecked")
public class OssPreheatProtocolReader implements RdfFileReaderSpi {
    private FileMeta                        fileMeta;
    private FileConfig                      config;
    private OssPreheatProtocolReaderWrapper preheatReader;
    /**分块数据读取*/
    private FileReader                      blockReader;
    /**文件头缓存*/
    private Object                          headCache;
    /**文件尾缓存*/
    private Object                          tailCache;

    private FileDataTypeEnum                lineReaderCursor;

    private Summary                         summary;

    @Override
    public void init(FileConfig config) {

        String templatePath = RdfFileUtil.assertTrimNotBlank(config.getTemplatePath());
        this.fileMeta = TemplateLoader.load(templatePath, config.getTemplateEncoding());
        this.config = config.clone();
        this.config.setType(FileCoreToolContants.PROTOCOL_READER);

        this.preheatReader = new OssPreheatProtocolReaderWrapper(this.config);

        // 初始化行读文件游标
        if (FileDataTypeEnum.ALL.equals(config.getFileDataType())
            || FileDataTypeEnum.HEAD.equals(config.getFileDataType())
            || FileDataTypeEnum.UNKOWN.equals(config.getFileDataType())) {
            lineReaderCursor = FileDataTypeEnum.HEAD;
        } else {
            lineReaderCursor = config.getFileDataType();
        }

        // 是否初始化summary
        if (config.isSummaryEnable()) {
            summary = SummaryLoader.getNewSummary(fileMeta);
            config.addProcessorKey(FileCoreProcessorConstants.SUMMARY);
        }

    }

    @Override
    public <T> T readHead(Class<?> requiredType) {
        if (null == headCache) {
            List<DataHolder> dataHolder = preheatReader.readHead();
            if (dataHolder.isEmpty()) {
                return null;
            }

            FileReader headReader = createBlockReader(dataHolder.get(0));
            try {
                headCache = headReader.readHead(requiredType);
            } finally {
                headReader.close();
            }
        }

        return (T) headCache;
    }

    @Override
    public <T> T readRow(Class<?> requiredType) {
        ensureOpenBodyReader();

        if (null == blockReader) {
            return null;
        }

        T row = blockReader.readRow(requiredType);
        if (null == row) {
            blockReader.close();
            blockReader = null;

            return readRow(requiredType);
        }

        return row;
    }

    @Override
    public <T> T readTail(Class<?> requiredType) {
        if (null == tailCache) {
            List<DataHolder> dataHolder = preheatReader.readTail();
            if (dataHolder.isEmpty()) {
                return null;
            }

            FileReader headReader = createBlockReader(dataHolder.get(0));
            try {
                tailCache = headReader.readTail(requiredType);
            } finally {
                headReader.close();
            }
        }

        return (T) tailCache;
    }

    @Override
    public String readLine() {
        ensureOpenReader();

        if (null == blockReader) {
            return null;
        }

        String line = ((RdfFileReaderSpi) blockReader).readLine();
        if (null == line) {
            blockReader.close();
            blockReader = null;
            return readLine();
        }

        return line;
    }

    @Override
    public Summary getSummary() {
        return summary;
    }

    @Override
    public void close() {
        if (null != preheatReader) {
            preheatReader.close();
        }

        if (null != blockReader) {
            blockReader.close();
        }

        headCache = null;
        tailCache = null;
    }

    @Override
    public String readBodyLine() {
        ensureOpenBodyReader();

        if (null == blockReader) {
            return null;
        }

        String line = ((RdfFileReaderSpi) blockReader).readBodyLine();
        if (null == line) {
            blockReader.close();
            blockReader = null;
            return readBodyLine();
        }

        return line;
    }

    @Override
    public FileConfig getFileConfig() {
        return config;
    }

    /**
     * body 数据块reader
     */
    private void ensureOpenBodyReader() {
        if (null == blockReader) {
            blockReader = createBlockReader(preheatReader.readBodyData());
        }
    }

    /**
     * 所有文件的数据块reader
     */
    private void ensureOpenReader() {
        if (null != blockReader) {
            return;
        }

        if (null == lineReaderCursor) {
            return;
        }

        if (lineReaderCursor == FileDataTypeEnum.HEAD) {
            List<DataHolder> dataHolder = preheatReader.readHead();
            lineReaderCursor = FileDataTypeEnum.BODY;
            if (!dataHolder.isEmpty()) {
                blockReader = createBlockReader(dataHolder.get(0));
                return;
            }
        }

        if (lineReaderCursor == FileDataTypeEnum.BODY) {
            DataHolder data = preheatReader.readBodyData();
            lineReaderCursor = FileDataTypeEnum.TAIL;
            if (null != data) {
                blockReader = createBlockReader(data);
                return;
            }
        }

        if (lineReaderCursor == FileDataTypeEnum.TAIL) {
            lineReaderCursor = null;
            List<DataHolder> dataHolder = preheatReader.readTail();
            if (!dataHolder.isEmpty()) {
                blockReader = createBlockReader(dataHolder.get(0));
            }
        }
    }

    private FileReader createBlockReader(DataHolder data) {
        FileReader blockReader = null;
        if (null != data) {
            FileConfig sliceConfig = config.clone();
            FileSlice slice = data.getFileSlice();
            sliceConfig.setFilePath(slice.getFilePath());
            sliceConfig.setFileDataType(slice.getFileDataType());
            sliceConfig.setPartial(slice.getStart(), slice.getLength(), slice.getFileDataType());
            sliceConfig.setInputStream(new ByteArrayInputStream(data.getBytes()));
            blockReader = FileFactory.createReader(sliceConfig);
            if (config.isSummaryEnable()) {
                ((ProtocolFileReader) blockReader).setSummary(summary);
            }
        }

        return blockReader;
    }
}
