package com.alipay.rdf.file.model;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 *
 * @author hongwei.quhw
 * @version $Id: FileInfo.java, v 0.1 2018年3月12日 下午4:21:59 hongwei.quhw Exp $
 */
public class FileInfo {
    /** 文件名 */
    private String                    fileName;
    /** 是否存在 */
    private boolean                   exists;
    /** 文件大小 */
    private long                      size;
    /** 最后修改时间*/
    private Date                      lastModifiedDate;

    /** 非用户自定义的元数据。 */
    private final Map<String, Object> metadata     = new HashMap<String, Object>();

    /** 用户自定义的元数据，表示以x-oss-meta-为前缀的请求头。 */
    private final Map<String, String> userMetadata = new HashMap<String, String>();

    /**
     * Getter method for property <tt>fileName</tt>.
     *
     * @return property value of exists
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Setter method for property <tt>fileName</tt>.
     *
     * @return property value of exists
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * Getter method for property <tt>exists</tt>.
     *
     * @return property value of exists
     */
    public boolean isExists() {
        return exists;
    }

    /**
     * Setter method for property <tt>exists</tt>.
     *
     * @param exists value to be assigned to property exists
     */
    public void setExists(boolean exists) {
        this.exists = exists;
    }

    /**
     * Getter method for property <tt>size</tt>.
     *
     * @return property value of size
     */
    public long getSize() {
        return size;
    }

    /**
     * Getter method for property <tt>lastModifiedDate</tt>.
     * 
     * @return property value of lastModifiedDate
     */
    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    /**
     * Setter method for property <tt>lastModifiedDate</tt>.
     * 
     * @param lastModifiedDate value to be assigned to property lastModifiedDate
     */
    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    /**
     * Setter method for property <tt>size</tt>.
     *
     * @param size value to be assigned to property size
     */
    public void setSize(long size) {
        this.size = size;
    }

    /**
     * Getter method for property <tt>metadata</tt>.
     * 
     * @return property value of metadata
     */
    public Map<String, Object> getMetadata() {
        return metadata;
    }

    /**
     * 根据Key获取元数据
     * 
     * @param key
     * @return
     */
    public Object getMetadata(String key) {
        return metadata.get(key);
    }

    /**
     * Getter method for property <tt>userMetadata</tt>.
     * 
     * @return property value of userMetadata
     */
    public Map<String, String> getUserMetadata() {
        return userMetadata;
    }

    /**
     * 根据Key获取用户元数据
     * 
     * @param key
     * @return
     */
    public String getUserMetadata(String key) {
        return userMetadata.get(key);
    }

    @Override
    public String toString() {
        return "FileInfo[fileName=" + fileName + ", size=" + size + "]";
    }
}
