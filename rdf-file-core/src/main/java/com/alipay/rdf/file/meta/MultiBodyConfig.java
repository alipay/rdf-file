package com.alipay.rdf.file.meta;

import java.util.List;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 *
 * body多个模板配置
 *
 * @author hongwei.quhw
 * @version $Id: MultiBodysConfig.java, v 0.1 2018年10月11日 下午5:10:50 hongwei.quhw Exp $
 */
public class MultiBodyConfig {
    private String       name;
    private String       condition;
    private List<String> bodyColumns;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public List<String> getBodyColumns() {
        return bodyColumns;
    }

    public void setBodyColumns(List<String> bodyColumns) {
        this.bodyColumns = bodyColumns;
    }
}
