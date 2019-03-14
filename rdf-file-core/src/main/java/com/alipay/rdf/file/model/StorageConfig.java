package com.alipay.rdf.file.model;

import java.util.HashMap;
import java.util.Map;

import com.alipay.rdf.file.exception.RdfErrorEnum;
import com.alipay.rdf.file.util.RdfFileUtil;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 * 
 * @author hongwei.quhw
 * @version $Id: StorageConfig.java, v 0.1 2017年4月7日 下午4:21:38 hongwei.quhw Exp $
 */
public class StorageConfig {
    private final String storageType;

    public StorageConfig(String storageType) {
        this.storageType = storageType;
    }

    private Map<String, Object> param = new HashMap<String, Object>();

    public void addParam(String key, Object value) {
        RdfFileUtil.assertNotNull(RdfFileUtil.findMethod(value.getClass(), "hashCode"),
            "rdf-file#StorageConfig.addParam value 必须实现Object对象中的hashCode方法",
            RdfErrorEnum.ILLEGAL_ARGUMENT);
        RdfFileUtil.assertNotNull(RdfFileUtil.findMethod(value.getClass(), "equals"),
            "rdf-file#StorageConfig.addParam value 必须实现Object对象中的equals方法",
            RdfErrorEnum.ILLEGAL_ARGUMENT);
        param.put(key, value);
        param.put(key, value);
    }

    public Object getParam(String key) {
        return param.get(key);
    }

    public String getStorageType() {
        return storageType;
    }

    @Override
    public String toString() {
        return "StorageConfig[storageType=" + storageType + ", param=" + param + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((param == null) ? 0 : param.hashCode());
        result = prime * result + ((storageType == null) ? 0 : storageType.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        StorageConfig other = (StorageConfig) obj;
        if (param == null) {
            if (other.param != null)
                return false;
        } else if (!param.equals(other.param))
            return false;
        if (storageType == null) {
            if (other.storageType != null)
                return false;
        } else if (!storageType.equals(other.storageType))
            return false;
        return true;
    }
}
