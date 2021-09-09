/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2019 All Rights Reserved.
 */
package com.alipay.rdf.file.codec;

import com.alipay.rdf.file.exception.RdfErrorEnum;
import com.alipay.rdf.file.exception.RdfFileException;
import com.alipay.rdf.file.loader.TemplateLoader;
import com.alipay.rdf.file.meta.FileColumnMeta;
import com.alipay.rdf.file.meta.FileMeta;
import com.alipay.rdf.file.model.FileConfig;
import com.alipay.rdf.file.model.FileDataTypeEnum;
import com.alipay.rdf.file.util.RdfFileUtil;

import java.util.List;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 *
 * 主要用于对字段信息用于头尾输出，针对nosql特性文件不能使用
 *
 * @author quhongwei
 * @version : AbstractColumnInfoCodec.java, v 0.1 2019年11月29日 12:55 quhongwei Exp $
 */
public abstract class AbstractColumnInfoCodec {

    protected static String getValue(FileColumnMeta colMeta, String method) {
        String value;
        if ("desc".equalsIgnoreCase(method)) {
            value = colMeta.getDesc();
        } else if ("name".equalsIgnoreCase(method)) {
            value = colMeta.getName();
        } else {
            throw new RdfFileException(
                    "rdf-file#AbstractColumnInfoCodec.getValue 无效方法参数method=" + method,
                    RdfErrorEnum.UNSUPPORTED_OPERATION);
        }
        return value;
    }

    protected static List<FileColumnMeta> getColumnMetas(FileConfig config, FileDataTypeEnum dataType) {
        FileMeta fileMeta = TemplateLoader.load(config);
        switch (dataType) {
            case HEAD:
                return fileMeta.getHeadColumns();
            case BODY:
                return fileMeta.getBodyColumns();
            case TAIL:
                return fileMeta.getTailColumns();
            default:
                throw new RdfFileException(
                        "rdf-file#AbstractColumnInfoCodec.getColumnMetas dateType=" + dataType.name(),
                        RdfErrorEnum.UNSUPPORTED_OPERATION);
        }
    }
}
