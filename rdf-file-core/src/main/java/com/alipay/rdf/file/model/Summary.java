package com.alipay.rdf.file.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alipay.rdf.file.exception.RdfErrorEnum;
import com.alipay.rdf.file.exception.RdfFileException;
import com.alipay.rdf.file.meta.StatisticPairMeta;
import com.alipay.rdf.file.summary.StatisticPair;
import com.alipay.rdf.file.util.RdfFileUtil;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 *
 * @author hongwei.quhw
 * @version $Id: Summary.java, v 0.1 2018年3月12日 下午4:22:36 hongwei.quhw Exp $
 */
@SuppressWarnings("rawtypes")
public class Summary {
    private String                    totalCountKey;
    /** 总记录数*/
    private Object                    totalCount;
    /** 汇总字段*/
    private final List<SummaryPair>   headSummaryPairs   = new ArrayList<SummaryPair>();
    private final List<SummaryPair>   tailSummaryPairs   = new ArrayList<SummaryPair>();
    private final List<SummaryPair>   summaryPairs       = new ArrayList<SummaryPair>();

    private final List<StatisticPair> headStatisticPairs = new ArrayList<StatisticPair>();
    private final List<StatisticPair> tailStatisticPairs = new ArrayList<StatisticPair>();
    private final List<StatisticPair> statisticPairs     = new ArrayList<StatisticPair>();

    public void addSummaryPair(SummaryPair pair) {
        if (RdfFileUtil.isNotBlank(pair.getHeadKey())) {
            headSummaryPairs.add(pair);
        }
        if (RdfFileUtil.isNotBlank(pair.getTailKey())) {
            tailSummaryPairs.add(pair);
        }

        summaryPairs.add(pair);
    }

    public void addStatisticPair(StatisticPairMeta pairMeta) {
        StatisticPair pair = new StatisticPair(pairMeta);
        if (FileDataTypeEnum.HEAD.equals(pairMeta.getStatisticdataType())) {
            headStatisticPairs.add(pair);
        }
        if (FileDataTypeEnum.TAIL.equals(pairMeta.getStatisticdataType())) {
            tailStatisticPairs.add(pair);
        }

        statisticPairs.add(pair);
    }

    public List<SummaryPair> getHeadSummaryPairs() {
        return headSummaryPairs;
    }

    public List<SummaryPair> getTailSummaryPairs() {
        return tailSummaryPairs;
    }

    public List<SummaryPair> getSummaryPairs() {
        return summaryPairs;
    }

    public List<StatisticPair> getHeadStatisticPairs() {
        return headStatisticPairs;
    }

    public List<StatisticPair> getTailStatisticPairs() {
        return tailStatisticPairs;
    }

    public List<StatisticPair> getStatisticPairs() {
        return statisticPairs;
    }

    /**
     * 总记录加加
     */
    public void addTotalCount(Object count) {
        RdfFileUtil.assertNotNull(count, "rdf-file#Summary.addTotalCount(Object count== null)");
        if (count instanceof BigDecimal) {
            if (totalCount == null) {
                totalCount = count;
            } else {
                totalCount = ((BigDecimal) totalCount).add((BigDecimal) count);
            }
        } else if (count instanceof Long) {
            if (null == totalCount) {
                totalCount = count;
            } else {
                totalCount = (Long) totalCount + (Long) count;
            }
        } else if (count instanceof Integer) {
            if (null == totalCount) {
                totalCount = count;
            } else {
                totalCount = (Integer) totalCount + (Integer) count;
            }
        } else {
            throw new RdfFileException(
                "rdf-file#Summary.addTotalCount  不支持计算类型为:" + count.getClass().getName(),
                RdfErrorEnum.COLUMN_TYPE_ERROR);
        }
    }

    public void setTotalCountKey(String totalCountKey) {
        this.totalCountKey = totalCountKey;
    }

    public Object getTotalCount() {
        return totalCount;
    }

    public Object getTotalCountWithoutNull() {
        return totalCount == null ? 0L : totalCount;
    }

    public Map<String, Object> summaryHeadToMap() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(totalCountKey, totalCount);

        for (SummaryPair summaryPair : headSummaryPairs) {
            map.put(summaryPair.getHeadKey(), summaryPair.getSummaryValue());
        }

        for (StatisticPair pair : headStatisticPairs) {
            map.put(pair.getHeadKey(), pair.getStaticsticValue());
        }

        return map;
    }

    public Map<String, Object> summaryTailToMap() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(totalCountKey, totalCount);

        for (SummaryPair summaryPair : tailSummaryPairs) {
            map.put(summaryPair.getTailKey(), summaryPair.getSummaryValue());
        }

        for (StatisticPair pair : tailStatisticPairs) {
            map.put(pair.getTailKey(), pair.getStaticsticValue());
        }

        return map;
    }

}
