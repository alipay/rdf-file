package com.alipay.rdf.file.split;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import com.alipay.rdf.file.exception.RdfErrorEnum;
import com.alipay.rdf.file.exception.RdfFileException;
import com.alipay.rdf.file.loader.TemplateLoader;
import com.alipay.rdf.file.meta.FileColumnMeta;
import com.alipay.rdf.file.meta.FileMeta;
import com.alipay.rdf.file.model.FileConfig;
import com.alipay.rdf.file.spi.RdfFileRowSplitSpi;
import com.alipay.rdf.file.util.RdfFileUtil;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 * 
 * 数据定长分割器
 * 
 * @author hongwei.quhw
 * @version $Id: RowSplitByFixedlLength.java, v 0.1 2017年8月8日 上午10:54:51 hongwei.quhw Exp $
 */
public class RowSplitByFixedlLength implements RdfFileRowSplitSpi {

    /** 
     * @see RdfFileRowSplitSpi.quhw.file.split.RowSplit#split(java.lang.String, hongwei.quhw.file.meta.FileMeta)
     */
    @Override
    public String[] split(SplitContext ctx) {
        FileMeta fileMeta = TemplateLoader.load(ctx.getFileConfig());
        try {
            List<FileColumnMeta> colMetas = fileMeta.getColumns(ctx.getRowType());
            List<String> colVals = new ArrayList<String>(colMetas.size());

            String encoding = RdfFileUtil.getFileEncoding(ctx.getFileConfig());
            byte[] content = ctx.getLine().getBytes(encoding);

            int preLen = 0;
            for (FileColumnMeta colMeta : colMetas) {
                int colLength = colMeta.getRange().getFirstAttr();
                //截取字段数组内容
                byte[] colbytes = new byte[colMeta.getRange().getFirstAttr()];
                System.arraycopy(content, preLen, colbytes, 0, colLength);
                colVals.add(new String(colbytes, encoding));
                preLen += colLength;
            }

            return colVals.toArray(new String[colVals.size()]);
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new RdfFileException(
                "rdf-file#RowSplitByFixedlLength.split 文件" + fileMeta.getTemplatePath() + ", line="
                                       + ctx.getLine() + "数据越界请检查模板定义数据总长度",
                e, RdfErrorEnum.DATA_ERROR);
        } catch (UnsupportedEncodingException e) {
            throw new RdfFileException(
                "rdf-file#RowSplitByFixedlLength.split 文件" + fileMeta.getTemplatePath() + ", line"
                                       + ctx.getLine() + "编码问题",
                e, RdfErrorEnum.ENCODING_ERROR);
        }
    }

    @Override
    public String getSplit(FileConfig fileConfig) {
        return null;
    }
}
