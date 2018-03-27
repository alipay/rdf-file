package com.alipay.rdf.file.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alipay.rdf.file.exception.RdfErrorEnum;
import com.alipay.rdf.file.exception.RdfFileException;
import com.alipay.rdf.file.util.RdfFileUtil;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 *
 * @author hongwei.quhw
 * @version $Id: Summary.java, v 0.1 2018年3月12日 下午4:22:36 hongwei.quhw Exp $
 */
@SuppressWarnings("rawtypes")
public class Summary {
    private String                  totalCountKey;
    /** 总记录数*/
    private Object                  totalCount;
    /** 汇总字段*/
    private final List<SummaryPair> headSummaryPairs = new ArrayList<SummaryPair>();
    private final List<SummaryPair> tailSummaryPairs = new ArrayList<SummaryPair>();
    private final List<SummaryPair> summaryPairs     = new ArrayList<SummaryPair>();

    public void addSummaryPair(SummaryPair pair) {
        if (RdfFileUtil.isNotBlank(pair.getHeadKey())) {
            headSummaryPairs.add(pair);
        }
        if (RdfFileUtil.isNotBlank(pair.getTailKey())) {
            tailSummaryPairs.add(pair);
        }

        summaryPairs.add(pair);
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

    public Long getTotalCountToLong() {
        if (totalCount instanceof BigDecimal) {
            return ((BigDecimal) totalCount).longValue();
        } else if (totalCount instanceof Long) {
            return (Long) totalCount;
        } else if (totalCount instanceof Integer) {
            return new Long(totalCount.toString());
        } else {
            throw new RdfFileException(
                "rdf-file#Summary.addTotalCount  不支持计算类型为:" + totalCount.getClass().getName(),
                RdfErrorEnum.COLUMN_TYPE_ERROR);
        }
    }

    public Map<String, Object> summaryHeadToMap() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(totalCountKey, totalCount);

        for (SummaryPair summaryPair : headSummaryPairs) {
            map.put(summaryPair.getHeadKey(), summaryPair.getSummaryValue());
        }

        return map;
    }

    public Map<String, Object> summaryTailToMap() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(totalCountKey, totalCount);

        for (SummaryPair summaryPair : tailSummaryPairs) {
            map.put(summaryPair.getTailKey(), summaryPair.getSummaryValue());
        }

        return map;
    }
}
