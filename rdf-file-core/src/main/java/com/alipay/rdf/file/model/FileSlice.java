package com.alipay.rdf.file.model;

import java.io.File;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 * 
 * 文件分片的信息
 * 
 * @author hongwei.quhw
 * @version $Id: FileSlice.java, v 0.1 2016-12-20 下午4:39:11 hongwei.quhw Exp $
 */
public class FileSlice {
    private final FileDataTypeEnum fileDataType;
    private final String           filePath;
    private final long             start;
    private final long             end;
    private final String           key;

    /**
     * 初始化一个文件分片的信息, 表示了从start ~ end之间的范围, 其中包括start, 不包括end
     * @param start 起始位置
     * @param end   终止位置
     */
    public FileSlice(String filePath, FileDataTypeEnum fileDataType, long start, long end) {
        this.filePath = filePath;
        this.fileDataType = fileDataType;
        this.start = start;
        this.end = end;
        this.key = new File(filePath).getName() + "-" + start + "-" + end;
    }

    public FileDataTypeEnum getFileDataType() {
        return fileDataType;
    }

    /**
     * Getter method for property <tt>start</tt>.
     * 
     * @return property value of start
     */
    public long getStart() {
        return start;
    }

    /**
     * Getter method for property <tt>end</tt>.
     * 
     * @return property value of end
     */
    public long getEnd() {
        return end;
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
     * 返回长度, end - start
     */
    public long getLength() {
        return getEnd() - getStart();
    }

    /**
     * Getter method for property <tt>key</tt>.
     * 
     * @return property value of key
     */
    public String getKey() {
        return key;
    }

    @Override
    public String toString() {
        return getKey();
    }
}
