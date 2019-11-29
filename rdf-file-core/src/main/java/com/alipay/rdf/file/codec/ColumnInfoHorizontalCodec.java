package com.alipay.rdf.file.codec;

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

import java.util.List;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 *
 * 字段信息水平编码解码
 *
 * @author hongwei.quhw
 * @version $Id: ColumnInfoHorizontalCodec.java, v 0.1 2017-1-3 下午5:50:00 hongwei.quhw Exp $
 */
public class ColumnInfoHorizontalCodec extends AbstractColumnInfoCodec {

    public static void serialize(FileDataTypeEnum layoutType, FileDataTypeEnum dataType, FileConfig config, FileWriter writer,
                                 String method) {
        FileMeta fileMeta = TemplateLoader.load(config);
        List<FileColumnMeta> colMetas = getColumnMetas(config, dataType);
        StringBuilder colHead = new StringBuilder();

        if (null != RdfFileUtil.getRowSplit(config) && fileMeta.isStartWithSplit(layoutType)) {
            colHead.append(RdfFileUtil.getRowSplit(config));
        }

        for (int i = 0; i < colMetas.size(); i++) {
            FileColumnMeta colMeta = colMetas.get(i);
            colHead.append(getValue(colMeta, method));
            //添加字段分割符
            if (null != RdfFileUtil.getRowSplit(config) && (i < colMetas.size() - 1 || fileMeta.isEndWithSplit(layoutType))) {
                colHead.append(RdfFileUtil.getRowSplit(config));
            }
        }
        writer.writeLine(colHead.toString());

    }

    public static <T> T deserialize(FileDataTypeEnum layoutType, FileDataTypeEnum dataType, FileConfig config,
                                    FileReader reader,
                                    String method) {
        String line = reader.readLine();
        RdfFileUtil.assertNotBlank(line, "文件=" + config.getFilePath() + ", " + layoutType.name() + " 内容缺失");

        FileMeta fileMeta = TemplateLoader.load(config);
        String[] columns = ProtocolLoader.loadProtocol(fileMeta.getProtocol()).getRowSplit().split(
                new SplitContext(line, config, FileDataTypeEnum.BODY));
        List<FileColumnMeta> colMetas = getColumnMetas(config, dataType);

        int splitLength = fileMeta.isStartWithSplit(layoutType) ? colMetas.size() + 1 : colMetas.size();
        splitLength = fileMeta.isEndWithSplit(layoutType) ? splitLength + 1 : splitLength;

        if (splitLength != columns.length) {
            throw new RdfFileException("文件=" + config.getFilePath() + "， " + layoutType.name() + " line=" + line,
                    RdfErrorEnum.DESERIALIZE_ERROR);
        }

        int statIndex = fileMeta.isStartWithSplit(layoutType) ? 1 : 0;
        int endIndex = fileMeta.isEndWithSplit(layoutType) ? columns.length - 1 : columns.length;

        for (int i = statIndex; i < endIndex; i++) {
            FileColumnMeta colMeta = colMetas.get(i - statIndex);
            if (!getValue(colMeta, method).equals(columns[i])) {
                throw new RdfFileException(
                        "文件" + layoutType.name() + "字段校验：文件模板定义的第" + i + "个column为[" + colMetas.get(i).getDesc() + "], 实际文件中为["
                                + columns[i] + "]", RdfErrorEnum.DESERIALIZE_ERROR);
            }
        }
        return null;
    }

}
