package com.alipay.rdf.file.sort;

import com.alipay.rdf.file.interfaces.FileFactory;
import com.alipay.rdf.file.interfaces.FileReader;
import com.alipay.rdf.file.interfaces.FileWriter;
import com.alipay.rdf.file.model.FileConfig;
import com.alipay.rdf.file.model.SortConfig;
import com.alipay.rdf.file.model.SortResult;
import com.alipay.rdf.file.spi.RdfFileWriterSpi;
import com.alipay.rdf.file.util.RdfFileUtil;
import com.alipay.rdf.file.util.RdfProfiler;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 * 
 * 分片文件有序合并
 * @author hongwei.quhw
 * @version $Id: SortedMeger.java, v 0.1 2017年6月27日 下午2:17:57 hongwei.quhw Exp $
 */
public class SortedMeger {

    public static void merge(FileConfig fileConfig, SortConfig sortConfig, SortResult sortResult) {
        FileConfig writerConfig = fileConfig.clone();

        writerConfig.setFilePath(FileSortUtil.getFullFilePath(sortConfig));
        writerConfig.setStorageConfig(sortConfig.getResultStorageConfig());
        FileWriter writer = FileFactory.createWriter(writerConfig);
        // 确保文件创建
        ((RdfFileWriterSpi) writer).ensureOpen();

        try {
            if (RdfFileUtil.isNotBlank(sortResult.getHeadSlicePath())) {
                FileConfig headSliceConfig = fileConfig.clone();
                headSliceConfig.setFilePath(sortResult.getHeadSlicePath());
                headSliceConfig.setStorageConfig(sortConfig.getResultStorageConfig());
                FileReader headSlieReader = FileFactory.createReader(headSliceConfig);
                try {
                    RdfProfiler.enter("rdf-file#merge sorted head start...");
                    String line = null;
                    while (null != (line = headSlieReader.readLine())) {
                        writer.writeLine(line);
                    }
                } finally {
                    if (null != headSlieReader) {
                        headSlieReader.close();
                    }
                    RdfProfiler.release("rdf-file#merge sorted head end.");
                }
            }

            SortedFileGroupReader bodySlicesReader = new SortedFileGroupReader(fileConfig,
                sortConfig, sortResult);
            try {
                RdfProfiler.enter("rdf-file#merge sorted body start...");
                RowData row = null;
                while (null != (row = bodySlicesReader.readRow())) {
                    writer.writeRow(row.getColumnSortDatas());
                }
            } finally {
                if (null != bodySlicesReader) {
                    bodySlicesReader.close();
                }
                RdfProfiler.release("rdf-file#merge sorted body end.");
            }

            //尾
            if (RdfFileUtil.isNotBlank(sortResult.getTailSlicePath())) {
                FileConfig tailSliceConfig = fileConfig.clone();
                tailSliceConfig.setFilePath(sortResult.getTailSlicePath());
                tailSliceConfig.setStorageConfig(sortConfig.getResultStorageConfig());
                FileReader tailSlieReader = FileFactory.createReader(tailSliceConfig);
                try {
                    RdfProfiler.enter("rdf-file#merge sorted tail start...");
                    String line = null;
                    while (null != (line = tailSlieReader.readLine())) {
                        writer.writeLine(line);
                    }
                } finally {
                    if (null != tailSlieReader) {
                        tailSlieReader.close();
                    }
                    RdfProfiler.release("rdf-file#merge sorted tail end.");
                }
            }

            sortResult.setFullFilePath(writerConfig.getFilePath());
        } finally {
            if (null != writer) {
                writer.close();
            }
        }
    }
}
