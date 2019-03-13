package com.alipay.rdf.file.processor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.alipay.rdf.file.exception.RdfErrorEnum;
import com.alipay.rdf.file.exception.RdfFileException;
import com.alipay.rdf.file.interfaces.FileCoreStorageContants;
import com.alipay.rdf.file.interfaces.FileFactory;
import com.alipay.rdf.file.model.FileConfig;
import com.alipay.rdf.file.model.StorageConfig;
import com.alipay.rdf.file.storage.FileOssStorage;
import com.alipay.rdf.file.storage.OssConfig;
import com.alipay.rdf.file.util.RdfFileLogUtil;
import com.alipay.rdf.file.util.RdfFileUtil;
import com.alipay.rdf.file.util.RdfProfiler;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 * 
 * 写完后上传到oss
 * 
 * @author hongwei.quhw
 * @version $Id: UploadOSSAfterWriteClose.java, v 0.1 2017年8月22日 下午5:54:09 hongwei.quhw Exp $
 */
public class UploadOSSAfterWriteClose extends AbstractOssProcessor {

    @Override
    public List<ProcessorTypeEnum> supportedTypes() {
        List<ProcessorTypeEnum> types = new ArrayList<ProcessorTypeEnum>();
        types.add(ProcessorTypeEnum.AFTER_CLOSE_WRITER);
        return types;
    }

    /** 
     * @see hongwei.quhw.file.processor.Processor#process(hongwei.quhw.file.processor.ProcessCotnext)
     */
    @Override
    public void doProcess(ProcessCotnext pc) {
        FileConfig config = pc.getFileConfig();

        OssConfig ossConfig = (OssConfig) config.getStorageConfig()
            .getParam(OssConfig.OSS_STORAGE_CONFIG_KEY);
        String localFilePath = RdfFileUtil.combinePath(ossConfig.getOssTempRoot(),
            String.valueOf(Thread.currentThread().getId()), config.getFilePath());

        try {
            File localFile = new File(localFilePath);

            if (RdfFileLogUtil.common.isInfo()) {
                RdfFileLogUtil.common
                    .info("rdf-file#UploadOSSAfterWriteClose 构建的本地路径localFile=" + localFilePath);
            }

            if (!localFile.exists()) {
                throw new RdfFileException(
                    "rdf-file#UploadOSSAfterWriteClose localFilePath=" + localFilePath + "不存在",
                    RdfErrorEnum.NOT_EXSIT);
            }

            Boolean hasError = (Boolean) pc.getBizData("hasError");

            if (null == hasError || !hasError) {
                FileOssStorage fileStorage = (FileOssStorage) FileFactory
                    .createStorage(pc.getFileConfig().getStorageConfig());

                RdfProfiler.enter("rdf-file#upload oss start...");
                if (config.isAppend()) {
                    fileStorage.appendUploadFile(localFilePath, config.getFilePath());
                } else {
                    fileStorage.upload(localFilePath, config.getFilePath(), true);
                }
                RdfProfiler.release("rdf-file#upload oss end.");
            }

        } finally {
            FileFactory.createStorage(new StorageConfig(FileCoreStorageContants.STORAGE_LOCAL))
                .delete(localFilePath);
        }
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
