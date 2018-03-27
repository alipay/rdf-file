package com.alipay.rdf.file.interfaces;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 * 
 * rdf-file-core 扩展常量
 * 
 * @author hongwei.quhw
 * @version $Id: CoreExtensionContants.java, v 0.1 2017年8月24日 下午2:03:16 hongwei.quhw Exp $
 */
public interface FileCoreToolContants {
    // --------------文件读工具扩展 key ---------
    /**协议读 @FileConfig.type 中默F认指定文件读方式*/
    public static final String PROTOCOL_READER            = "protocol";
    /**不需要模板只支持readline*/
    public static final String RAW_READER                 = "raw";

    // --------------文件写工具扩展 key ---------
    /**协议读 @FileConfig.type 中默认指定文件写方式*/
    public static final String PROTOCOL_WRITER            = "protocol";
    /**不需要模板只支持writeline*/
    public static final String RAW_WRITER                 = "raw";

    // --------------文件校验工具扩展 key ---------
    /**协议读 @FileConfig.type 中默认指定文件写方式*/
    public static final String PROTOCOL_VALIDATOR         = "protocol";

    // --------------文件排序工具------------
    /**协议读 @FileConfig.type 中默认指定文件写方式*/
    public static final String PROTOCOL_SORTER            = "protocol";
    /**文件排序，不需要模板，字符直接分割字符串*/
    public static final String RAW_SORTER                 = "raw";
    /**多文件排序*/
    public static final String PROTOCOL_MULTI_FILE_SORTER = "protocol_multiFile";
}
