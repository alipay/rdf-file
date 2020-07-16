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
 * 读文件长度校验
 * 
 * @author hongwei.quhw
 * @version $Id: LengthReadValidator.java, v 0.1 2017年8月9日 下午2:54:15 hongwei.quhw Exp $
 */
public class LengthReadValidator implements RdfFileProcessorSpi {

    private long readedSize = 0L;

    @Override
    public List<ProcessorTypeEnum> supportedTypes() {
        List<ProcessorTypeEnum> types = new ArrayList<ProcessorTypeEnum>();
        types.add(ProcessorTypeEnum.AFTER_READ_BYTES);
        types.add(ProcessorTypeEnum.AFTER_CLOSE_READER);
        return types;
    }

    @Override
    public void process(ProcessCotnext pc) {
        ProcessorTypeEnum processorType = pc.getProcessorType();
        switch (processorType) {
            case AFTER_READ_BYTES:
                byte[] bytes = (byte[]) pc.getBizData("inputByte");
                readedSize += bytes.length;
                break;
            case AFTER_CLOSE_READER:
                FileConfig config = pc.getFileConfig();

                long fileSize = 0L;
                if (config.isPartial()) {
                    fileSize = config.getLength();
                } else {
                    FileStorage storage = FileFactory.createStorage(config.getStorageConfig());
                    fileSize = storage.getFileInfo(config.getFilePath()).getSize();
                }

                if (fileSize != readedSize) {
                    throw new RdfFileException(
                        "rdf-file#LengthReadValidator 读取的数据和文件大小不一致！filePath="
                                               + config.getFilePath() + " fileSize=" + fileSize
                                               + ", readedSize=" + readedSize,
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
