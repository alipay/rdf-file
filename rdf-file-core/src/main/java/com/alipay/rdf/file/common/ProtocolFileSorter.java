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
import com.alipay.rdf.file.loader.TemplateLoader;
import com.alipay.rdf.file.meta.FileMeta;
import com.alipay.rdf.file.model.FileConfig;
import com.alipay.rdf.file.model.FileDataTypeEnum;
import com.alipay.rdf.file.model.FileSlice;
import com.alipay.rdf.file.model.SortConfig;
import com.alipay.rdf.file.model.SortResult;
import com.alipay.rdf.file.sort.AbstractFileSort;
import com.alipay.rdf.file.sort.FileSortUtil;
import com.alipay.rdf.file.sort.ProtocolBodySortExecutor;
import com.alipay.rdf.file.sort.ProtocolHeadSortExecutor;
import com.alipay.rdf.file.sort.ProtocolTailSortExecutor;
import com.alipay.rdf.file.sort.SortExecutor;
import com.alipay.rdf.file.util.RdfFileUtil;
import com.alipay.rdf.file.util.RdfProfiler;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 * 
 * 协议文件排序
 * 
 * @author hongwei.quhw
 * @version $Id: CommonFileSorter.java, v 0.1 2017年8月22日 下午9:18:42 hongwei.quhw Exp $
 */
public class ProtocolFileSorter extends AbstractFileSort {
    private static final Map<FileDataTypeEnum, SortExecutor> EXECUTORS = new HashMap<FileDataTypeEnum, SortExecutor>();

    static {
        EXECUTORS.put(FileDataTypeEnum.HEAD, ProtocolHeadSortExecutor.INSTANCE);
        EXECUTORS.put(FileDataTypeEnum.BODY, ProtocolBodySortExecutor.INSTANCE);
        EXECUTORS.put(FileDataTypeEnum.TAIL, ProtocolTailSortExecutor.INSTANCE);
    }

    @Override
    public void init(FileConfig fileConfig) {
        super.init(fileConfig);

        //做一些校验
        RdfFileUtil.assertNotBlank(fileConfig.getTemplatePath(),
            "rdf-file#协议文件排序必须指定模板 filePath=" + fileConfig.getFilePath(),
            RdfErrorEnum.ILLEGAL_ARGUMENT);
    }

    @Override
    public SortResult doSplitAndSort(SortConfig sortConfig) {
        SortResult sortResult = new SortResult();

        RdfProfiler.enter("rdf-file#file split start...");
        FileSplitter splitter = FileFactory.createSplitter(fileConfig.getStorageConfig());
        List<FileSlice> bodySlices = splitter.getBodySlices(fileConfig, sortConfig.getSliceSize());
        RdfProfiler.release("rdf-file#file split end.");

        RdfProfiler.enter("rdf-file#file slices sort start...");
        List<Future<String>> dataFutures = new ArrayList<Future<String>>(bodySlices.size() + 2);
        List<String> datas = new ArrayList<String>(bodySlices.size() + 2);
        submit(dataFutures, datas, fileConfig, sortConfig, bodySlices);

        if (!FileDataTypeEnum.BODY.equals(fileConfig.getFileDataType())) {
            FileMeta fileMeta = TemplateLoader.load(fileConfig);
            if (fileMeta.hasHead()) {
                FileSlice headSlice = splitter.getHeadSlice(fileConfig);
                submit(dataFutures, datas, fileConfig, sortConfig, headSlice);
            }

            if (fileMeta.hasTail()) {
                FileSlice tailSlice = splitter.getTailSlice(fileConfig);
                submit(dataFutures, datas, fileConfig, sortConfig, tailSlice);
            }
        }

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
        RdfProfiler.release("rdf-file#file slices sort end.");

        String headPath = FileSortUtil.getHeadFilePath(sortConfig);
        if (datas.remove(headPath)) {
            sortResult.setHeadSlicePath(headPath);
        }
        String tailPath = FileSortUtil.getTailFilePath(sortConfig);
        if (datas.remove(tailPath)) {
            sortResult.setTailSlicePath(tailPath);
        }
        sortResult.setBodySlicePath(datas);

        return sortResult;
    }

    @Override
    protected Map<FileDataTypeEnum, SortExecutor> getSortExecutors() {
        return EXECUTORS;
    }
}
