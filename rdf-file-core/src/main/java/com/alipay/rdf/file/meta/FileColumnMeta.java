package com.alipay.rdf.file.meta;

import com.alipay.rdf.file.model.FileDataTypeEnum;
import org.antlr.v4.runtime.tree.ParseTree;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 * 
 * 字段元数据
 * 
 * @author hongwei.quhw
 * @version $Id: FileColumnMeta.java, v 0.1 2016-12-20 下午4:21:41 hongwei.quhw Exp $
 */
public class FileColumnMeta {
    /**列的顺序*/
    private final int                 colIndex;
    /**字段的名称, 键值, 用于获取数据*/
    private final String              name;
    /**文件中的显示的名称*/
    private final String              desc;
    /**数据类型*/
    private final FileColumnTypeMeta  type;
    /**定义数据的范围*/
    private final FileColumnRangeMeta range;
    /**是否必填*/
    private final boolean             required;
    /**默认值配置*/
    private final String              defaultValue;
    private final FileMeta            fileMeta;
    /**数据字段在文件中部位*/
    private final FileDataTypeEnum    dataType;

    private final ParseTree           generateRule;

    public FileColumnMeta(int colIndex, String name, String desc, FileColumnTypeMeta type,
                          boolean required, FileColumnRangeMeta range, String defaultValue,
                          FileMeta fileMeta, FileDataTypeEnum    dataType, ParseTree generateRule) {
        this.colIndex = colIndex;
        this.desc = desc;
        this.name = name;
        this.type = type;
        this.range = range;
        this.required = required;
        this.defaultValue = defaultValue;
        this.fileMeta = fileMeta;
        this.dataType = dataType;
        this.generateRule = generateRule;
    }

    /**
     * Getter method for property <tt>colIndex</tt>.
     * 
     * @return property value of colIndex
     */
    public int getColIndex() {
        return colIndex;
    }

    /**
     * Getter method for property <tt>name</tt>.
     * 
     * @return property value of name
     */
    public String getName() {
        return name;
    }

    /**
     * Getter method for property <tt>type</tt>.
     * 
     * @return property value of type
     */
    public FileColumnTypeMeta getType() {
        return type;
    }

    /**
     * Getter method for property <tt>range</tt>.
     * 
     * @return property value of range
     */
    public FileColumnRangeMeta getRange() {
        return range;
    }

    /**
     * Getter method for property <tt>required</tt>.
     * 
     * @return property value of required
     */
    public boolean isRequired() {
        return required;
    }

    /**
     * Getter method for property <tt>defaultValue</tt>.
     * 
     * @return property value of defaultValue
     */
    public String getDefaultValue() {
        return defaultValue;
    }

    /**
     * Getter method for property <tt>desc</tt>.
     * 
     * @return property value of desc
     */
    public String getDesc() {
        return desc;
    }

    /**
     * Getter method for property <tt>fileMeta</tt>.
     * 
     * @return property value of fileMeta
     */
    public FileMeta getFileMeta() {
        return fileMeta;
    }

    public FileDataTypeEnum getDataType() {
        return dataType;
    }

    public ParseTree getGenerateRule() {return generateRule; }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer("FileColumnMeta[");
        sb.append("colIndex=").append(colIndex);
        sb.append(",name=").append(name);
        sb.append(",desc=").append(desc);
        sb.append(",type=").append(type.getName());
        sb.append(",range=").append(range);
        sb.append(",required=").append(required);
        sb.append(",dataType=").append(dataType.name());
        sb.append(",generateType=").append(generateRule);
        sb.append("]");
        return sb.toString();
    }
}
