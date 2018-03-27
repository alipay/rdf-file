package com.alipay.rdf.file.model;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.alipay.rdf.file.interfaces.LogCallback;
import com.alipay.rdf.file.util.RdfFileLogUtil;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 * 
 * 提供系统级别的统一配置
 * 
 * 目前默认编码是utf-8，对于gbk编码的项目工程创建的模板默认是gbk ，可以统一指定
 * 
 * 如在spring中配置, 目的在于设置类变量
 * <bean class="com.alipay.rdf.file.util.FileDefaultConfig">
 *      <property name="defaultTemplateEncoding" value="UTF-8" />
 *      <property name="defaultFileEncoding" value="UTF-8" />
 * </bean>
 * @author hongwei.quhw
 * @version $Id: FileDefaultConfig.java, v 0.1 2016-12-22 下午3:43:12 hongwei.quhw Exp $
 */
public class FileDefaultConfig {
    /**加载的模板的编码格式*/
    public static String              DEFAULT_TEMPLATE_ENCONDIG = "utf-8";
    /**读取或者生成的文件编码格式*/
    public static String              DEFAULT_FILE_ENCONDIG     = "utf-8";
    /**写文件时的默认换行符*/
    public static String              DEFAULT_LINE_BREAK        = "\r\n";

    public static Map<String, Object> DEFAULT_FILE_PARAMS       = new ConcurrentHashMap<String, Object>();

    // -------------默认扩展资源地址-------------
    /**自动执行处理器*/
    public static String              RDF_PROCESSOR_PATH        = "classpath*:META-INF/rdf-file/auto-processor/";
    /**定义的文件协议(格式)*/
    public static String              RDF_PROTOCOL_PATH         = "classpath*:META-INF/rdf-file/protocol/";
    /**文件格式化映射文件*/
    public static String              RDF_FORMAT_PATH           = "classpath*:META-INF/rdf-file/format/";
    /**默认模板存放地址*/
    public static String              RDF_TEMPLATE_PATH         = "classpath:";

    public void setRdfProcessorPath(String processorPath) {
        FileDefaultConfig.RDF_PROCESSOR_PATH = processorPath;
    }

    public void setRdfProtocolPath(String protocolPath) {
        FileDefaultConfig.RDF_PROTOCOL_PATH = protocolPath;
    }

    public void setRdfFormatPath(String formatPath) {
        FileDefaultConfig.RDF_FORMAT_PATH = formatPath;
    }

    public void setRdfTemplatePath(String templatePath) {
        FileDefaultConfig.RDF_TEMPLATE_PATH = templatePath;
    }

    /**
     * 设置模板编码
     * 
     * @param enconding
     */
    public void setDefaultTemplateEncoding(String enconding) {
        FileDefaultConfig.DEFAULT_TEMPLATE_ENCONDIG = enconding;
    }

    /**
     * 设置文件编码
     * 
     * @param enconding
     */
    public void setDefaultFileEncoding(String enconding) {
        FileDefaultConfig.DEFAULT_FILE_ENCONDIG = enconding;
    }

    /**
     * 设置默认行换行符
     * 
     * @param lineBreak
     */
    public void setDefaultLineBreak(String lineBreak) {
        FileDefaultConfig.DEFAULT_LINE_BREAK = lineBreak;
    }

    public void addDefaultFleParam(String key, Object value) {
        FileDefaultConfig.DEFAULT_FILE_PARAMS.put(key, value);
    }

    public void setDefaultFleParam(Map<String, Object> params) {
        FileDefaultConfig.DEFAULT_FILE_PARAMS = params;
    }

    public void setCommonLog(LogCallback logCallback) {
        RdfFileLogUtil.common = logCallback;
    }
}
