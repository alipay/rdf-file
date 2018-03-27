package com.alipay.rdf.file.model;

import java.util.List;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 * 
 * 排序结果对象
 * 
 * @author hongwei.quhw
 * @version $Id: SortResult.java, v 0.1 2017年8月22日 下午9:57:08 hongwei.quhw Exp $
 */
public class SortResult {
    // 返回一个排好序的文件
    /** ResultFileTypeEnum.FULL_FILE_PATH   整个排完序文件path*/
    private String       fullFilePath;
    // 分片排序， 分片文件跟头文件分离
    /**ResultFileTypeEnum.SLICE_FILE_PATH  分片头路径*/
    private String       headSlicePath;
    /**ResultFileTypeEnum.SLICE_FILE_PATH  分片体路径*/
    private List<String> bodySlicePath;
    /**ResultFileTypeEnum.SLICE_FILE_PATH  分片尾路径*/
    private String       tailSlicePath;

    public String getFullFilePath() {
        return fullFilePath;
    }

    public void setFullFilePath(String fullFilePath) {
        this.fullFilePath = fullFilePath;
    }

    public String getHeadSlicePath() {
        return headSlicePath;
    }

    public void setHeadSlicePath(String headSlicePath) {
        this.headSlicePath = headSlicePath;
    }

    public List<String> getBodySlicePath() {
        return bodySlicePath;
    }

    public void setBodySlicePath(List<String> bodySlicePath) {
        this.bodySlicePath = bodySlicePath;
    }

    public String getTailSlicePath() {
        return tailSlicePath;
    }

    public void setTailSlicePath(String tailSlicePath) {
        this.tailSlicePath = tailSlicePath;
    }
}
