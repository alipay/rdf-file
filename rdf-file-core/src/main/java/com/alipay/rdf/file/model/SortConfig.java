package com.alipay.rdf.file.model;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 * 
 * 排序请求参数对象
 * 
 * @author hongwei.quhw
 * @version $Id: SortConfig.java, v 0.1 2017年6月23日 上午11:04:16 hongwei.quhw Exp $
 */
public class SortConfig implements Cloneable {
    /**
     * 指定排序字段索引， 
     * 如排序字段值相等整行排序 
     * 如sortIndexes没有指定，整行排序
     */
    private int                      sortIndexes[];
    /**排序临时文件存放目录地址*/
    private final String             resultPath;
    /**结果文件名 非必填*/
    private String                   resultFileName;
    /**排序方式*/
    private final SortTypeEnum       sortType;
    /**指定头包含多少行, 文件头不参与排序*/
    private int                      headLines;
    /**排序使用的线程池*/
    private final ThreadPoolExecutor executor;
    /**返回文件类型*/
    private final ResultFileTypeEnum resultFileType;
    /**对字段重新排序 如：{5,3,6,0,2,1}*/
    private int[]                    columnRearrangeIndex;
    /**行过滤器*/
    private RowFilter[]              rowFilters = new RowFilter[0];
    /**分片大小默认1M*/
    private int                      sliceSize  = 1024 * 1024;
    /**目标文件存储, 默认存放在nas*/
    private StorageConfig            resultStorageConfig;
    /**覆盖FileConfig.filePath参数*/
    private String                   sourceFilePaths[];

    /**
     * @param sortTempPath          排序临时文件存放目录地址
     * @param executor              排序使用的线程池    
     * @param resultFileType        返回文件类型
     */
    public SortConfig(String resultPath, SortTypeEnum sortType, ThreadPoolExecutor executor,
                      ResultFileTypeEnum resultFileType) {
        this.resultPath = resultPath;
        this.sortType = sortType;
        this.executor = executor;
        this.resultFileType = resultFileType;
    }

    @Override
    public SortConfig clone() {
        SortConfig sortConfig = new SortConfig(resultPath, sortType, executor, resultFileType);
        sortConfig.setColumnRearrangeIndex(columnRearrangeIndex);
        sortConfig.setHeadLines(headLines);
        sortConfig.setResultFileName(resultFileName);
        sortConfig.setResultStorageConfig(resultStorageConfig);
        sortConfig.setRowFilters(rowFilters);
        sortConfig.setSliceSize(sliceSize);
        sortConfig.setSortIndexes(sortIndexes);
        sortConfig.setSourceFilePaths(sourceFilePaths);
        return sortConfig;
    }

    /**
     * Getter method for property <tt>resultStorageConfig</tt>.
     * 
     * @return property value of resultStorageConfig
     */
    public StorageConfig getResultStorageConfig() {
        return resultStorageConfig;
    }

    /**
     * Setter method for property <tt>resultStorageConfig</tt>.
     * 
     * @param resultStorageConfig value to be assigned to property resultStorageConfig
     */
    public void setResultStorageConfig(StorageConfig resultStorageConfig) {
        this.resultStorageConfig = resultStorageConfig;
    }

    /**
     * Getter method for property <tt>sortType</tt>.
     * 
     * @return property value of sortType
     */
    public SortTypeEnum getSortType() {
        return sortType;
    }

    /**
     * Setter method for property <tt>sliceSize</tt>.
     * 
     * @param resultFileName value to be assigned to property resultFileName
     */
    public void setResultFileName(String resultFileName) {
        this.resultFileName = resultFileName;
    }

    /**
     * Getter method for property <tt>resultFileName</tt>.
     * 
     * @return property value of resultFileName
     */
    public String getResultFileName() {
        return resultFileName;
    }

    /**
     * Getter method for property <tt>sliceSize</tt>.
     * 
     * @return property value of sliceSize
     */
    public int getSliceSize() {
        return sliceSize;
    }

    /**
     * Setter method for property <tt>sliceSize</tt>.
     * 
     * @param sliceSize value to be assigned to property sliceSize
     */
    public void setSliceSize(int sliceSize) {
        this.sliceSize = sliceSize;
    }

    public int[] getColumnRearrangeIndex() {
        return columnRearrangeIndex;
    }

    public void setColumnRearrangeIndex(int[] columnRearrangeIndex) {
        this.columnRearrangeIndex = columnRearrangeIndex;
    }

    /**
     * Getter method for property <tt>rowFilters</tt>.
     * 
     * @return property value of rowFilters
     */
    public RowFilter[] getRowFilters() {
        return rowFilters;
    }

    /**
     * Setter method for property <tt>rowFilters</tt>.
     * 
     * @param rowFilters value to be assigned to property rowFilters
     */
    public void setRowFilters(RowFilter[] rowFilters) {
        this.rowFilters = rowFilters;
    }

    /**
     * Getter method for property <tt>sortIndexes</tt>.
     * 
     * @return property value of sortIndexes
     */
    public int[] getSortIndexes() {
        return sortIndexes;
    }

    /**
     * Setter method for property <tt>sortIndexes</tt>.
     * 
     * @param sortIndexes value to be assigned to property sortIndexes
     */
    public void setSortIndexes(int[] sortIndexes) {
        this.sortIndexes = sortIndexes;
    }

    /**
     * Getter method for property <tt>headLines</tt>.
     * 
     * @return property value of headLines
     */
    public int getHeadLines() {
        return headLines;
    }

    /**
     * Setter method for property <tt>headLines</tt>.
     * 
     * @param headLines value to be assigned to property headLines
     */
    public void setHeadLines(int headLines) {
        this.headLines = headLines;
    }

    /**
     * Getter method for property <tt>sortTempPath</tt>.
     * 
     * @return property value of sortTempPath
     */
    public String getResultPath() {
        return resultPath;
    }

    /**
     * Getter method for property <tt>executor</tt>.
     * 
     * @return property value of executor
     */
    public ThreadPoolExecutor getExecutor() {
        return executor;
    }

    /**
     * Getter method for property <tt>resultFileType</tt>.
     * 
     * @return property value of resultFileType
     */
    public ResultFileTypeEnum getResultFileType() {
        return resultFileType;
    }

    public String[] getSourceFilePaths() {
        return sourceFilePaths;
    }

    public void setSourceFilePaths(String[] sourceFilePaths) {
        this.sourceFilePaths = sourceFilePaths;
    }

    /**
     * 返回文件类型
     * 
     * @author hongwei.quhw
     * @version $Id: SortConfig.java, v 0.1 2017年6月23日 上午11:38:22 hongwei.quhw Exp $
     */
    public enum ResultFileTypeEnum {
                                    /**分片文件path*/
                                    SLICE_FILE_PATH,
                                    /**整个排完序文件path*/
                                    FULL_FILE_PATH
    }

    public enum SortTypeEnum {
                              /**升序*/
                              ASC,
                              /**降序*/
                              DESC
    }
}
