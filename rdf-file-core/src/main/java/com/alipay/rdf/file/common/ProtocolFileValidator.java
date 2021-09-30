package com.alipay.rdf.file.common;

import java.util.HashMap;
import java.util.Map;

import com.alipay.rdf.file.exception.RdfErrorEnum;
import com.alipay.rdf.file.exception.RdfFileException;
import com.alipay.rdf.file.interfaces.FileFactory;
import com.alipay.rdf.file.interfaces.FileReader;
import com.alipay.rdf.file.loader.TemplateLoader;
import com.alipay.rdf.file.meta.FileMeta;
import com.alipay.rdf.file.model.FileConfig;
import com.alipay.rdf.file.model.Summary;
import com.alipay.rdf.file.model.SummaryPair;
import com.alipay.rdf.file.model.ValidateResult;
import com.alipay.rdf.file.spi.RdfFileValidatorSpi;
import com.alipay.rdf.file.summary.StatisticPair;
import com.alipay.rdf.file.util.RdfFileUtil;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 *
 * 协议文件通用校验
 * <li>根据文件模板校验文件格式，包括非空字段 </li>
 * <li>根据文件模板配置的RowValidator校验每行数据</li>
 * <li>根据文件模板配置的汇总信息校验文件汇总信息和总笔数</li>
 * @author hongwei.quhw
 * @version $Id: ProtocolFileValidator.java, v 0.1 2017年8月17日 上午11:04:50 hongwei.quhw Exp $
 */
public class ProtocolFileValidator implements RdfFileValidatorSpi {
    private FileReader reader;
    private FileMeta   fileMeta;
    private FileConfig fileConfig;

    @Override
    public void init(FileConfig fileConfig) {
        this.fileConfig = fileConfig.clone();
        this.fileConfig.setSummaryEnable(true); // 打开汇总功能
        this.fileMeta = TemplateLoader.load(this.fileConfig);
        this.reader = FileFactory.createReader(this.fileConfig);
    }

    @SuppressWarnings({ "unused", "rawtypes" })
    @Override
    public ValidateResult validate() throws RdfFileException {
        try {
            ValidateResult result = new ValidateResult();
            String totalCountKey = fileMeta.getTotalCountKey();
            Object totalCount = null;
            if (fileMeta.hasHead()) {
                Map<String, Object> head = reader.readHead(HashMap.class);
                if (null == head || head.isEmpty()) {
                    result.fail("rdf-file#文件头不存在");
                    return result;
                }

                if (null != head.get(totalCountKey)) {
                    totalCount = head.get(totalCountKey);
                }
            }

            Map<String, Object> row = null;
            try {
                while (null != (row = reader.readRow(HashMap.class))) {
                }
            } catch (RdfFileException e) {
                if (RdfErrorEnum.VALIDATE_ERROR.equals(e.getErrorEnum())) {
                    result.fail(e);
                    return result;
                } else {
                    throw e;
                }
            }

            if (fileMeta.hasTail()) {
                Map<String, Object> tail = reader.readTail(HashMap.class);
                if (null == tail || tail.isEmpty()) {
                    result.fail("rdf-file#文件尾不存在");
                    return result;
                }

                if (null != tail.get(totalCountKey)) {
                    totalCount = tail.get(totalCountKey);
                }
            }

            Summary summary = reader.getSummary();

            if (null != totalCount
                && !RdfFileUtil.compare(totalCount, summary.getTotalCountWithoutNull())) {
                result.fail(String.format("文件笔数错误, 文件头中的总笔数为%s, 实际检测到的行数是%s", totalCount.toString(),
                    summary.getTotalCount().toString()));
            }

            //校验汇总字段
            for (SummaryPair pair : summary.getSummaryPairs()) {
                if (!pair.isSummaryEquals()) {
                    result.fail(pair.summaryMsg());
                }
            }

            // 校验统计字段
            for (StatisticPair pair : summary.getStatisticPairs()) {
                if (!pair.isStatisticEquals()) {
                    result.fail(pair.staticsticMsg());
                }
            }

            return result;
        } finally {
            if (null != reader) {
                reader.close();
            }
        }
    }

}
