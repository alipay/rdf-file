package com.alipay.rdf.file.loader;

import com.alipay.rdf.file.init.RdfInit;
import com.alipay.rdf.file.model.FileConfig;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 * 
 * 加载组件工具
 * 
 * 不需要缓存
 * 
 * @author hongwei.quhw
 * @version $Id: FileToolLoader.java, v 0.1 2017年8月11日 上午10:37:40 hongwei.quhw Exp $
 */
public class FileToolLoader {

    /**
     * 创建文件组件工具
     * 
     * @param fileConfig
     * @param requiredClass
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T loader(FileConfig fileConfig, Class<?> requiredClass) {
        T tool = (T) ExtensionLoader.getExtensionLoader(requiredClass)
            .getNewExtension(fileConfig.getType());
        if (tool instanceof RdfInit) {
            ((RdfInit<FileConfig>) tool).init(fileConfig);
        }

        return tool;
    }
}
