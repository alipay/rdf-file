package com.alipay.rdf.file.loader;

import com.alipay.rdf.file.exception.RdfErrorEnum;
import com.alipay.rdf.file.exception.RdfFileException;
import com.alipay.rdf.file.model.RowCondition;
import com.alipay.rdf.file.spi.RdfFileRowConditionSpi;
import com.alipay.rdf.file.util.RdfFileUtil;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 *
 * 行条件判定器加载
 *
 * @author hongwei.quhw
 * @version $Id: RowConditionLoader.java, v 0.1 2018年11月13日 下午4:39:42 hongwei.quhw Exp $
 */
public class RowConditionLoader {

    public static RdfFileRowConditionSpi loadRowCondition(RowCondition condition) {

        String[] conditions = condition.getConditionConfig().split(":");
        if (conditions.length > 2) {
            throw new RdfFileException(
                "rdf-file#RowConditionLoader   condition配置格式错误 condition=" + condition,
                RdfErrorEnum.CONDITION_ERROR);
        }

        // 默认使用基于表达式行条件计算器
        String conditionType = conditions.length == 2 ? conditions[0] : "match";
        String conditionParam = conditions.length == 2 ? conditions[1] : conditions[0];

        RdfFileRowConditionSpi rowCondition = ExtensionLoader
            .getExtensionLoader(RdfFileRowConditionSpi.class).getNewExtension(conditionType);
        RdfFileUtil.assertNotNull(rowCondition, "rdf-file#RowConditionLoader " + condition
                                                + ", rowCondition=" + conditionType + " 没有对应实现类");

        condition.setConditionParam(conditionParam);
        rowCondition.init(condition);

        return rowCondition;
    }
}
