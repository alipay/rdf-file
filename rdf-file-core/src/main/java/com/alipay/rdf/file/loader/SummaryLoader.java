package com.alipay.rdf.file.loader;

import com.alipay.rdf.file.condition.RowConditionType;
import com.alipay.rdf.file.exception.RdfErrorEnum;
import com.alipay.rdf.file.exception.RdfFileException;
import com.alipay.rdf.file.meta.FileBodyMeta;
import com.alipay.rdf.file.meta.FileColumnMeta;
import com.alipay.rdf.file.meta.FileMeta;
import com.alipay.rdf.file.meta.StatisticPairMeta;
import com.alipay.rdf.file.meta.SummaryPairMeta;
import com.alipay.rdf.file.model.FileDataTypeEnum;
import com.alipay.rdf.file.model.RowCondition;
import com.alipay.rdf.file.model.Summary;
import com.alipay.rdf.file.spi.RdfFileColumnTypeSpi;
import com.alipay.rdf.file.spi.RdfFileSummaryPairSpi;
import com.alipay.rdf.file.util.RdfFileLogUtil;
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
            summaryPair.setRowCondition(pair.getRowCondition());

            summary.addSummaryPair(summaryPair);
        }

        for (StatisticPairMeta pair : fileMeta.getStatisticPairMetas()) {
            summary.addStatisticPair(pair);
        }

        return summary;
    }

    public static void main(String[] args) {
        //String summaryColumnPair = "a|a|successCount|count|bol=true|seq(0,4)=aaa|age=15";
        String summaryColumnPair = "successCount|count";
        int firstIdx = summaryColumnPair.indexOf("|");
        int secIdx = summaryColumnPair.indexOf("|", firstIdx + 1);
        System.out.println(firstIdx);
        System.out.println(secIdx);
        System.out.println(summaryColumnPair.substring(0, firstIdx));
        System.out.println(summaryColumnPair.substring(firstIdx + 1, secIdx));
        System.out.println(summaryColumnPair.substring(secIdx + 1));
    }

    public static SummaryPairMeta parseSummaryPairMeta(FileMeta fileMeta,
                                                       String summaryColumnPair) {
        int firstIdx = summaryColumnPair.indexOf("|");
        int secIdx = summaryColumnPair.indexOf("|", firstIdx + 1);
        if (firstIdx < 1) {
            throw new RdfFileException("summaryColumnPair=" + summaryColumnPair
                                       + ",配置错误 格式如:\"headKey|columnKey\" 或者 \"headKey|columnKey|condition\" ",
                RdfErrorEnum.SUMMARY_DEFINED_ERROR);
        }

        String summaryKey = summaryColumnPair.substring(0, firstIdx);
        String columnKey = secIdx == -1 ? summaryColumnPair.substring(firstIdx + 1)
            : summaryColumnPair.substring(firstIdx + 1, secIdx);
        String condition = secIdx == -1 ? null : summaryColumnPair.substring(secIdx + 1);

        if (RdfFileLogUtil.common.isInfo()) {
            RdfFileLogUtil.common.info("rdf-file#SummaryLoader.parseSummaryPairMeta templatePath=["
                                       + fileMeta.getTemplatePath() + "] summaryKey=[" + summaryKey
                                       + "], columnKey=[" + columnKey + "], condition=["
                                       + (condition == null ? "null" : condition) + "]");
        }

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
        FileBodyMeta bodyMetaHolder = null;
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
                bodyMetaHolder = bodyMeta;
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

        SummaryPairMeta summaryPairMeta = new SummaryPairMeta(summaryKey, columnKey, summaryColMeta,
            summaryDataType);

        if (RdfFileUtil.isBlank(condition)) {
            return summaryPairMeta;
        }

        RowCondition rowCondition = new RowCondition(fileMeta, bodyMetaHolder.getName(), condition,
            RowConditionType.STATISTIC);
        summaryPairMeta.setRowCondition(RowConditionLoader.loadRowCondition(rowCondition));

        return summaryPairMeta;
    }

    public static StatisticPairMeta parseStatisticPairMeta(FileMeta fileMeta,
                                                           String statisticColumnPair) {

        int idx = statisticColumnPair.indexOf("|");
        if (idx < 1) {
            throw new RdfFileException(
                "statisticColumnPair=" + statisticColumnPair + ",配置错误 格式如:\"headKey|condition\" ",
                RdfErrorEnum.STATISTIC_DEFINED_ERROR);
        }

        String statisticKey = statisticColumnPair.substring(0, idx);
        String condition = statisticColumnPair.substring(idx + 1);

        if (RdfFileLogUtil.common.isInfo()) {
            RdfFileLogUtil.common
                .info("rdf-file#SummaryLoader.parseStatisticPairMeta templatePath=["
                      + fileMeta.getTemplatePath() + "], statisticKey=[" + statisticKey
                      + "], condition=[" + condition + "]");
        }

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
                            RdfErrorEnum.STATISTIC_DEFINED_ERROR);
                    } else {
                        throw e;
                    }
                }
            } else {
                throw e;
            }
        }

        StatisticPairMeta statisticPairMeta = new StatisticPairMeta(statisticKey, statisticColMeta,
            statisticDataType);

        RowCondition rowCondition = new RowCondition(fileMeta, null, condition,
            RowConditionType.STATISTIC);
        statisticPairMeta.setRowCondition(RowConditionLoader.loadRowCondition(rowCondition));

        return statisticPairMeta;
    }

}
