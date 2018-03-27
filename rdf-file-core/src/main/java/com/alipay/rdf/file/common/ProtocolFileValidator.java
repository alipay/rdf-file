package com.alipay.rdf.file.common;

import java.util.HashMap;
import java.util.Map;

import com.alipay.rdf.file.exception.RdfErrorEnum;
import com.alipay.rdf.file.exception.RdfFileException;
import com.alipay.rdf.file.interfaces.FileFactory;
import com.alipay.rdf.file.interfaces.FileReader;
import com.alipay.rdf.file.loader.SummaryLoader;
import com.alipay.rdf.file.loader.TemplateLoader;
import com.alipay.rdf.file.meta.FileMeta;
import com.alipay.rdf.file.model.FileConfig;
import com.alipay.rdf.file.model.Summary;
import com.alipay.rdf.file.model.SummaryPair;
import com.alipay.rdf.file.model.ValidateResult;
import com.alipay.rdf.file.spi.RdfFileSummaryPairSpi;
import com.alipay.rdf.file.spi.RdfFileValidatorSpi;

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

    @Override
    public void init(FileConfig fileConfig) {
        this.fileMeta = TemplateLoader.load(fileConfig);
        this.reader = FileFactory.createReader(fileConfig);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public ValidateResult validate() {
        ValidateResult result = new ValidateResult();

        Summary summary = SummaryLoader.getNewSummary(fileMeta);
        String totalCountKey = fileMeta.getTotalCountKey();
        boolean validateTotalCount = false;

        if (fileMeta.hasHead()) {
            Map<String, Object> head = reader.readHead(HashMap.class);
            if (null == head || head.isEmpty()) {
                result.fail("rdf-file#文件头不存在");
                return result;
            }

            if (null != head.get(totalCountKey)) {
                summary.addTotalCount(head.get(totalCountKey));
                validateTotalCount = true;
            }

            for (SummaryPair headPair : summary.getHeadSummaryPairs()) {
                Object val = head.get(headPair.getHeadKey());
                if (null == val) {
                    result.fail("rdf-file#汇总字段" + headPair.getHeadKey() + "不存在");
                    return result;
                }
                ((RdfFileSummaryPairSpi) headPair).setHeadValue(val);
            }
        }

        long rowCount = 0;
        Map<String, Object> row = null;
        try {
            while (null != (row = reader.readRow(HashMap.class))) {
                rowCount++;
                for (SummaryPair pair : summary.getSummaryPairs()) {
                    ((RdfFileSummaryPairSpi) pair).addColValue(row.get(pair.getColumnKey()));
                }
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
                summary.addTotalCount(tail.get(totalCountKey));
                validateTotalCount = true;
            }

            for (SummaryPair tailPair : summary.getTailSummaryPairs()) {
                Object val = tail.get(tailPair.getTailKey());
                if (null == val) {
                    result.fail("rdf-file#汇总字段" + tailPair.getTailKey() + "不存在");
                    return result;
                }
                ((RdfFileSummaryPairSpi) tailPair).setTailValue(val);
            }
        }

        if (validateTotalCount && summary.getTotalCountToLong() != rowCount) {
            result.fail(String.format("文件笔数错误, 文件头中的总笔数为%d, 实际检测到的行数是%d", summary.getTotalCount(),
                rowCount));
        }

        //校验汇总字段
        for (SummaryPair pair : summary.getSummaryPairs()) {
            if (!pair.isSummaryEquals()) {
                result.fail(pair.summaryMsg());
            }
        }

        return result;
    }

}
