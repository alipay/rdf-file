package com.alipay.rdf.file.meta;

import java.util.ArrayList;
import java.util.List;

import com.alipay.rdf.file.exception.RdfErrorEnum;
import com.alipay.rdf.file.exception.RdfFileException;
import com.alipay.rdf.file.spi.RdfFileRowConditionSpi;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 *
 * 文件体元数据定
 *
 * @author hongwei.quhw
 * @version $Id: FileBodyMeta.java, v 0.1 2018年10月11日 下午5:52:18 hongwei.quhw Exp $
 */
public class FileBodyMeta {
    /**body模板名*/
    private String                 name    = "singleBody";
    /**模板路径*/
    private String                 templatePath;
    /**行条件计算器*/
    private RdfFileRowConditionSpi rowCondition;
    /**定义的body字段*/
    private List<FileColumnMeta>   columns = new ArrayList<FileColumnMeta>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public RdfFileRowConditionSpi getRowCondition() {
        return rowCondition;
    }

    public void setRowCondition(RdfFileRowConditionSpi rowCondition) {
        this.rowCondition = rowCondition;
    }

    public List<FileColumnMeta> getColumns() {
        return columns;
    }

    public void setColumns(List<FileColumnMeta> columns) {
        this.columns = columns;
    }

    public String getTemplatePath() {
        return templatePath;
    }

    public void setTemplatePath(String templatePath) {
        this.templatePath = templatePath;
    }

    public FileColumnMeta getColumn(String columName) {
        for (FileColumnMeta col : columns) {
            if (col.getName().equals(columName)) {
                return col;
            }
        }

        throw new RdfFileException("rdf-file#FileMeta.FileBodyMeta templatePath=[" + templatePath
                                   + "]  bodyTempldateName=[" + name + "], columName=[" + columName
                                   + "] 有没有定义",
            RdfErrorEnum.COLUMN_NOT_DEFINED);
    }
}
