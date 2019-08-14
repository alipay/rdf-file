package com.alipay.rdf.file.model;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alipay.rdf.file.exception.RdfErrorEnum;
import com.alipay.rdf.file.interfaces.RowValidator;
import com.alipay.rdf.file.util.RdfFileUtil;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 * 
 * 文件模块配置
 * 
 * @author hongwei.quhw
 * @version $Id: FileConfig.java, v 0.1 2016-12-22 下午3:18:02 hongwei.quhw Exp $
 */
public class FileConfig implements Cloneable {
    /**文件读写合并校验组件类型  默认 type=protocol */
    private String              type             = "protocol";
    /** 文件路径*/
    private String              filePath;
    /**文件数据类型*/
    private FileDataTypeEnum    fileDataType     = FileDataTypeEnum.ALL;
    /** 模板路径*/
    private final String        templatePath;
    /** 定义的模板编码格式*/
    private String              templateEncoding = FileDefaultConfig.DEFAULT_TEMPLATE_ENCONDIG;
    /** 读取或者生成文件的编码格式*/
    private String              fileEncoding;
    /** 写文件时换行符*/
    private String              lineBreak;
    /**存储配置*/
    private StorageConfig       storageConfig;
    /**手动触发processor*/
    private List<String>        processorKeys    = new ArrayList<String>();
    /**是否开启字段汇总功能*/
    private boolean             summaryEnable    = false;
    /**写文件时，如果没有数据是否生成空文件*/
    private boolean             createEmptyFile  = false;
    /**传入行校验器*/
    private List<RowValidator>  rowValidators    = new ArrayList<RowValidator>();

    /**此参数只有在type=raw时指定文件内容分隔符时有效*/
    private String              columnSplit;
    //===================部分读属性==================
    /** 数据所在的偏移量*/
    private long                offset;
    /** 数据的长度*/
    private long                length;
    /** 是否部分读取*/
    private boolean             isPartial;

    //=====================其他===================
    /** 写文件的时候是否在文件尾部追加*/
    private boolean             isAppend         = false;
    /** 外部构建的输入流*/
    private InputStream         is;
    /** 透传给插件的参数*/
    private Map<String, Object> params           = new HashMap<String, Object>();

    /**
     * 构造方法
     * 
     * @param filePath
     * @param templatePath
     * @param storageConfig
     */
    public FileConfig(String filePath, String templatePath, StorageConfig storageConfig) {
        RdfFileUtil.assertNotBlank(filePath, "rdf-file#文件路径不能为空!", RdfErrorEnum.ILLEGAL_ARGUMENT);
        RdfFileUtil.assertNotNull(storageConfig, "rdf-file#文件存储配置对象不能为空!",
            RdfErrorEnum.ILLEGAL_ARGUMENT);

        this.filePath = filePath;
        this.templatePath = templatePath;
        this.storageConfig = storageConfig;
    }

    /**
     * 构造方法
     * 
     * @param filePath
     * @param templatePath
     * @param storageConfig
     */
    public FileConfig(String templatePath, StorageConfig storageConfig) {
        RdfFileUtil.assertNotNull(storageConfig, "rdf-file#文件存储配置对象不能为空!",
            RdfErrorEnum.ILLEGAL_ARGUMENT);
        this.templatePath = templatePath;
        this.storageConfig = storageConfig;
    }

    /**
     * 读数据时， 输入流由外界构造
     * 
     * @param filePath
     * @param templatePath
     * @param storageConfig 存储对象可空
     */
    public FileConfig(InputStream is, String templatePath) {
        RdfFileUtil.assertNotNull(is, "rdf-file#外部构建的输入流不能为空!", RdfErrorEnum.ILLEGAL_ARGUMENT);
        this.is = is;
        this.templatePath = templatePath;
        this.storageConfig = new StorageConfig("inputstream");
    }

    /**
     * 设置部分读属性
     * 
     * @param offset
     * @param length
     */
    public void setPartial(long offset, long length, FileDataTypeEnum fileDataType) {
        this.offset = offset;
        this.length = length;
        this.isPartial = true;
        this.fileDataType = fileDataType;
    }

    public void addProcessorKey(String key) {
        if (!processorKeys.contains(key)) {
            processorKeys.add(key);
        }
    }

    public List<String> getProcessorKeys() {
        return processorKeys;
    }

    /**
     * Getter method for property <tt>filePath</tt>.
     * 
     * @return property value of filePath
     */
    public String getFilePath() {
        return filePath;
    }

    /**
     * Getter method for property <tt>templatePath</tt>.
     * 
     * @return property value of templatePath
     */
    public String getTemplatePath() {
        return templatePath;
    }

    /**
     * Getter method for property <tt>offset</tt>.
     * 
     * @return property value of offset
     */
    public long getOffset() {
        return offset;
    }

    /**
     * Getter method for property <tt>length</tt>.
     * 
     * @return property value of length
     */
    public long getLength() {
        return length;
    }

    /**
     * Getter method for property <tt>partial</tt>.
     * 
     * @return property value of partial
     */
    public boolean isPartial() {
        return isPartial;
    }

    /**
     * Getter method for property <tt>templatEncoding</tt>.
     * 
     * @return property value of templatEncoding
     */
    public String getTemplateEncoding() {
        return templateEncoding;
    }

    /**
     * Setter method for property <tt>templatEncoding</tt>.
     * 
     * @param templatEncoding value to be assigned to property templatEncoding
     */
    public void setTemplateEncoding(String templatEncoding) {
        this.templateEncoding = templatEncoding;
    }

    /**
     * Getter method for property <tt>isAppend</tt>.
     * 
     * @return property value of isAppend
     */
    public boolean isAppend() {
        return isAppend;
    }

    /**
     * Setter method for property <tt>isAppend</tt>.
     * 
     * @param isAppend value to be assigned to property isAppend
     */
    public void setAppend(boolean isAppend) {
        this.isAppend = isAppend;
    }

    /**
     * Setter method for property <tt>filePath</tt>.
     * 
     * @param filePath value to be assigned to property filePath
     */
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Getter method for property <tt>fileEncoding</tt>.
     * 
     * @return property value of fileEncoding
     */
    public String getFileEncoding() {
        return fileEncoding;
    }

    /**
     * Setter method for property <tt>fileEncoding</tt>.
     * 
     * @param fileEncoding value to be assigned to property fileEncoding
     */
    public void setFileEncoding(String fileEncoding) {
        this.fileEncoding = fileEncoding;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public StorageConfig getStorageConfig() {
        return storageConfig;
    }

    public String getLineBreak() {
        return lineBreak;
    }

    public void setLineBreak(String lineBreak) {
        this.lineBreak = lineBreak;
    }

    public boolean isSummaryEnable() {
        return summaryEnable;
    }

    public void setSummaryEnable(boolean summaryEnable) {
        this.summaryEnable = summaryEnable;
    }

    public void setStorageConfig(StorageConfig storageConfig) {
        this.storageConfig = storageConfig;
    }

    public FileDataTypeEnum getFileDataType() {
        return fileDataType;
    }

    public void setFileDataType(FileDataTypeEnum fileDataType) {
        this.fileDataType = fileDataType;
    }

    public List<RowValidator> getRowValidators() {
        return rowValidators;
    }

    public void addRowValidator(RowValidator rowValidator) {
        rowValidators.add(rowValidator);
    }

    private void setRowValidators(List<RowValidator> rowValidators) {
        this.rowValidators = rowValidators;
    }

    public InputStream getInputStream() {
        return is;
    }

    public void setInputStream(InputStream is) {
        this.is = is;
    }

    public String getColumnSplit() {
        return columnSplit;
    }

    public void setColumnSplit(String columnSplit) {
        this.columnSplit = columnSplit;
    }

    public void addParam(String key, Object value) {
        params.put(key, value);
    }

    public Object getParam(String key) {
        return params.get(key);
    }

    public boolean isCreateEmptyFile() {
        return createEmptyFile;
    }

    public void setCreateEmptyFile(boolean createEmptyFile) {
        this.createEmptyFile = createEmptyFile;
    }

    /**
     * 
     * @see java.lang.Object#clone()
     */
    @Override
    public FileConfig clone() {
        FileConfig config = null;
        if (RdfFileUtil.isBlank(filePath)) {
            config = new FileConfig(templatePath, storageConfig);
        } else {
            config = new FileConfig(filePath, templatePath, storageConfig);
        }

        if (isPartial) {
            config.setPartial(offset, length, fileDataType);
        }
        config.setType(type);
        config.setAppend(isAppend);
        config.setTemplateEncoding(templateEncoding);
        config.setFileEncoding(fileEncoding);
        config.setLineBreak(lineBreak);
        config.setSummaryEnable(summaryEnable);
        config.setCreateEmptyFile(createEmptyFile);
        config.setFileDataType(fileDataType);
        config.setRowValidators(rowValidators);
        config.setInputStream(is);
        config.setColumnSplit(columnSplit);
        config.params = params;
        for(String processKey : processorKeys){
            config.addProcessorKey(processKey);
        }
        return config;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer("FileConfig[");
        sb.append("type=").append(type);
        sb.append(",filepath=").append(filePath);
        sb.append(",fileDataType=").append(fileDataType.name());
        sb.append(",fileEncoding=").append(fileEncoding);
        sb.append(",templatePath=").append(templatePath);
        sb.append(",templateEncoding=").append(templateEncoding);
        sb.append(",lineBreak=").append(lineBreak);
        if (processorKeys.size() > 0) {
            sb.append(", processorKeys=");
            for (int i = 0; i < processorKeys.size(); i++) {
                sb.append(processorKeys.get(i));
                if (i < processorKeys.size() - 1) {
                    sb.append("|");
                }
            }
        }
        if (rowValidators.size() > 0) {
            sb.append(", rowValidators=");
            for (int i = 0; i < rowValidators.size(); i++) {
                sb.append(rowValidators.get(i).getClass().getName());
                if (i < rowValidators.size() - 1) {
                    sb.append("|");
                }
            }
        }

        if (null != storageConfig) {
            sb.append(",storageType=").append(storageConfig.getStorageType());
        } else {
            sb.append(",storageType=").append("null");
        }
        sb.append(",summaryEnable=").append(summaryEnable);
        sb.append(",createEmptyFile=").append(createEmptyFile);
        if (isPartial) {
            sb.append(", offset=").append(offset);
            sb.append(",length=").append(length);
        }
        sb.append(",isAppend=").append(isAppend);
        sb.append(", is=").append(is);
        sb.append("]");
        return sb.toString();
    }
}
