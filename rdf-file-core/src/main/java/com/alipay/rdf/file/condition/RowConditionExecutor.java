package com.alipay.rdf.file.condition;

import java.util.List;

import com.alipay.rdf.file.exception.RdfErrorEnum;
import com.alipay.rdf.file.exception.RdfFileException;
import com.alipay.rdf.file.loader.TemplateLoader;
import com.alipay.rdf.file.meta.FileBodyMeta;
import com.alipay.rdf.file.meta.FileColumnMeta;
import com.alipay.rdf.file.meta.FileMeta;
import com.alipay.rdf.file.model.FileConfig;
import com.alipay.rdf.file.model.FileDataTypeEnum;
import com.alipay.rdf.file.util.BeanMapWrapper;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 *
 * 行条件计算执行器
 *
 * @author hongwei.quhw
 * @version $Id: RowConditionExecutor.java, v 0.1 2018年10月13日 下午9:17:39 hongwei.quhw Exp $
 */
public class RowConditionExecutor {

    public static List<FileColumnMeta> deserializeRow(FileConfig config, String[] row,
                                                      FileDataTypeEnum rowType, String line) {
        FileMeta fileMeta = TemplateLoader.load(config);

        if (!fileMeta.isMultiBody()) {
            return fileMeta.getColumns(rowType);
        }

        for (FileBodyMeta bodyMeta : fileMeta.getBodyMetas()) {
            if (bodyMeta.getRowCondition().deserialize(config, row)) {
                return bodyMeta.getColumns();
            }
        }

        throw new RdfFileException(
            "rdf-file#RowConditionExecutor.deserializeRow templatePath=[" + config.getTemplatePath()
                                   + "],line=[" + line + "]多模板配置， 没有满足条件的配置",
            RdfErrorEnum.UNSUPPORTED_OPERATION);
    }

    public static List<FileColumnMeta> serializeRow(FileConfig config, BeanMapWrapper row,
                                                    FileDataTypeEnum rowType) {
        FileMeta fileMeta = TemplateLoader.load(config);

        if (!fileMeta.isMultiBody()) {
            return fileMeta.getColumns(rowType);
        }

        for (FileBodyMeta bodyMeta : fileMeta.getBodyMetas()) {
            if (bodyMeta.getRowCondition().serialize(config, row)) {
                return bodyMeta.getColumns();
            }
        }

        throw new RdfFileException(
            "rdf-file#RowConditionExecutor.serializeRow templatePath=[" + config.getTemplatePath()
                                   + "], row=[" + row.toString() + "]多模板配置， 没有满足条件的配置 ",
            RdfErrorEnum.UNSUPPORTED_OPERATION);
    }
}
