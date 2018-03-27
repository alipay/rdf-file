package com.alipay.rdf.file.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import com.alipay.rdf.file.exception.RdfErrorEnum;
import com.alipay.rdf.file.exception.RdfFileException;
import com.alipay.rdf.file.interfaces.FileFactory;
import com.alipay.rdf.file.interfaces.FileSplitter;
import com.alipay.rdf.file.model.FileConfig;
import com.alipay.rdf.file.model.FileDataTypeEnum;
import com.alipay.rdf.file.model.FileSlice;
import com.alipay.rdf.file.model.SortConfig;
import com.alipay.rdf.file.model.SortResult;
import com.alipay.rdf.file.sort.AbstractFileSort;
import com.alipay.rdf.file.sort.FileSortUtil;
import com.alipay.rdf.file.sort.SortExecutor;
import com.alipay.rdf.file.sort.SplitBodySortExecutor;
import com.alipay.rdf.file.sort.SplitHeadSortExecutor;
import com.alipay.rdf.file.util.RdfFileUtil;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 * 
 * 对文件数据进行简单分割排序， 不需要根据文件协议解析
 * 
 * 不支持含有文件尾的文件
 * 
 * @author hongwei.quhw
 * @version $Id: SplitFileSorter.java, v 0.1 2017年8月24日 下午7:18:23 hongwei.quhw Exp $
 */
public class SplitFileSorter extends AbstractFileSort {
    private static final Map<FileDataTypeEnum, SortExecutor> EXECUTORS = new HashMap<FileDataTypeEnum, SortExecutor>();

    static {
        EXECUTORS.put(FileDataTypeEnum.HEAD, SplitHeadSortExecutor.INSTANCE);
        EXECUTORS.put(FileDataTypeEnum.UNKOWN, SplitBodySortExecutor.INSTANCE);
    }

    @Override
    public void init(FileConfig fileConfig) {
        super.init(fileConfig);

        RdfFileUtil.assertNotBlank(RdfFileUtil.getColumnSplit(fileConfig),
            "rdf-file#SplitFileSorter 必须指定文件分隔符", RdfErrorEnum.ILLEGAL_ARGUMENT);
    }

    @Override
    protected SortResult doSplitAndSort(SortConfig sortConfig) {
        SortResult sortResult = new SortResult();

        FileSplitter splitter = FileFactory.createSplitter(fileConfig.getStorageConfig());
        List<FileSlice> fileSlices = splitter.split(fileConfig.getFilePath(),
            sortConfig.getSliceSize());
        List<Future<String>> dataFutures = new ArrayList<Future<String>>(fileSlices.size() + 1);
        List<String> datas = new ArrayList<String>(fileSlices.size() + 1);
        // 含有文件头
        if (sortConfig.getHeadLines() > 0) {
            FileSlice first = fileSlices.get(0);
            FileSlice fileSlice = new FileSlice(first.getFilePath(), FileDataTypeEnum.HEAD,
                first.getStart(), first.getEnd());
            //提交头部
            submit(dataFutures, datas, fileConfig, sortConfig, fileSlice);
        }
        //提交body数据部分
        submit(dataFutures, datas, fileConfig, sortConfig, fileSlices);

        for (Future<String> future : dataFutures) {
            try {
                String path = future.get();
                if (RdfFileUtil.isNotBlank(path)) {
                    datas.add(path);
                }
            } catch (InterruptedException e) {
                throw new RdfFileException("rdf-file#文件排序被打断, future=" + future,
                    RdfErrorEnum.SORT_ERROR);
            } catch (ExecutionException e) {
                throw new RdfFileException("rdf-file#文件排序异常, future=" + future, e,
                    RdfErrorEnum.SORT_ERROR);
            }
        }

        String headPath = FileSortUtil.getHeadFilePath(sortConfig);
        if (datas.remove(headPath)) {
            sortResult.setHeadSlicePath(headPath);
        }

        sortResult.setBodySlicePath(datas);

        return sortResult;
    }

    @Override
    protected Map<FileDataTypeEnum, SortExecutor> getSortExecutors() {
        return EXECUTORS;
    }

}
