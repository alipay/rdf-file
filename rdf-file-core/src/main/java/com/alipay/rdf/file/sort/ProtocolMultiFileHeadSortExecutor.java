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
import com.alipay.rdf.file.summary.StatisticPair;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 * 
 * 多个文件的头排序
 * 
 * 头合并， 汇总字段累加， 其他字段认为是常量
 * 
 * @author hongwei.quhw
 * @version $Id: ProtocolMultiHeadSortExecutor.java, v 0.1 2017年12月11日 下午5:20:02 hongwei.quhw Exp $
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class ProtocolMultiFileHeadSortExecutor implements SortExecutor {
    public static final ProtocolMultiFileHeadSortExecutor INSTANCE = new ProtocolMultiFileHeadSortExecutor();

    @Override
    public String sort(FileConfig fileConfig, SortConfig sortConfig, FileSlice fileSlice) {
        String[] filePaths = FileSortUtil.getSourceFilePaths(sortConfig, fileConfig);

        FileMeta fileMeta = TemplateLoader.load(fileConfig.getTemplatePath(),
            fileConfig.getTemplateEncoding());
        Summary summary = SummaryLoader.getNewSummary(fileMeta);
        Map<String, Object> head = new HashMap<String, Object>();
        for (String path : filePaths) {
            FileConfig headConfig = fileConfig.clone();
            headConfig.setFilePath(path);
            FileReader reader = FileFactory.createReader(headConfig);
            try {
                head = reader.readHead(HashMap.class);
                //总记录数累计
                if (null != head.get(fileMeta.getTotalCountKey())) {
                    summary.addTotalCount(head.get(fileMeta.getTotalCountKey()));
                }
                for (SummaryPair pair : summary.getHeadSummaryPairs()) {
                    ((RdfFileSummaryPairSpi) pair).addColValue(head.get(pair.getHeadKey()));
                }

                for (StatisticPair pair : summary.getHeadStatisticPairs()) {
                    pair.addColValue(head.get(pair.getHeadKey()));
                }
            } finally {
                if (null != reader) {
                    reader.close();
                }
            }
        }

        head.putAll(summary.summaryHeadToMap());

        String tempHeadPath = FileSortUtil.getHeadFilePath(sortConfig);
        FileConfig writerConfig = fileConfig.clone();
        writerConfig.setFilePath(tempHeadPath);
        writerConfig.setStorageConfig(sortConfig.getResultStorageConfig());
        FileWriter headWriter = FileFactory.createWriter(writerConfig);

        try {
            headWriter.writeHead(head);
            return tempHeadPath;
        } finally {
            if (null != headWriter) {
                headWriter.close();
            }
        }
    }
}
