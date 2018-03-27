package com.alipay.rdf.file.storage;

import java.io.IOException;
import java.io.InputStream;

import com.alipay.rdf.file.model.FileConfig;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 * 
 * 组件内部使用接口
 * 
 * @author hongwei.quhw
 * @version $Id: FileInnterStorage.java, v 0.1 2017年4月7日 下午3:02:23 hongwei.quhw Exp $
 */
public interface FileInnterStorage {

    /**
     * 获取文件输入流
     * 
     * @param filename
     * @return
     * @throws IOException
     */
    InputStream getInputStream(String filename);

    /**
     * 获取文件部分输入流
     * 
     * @param filename
     * @param start
     * @param length
     * @return
     * @throws IOException
     */
    InputStream getInputStream(String filename, long start, long length);

    /**
     * 获取tail部分的输入流
     * 
     * @param filePath
     * @param fileConfig
     * @return
     */
    InputStream getTailInputStream(FileConfig fileConfig);
}
