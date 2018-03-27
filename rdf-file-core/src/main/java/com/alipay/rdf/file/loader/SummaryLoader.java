package com.alipay.rdf.file.loader;

import com.alipay.rdf.file.exception.RdfErrorEnum;
import com.alipay.rdf.file.exception.RdfFileException;
import com.alipay.rdf.file.meta.FileColumnMeta;
import com.alipay.rdf.file.meta.FileMeta;
import com.alipay.rdf.file.meta.SummaryPairMeta;
import com.alipay.rdf.file.model.FileDataTypeEnum;
import com.alipay.rdf.file.model.Summary;
import com.alipay.rdf.file.spi.RdfFileColumnTypeSpi;
import com.alipay.rdf.file.spi.RdfFileSummaryPairSpi;
import com.alipay.rdf.file.util.RdfFileUtil;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 * 
 * 加载汇总字段
 * 
 * @author hongwei.quhw
 * @version $Id: SummaryLoader.java, v 0.1 2017年8月14日 上午11:55:29 hongwei.quhw Exp $
 */
@SuppressWarnings("rawtypes")
public class SummaryLoader {

    public static Summary getNewSummary(FileMeta fileMeta) {
        Summary summary = new Summary();
        summary.setTotalCountKey(fileMeta.getTotalCountKey());

        for (SummaryPairMeta pair : fileMeta.getSummaryPairMetas()) {

            RdfFileSummaryPairSpi summaryPair = ExtensionLoader
                .getExtensionLoader(RdfFileSummaryPairSpi.class)
                .getNewExtension(pair.getColumnMeta().getType().getName());

            RdfFileUtil.assertNotNull(
                summaryPair,
                "rdf-file#SummaryLoader.getNewSummary(fileMeta=" + fileMeta + ") 类型type="
                             + pair.getColumnMeta().getType().getName() + ", 没有SummaryPairSpi的实现",
                RdfErrorEnum.EXTENSION_ERROR);

            if (FileDataTypeEnum.HEAD.equals(pair.getSummaryDataType())) {
                summaryPair.setHeadKey(pair.getSummaryKey());
            } else if (FileDataTypeEnum.TAIL.equals(pair.getSummaryDataType())) {
                summaryPair.setTailKey(pair.getSummaryKey());
            } else {
                throw new RdfFileException(
                    "rdf-file#SummaryLoader.getNewSummary(fileMeta=" + fileMeta + "), pair=" + pair
                                           + ", 不支持 summaryKey FileDataTypeEnum="
                                           + pair.getSummaryDataType(),
                    RdfErrorEnum.SUMMARY_DEFINED_ERROR);
            }
            summaryPair.setColumnKey(pair.getColumnKey());

            summary.addSummaryPair(summaryPair);
        }

        return summary;
    }

    public static SummaryPairMeta parseMeta(FileMeta fileMeta, String summaryColumnPair) {
        String[] pair = summaryColumnPair.split("\\|");
        if (2 != pair.length) {
            throw new RdfFileException(
                "summaryColumnPair=" + summaryColumnPair + ",配置错误 格式如:\"headKey|columnKey\" ",
                RdfErrorEnum.SUMMARY_DEFINED_ERROR);
        }

        String summaryKey = pair[0];
        String columnKey = pair[1];

        FileColumnMeta summaryColMeta;
        FileDataTypeEnum summaryDataType;
        try {
            summaryColMeta = fileMeta.getHeadColumn(summaryKey);
            summaryDataType = FileDataTypeEnum.HEAD;

            try {
                fileMeta.getTailColumn(summaryKey);
                throw new RdfFileException("rdf-file#SummaryLoader.parseMeta summaryKey="
                                           + summaryKey + "在head和tail中重复定义了",
                    RdfErrorEnum.DUPLICATE_DEFINED);
            } catch (RdfFileException ex) {
                if (!RdfErrorEnum.COLUMN_NOT_DEFINED.equals(ex.getErrorEnum())) {
                    throw ex;
                }
            }

        } catch (RdfFileException e) {
            if (RdfErrorEnum.COLUMN_NOT_DEFINED.equals(e.getErrorEnum())) {
                try {
                    summaryColMeta = fileMeta.getTailColumn(summaryKey);
                    summaryDataType = FileDataTypeEnum.TAIL;
                } catch (RdfFileException ex) {
                    if (RdfErrorEnum.COLUMN_NOT_DEFINED.equals(ex.getErrorEnum())) {
                        throw new RdfFileException("rdf-file#SummaryLoader.parseMeta summaryKey="
                                                   + summaryKey + ", head or tail 没有对应字段定义",
                            RdfErrorEnum.SUMMARY_DEFINED_ERROR);
                    } else {
                        throw e;
                    }
                }
            } else {
                throw e;
            }
        }

        FileColumnMeta bodyColMeta = fileMeta.getBodyColumn(columnKey);

        //校验
        RdfFileColumnTypeSpi summaryType = ExtensionLoader
            .getExtensionLoader(RdfFileColumnTypeSpi.class)
            .getExtension(summaryColMeta.getType().getName());
        RdfFileColumnTypeSpi column = ExtensionLoader.getExtensionLoader(RdfFileColumnTypeSpi.class)
            .getExtension(bodyColMeta.getType().getName());

        if (!summaryType.getClass().getName().equals(column.getClass().getName())) {
            throw new RdfFileException(
                "rdf-file#SummaryPair定义的head=[" + summaryType.getClass().getName() + "]和Column=["
                                       + column.getClass().getName() + "]字段类型不一致",
                RdfErrorEnum.SUMMARY_DEFINED_ERROR);
        }

        return new SummaryPairMeta(summaryKey, columnKey, summaryColMeta, summaryDataType);
    }
}
