package com.alipay.rdf.file.validate;

import java.util.ArrayList;
import java.util.List;

import com.alipay.rdf.file.exception.RdfErrorEnum;
import com.alipay.rdf.file.exception.RdfFileException;
import com.alipay.rdf.file.interfaces.FileFactory;
import com.alipay.rdf.file.interfaces.FileStorage;
import com.alipay.rdf.file.model.FileConfig;
import com.alipay.rdf.file.processor.ProcessCotnext;
import com.alipay.rdf.file.processor.ProcessorTypeEnum;
import com.alipay.rdf.file.spi.RdfFileProcessorSpi;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 *
 * @author hongwei.quhw
 * @version $Id: LengthWriteValidator.java, v 0.1 2018年3月12日 下午4:30:06 hongwei.quhw Exp $
 */
public class LengthWriteValidator implements RdfFileProcessorSpi {

    private long inputSize = 0L;

    @Override
    public List<ProcessorTypeEnum> supportedTypes() {
        List<ProcessorTypeEnum> types = new ArrayList<ProcessorTypeEnum>();
        types.add(ProcessorTypeEnum.AFTER_CLOSE_WRITER);
        types.add(ProcessorTypeEnum.AFTER_WRITE_BYTES);
        return types;
    }

    @Override
    public void process(ProcessCotnext pc) {
        ProcessorTypeEnum processorType = pc.getProcessorType();
        switch (processorType) {
            case AFTER_CLOSE_WRITER:
                FileConfig config = pc.getFileConfig();
                FileStorage fileStorage = FileFactory.createStorage(config.getStorageConfig());
                long fileSize = fileStorage.getFileInfo(config.getFilePath()).getSize();
                if (inputSize != fileSize) {
                    throw new RdfFileException("生成的文件和输入数据大小不一致！filePath=" + config.getFilePath()
                                               + ", 输入数据大小：" + inputSize + ", 文件大小：" + fileSize,
                        RdfErrorEnum.VALIDATE_ERROR);
                }
                break;
            case AFTER_WRITE_BYTES:
                byte[] inputByte = (byte[]) pc.getBizData("inputByte");
                inputSize += inputByte.length;
                break;
            default:
                throw new RdfFileException("不支持" + processorType.name(),
                    RdfErrorEnum.UNSUPPORTED_OPERATION);
        }
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
