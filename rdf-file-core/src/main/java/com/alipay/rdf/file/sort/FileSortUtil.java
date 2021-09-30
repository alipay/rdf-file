package com.alipay.rdf.file.sort;

import java.io.File;

import com.alipay.rdf.file.exception.RdfErrorEnum;
import com.alipay.rdf.file.model.FileConfig;
import com.alipay.rdf.file.model.FileSlice;
import com.alipay.rdf.file.model.SortConfig;
import com.alipay.rdf.file.util.RdfFileUtil;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 *
 * 排序工具类
 *
 * @author hongwei.quhw
 * @version $Id: FileSortUtil.java, v 0.1 2017年8月22日 下午9:27:05 hongwei.quhw Exp $
 */
public class FileSortUtil {
    public static final String HEADER_KEY   = "header";

    public static final String TAIL_KEY     = "tail";

    public static final String DEFAULT_NAME = "sortedFile";

    /**
     * 构建头文件目录
     *
     * @param sortConfig
     * @return
     */
    public static String getHeadFilePath(SortConfig sortConfig) {
        String tempHeadFileName = HEADER_KEY;
        if (RdfFileUtil.isNotBlank(sortConfig.getResultFileName())) {
            tempHeadFileName += "-" + sortConfig.getResultFileName();
        }
        String tempHeadPath = RdfFileUtil.combinePath(sortConfig.getResultPath(), tempHeadFileName);
        return tempHeadPath;
    }

    /**
     * 构建头文件尾目录
     *
     * @param sortConfig
     * @return
     */
    public static String getTailFilePath(SortConfig sortConfig) {
        String tempTailFileName = TAIL_KEY;
        if (RdfFileUtil.isNotBlank(sortConfig.getResultFileName())) {
            tempTailFileName += "-" + sortConfig.getResultFileName();
        }
        String tempTailPath = RdfFileUtil.combinePath(sortConfig.getResultPath(), tempTailFileName);
        return tempTailPath;
    }

    /**
     * 构建分片文件路径
     *
     * @param slice
     * @param sortConfig
     * @return
     */
    public static String getBodyFilePath(FileSlice slice, SortConfig sortConfig,
                                         boolean withFileName) {
        String tempBodyFileName = String.valueOf(slice.getStart());
        if (RdfFileUtil.isNotBlank(sortConfig.getResultFileName())) {
            tempBodyFileName += "-" + sortConfig.getResultFileName();
        }

        if (withFileName) {
            return RdfFileUtil.combinePath(sortConfig.getResultPath(),
                new File(slice.getFilePath()).getName(), tempBodyFileName);
        } else {
            return RdfFileUtil.combinePath(sortConfig.getResultPath(), tempBodyFileName);
        }
    }

    /**
     * 构建分片文件路径
     *
     * @param slice
     * @param sortConfig
     * @return
     */
    public static String getBodyFilePath(FileSlice slice, SortConfig sortConfig) {
        return getBodyFilePath(slice, sortConfig, false);
    }

    public static String getFullFilePath(SortConfig sortConfig) {
        String fileName = RdfFileUtil.isBlank(sortConfig.getResultFileName()) ? DEFAULT_NAME
            : sortConfig.getResultFileName();
        return RdfFileUtil.combinePath(sortConfig.getResultPath(), fileName);
    }

    public static String[] getSourceFilePaths(SortConfig sortConfig, FileConfig fileConfig) {
        String[] filePaths = sortConfig.getSourceFilePaths();
        if (null == filePaths || filePaths.length == 0) {
            RdfFileUtil.assertNotBlank(fileConfig.getFilePath(),
                "rdf-file#ProtocolMultiFileSorter协议文件排序 sortConfig.getSourceFilePaths() is null and fileConfig.getFilePath() is blank.",
                RdfErrorEnum.ILLEGAL_ARGUMENT);

            filePaths = new String[] { fileConfig.getFilePath() };
        }

        return filePaths;
    }
}
