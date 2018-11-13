package com.alipay.rdf.file.loader;

import com.alipay.rdf.file.exception.RdfErrorEnum;
import com.alipay.rdf.file.exception.RdfFileException;
import com.alipay.rdf.file.meta.FileBodyMeta;
import com.alipay.rdf.file.meta.FileColumnMeta;
import com.alipay.rdf.file.meta.FileMeta;
import com.alipay.rdf.file.meta.StatisticPairMeta;
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

    public static SummaryPairMeta parseSummaryPairMeta(FileMeta fileMeta,
                                                       String summaryColumnPair) {
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

        boolean exsitBodyColumn = false;
        for (FileBodyMeta bodyMeta : fileMeta.getBodyMetas()) {
            FileColumnMeta colMeta = null;
            try {
                colMeta = bodyMeta.getColumn(columnKey);
            } catch (RdfFileException e) {
                if (RdfErrorEnum.COLUMN_NOT_DEFINED.equals(e.getErrorEnum())
                    && fileMeta.isMultiBody()) {
                    continue;
                }
            }

            if (null != colMeta) {
                exsitBodyColumn = true;
            }

            //校验
            RdfFileColumnTypeSpi summaryType = ExtensionLoader
                .getExtensionLoader(RdfFileColumnTypeSpi.class)
                .getExtension(summaryColMeta.getType().getName());
            RdfFileColumnTypeSpi column = ExtensionLoader
                .getExtensionLoader(RdfFileColumnTypeSpi.class)
                .getExtension(colMeta.getType().getName());

            if (!summaryType.getClass().getName().equals(column.getClass().getName())) {
                throw new RdfFileException("rdf-file#SummaryPair定义的head=["
                                           + summaryType.getClass().getName() + "]和Column=["
                                           + column.getClass().getName() + "]字段类型不一致",
                    RdfErrorEnum.SUMMARY_DEFINED_ERROR);
            }

        }

        RdfFileUtil.assertTrue(exsitBodyColumn,
            "rdf-file#SummaryPair body模板中么有定义 templatePath=[" + fileMeta.getTemplatePath()
                                                + "],column=[" + columnKey + "]",
            RdfErrorEnum.COLUMN_NOT_DEFINED);

        return new SummaryPairMeta(summaryKey, columnKey, summaryColMeta, summaryDataType);
    }

    public static StatisticPairMeta parseStatisticPairMeta(FileMeta fileMeta,
                                                           String statisticColumnPair) {
        String[] pair = statisticColumnPair.split("\\|");
        if (pair.length != 2 && pair.length != 3) {
            throw new RdfFileException("statisticColumnPair=" + statisticColumnPair
                                       + ",配置错误 格式如:\"headKey|columnKey\"  或者 \"headKey|columnKey|condition\"",
                RdfErrorEnum.STATISTIC_DEFINED_ERROR);
        }

        String statisticKey = pair[0];
        String columnKey = pair[1];
        String condition = pair.length == 3 ? pair[3] : null;

        FileColumnMeta statisticColMeta;
        FileDataTypeEnum statisticDataType;
        try {
            statisticColMeta = fileMeta.getHeadColumn(statisticKey);
            statisticDataType = FileDataTypeEnum.HEAD;

            try {
                fileMeta.getTailColumn(statisticKey);
                throw new RdfFileException(
                    "rdf-file#SummaryLoader.parseStatisticPairMeta statisticKey=" + statisticKey
                                           + "在head和tail中重复定义了",
                    RdfErrorEnum.DUPLICATE_DEFINED);
            } catch (RdfFileException ex) {
                if (!RdfErrorEnum.COLUMN_NOT_DEFINED.equals(ex.getErrorEnum())) {
                    throw ex;
                }
            }

        } catch (RdfFileException e) {
            if (RdfErrorEnum.COLUMN_NOT_DEFINED.equals(e.getErrorEnum())) {
                try {
                    statisticColMeta = fileMeta.getTailColumn(statisticKey);
                    statisticDataType = FileDataTypeEnum.TAIL;
                } catch (RdfFileException ex) {
                    if (RdfErrorEnum.COLUMN_NOT_DEFINED.equals(ex.getErrorEnum())) {
                        throw new RdfFileException(
                            "rdf-file#SummaryLoader.parseStatisticPairMeta statisticKey="
                                                   + statisticKey + ", head or tail 没有对应字段定义",
                            RdfErrorEnum.SUMMARY_DEFINED_ERROR);
                    } else {
                        throw e;
                    }
                }
            } else {
                throw e;
            }
        }

        FileColumnMeta colMetaHolder = null;
        FileBodyMeta bodyMetaHolder = null;
        for (FileBodyMeta bodyMeta : fileMeta.getBodyMetas()) {
            FileColumnMeta columnMeta = null;
            try {
                columnMeta = bodyMeta.getColumn(columnKey);
            } catch (RdfFileException e) {
                if (RdfErrorEnum.COLUMN_NOT_DEFINED.equals(e.getErrorEnum())
                    && fileMeta.isMultiBody()) {
                    continue;
                }
            }

            if (null != columnMeta) {
                if (null != colMetaHolder && !columnMeta.getType().getClass().getClass().getName()
                    .equals(colMetaHolder.getType().getClass().getName())) {
                    throw new RdfFileException("rdf-file#parseStatisticPairMeta templatePaht=["
                                               + fileMeta.getTemplatePath() + "] 请检查配置的columName=["
                                               + columnKey + "] 存在不一致的数据类型",
                        RdfErrorEnum.TEMPLATE_ERROR);
                }
            }
            colMetaHolder = columnMeta;
            bodyMetaHolder = bodyMeta;
        }

        StatisticPairMeta statisticPairMeta = new StatisticPairMeta(statisticKey, columnKey,
            colMetaHolder, statisticDataType);

        if (RdfFileUtil.isBlank(condition)) {
            return statisticPairMeta;
        }
        
        

        return null;
    }

}
