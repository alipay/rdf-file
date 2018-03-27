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
import com.alipay.rdf.file.sort.ProtocolMultiFileBodySortExecutor;
import com.alipay.rdf.file.sort.ProtocolMultiFileHeadSortExecutor;
import com.alipay.rdf.file.sort.ProtocolMultiFileTailSortExecutor;
import com.alipay.rdf.file.sort.SortExecutor;
import com.alipay.rdf.file.util.RdfFileUtil;
import com.alipay.rdf.file.util.RdfProfiler;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 * 
 * 协议多个文件一起排序
 * 
 * @author hongwei.quhw
 * @version $Id: CommonFileSorter.java, v 0.1 2017年8月22日 下午9:18:42 hongwei.quhw Exp $
 */
public class ProtocolMultiFileSorter extends AbstractFileSort {
    private static final Map<FileDataTypeEnum, SortExecutor> EXECUTORS = new HashMap<FileDataTypeEnum, SortExecutor>();

    static {
        EXECUTORS.put(FileDataTypeEnum.HEAD, ProtocolMultiFileHeadSortExecutor.INSTANCE);
        EXECUTORS.put(FileDataTypeEnum.BODY, ProtocolMultiFileBodySortExecutor.INSTANCE);
        EXECUTORS.put(FileDataTypeEnum.TAIL, ProtocolMultiFileTailSortExecutor.INSTANCE);
    }

    @Override
    public void init(FileConfig fileConfig) {
        FileConfig cloneConfig = fileConfig.clone();
        cloneConfig.setType("protocol");
        super.init(cloneConfig);

        //做一些校验
        RdfFileUtil.assertNotBlank(fileConfig.getTemplatePath(),
            "rdf-file#协议文件排序必须指定模板 filePath=" + fileConfig.getFilePath(),
            RdfErrorEnum.ILLEGAL_ARGUMENT);
    }

    @Override
    public SortResult doSplitAndSort(SortConfig sortConfig) {
        SortResult sortResult = new SortResult();

        RdfProfiler.enter("rdf-file#file split start...");
        String[] filePaths = FileSortUtil.getSourceFilePaths(sortConfig, fileConfig);
        FileSplitter splitter = FileFactory.createSplitter(fileConfig.getStorageConfig());
        List<FileSlice> bodySlices = new ArrayList<FileSlice>();
        for (String filePath : filePaths) {
            FileConfig bodyConfig = fileConfig.clone();
            bodyConfig.setFilePath(filePath);
            bodySlices.addAll(splitter.getBodySlices(bodyConfig, sortConfig.getSliceSize()));
        }

        RdfProfiler.release("rdf-file#file split end.");

        RdfProfiler.enter("rdf-file#file slices sort start...");
        List<Future<String>> dataFutures = new ArrayList<Future<String>>(bodySlices.size() + 2);
        List<String> datas = new ArrayList<String>(bodySlices.size() + 2);

        submit(dataFutures, datas, fileConfig, sortConfig, bodySlices);

        if (!FileDataTypeEnum.BODY.equals(fileConfig.getFileDataType())) {
            FileMeta fileMeta = TemplateLoader.load(fileConfig);
            if (fileMeta.hasHead()) {
                // 头分片只是一个占位作用
                FileSlice headSlice = new FileSlice("head", FileDataTypeEnum.HEAD, 0, 0);
                submit(dataFutures, datas, fileConfig, sortConfig, headSlice);
            }

            if (fileMeta.hasTail()) {
                // 尾分片只是一个占位作用
                FileSlice tailSlice = new FileSlice("tail", FileDataTypeEnum.TAIL, 0, 0);
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
