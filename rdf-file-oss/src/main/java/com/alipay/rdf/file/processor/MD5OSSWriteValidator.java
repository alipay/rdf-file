package com.alipay.rdf.file.processor;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.binary.Base64;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 *
 * @author hongwei.quhw
 * @version $Id: MD5OSSWriteValidator.java, v 0.1 2018年3月12日 下午4:30:47 hongwei.quhw Exp $
 */
public class MD5OSSWriteValidator extends AbstractOssProcessor {
    private final MessageDigest md5;

    public MD5OSSWriteValidator() {
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("不支持MD5计算");
        }
    }

    @Override
    public List<ProcessorTypeEnum> supportedTypes() {
        List<ProcessorTypeEnum> types = new ArrayList<ProcessorTypeEnum>();
        types.add(ProcessorTypeEnum.AFTER_WRITE_BYTES);
        types.add(ProcessorTypeEnum.AFTER_CLOSE_WRITER);
        return types;
    }

    @Override
    public void doProcess(ProcessCotnext pc) {
        ProcessorTypeEnum processorType = pc.getProcessorType();

        switch (processorType) {
            case AFTER_CLOSE_WRITER:
                pc.putBizData("md5", Base64.encodeBase64String(md5.digest()));
                break;
            case AFTER_WRITE_BYTES:
                byte[] inputByte = (byte[]) pc.getBizData("inputByte");
                md5.update(inputByte);
                break;
            default:
                throw new RuntimeException("不支持" + processorType.name());
        }

    }

    @Override
    public int getOrder() {
        return 0;
    }
}
