package com.alipay.rdf.file.processor;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.binary.Base64;

import com.alipay.rdf.file.exception.RdfErrorEnum;
import com.alipay.rdf.file.exception.RdfFileException;
import com.alipay.rdf.file.interfaces.FileFactory;
import com.alipay.rdf.file.interfaces.FileStorage;
import com.alipay.rdf.file.model.FileConfig;
import com.alipay.rdf.file.util.RdfFileUtil;
import com.aliyun.oss.internal.OSSHeaders;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 * 
 * @author quhongwei
 * @version $Id: MD5OSSReadValidator.java, v 0.1 2017年3月27日 下午9:05:47 quhongwei Exp $
 */
public class MD5OSSReadValidator extends AbstractOssProcessor {
    private final MessageDigest md5;

    public MD5OSSReadValidator() {
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RdfFileException("不支持MD5计算", RdfErrorEnum.VALIDATE_ERROR);
        }
    }

    @Override
    public List<ProcessorTypeEnum> supportedTypes() {
        List<ProcessorTypeEnum> types = new ArrayList<ProcessorTypeEnum>();
        types.add(ProcessorTypeEnum.AFTER_CLOSE_READER);
        types.add(ProcessorTypeEnum.AFTER_READ_BYTES);
        return types;
    }

    @Override
    public void doProcess(ProcessCotnext pc) {
        ProcessorTypeEnum processorType = pc.getProcessorType();
        switch (processorType) {
            case AFTER_READ_BYTES:
                byte[] inputByte = (byte[]) pc.getBizData("inputByte");
                md5.update(inputByte);
                break;
            case AFTER_CLOSE_READER:
                String readMd5 = Base64.encodeBase64String(md5.digest());
                FileConfig config = pc.getFileConfig();
                FileStorage ossStorage = FileFactory.createStorage(config.getStorageConfig());
                String ossMd5 = (String) ossStorage.getFileInfo(config.getFilePath())
                    .getMetadata(OSSHeaders.CONTENT_MD5);
                if (!RdfFileUtil.equals(readMd5, ossMd5)) {
                    throw new RdfFileException("读取的数据和文件MD5不一致！filePath=" + config.getFilePath(),
                        RdfErrorEnum.VALIDATE_ERROR);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
