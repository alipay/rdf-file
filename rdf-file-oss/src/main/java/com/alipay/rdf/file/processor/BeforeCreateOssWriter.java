package com.alipay.rdf.file.processor;

import java.util.ArrayList;
import java.util.List;

import com.alipay.rdf.file.exception.RdfErrorEnum;
import com.alipay.rdf.file.exception.RdfFileException;
import com.alipay.rdf.file.interfaces.FileFactory;
import com.alipay.rdf.file.model.FileConfig;
import com.alipay.rdf.file.model.FileInfo;
import com.alipay.rdf.file.model.StorageConfig;
import com.alipay.rdf.file.storage.OssConfig;
import com.alipay.rdf.file.util.RdfFileLogUtil;
import com.alipay.rdf.file.util.RdfFileUtil;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 * 
 * oss 创建writer之前
 * 
 * @author hongwei.quhw
 * @version $Id: BeforeCreateOssWriter.java, v 0.1 2017年8月23日 下午1:40:26 hongwei.quhw Exp $
 */
public class BeforeCreateOssWriter extends AbstractOssProcessor {
    @Override
    public List<ProcessorTypeEnum> supportedTypes() {
        List<ProcessorTypeEnum> types = new ArrayList<ProcessorTypeEnum>();
        types.add(ProcessorTypeEnum.BEFORE_CREATE_WRITER);
        return types;
    }

    @Override
    public void doProcess(ProcessCotnext pc) {
        FileConfig fileConfig = pc.getFileConfig();
        StorageConfig storageConfig = fileConfig.getStorageConfig();
        OssConfig ossConfig = (OssConfig) storageConfig.getParam(OssConfig.OSS_STORAGE_CONFIG_KEY);

        //对于oss append上传 大于5G不支持
        if (fileConfig.isAppend()) {
            FileInfo fileInfo = FileFactory.createStorage(storageConfig)
                .getFileInfo(fileConfig.getFilePath());
            if (fileInfo.isExists() && fileInfo.getSize() >= ossConfig.getOssAppendSizeLimit()) {
                throw new RdfFileException("rdf-file#oss file append write exist file size="
                                           + fileInfo.getSize() + " 大于限制 OSS_APPEND_SIZE_LIMIT"
                                           + ossConfig.getOssAppendSizeLimit(),
                    RdfErrorEnum.VALIDATE_ERROR);
            }
        }

        // oss 构建本地路径
        String localPath = RdfFileUtil.combinePath(ossConfig.getOssTempRoot(),
            String.valueOf(Thread.currentThread().getId()), fileConfig.getFilePath());
        if (RdfFileLogUtil.common.isInfo()) {
            RdfFileLogUtil.common.info("rdf-file# BeforeCreateOssWriter 本地写路径：" + localPath);
        }
        fileConfig.setFilePath(localPath);
    }

    @Override
    public int getOrder() {
        return 0;
    }

}
