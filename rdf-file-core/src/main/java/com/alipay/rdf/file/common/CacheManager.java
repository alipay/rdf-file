package com.alipay.rdf.file.common;

import com.alipay.rdf.file.function.ColumnFunctionWrapper;
import com.alipay.rdf.file.loader.FormatLoader;
import com.alipay.rdf.file.loader.ProcessorLoader;
import com.alipay.rdf.file.loader.ProtocolLoader;
import com.alipay.rdf.file.loader.TemplateLoader;
import com.alipay.rdf.file.util.RdfFileUtil;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 *
 * 配置缓存管理
 *
 * @author hongwei.quhw
 * @version $Id: CacheManager.java, v 0.1 2018年9月27日 上午10:17:55 hongwei.quhw Exp $
 */
public class CacheManager {

    /**
     * 删除数据定义模板缓存
     * 
     * @param templatePath
     */
    public static void removeDataTempalteCache(String templatePath) {
        templatePath = RdfFileUtil.assertTrimNotBlank(templatePath,
            "rdf-file#CacheManager.removeDataTempalteCache templatePath is blank.");

        TemplateLoader.removeCache(templatePath);

        for (String key : ColumnFunctionWrapper.columnRegExs.keySet()) {
            if (key.startsWith(templatePath)) {
                ColumnFunctionWrapper.columnRegExs.remove(key);
            }
        }
    }

    /**
     * 去除协议布局模板变更后的缓存
     * 
     * @param protocol
     */
    public static void removeProtocolTemplateCache(String protocol) {
        protocol = RdfFileUtil.assertTrimNotBlank(protocol,
            "rdf-file#CacheManager.removeProtocolTemplateCache protocol is blank.");

        ProtocolLoader.PROTOCOL_PROCESSOR_CACHE.remove(protocol.toUpperCase());
        ProtocolLoader.PROTOCOL_PROCESSOR_CACHE.remove(protocol.toLowerCase());
    }

    /**
     * 去除格式化配置变更后的缓存
     * 
     * @param protocol
     */
    public static void removeFormatCache(String protocol) {
        protocol = RdfFileUtil.assertTrimNotBlank(protocol,
            "rdf-file#CacheManager.removeFormatCache protocol is blank.");

        FormatLoader.TYPEFORMATHOLDER_CACHE.remove(protocol.toUpperCase());
        FormatLoader.TYPEFORMATHOLDER_CACHE.remove(protocol.toLowerCase());
    }

    /**
     * 全局默认处理器配置变更后缓存
     */
    public static void removeGlobalDefaultProcessorsCache() {
        ProcessorLoader.DEFAULT_PROCESSORS = null;
    }

    /**
     * 协议对应的默认处理器配置变更后缓存
     */
    public static void removeProtocolDefaultProcessorsCache(String protocol) {
        protocol = RdfFileUtil.assertTrimNotBlank(protocol,
            "rdf-file#CacheManager.removeProtocolDefaultProcessorsCache protocol is blank.");

        ProtocolLoader.PROTOCOL_PROCESSOR_CACHE.remove(protocol.toUpperCase());
        ProtocolLoader.PD_CACHE.remove(protocol.toUpperCase());
        ProtocolLoader.PROTOCOL_PROCESSOR_CACHE.remove(protocol.toLowerCase());
        ProtocolLoader.PD_CACHE.remove(protocol.toLowerCase());
    }
}
