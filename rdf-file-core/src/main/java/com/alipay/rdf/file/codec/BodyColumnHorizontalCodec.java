package com.alipay.rdf.file.codec;

import java.util.List;

import com.alipay.rdf.file.exception.RdfErrorEnum;
import com.alipay.rdf.file.exception.RdfFileException;
import com.alipay.rdf.file.interfaces.FileReader;
import com.alipay.rdf.file.interfaces.FileWriter;
import com.alipay.rdf.file.loader.ProtocolLoader;
import com.alipay.rdf.file.loader.TemplateLoader;
import com.alipay.rdf.file.meta.FileColumnMeta;
import com.alipay.rdf.file.meta.FileMeta;
import com.alipay.rdf.file.model.FileConfig;
import com.alipay.rdf.file.model.FileDataTypeEnum;
import com.alipay.rdf.file.spi.RdfFileRowSplitSpi.SplitContext;
import com.alipay.rdf.file.util.RdfFileUtil;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 * 
 * 头部字段序列化反序列化
 * 
 * @author hongwei.quhw
 * @version $Id: HeadColumnCodec.java, v 0.1 2017-1-3 下午5:50:00 hongwei.quhw Exp $
 */
@Deprecated
public class BodyColumnHorizontalCodec {

    /**
     * 写入头部字段
     * @see com.alipay.rdf.file.codec.FileCodec#serialize(Object, com.alipay.rdf.file.common.ProtocolFileWriter)
     */
    public static void serialize(Object bean, FileConfig config, FileWriter writer, String method) {
        FileMeta fileMeta = TemplateLoader.load(config);
        StringBuilder colHead = new StringBuilder();
        List<FileColumnMeta> colMetas = fileMeta.getBodyColumns();

        if (null != RdfFileUtil.getRowSplit(config)
            && fileMeta.isStartWithSplit(FileDataTypeEnum.HEAD)) {
            colHead.append(RdfFileUtil.getRowSplit(config));
        }

        for (int i = 0; i < colMetas.size(); i++) {
            FileColumnMeta colMeta = colMetas.get(i);
            colHead.append(getValue(colMeta, method));
            //添加字段分割符
            if (null != RdfFileUtil.getRowSplit(config)
                && (i < colMetas.size() - 1 || fileMeta.isEndWithSplit(FileDataTypeEnum.HEAD))) {
                colHead.append(RdfFileUtil.getRowSplit(config));
            }
        }
        writer.writeLine(colHead.toString());

    }

    /**
     * 读取头部字段
     * @see com.alipay.rdf.file.codec.FileCodec#deserialize(com.alipay.rdf.file.common.ProtocolFileReader)
     */
    public static <T> T deserialize(Class<?> clazz, FileConfig config, FileReader reader,
                                    String method) {
        String line = reader.readLine();
        RdfFileUtil.assertNotBlank("文件=" + config.getFilePath() + "头部第二行缺失");

        FileMeta fileMeta = TemplateLoader.load(config);
        String[] columns = ProtocolLoader.loadProtocol(fileMeta.getProtocol()).getRowSplit()
            .split(new SplitContext(line, config, FileDataTypeEnum.BODY));
        List<FileColumnMeta> colMetas = fileMeta.getBodyColumns();

        int splitLength = fileMeta.isStartWithSplit(FileDataTypeEnum.HEAD) ? colMetas.size() + 1
            : colMetas.size();
        splitLength = fileMeta.isEndWithSplit(FileDataTypeEnum.HEAD) ? splitLength + 1
            : splitLength;

        if (splitLength != columns.length) {
            throw new RdfFileException("文件=" + config.getFilePath() + "头部第二行line=" + line,
                RdfErrorEnum.DESERIALIZE_ERROR);
        }

        int statIndex = fileMeta.isStartWithSplit(FileDataTypeEnum.HEAD) ? 1 : 0;
        int endIndex = fileMeta.isEndWithSplit(FileDataTypeEnum.HEAD) ? columns.length - 1
            : columns.length;

        for (int i = statIndex; i < endIndex; i++) {
            FileColumnMeta colMeta = colMetas.get(i - statIndex);
            if (!getValue(colMeta, method).equals(columns[i])) {
                throw new RdfFileException(
                    "文件头字段校验：文件模板定义的第" + i + "个column为[" + colMetas.get(i).getDesc() + "], 实际文件中为["
                                           + columns[i] + "]",
                    RdfErrorEnum.DESERIALIZE_ERROR);
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
