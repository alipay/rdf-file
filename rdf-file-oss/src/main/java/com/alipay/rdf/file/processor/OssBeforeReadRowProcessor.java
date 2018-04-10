package com.alipay.rdf.file.processor;

import java.util.ArrayList;
import java.util.List;

import com.alipay.rdf.file.util.RdfFileLogUtil;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 * 
 * oss read row 
 * 
 * oss 长度为0会返回start后的所有数据
 * 
 * @author hongwei.quhw
 * @version $Id: OssBeforeReadRowProcessor.java, v 0.1 2018年3月12日 下午3:35:38 hongwei.quhw Exp $
 */
public class OssBeforeReadRowProcessor extends AbstractOssProcessor {

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public List<ProcessorTypeEnum> supportedTypes() {
        List<ProcessorTypeEnum> types = new ArrayList<ProcessorTypeEnum>();
        types.add(ProcessorTypeEnum.BEFORE_READ_ROW);
        return types;
    }

    @Override
    protected void doProcess(ProcessCotnext pc) {
        if (null != pc.getFileConfig().getInputStream()) {
            return;
        }

        if (pc.getFileConfig().isPartial() && pc.getFileConfig().getLength() == 0) {
            if (RdfFileLogUtil.common.isInfo()) {
                RdfFileLogUtil.common
                    .warn("rdf-file#ProtocolFileReader.readRow filePath="
                          + pc.getFileConfig().getFilePath() + ", 文件类型fileDataType="
                          + pc.getFileConfig().getFileDataType().name() + " body length == 0");
            }

            pc.setSuccess(false);
        }
    }

}
