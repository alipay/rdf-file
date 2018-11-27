package com.alipay.rdf.file.model;

import com.alipay.rdf.file.condition.RowConditionType;
import com.alipay.rdf.file.meta.FileMeta;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 *
 * 行条件值
 *
 * @author hongwei.quhw
 * @version $Id: RowCondition.java, v 0.1 2018年11月13日 下午4:46:10 hongwei.quhw Exp $
 */
public class RowCondition {
    private final FileMeta         fileMeta;
    private final String           bodyTemplateName;
    private final String           conditionConfig;
    private final RowConditionType type;
    private String                 conditionParam;

    public RowCondition(FileMeta fileMeta, String bodyTemplateName, String conditionConfig,
                        RowConditionType type) {
        super();
        this.fileMeta = fileMeta;
        this.bodyTemplateName = bodyTemplateName;
        this.conditionConfig = conditionConfig;
        this.type = type;
    }

    public FileMeta getFileMeta() {
        return fileMeta;
    }

    public String getBodyTemplateName() {
        return bodyTemplateName;
    }

    public RowConditionType getType() {
        return type;
    }

    public String getConditionConfig() {
        return conditionConfig;
    }

    public String getConditionParam() {
        return conditionParam;
    }

    public void setConditionParam(String conditionParam) {
        this.conditionParam = conditionParam;
    }

    @Override
    public String toString() {
        return "RowCondition templatePath=" + fileMeta.getTemplatePath() + ", isMutiBody="
               + fileMeta.isMultiBody() + ", rowConditionType=" + type.name() + ", conditionConfig="
               + conditionConfig;
    }
}
