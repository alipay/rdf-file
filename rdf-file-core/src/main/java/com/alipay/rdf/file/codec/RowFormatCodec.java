package com.alipay.rdf.file.codec;

import com.alipay.rdf.file.loader.TemplateLoader;
import com.alipay.rdf.file.meta.FileMeta;
import com.alipay.rdf.file.model.FileConfig;
import com.alipay.rdf.file.model.FileDataTypeEnum;
import com.alipay.rdf.file.util.RdfFileUtil;

/**
 *
 * 对一行数据进行格式化处理
 *
 * @Author: hongwei.quhw 2021/6/27 11:20 上午
 */
public class RowFormatCodec {

    public static String deserialize(FileConfig fileConfig, String line, FileDataTypeEnum rowType) {
        FileMeta fileMeta = TemplateLoader.load(fileConfig);
        boolean startWithSplit = fileMeta.isStartWithSplit(rowType);
        boolean endWithSplit = fileMeta.isEndWithSplit(rowType);
        String split = RdfFileUtil.getRowSplit(fileConfig);
        if (RdfFileUtil.isBlank(split)) {
            return line;
        }

        int splitLength = split.length();

        if (startWithSplit) {
            line = line.substring(splitLength);
        }

        if (endWithSplit) {
            line = line.substring(0, line.length() - splitLength);
        }
        return line;
    }

    public static String serialize(FileConfig fileConfig, String line, FileDataTypeEnum rowType) {
        FileMeta fileMeta = TemplateLoader.load(fileConfig);
        boolean startWithSplit = fileMeta.isStartWithSplit(rowType);
        boolean endWithSplit = fileMeta.isEndWithSplit(rowType);
        String split = RdfFileUtil.getRowSplit(fileConfig);
        if (RdfFileUtil.isBlank(split)) {
            return line;
        }

        if (startWithSplit) {
            line = split + line;
        }

        if (endWithSplit) {
            line = line + split;
        }

        return line;
    }
}
