package com.alipay.rdf.file.codec;

import com.alipay.rdf.file.exception.RdfErrorEnum;
import com.alipay.rdf.file.exception.RdfFileException;
import com.alipay.rdf.file.interfaces.FileReader;
import com.alipay.rdf.file.interfaces.FileWriter;
import com.alipay.rdf.file.loader.TemplateLoader;
import com.alipay.rdf.file.meta.FileColumnMeta;
import com.alipay.rdf.file.meta.FileMeta;
import com.alipay.rdf.file.model.FileConfig;
import com.alipay.rdf.file.model.FileDataTypeEnum;
import com.alipay.rdf.file.util.RdfFileUtil;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 *
 * 字段信息纵向编码解码
 *
 * @author hongwei.quhw
 * @version $Id: ColumnInfoVerticalCodec.java, v 0.1 2017-1-3 下午5:50:00 hongwei.quhw Exp $
 */
public class ColumnInfoVerticalCodec extends AbstractColumnInfoCodec {
    public static final ColumnInfoVerticalCodec instance = new ColumnInfoVerticalCodec();

    public static void serialize(FileDataTypeEnum layoutType, FileDataTypeEnum dataType, FileConfig fileConfig,
                                 FileWriter writer, String method) {
        FileMeta fileMeta = TemplateLoader.load(fileConfig);

        boolean startWithSplit = null != RdfFileUtil.getRowSplit(fileConfig) && fileMeta.isStartWithSplit(layoutType);
        boolean endtWithSplit = null != RdfFileUtil.getRowSplit(fileConfig) && fileMeta.isEndWithSplit(layoutType);

        //按行写入column字段
        for (FileColumnMeta colMeta : getColumnMetas(fileConfig, dataType)) {
            StringBuilder line = new StringBuilder();
            if (startWithSplit) {
                line.append(RdfFileUtil.getRowSplit(fileConfig));
            }

            line.append(getValue(colMeta, method));

            if (endtWithSplit) {
                line.append(RdfFileUtil.getRowSplit(fileConfig));
            }

            writer.writeLine(line.toString());
        }
    }

    public static <T> T deserialize(FileDataTypeEnum layoutType, FileDataTypeEnum dataType, FileConfig fileConfig,
                                    FileReader reader, String method) {
        FileMeta fileMeta = TemplateLoader.load(fileConfig);
        boolean startWithSplit = null != RdfFileUtil.getRowSplit(fileConfig) && fileMeta.isStartWithSplit(layoutType);
        boolean endtWithSplit = null != RdfFileUtil.getRowSplit(fileConfig) && fileMeta.isEndWithSplit(layoutType);

        for (FileColumnMeta colMeta : getColumnMetas(fileConfig, dataType)) {
            String columName = RdfFileUtil.assertTrimNotBlank(reader.readLine());

            RdfFileUtil.assertNotBlank(columName, "文件=" + fileConfig.getFilePath() + ", " + layoutType.name() + " 内容缺失");

            if (startWithSplit) {
                columName = columName.substring(RdfFileUtil.getRowSplit(fileConfig).length());
            }

            if (endtWithSplit) {
                columName = columName.substring(0, columName.length() - RdfFileUtil.getRowSplit(fileConfig).length());
            }

            String tempalteValue = getValue(colMeta, method);
            if (!tempalteValue.equals(columName)) {
                throw new RdfFileException(
                        "rdf-file#" + layoutType.name() + "模板中定义的column为" + tempalteValue + ", 文件中读取的column为" + columName + " 不一致",
                        RdfErrorEnum.VALIDATE_ERROR);

            }
        }
        return null;
    }
}
