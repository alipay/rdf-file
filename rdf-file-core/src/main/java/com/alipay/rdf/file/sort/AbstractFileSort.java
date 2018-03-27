package com.alipay.rdf.file.sort;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;

import com.alipay.rdf.file.exception.RdfErrorEnum;
import com.alipay.rdf.file.exception.RdfFileException;
import com.alipay.rdf.file.model.FileConfig;
import com.alipay.rdf.file.model.FileDataTypeEnum;
import com.alipay.rdf.file.model.FileSlice;
import com.alipay.rdf.file.model.SortConfig;
import com.alipay.rdf.file.model.SortConfig.ResultFileTypeEnum;
import com.alipay.rdf.file.model.SortResult;
import com.alipay.rdf.file.spi.RdfFileSorterSpi;
import com.alipay.rdf.file.util.RdfFileUtil;
import com.alipay.rdf.file.util.RdfProfiler;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 * 
 *  排序实现抽象接口
 * 
 * @author hongwei.quhw
 * @version $Id: AbstractFileSort.java, v 0.1 2017年8月24日 下午4:37:38 hongwei.quhw Exp $
 */
public abstract class AbstractFileSort implements RdfFileSorterSpi {
    protected FileConfig fileConfig;

    @Override
    public void init(FileConfig fileConfig) {
        this.fileConfig = fileConfig;
    }

    @Override
    public final SortResult sort(SortConfig sortConfig) {
        if (!(FileDataTypeEnum.ALL.equals(fileConfig.getFileDataType())
              || FileDataTypeEnum.BODY.equals(fileConfig.getFileDataType()))) {
            throw new RdfFileException(
                "rdf-file#" + getClass().getSimpleName() + ".sort fileConfig=" + fileConfig
                                       + ", 只有FileDataTypeEnum.ALL和FileDataTypeEnum.BODY数据可以进行排序",
                RdfErrorEnum.ILLEGAL_ARGUMENT);
        }

        // 没有指定结果存储，同源文件存储
        if (null == sortConfig.getResultStorageConfig()) {
            sortConfig.setResultStorageConfig(fileConfig.getStorageConfig());
        }

        try {
            RdfProfiler.enter("rdf-file#file sort start...");
            SortResult sortResult = doSplitAndSort(sortConfig);

            // 需要合并的合并 
            if (ResultFileTypeEnum.FULL_FILE_PATH.equals(sortConfig.getResultFileType())) {
                SortedMeger.merge(fileConfig, sortConfig, sortResult);
            }

            return sortResult;
        } finally {
            RdfProfiler.release("rdf-file#file sort end.");
        }
    }

    /**
     * 执行排序操作
     * */
    protected abstract SortResult doSplitAndSort(SortConfig sortConfig);

    /**
     * 构建执行器
     * 
     * @return
     */
    protected abstract Map<FileDataTypeEnum, SortExecutor> getSortExecutors();

    protected void submit(List<Future<String>> dataFutures, List<String> datas,
                          final FileConfig fileConfig, final SortConfig sortConfig,
                          List<FileSlice> fileSlices) {
        for (FileSlice fileSlice : fileSlices) {
            submit(dataFutures, datas, fileConfig, sortConfig, fileSlice);
        }
    }

    protected void submit(List<Future<String>> dataFutures, List<String> datas,
                          final FileConfig fileConfig, final SortConfig sortConfig,
                          final FileSlice fileSlice) {
        try {
            dataFutures.add(sortConfig.getExecutor().submit(new Callable<String>() {
                @Override
                public String call() throws Exception {
                    SortExecutor executor = getSortExecutors().get(fileSlice.getFileDataType());
                    RdfFileUtil.assertNotNull(executor,
                        "rdf-file#fileDataType=" + fileSlice.getFileDataType() + " 没有注册对应的排序执行器。");
                    return executor.sort(fileConfig, sortConfig, fileSlice);
                }
            }));
        } catch (RejectedExecutionException e) {
            String path = getSortExecutors().get(fileSlice.getFileDataType()).sort(fileConfig,
                sortConfig, fileSlice);
            if (RdfFileUtil.isNotBlank(path)) {
                datas.add(path);
            }
        }
    }
}
