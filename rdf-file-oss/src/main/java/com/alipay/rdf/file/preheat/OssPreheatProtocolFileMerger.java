package com.alipay.rdf.file.preheat;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.alipay.rdf.file.common.ProtocolFileMerger;
import com.alipay.rdf.file.interfaces.FileCoreToolContants;
import com.alipay.rdf.file.interfaces.FileFactory;
import com.alipay.rdf.file.interfaces.FileSplitter;
import com.alipay.rdf.file.model.FileConfig;
import com.alipay.rdf.file.model.FileDataTypeEnum;
import com.alipay.rdf.file.model.FileSlice;
import com.alipay.rdf.file.model.MergerConfig;
import com.alipay.rdf.file.model.MergerConfig.PathHolder;
import com.alipay.rdf.file.model.StorageConfig;
import com.alipay.rdf.file.preheat.OssPreheatProtocolReaderWrapper.DataHolder;
import com.alipay.rdf.file.spi.RdfFileWriterSpi;
import com.alipay.rdf.file.util.RdfFileLogUtil;
import com.alipay.rdf.file.util.RdfProfiler;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 * 
 * 采用预热读合并
 * 
 * @author hongwei.quhw
 * @version $Id: OssPreheatProtocolFileMerger.java, v 0.1 2018年4月11日 下午7:36:11 hongwei.quhw Exp $
 */
public class OssPreheatProtocolFileMerger extends ProtocolFileMerger {

    @Override
    public void init(FileConfig fileConfig) {
        fileConfig.setType(FileCoreToolContants.PROTOCOL_READER);
        super.init(fileConfig);

        if (RdfFileLogUtil.common.isInfo()) {
            RdfFileLogUtil.common
                .info("rdf-file#OssPreheatProtocolFileMerger.init(" + fileConfig + ")");
        }
    }

    @Override
    protected void mergeBody(MergerConfig config) {
        if (RdfFileLogUtil.common.isInfo()) {
            RdfFileLogUtil.common.info("rdf-file#OssPreheatProtocolFileMerger.mergeBody start...");
        }
        RdfProfiler.enter("rdf-file#merge exist body start...");
        mergeBodyStream(config.getExistFilePaths(), true);
        RdfProfiler.release("rdf-file#merge exist body end.");
        RdfProfiler.enter("rdf-file#merge slice body start...");
        mergeBodyStream(config.getBodyFilePaths(), false);
        RdfProfiler.release("rdf-file#merge slice body end.");
        if (RdfFileLogUtil.common.isInfo()) {
            RdfFileLogUtil.common.info("rdf-file#OssPreheatProtocolFileMerger.mergeBody end.");
        }
    }

    @Override
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
                if (!existFile) {
                    bodyFileConfig.setFileDataType(FileDataTypeEnum.BODY);
                } else {
                    FileSlice fileslice = fileSplitter.getBodySlice(bodyFileConfig);
                    bodyFileConfig.setPartial(fileslice.getStart(), fileslice.getLength(),
                        fileslice.getFileDataType());
                }

                OssPreheatProtocolReaderWrapper preheatReader = new OssPreheatProtocolReaderWrapper(
                    bodyFileConfig);
                try {
                    DataHolder dataHoler = null;
                    while (null != (dataHoler = preheatReader.readBodyData())) {
                        ((RdfFileWriterSpi) fileWriter)
                            .append(new ByteArrayInputStream(dataHoler.getBytes()));
                    }
                } finally {
                    preheatReader.close();
                }

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
