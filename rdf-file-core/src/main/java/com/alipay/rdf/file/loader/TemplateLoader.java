package com.alipay.rdf.file.loader;

import com.alibaba.fastjson.JSON;
import com.alipay.rdf.file.condition.RowConditionType;
import com.alipay.rdf.file.exception.RdfErrorEnum;
import com.alipay.rdf.file.exception.RdfFileException;
import com.alipay.rdf.file.interfaces.RowValidator;
import com.alipay.rdf.file.meta.FileBodyMeta;
import com.alipay.rdf.file.meta.FileColumnAttribute;
import com.alipay.rdf.file.meta.FileColumnMeta;
import com.alipay.rdf.file.meta.FileColumnRangeMeta;
import com.alipay.rdf.file.meta.FileColumnTypeMeta;
import com.alipay.rdf.file.meta.FileMeta;
import com.alipay.rdf.file.meta.MultiBodyConfig;
import com.alipay.rdf.file.meta.TemplateConfig;
import com.alipay.rdf.file.model.FileConfig;
import com.alipay.rdf.file.model.FileDataTypeEnum;
import com.alipay.rdf.file.model.FileDefaultConfig;
import com.alipay.rdf.file.model.RowCondition;
import com.alipay.rdf.file.util.RdfFileUtil;

import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 *
 * 模板加载, 解析
 *
 * @author hongwei.quhw
 * @version $Id: TemplateLoader.java, v 0.1 2016-12-20 下午5:21:48 hongwei.quhw Exp $
 */
public class TemplateLoader {
    public static final Map<String, FileMeta> CACHE            = new ConcurrentHashMap<String, FileMeta>();

    public static final Map<String, Integer>  ROW_LENGTH_CACHE = new ConcurrentHashMap<String, Integer>();

    private static final Object               LOCK             = new Object();

    /**
     * 计算行总长度
     *
     * @param fileConfig
     * @return
     */
    public static int getRowLength(FileConfig fileConfig) {
        Integer len = ROW_LENGTH_CACHE.get(fileConfig.getTemplatePath());

        if (null == len) {
            FileMeta fileMeta = load(fileConfig);
            len = 0;
            for (FileColumnMeta colMeta : fileMeta.getBodyColumns()) {
                len += colMeta.getRange().getFirstAttr();
            }
            ROW_LENGTH_CACHE.put(fileConfig.getTemplatePath(), len);
        }

        return len;
    }

    /**
     * 加载模板
     *
     * @param fileConfig
     * @return
     */
    public static FileMeta load(FileConfig fileConfig) {
        return load(fileConfig.getTemplatePath(), fileConfig.getTemplateEncoding());
    }

    /**
     * 加载模板
     *
     * @param templatePath
     * @param templateEncoding
     * @param templateEncoding
     * @return
     */
    public static FileMeta load(String templatePath, String templateEncoding) {
        if (RdfFileUtil.isBlank(templateEncoding)) {
            templateEncoding = FileDefaultConfig.DEFAULT_TEMPLATE_ENCONDIG;
        }

        templatePath = ResourceLoader.buildResource(templatePath,
            FileDefaultConfig.RDF_TEMPLATE_PATH);

        FileMeta fileMeta = CACHE.get(templatePath);

        if (null != fileMeta) {
            return fileMeta;
        }

        synchronized (LOCK) {
            fileMeta = CACHE.get(templatePath);
            if (null != fileMeta) {
                return fileMeta;
            }

            InputStream is = ResourceLoader.getInputStream(templatePath);

            RdfFileUtil.assertNotNull(is, "rdf-file#模板不存在 templatePath=" + templatePath);

            String content = RdfFileUtil.safeReadFully(is, templateEncoding);
            TemplateConfig templateConfig = JSON.parseObject(content, TemplateConfig.class);

            fileMeta = new FileMeta();
            fileMeta.setTemplatePath(templatePath);

            int colIndex = 0;
            //head
            for (String head : templateConfig.getHead()) {
                fileMeta.addHeadColumn(parseFileColumn(templatePath, head, colIndex++, fileMeta, FileDataTypeEnum.HEAD));
            }

            // body 配置多模板解析
            if (templateConfig.getMultiBodys().size() > 0) {
                fileMeta.setMultiBody(true);
                for (MultiBodyConfig bodyConfig : templateConfig.getMultiBodys()) {
                    // body
                    FileBodyMeta bodyMeta = new FileBodyMeta();
                    RdfFileUtil.assertNotBlank(
                        bodyConfig.getName(), "rdf-file#multiTempalte config templatePath="
                                              + templatePath + ", bodyConfig name is blank");
                    bodyMeta.setName(bodyConfig.getName());
                    bodyMeta.setTemplatePath(templatePath);
                    colIndex = 0;
                    for (String body : bodyConfig.getBodyColumns()) {
                        bodyMeta.getColumns()
                            .add(parseFileColumn(templatePath, body, colIndex++, fileMeta, FileDataTypeEnum.BODY));
                    }
                    fileMeta.addBodyColumn(bodyMeta);

                    // 解析行条件
                    RdfFileUtil.assertNotBlank(bodyConfig.getCondition(),
                        "rdf-file#TemplateLoader 多模板配置 path=" + templatePath + " bodyTempateName="
                                                                          + bodyMeta.getName()
                                                                          + " 没有配置condition");
                    RowCondition rowCondition = new RowCondition(fileMeta, bodyConfig.getName(),
                        bodyConfig.getCondition(), RowConditionType.MULTI_BODY_TEMPLATE);
                    bodyMeta.setRowCondition(RowConditionLoader.loadRowCondition(rowCondition));
                }
            }

            // body单模板解析
            if (templateConfig.getBody().size() > 0) {
                if (fileMeta.isMultiBody()) {
                    throw new RdfFileException(
                        "rdf-file#TemplateLoader 数据定义模板,不支持同时定义body和multiBodys属性",
                        RdfErrorEnum.TEMPLATE_ERROR);
                }
                colIndex = 0;
                // body
                FileBodyMeta bodyMeta = new FileBodyMeta();
                for (String body : templateConfig.getBody()) {
                    bodyMeta.getColumns()
                        .add(parseFileColumn(templatePath, body, colIndex++, fileMeta, FileDataTypeEnum.BODY));
                }
                fileMeta.addBodyColumn(bodyMeta);
            }

            colIndex = 0;
            //tail
            for (String tail : templateConfig.getTail()) {
                fileMeta.addTailColumn(parseFileColumn(templatePath, tail, colIndex++, fileMeta, FileDataTypeEnum.TAIL));
            }

            colIndex = 0;
            // protocolDataType
            for (String dataType : templateConfig.getProtocolDataType()) {
                fileMeta.addProtocolDataType(parseFileColumn(templatePath, dataType, colIndex++, fileMeta, FileDataTypeEnum.UNKOWN));
            }

            //解析汇总字段
            for (String summaryColumnPair : templateConfig.getSummaryColumnPairs()) {
                fileMeta.addSummaryColumnPair(
                    SummaryLoader.parseSummaryPairMeta(fileMeta, summaryColumnPair));
            }

            //解析统计字段
            for (String statisticColumnPair : templateConfig.getStatisticColumnPairs()) {
                fileMeta.addStatisticPair(
                    SummaryLoader.parseStatisticPairMeta(fileMeta, statisticColumnPair));
            }

            //解析协议
            fileMeta.setProtocol(RdfFileUtil.assertTrimNotBlank(templateConfig.getProtocol()));

            //解析行校验器
            for (String rowValidator : templateConfig.getRowValidators()) {
                fileMeta.addRowValidator((RowValidator) RdfFileUtil.newInstance(rowValidator));
            }

            //解析startWithSplit
            if (RdfFileUtil.isNotBlank(templateConfig.getStartWithSplit())) {
                parseStartWithSplit(fileMeta, templateConfig.getStartWithSplit());
            }

            //解析endWithSplit
            if (RdfFileUtil.isNotBlank(templateConfig.getEndWithSplit())) {
                parseEndWithSplit(fileMeta, templateConfig.getEndWithSplit());
            }

            fileMeta.setColumnSplit(templateConfig.getColumnSplit());
            fileMeta.setFileEncoding(templateConfig.getFileEncoding());
            fileMeta.setLineBreak(templateConfig.getLineBreak());

            CACHE.put(templatePath, fileMeta);

            return fileMeta;
        }
    }

    private static void parseStartWithSplit(FileMeta fileMeta, String startSplit) {
        String[] types = startSplit.split("\\|");
        for (String type : types) {
            if (type.equalsIgnoreCase("head")) {
                fileMeta.setStartWithSplit(FileDataTypeEnum.HEAD, true);
            } else if (type.equalsIgnoreCase("body")) {
                fileMeta.setStartWithSplit(FileDataTypeEnum.BODY, true);
            } else if (type.equalsIgnoreCase("tail")) {
                fileMeta.setStartWithSplit(FileDataTypeEnum.TAIL, true);
            }
        }
    }

    private static void parseEndWithSplit(FileMeta fileMeta, String startSplit) {
        String[] types = startSplit.split("\\|");
        for (String type : types) {
            if (type.equalsIgnoreCase("head")) {
                fileMeta.setEndWithSplit(FileDataTypeEnum.HEAD, true);
            } else if (type.equalsIgnoreCase("body")) {
                fileMeta.setEndWithSplit(FileDataTypeEnum.BODY, true);
            } else if (type.equalsIgnoreCase("tail")) {
                fileMeta.setEndWithSplit(FileDataTypeEnum.TAIL, true);
            }
        }
    }

    private static FileColumnMeta parseFileColumn(String tempaltePath, String colConfig,
                                                  int colIndex, FileMeta fileMeta, FileDataTypeEnum dataType) {
        colConfig = RdfFileUtil.assertTrimNotBlank(colConfig, "字段为空 index=" + colIndex);

        String[] fields = colConfig.trim().split("\\|");

        String key = fields[0];

        //如果只定义了1列, 则key和name都是使用相同的内容
        String name = fields.length > 1 ? fields[1] : key;

        FileColumnTypeMeta type = null;
        boolean required = false;
        FileColumnRangeMeta range = null;
        String defaultValue = null;

        if (fields.length > 2) {
            for (int i = 2; i < fields.length; i++) {
                String field = fields[i];
                int semicolonIndex = field.indexOf(":");
                String extra = null;
                if (semicolonIndex > 0) {
                    extra = field.substring(semicolonIndex + 1);
                    field = field.substring(0, semicolonIndex);
                }

                field = RdfFileUtil.assertTrimNotBlank(field,
                    "rdf-dal#TemplateLoader tempaltePath=" + tempaltePath + ", 字段定义[" + colConfig
                                                              + "], index=" + colIndex
                                                              + ", 为空请检查数据定义模板");

                // 尝试解析类型属性
                FileColumnTypeMeta typeMeta = FileColumnTypeMeta.tryValueOf(field, extra);
                if (null != typeMeta) {
                    type = typeMeta;
                    continue;
                }

                //配置了范围属性
                FileColumnRangeMeta rangeMeta = FileColumnRangeMeta.tryValueOf(field, extra);
                if (null != rangeMeta) {
                    range = rangeMeta;
                    continue;
                }

                //配置了必读属性
                if (FileColumnAttribute.REQUIRED.name().equalsIgnoreCase(field)) {
                    if (RdfFileUtil.isBlank(extra)) {
                        required = true;
                    } else {
                        required = Boolean.valueOf(extra);
                    }

                    continue;
                }

                //配置默认值
                if (FileColumnAttribute.DEFAULT.name().equalsIgnoreCase(field)) {
                    defaultValue = RdfFileUtil.assertTrimNotBlank(extra);
                    continue;
                }

                throw new RdfFileException("请检查模板配置, 无法匹配属性！ 字段=" + field + ", index=" + colIndex,
                    RdfErrorEnum.TEMPLATE_ERROR);
            }
        }

        //如果没有定义数据类型，默认string
        if (null == type) {
            type = new FileColumnTypeMeta("String", null);
        }

        FileColumnMeta column = new FileColumnMeta(colIndex, key, name, type, required, range,
            defaultValue, fileMeta, dataType);

        return column;
    }
}
