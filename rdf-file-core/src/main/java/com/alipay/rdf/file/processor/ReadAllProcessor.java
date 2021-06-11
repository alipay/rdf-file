package com.alipay.rdf.file.processor;

import com.alipay.rdf.file.model.FileConfig;
import com.alipay.rdf.file.spi.RdfFileProcessorSpi;
import com.alipay.rdf.file.util.RdfFileConstants;
import com.alipay.rdf.file.util.RdfFileLogUtil;
import com.alipay.rdf.file.util.RdfFileUtil;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (C) 2013-2021 Ant Financial Services Group
 *
 * 将输入流中数据预先读入到本地字节缓存中
 *
 * @version $Id: ReadAllProcessor.java, v 0.1 2021年6月21日 下午4:21:31 hongwei.quhw Exp $
 */
public class ReadAllProcessor implements RdfFileProcessorSpi {

    @Override
    public void process(ProcessCotnext pc) {
        FileConfig fileConfig = pc.getFileConfig();

        // 控制开关，判断是否是要把字节数据线读入到内存
        // 外部直接构建的输入流不走逻辑处理
        if (!fileConfig.isReadAll() || null != fileConfig.getInputStream()) {
            return;
        }

        RdfFileLogUtil.common.info("ReadAllProcessor#process filePath=" + fileConfig.getFilePath());
        InputStream is = (InputStream) pc.getBizData(RdfFileConstants.INPUT_STREAM);
        byte[] bytes = RdfFileUtil.read(is);
        is = new ByteArrayInputStream(bytes);
        pc.putBizData(RdfFileConstants.INPUT_STREAM, is);
    }

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public List<ProcessorTypeEnum> supportedTypes() {
        List<ProcessorTypeEnum> types = new ArrayList<ProcessorTypeEnum>();
        types.add(ProcessorTypeEnum.BEFORE_CREATE_READER);
        return types;
    }
}
