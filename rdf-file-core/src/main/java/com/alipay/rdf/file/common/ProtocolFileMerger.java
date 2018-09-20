package com.alipay.rdf.file.common;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alipay.rdf.file.exception.RdfErrorEnum;
import com.alipay.rdf.file.exception.RdfFileException;
import com.alipay.rdf.file.interfaces.FileFactory;
import com.alipay.rdf.file.interfaces.FileReader;
import com.alipay.rdf.file.interfaces.FileSplitter;
import com.alipay.rdf.file.interfaces.FileStorage;
import com.alipay.rdf.file.interfaces.FileWriter;
import com.alipay.rdf.file.loader.SummaryLoader;
import com.alipay.rdf.file.loader.TemplateLoader;
import com.alipay.rdf.file.meta.FileMeta;
import com.alipay.rdf.file.model.FileConfig;
import com.alipay.rdf.file.model.FileDataTypeEnum;
import com.alipay.rdf.file.model.FileSlice;
import com.alipay.rdf.file.model.MergerConfig;
import com.alipay.rdf.file.model.MergerConfig.PathHolder;
import com.alipay.rdf.file.model.StorageConfig;
import com.alipay.rdf.file.model.Summary;
import com.alipay.rdf.file.model.SummaryPair;
import com.alipay.rdf.file.spi.RdfFileMergerSpi;
import com.alipay.rdf.file.spi.RdfFileReaderSpi;
import com.alipay.rdf.file.spi.RdfFileSummaryPairSpi;
import com.alipay.rdf.file.spi.RdfFileWriterSpi;
import com.alipay.rdf.file.storage.FileInnterStorage;
import com.alipay.rdf.file.util.RdfFileLogUtil;
import com.alipay.rdf.file.util.RdfFileUtil;
import com.alipay.rdf.file.util.RdfProfiler;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 * 
 * 协议文件合并
 * 
 * @author hongwei.quhw
 * @version $Id: ProtocolFileMerger.java, v 0.1 2017年8月10日 下午8:01:11 hongwei.quhw Exp $
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class ProtocolFileMerger implements RdfFileMergerSpi {
    protected FileConfig fileConfig;
    protected FileMeta   fileMeta;
    protected FileWriter fileWriter;

    @Override
    public void init(FileConfig fileConfig) {
        this.fileConfig = fileConfig;
        this.fileMeta = TemplateLoader.load(fileConfig.getTemplatePath(),
            fileConfig.getTemplateEncoding());
        this.fileWriter = FileFactory.createWriter(fileConfig);
    }

    @Override
    public void merge(MergerConfig config) {
        RdfFileLogUtil.common.info("rdf-file#ProtocolFileMerger MergerConfig=" + config
                                   + ", target fileConfig=" + fileConfig);
        //判断目标文件是否存在
        if (config.getHeadFilePaths() != null) {
            check(config.getHeadFilePaths());
        }
        if (config.getBodyFilePaths() != null) {
            check(config.getBodyFilePaths());
        }
        if (config.getTailFilePaths() != null) {
            check(config.getTailFilePaths());
        }
        if (config.getExistFilePaths() != null) {
            check(config.getExistFilePaths());
        }

        try {
            RdfProfiler.enter("rdf-file#merge start...");
            RdfProfiler.enter("rdf-file#merge head start...");
            mergeHead(config);
            RdfProfiler.release("rdf-file#merge head end.");
            RdfProfiler.enter("rdf-file#merge body start...");
            mergeBody(config);
            RdfProfiler.release("rdf-file#merge body end");
            RdfProfiler.enter("rdf-file#merge tail start...");
            mergeTail(config);
            RdfProfiler.release("rdf-file#merge tail end");
        } finally {
            if (null != fileWriter) {
                fileWriter.close();
            }
            RdfProfiler.release("rdf-file#merge end.");
        }
    }

    private void check(List<PathHolder> pathHoders) {
        for (PathHolder filePath : pathHoders) {
            StorageConfig storageConfig = filePath.getStorageConfig();
            if (null == storageConfig) {
                storageConfig = fileConfig.getStorageConfig();
            }
            FileStorage storage = FileFactory.createStorage(storageConfig);
            if (!storage.getFileInfo(filePath.getFilePath()).isExists()) {
                throw new RdfFileException(
                    "rdf-file#合并文件 filePath=【" + filePath.getFilePath() + "】不存在",
                    RdfErrorEnum.NOT_EXSIT);
            }
        }
    }

    private void mergeHead(MergerConfig config) {
        if (!fileMeta.hasHead()) {
            if (RdfFileLogUtil.common.isInfo()) {
                RdfFileLogUtil.common
                    .info("rdf-file#mergeHead不执行 模板没有定义文件头 fileConfig=" + fileConfig);
            }
            return;
        }

        if (null == config.getExistFilePaths() && null == config.getHeadFilePaths()) {
            return;
        }

        // 保存头部的常量信息
        Map<String, Object> head = new HashMap<String, Object>();
        Summary summary = SummaryLoader.getNewSummary(fileMeta);

        head.putAll(readHeadSummary(summary, config.getExistFilePaths()));
        head.putAll(readHeadSummary(summary, config.getHeadFilePaths()));
        head.putAll(summary.summaryHeadToMap());

        fileWriter.writeHead(head);
    }

    protected void mergeBody(MergerConfig config) {
        if (config.isStreamAppend()) {
            RdfProfiler.enter("rdf-file#merge exist body start...");
            mergeBodyStream(config.getExistFilePaths(), true);
            RdfProfiler.release("rdf-file#merge exist body end.");
            RdfProfiler.enter("rdf-file#merge slice body start...");
            mergeBodyStream(config.getBodyFilePaths(), false);
            RdfProfiler.release("rdf-file#merge slice body end.");
        } else {
            RdfProfiler.enter("rdf-file#merge exist body start...");
            mergeBodyLines(config.getExistFilePaths(), true);
            RdfProfiler.release("rdf-file#merge exist body end.");
            RdfProfiler.enter("rdf-file#merge slice body start...");
            mergeBodyLines(config.getBodyFilePaths(), false);
            RdfProfiler.release("rdf-file#merge slice body end.");
        }
    }

    private void mergeTail(MergerConfig config) {
        if (!fileMeta.hasTail()) {
            if (RdfFileLogUtil.common.isInfo()) {
                RdfFileLogUtil.common
                    .info("rdf-file#mergeTail不执行 模板没有定义文件尾 fileConfig=" + fileConfig);
            }
            return;
        }

        if (null == config.getExistFilePaths() && null == config.getTailFilePaths()) {
            return;
        }

        // 保存尾部的常量信息
        Map<String, Object> tail = new HashMap<String, Object>();
        Summary summary = SummaryLoader.getNewSummary(fileMeta);

        tail.putAll(readTailSummary(summary, config.getExistFilePaths()));
        tail.putAll(readTailSummary(summary, config.getTailFilePaths()));
        tail.putAll(summary.summaryTailToMap());

        fileWriter.writeTail(tail);
    }

    private Map<String, Object> readHeadSummary(Summary summary, List<PathHolder> pathHolders) {
        Map<String, Object> head = new HashMap<String, Object>();
        if (null == pathHolders) {
            return new HashMap<String, Object>();
        }

        for (PathHolder path : pathHolders) {
            StorageConfig storageConfig = path.getStorageConfig();
            if (null == storageConfig) {
                storageConfig = fileConfig.getStorageConfig();
            }
            FileConfig exsitFileConfig = fileConfig.clone();
            exsitFileConfig.setFilePath(path.getFilePath());
            exsitFileConfig.setStorageConfig(storageConfig);

            FileReader reader = FileFactory.createReader(exsitFileConfig);
            head = reader.readHead(HashMap.class);
            try {
                //总记录数累计
                if (null != head.get(fileMeta.getTotalCountKey())) {
                    summary.addTotalCount(head.get(fileMeta.getTotalCountKey()));
                }
                for (SummaryPair pair : summary.getHeadSummaryPairs()) {
                    ((RdfFileSummaryPairSpi) pair).addColValue(head.get(pair.getHeadKey()));
                }

            } finally {
                if (null != reader) {
                    reader.close();
                }
            }
        }
        return head;

    }

    private Map<String, Object> readTailSummary(Summary summary, List<PathHolder> pathHolders) {
        Map<String, Object> tail = new HashMap<String, Object>();

        if (null == pathHolders) {
            return tail;
        }

        for (PathHolder path : pathHolders) {
            StorageConfig storageConfig = path.getStorageConfig();
            if (null == storageConfig) {
                storageConfig = fileConfig.getStorageConfig();
            }
            FileConfig exsitFileConfig = fileConfig.clone();
            exsitFileConfig.setFilePath(path.getFilePath());
            exsitFileConfig.setStorageConfig(storageConfig);

            FileReader reader = FileFactory.createReader(exsitFileConfig);
            try {
                tail = reader.readTail(HashMap.class);
                //总记录数累计
                if (null != tail.get(fileMeta.getTotalCountKey())) {
                    summary.addTotalCount(tail.get(fileMeta.getTotalCountKey()));
                }
                for (SummaryPair pair : summary.getTailSummaryPairs()) {
                    ((RdfFileSummaryPairSpi) pair).addColValue(tail.get(pair.getTailKey()));
                }
            } finally {
                if (null != reader) {
                    reader.close();
                }
            }

        }
        return tail;
    }

    protected void mergeBodyLines(List<PathHolder> bodyFilePaths, boolean existFile) {
        if (null == bodyFilePaths || 0 == bodyFilePaths.size()) {
            return;
        }

        for (PathHolder path : bodyFilePaths) {
            StorageConfig storageConfig = path.getStorageConfig();
            if (null == storageConfig) {
                storageConfig = fileConfig.getStorageConfig();
            }
            FileConfig bodyFileConfig = fileConfig.clone();
            bodyFileConfig.setFilePath(path.getFilePath());
            bodyFileConfig.setStorageConfig(storageConfig);
            if (!existFile) {
                bodyFileConfig.setFileDataType(FileDataTypeEnum.BODY);
            }

            RdfFileReaderSpi reader = (RdfFileReaderSpi) FileFactory.createReader(bodyFileConfig);
            try {
                String line = null;
                while (RdfFileUtil.isNotBlank(line = reader.readBodyLine())) {
                    fileWriter.writeLine(line);
                }
            } finally {
                if (null != reader) {
                    reader.close();
                }
            }
        }
    }

    protected void mergeBodyStream(List<PathHolder> bodyFilePaths, boolean existFile) {
        if (null == bodyFilePaths || 0 == bodyFilePaths.size()) {
            return;
        }

        for (PathHolder path : bodyFilePaths) {
            StorageConfig storageConfig = path.getStorageConfig();
            if (null == storageConfig) {
                storageConfig = fileConfig.getStorageConfig();
            }

            FileSplitter fileSplitter = FileFactory.createSplitter(storageConfig);
            FileConfig bodyFileConfig = fileConfig.clone();
            bodyFileConfig.setFilePath(path.getFilePath());
            bodyFileConfig.setStorageConfig(storageConfig);
            InputStream is = null;
            try {
                FileStorage fileStorage = FileFactory.createStorage(storageConfig);
                if (!existFile) {
                    is = ((FileInnterStorage) fileStorage).getInputStream(path.getFilePath());
                } else {
                    FileSlice fileslice = fileSplitter.getBodySlice(bodyFileConfig);
                    is = ((FileInnterStorage) fileStorage).getInputStream(path.getFilePath(),
                        fileslice.getStart(), fileslice.getLength());
                }
                ((RdfFileWriterSpi) fileWriter).append(is);
            } finally {
                if (null != is) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        if (RdfFileLogUtil.common.isWarn()) {
                            RdfFileLogUtil.common.warn("Rdf-file#ProtocolFileMerger close error",
                                e);
                        }
                    }
                }
            }
        }
    }
}
