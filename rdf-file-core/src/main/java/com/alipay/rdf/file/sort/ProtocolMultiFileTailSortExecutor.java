package com.alipay.rdf.file.sort;

import java.util.HashMap;
import java.util.Map;

import com.alipay.rdf.file.interfaces.FileFactory;
import com.alipay.rdf.file.interfaces.FileReader;
import com.alipay.rdf.file.interfaces.FileWriter;
import com.alipay.rdf.file.loader.SummaryLoader;
import com.alipay.rdf.file.loader.TemplateLoader;
import com.alipay.rdf.file.meta.FileMeta;
import com.alipay.rdf.file.model.FileConfig;
import com.alipay.rdf.file.model.FileSlice;
import com.alipay.rdf.file.model.SortConfig;
import com.alipay.rdf.file.model.Summary;
import com.alipay.rdf.file.model.SummaryPair;
import com.alipay.rdf.file.spi.RdfFileSummaryPairSpi;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 * 
 * 多文件的尾排序执行器
 * 
 * 尾合并， 汇总字段累加， 其他字段认为是常量
 * 
 * @author hongwei.quhw
 * @version $Id: ProtocolMultiFileTailSortExecutor.java, v 0.1 2017年12月11日 下午5:21:30 hongwei.quhw Exp $
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class ProtocolMultiFileTailSortExecutor implements SortExecutor {
    public static final ProtocolMultiFileTailSortExecutor INSTANCE = new ProtocolMultiFileTailSortExecutor();

    @Override
    public String sort(FileConfig fileConfig, SortConfig sortConfig, FileSlice fileSlice) {
        String[] filePaths = FileSortUtil.getSourceFilePaths(sortConfig, fileConfig);

        FileMeta fileMeta = TemplateLoader.load(fileConfig.getTemplatePath(),
            fileConfig.getTemplateEncoding());
        Summary summary = SummaryLoader.getNewSummary(fileMeta);
        Map<String, Object> tail = new HashMap<String, Object>();
        for (String path : filePaths) {
            FileConfig tailConfig = fileConfig.clone();
            tailConfig.setFilePath(path);
            FileReader reader = FileFactory.createReader(tailConfig);
            try {
                tail = reader.readTail(HashMap.class);
                //总记录数累计
                if (null != tail.get(fileMeta.getTotalCountKey())) {
                    summary.addTotalCount(tail.get(fileMeta.getTotalCountKey()));
                }
                for (SummaryPair pair : summary.getTailSummaryPairs()) {
                    ((RdfFileSummaryPairSpi) pair).addColValue(tail.get(pair.getTailKey()));
                }

            } finally {
                if (null != reader) {
                    reader.close();
                }
            }
        }

        tail.putAll(summary.summaryTailToMap());

        String tempTailPath = FileSortUtil.getTailFilePath(sortConfig);
        FileConfig writerConfig = fileConfig.clone();
        writerConfig.setFilePath(tempTailPath);
        writerConfig.setStorageConfig(sortConfig.getResultStorageConfig());
        FileWriter tailWriter = FileFactory.createWriter(writerConfig);

        try {
            tailWriter.writeTail(tail);
            return tempTailPath;
        } finally {
            if (null != tailWriter) {
                tailWriter.close();
            }
        }
    }

}
