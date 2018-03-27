package com.alipay.rdf.file.codec;

import com.alipay.rdf.file.exception.RdfErrorEnum;
import com.alipay.rdf.file.exception.RdfFileException;
import com.alipay.rdf.file.interfaces.FileReader;
import com.alipay.rdf.file.interfaces.FileWriter;
import com.alipay.rdf.file.loader.TemplateLoader;
import com.alipay.rdf.file.meta.FileColumnMeta;
import com.alipay.rdf.file.meta.FileMeta;
import com.alipay.rdf.file.model.FileConfig;
import com.alipay.rdf.file.util.RdfFileUtil;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 * 
 * 头部字段序列化反序列化
 * 
 * @author hongwei.quhw
 * @version $Id: HeadColumnCodec.java, v 0.1 2017-1-3 下午5:50:00 hongwei.quhw Exp $
 */
public class BodyColumnVerticalCodec {
    public static final BodyColumnVerticalCodec instance = new BodyColumnVerticalCodec();

    /**
     * 写入头部字段
     */
    public static void serialize(Object bean, FileConfig fileConfig, FileWriter writer,
                                 String method) {
        FileMeta fileMeta = TemplateLoader.load(fileConfig);
        //按行写入column字段
        for (FileColumnMeta colMeta : fileMeta.getBodyColumns()) {
            writer.writeLine(getValue(colMeta, method));
        }
    }

    /**
     * 读取头部字段
     */
    public static <T> T deserialize(Class<?> clazz, FileConfig fileConfig, FileReader reader,
                                    String method) {
        FileMeta fileMeta = TemplateLoader.load(fileConfig);
        String columName = null;
        for (FileColumnMeta colMeta : fileMeta.getBodyColumns()) {
            columName = RdfFileUtil.assertTrimNotBlank(reader.readLine());
            if (!getValue(colMeta, method).equalsIgnoreCase(columName)) {
                throw new RdfFileException("rdf-file#模板中定义的column为" + colMeta.getName()
                                           + ", 文件中读取的column为" + columName + " 不一致",
                    RdfErrorEnum.VALIDATE_ERROR);

            }
        }
        return null;
    }

    private static String getValue(FileColumnMeta colMeta, String method) {
        String value;
        if ("desc".equalsIgnoreCase(method)) {
            value = colMeta.getDesc();
        } else if ("name".equalsIgnoreCase(method)) {
            value = colMeta.getName();
        } else {
            throw new RdfFileException(
                "rdf-file#BodyColumnHorizontalCodec.serialize 无效方法参数method=" + method,
                RdfErrorEnum.UNSUPPORTED_OPERATION);
        }
        return value;
    }
}
