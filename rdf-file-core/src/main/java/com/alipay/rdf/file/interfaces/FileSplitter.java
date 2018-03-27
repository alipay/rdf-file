package com.alipay.rdf.file.interfaces;

import java.util.List;

import com.alipay.rdf.file.model.FileConfig;
import com.alipay.rdf.file.model.FileSlice;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 * 
 * 文件切分
 * 
 * @author hongwei.quhw
 * @version $Id: FileSplitter.java, v 0.1 2016-12-20 下午4:33:49 hongwei.quhw Exp $
 */
public interface FileSplitter {

    /**
     * 按大小切分文件
     */
    List<FileSlice> split(String path, int sliceSize);

    FileSlice getHeadSlice(FileConfig fileConfig);

    FileSlice getBodySlice(FileConfig fileConfig);

    List<FileSlice> getBodySlices(FileConfig fileConfig, int sliceSize);

    FileSlice getTailSlice(FileConfig fileConfig);

}
