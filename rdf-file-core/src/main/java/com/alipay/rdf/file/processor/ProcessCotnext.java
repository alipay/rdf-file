package com.alipay.rdf.file.processor;

import java.util.HashMap;
import java.util.Map;

import com.alipay.rdf.file.model.FileConfig;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 * 
 * 处理器执行上下文
 * 
 * @author hongwei.quhw
 * @version $Id: ProcessCotnext.java, v 0.1 2017年8月8日 上午11:01:23 hongwei.quhw Exp $
 */
public class ProcessCotnext {
    private final FileConfig          fileConfig;
    private final ProcessorTypeEnum   processorType;
    private final Map<String, Object> bizData = new HashMap<String, Object>();
    private boolean                   success = true;

    public ProcessCotnext(FileConfig fileConfig, ProcessorTypeEnum processorType) {
        this.fileConfig = fileConfig;
        this.processorType = processorType;
    }

    public void putBizData(String key, Object value) {
        bizData.put(key, value);
    }

    public Object getBizData(String key) {
        return bizData.get(key);
    }

    public FileConfig getFileConfig() {
        return fileConfig;
    }

    public ProcessorTypeEnum getProcessorType() {
        return processorType;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
