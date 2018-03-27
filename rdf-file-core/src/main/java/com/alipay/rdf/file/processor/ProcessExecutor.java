package com.alipay.rdf.file.processor;

import java.util.List;
import java.util.Map;

import com.alipay.rdf.file.model.FileConfig;
import com.alipay.rdf.file.spi.RdfFileProcessorSpi;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 *
 * @author hongwei.quhw
 * @version $Id: ProcessExecutor.java, v 0.1 2018年3月12日 下午4:23:04 hongwei.quhw Exp $
 */
public class ProcessExecutor {

    public static boolean execute(ProcessorTypeEnum type,
                                  Map<ProcessorTypeEnum, List<RdfFileProcessorSpi>> processors,
                                  FileConfig fileConfig, BizData... bizDatas) {

        if (null == processors) {
            return true;
        }

        List<RdfFileProcessorSpi> processorSpis = processors.get(type);

        if (null == processorSpis || 0 == processorSpis.size()) {
            return true;
        }

        ProcessCotnext ctx = new ProcessCotnext(fileConfig, type);
        if (null != bizDatas) {
            for (BizData bd : bizDatas) {
                ctx.putBizData(bd.key, bd.vaule);
            }
        }

        for (RdfFileProcessorSpi ps : processorSpis) {
            ps.process(ctx);
        }

        return ctx.isSuccess();
    }

    public static class BizData {
        private final String key;
        private final Object vaule;

        public BizData(String key, Object vaule) {
            this.key = key;
            this.vaule = vaule;
        }
    }
}
