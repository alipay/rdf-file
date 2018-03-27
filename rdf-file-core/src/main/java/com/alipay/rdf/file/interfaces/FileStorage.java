package com.alipay.rdf.file.interfaces;

import java.io.IOException;
import java.util.List;

import com.alipay.rdf.file.model.FileInfo;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 * 
 * @author hongwei.quhw
 * @version $Id: FileStorage.java, v 0.1 2017年4月7日 下午3:46:01 hongwei.quhw Exp $
 */
public interface FileStorage {

    /**
     * 创建一个空文件
     * <li>不能创建文件夹
     * <li>如果创建文件前文件已存在则覆盖原文件
     * <li>如果创建文件失败抛出RuntimeException
     * 
     * @param filePath
     * @throws IOException  
     */
    void createNewFile(String filePath);

    /**
     * 获取文件信息
     * 
     * @param filePath
     * @return
     */
    FileInfo getFileInfo(String filePath);

    /**
     * 只查询当前文件夹下的文件和文件夹全路径
     * 
     * @param folderName
     * @param regexs
     * @return
     */
    List<String> listFiles(String folderName, String[] regexs);

    /**
     * 只查询当前文件夹下的文件和文件夹全路径
     * 
     * @param folderName
     * @param fileFilters 
     * @return
     */
    List<String> listFiles(String folderName, FilePathFilter... fileFilters);

    /**
     * 只查询文件夹子文件夹中的所有文件 （不包括文件夹）
     * 
     * @param folderName
     * @param regexs
     * @return
     */
    List<String> listAllFiles(String folderName, String[] regexs);

    /**
     * 只查询文件夹子文件夹中的所有文件 （不包括文件夹）
     * 
     * @param folderName
     * @param fileFilters
     * @return
     */
    List<String> listAllFiles(String folderName, FilePathFilter... fileFilters);

    /**
     * 1. 下载 文件   
     *  srcFile     oss/yeb/test/aa.txt
     *  toFile      /sharedata/test/bb/aa.txt
     * 
     * 2. 下载文件夹
     *  srcFile     oss/yeb/test/
     *  toFile      /sharedata/kkk/
     *  将oss/yeb/test/下目录包括子目录下文件下载到/sharedata/kkk/目录下
     */
    void download(String srcFile, String toFile);

    /**
     * 1. 上传文件
     * 2. 文件夹上传， 将原文件夹下所有文件及子文件上传到目标目录
     * 
     * @param srcFile
     * @param toFile
     * @param override
     */
    void upload(String srcFile, String toFile, boolean override);

    /**
     * 文件重名
     * <p>说明：如果要重命名文件夹，目标文件夹不能是原始文件夹的子目录。
     * <p>如果目标文件已存在，则覆盖目标文件
     * 
     * @param srcFile  重命名的原始文件
     * @param toFile   重命名的目标文件
     */
    void rename(String srcFile, String toFile);

    /**
     * 文件拷贝
     * 
     * @param srcFile 原始文件
     * @param toFile  目标文件
     */
    void copy(String srcFile, String toFile);

    /**
     * 删除文件
     * <li>可以删除文件或文件夹
     * <li>如果删除失败抛出RuntimeException
     * 
     * @param fullPath
     */
    void delete(String fullPath);

    /**
     * 文件过滤接口
     * 
     * @author zhao.yan
     * @version $Id: FileStorage.java, v 0.1 2017年1月5日 下午5:37:52 zhao.yan Exp $
     */
    public interface FilePathFilter {

        /**
         * 过滤文件
         * 
         * @param file  文件全路径
         * @return  true：文件不被过滤
         */
        boolean accept(String file);
    }

}
