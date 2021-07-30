package com.alipay.rdf.file.codec;

import com.alipay.rdf.file.exception.RdfErrorEnum;
import com.alipay.rdf.file.exception.RdfFileException;
import com.alipay.rdf.file.meta.FileColumnMeta;
import com.alipay.rdf.file.model.FileConfig;
import com.alipay.rdf.file.model.FileDataTypeEnum;
import com.alipay.rdf.file.protocol.RowDefinition;
import com.alipay.rdf.file.spi.RdfFileFunctionSpi;
import com.alipay.rdf.file.spi.RdfFileRowCodecSpi;
import com.alipay.rdf.file.util.BeanMapWrapper;
import com.alipay.rdf.file.util.RdfFileUtil;

import java.util.List;

/**
 *
 * @Author: hongwei.quhw 2021/6/27 3:02 下午
 */
public class RowRelationCodec implements RdfFileRowCodecSpi {
    @Override
    public String serialize(RowCodecContext rccCtx) {
        FileConfig fileConfig = rccCtx.fileConfig;
        BeanMapWrapper bmw = rccCtx.bmw;
        RowDefinition rd = rccCtx.rd;
        List<FileColumnMeta> columnMetas = rccCtx.columnMetas;

        StringBuffer line = new StringBuffer();
        String split = RdfFileUtil.getRowSplit(fileConfig);

        for (int i = 0; i < columnMetas.size(); i++) {
            FileColumnMeta columnMeta = columnMetas.get(i);
            RdfFileFunctionSpi.FuncContext ctx = new RdfFileFunctionSpi.FuncContext();
            try {
                ctx.codecType = RdfFileFunctionSpi.CodecType.SERIALIZE;
                ctx.field = bmw.getProperty(columnMeta.getName());
                ctx.columnMeta = columnMeta;
                ctx.fileConfig = fileConfig;
                line.append(rd.getOutput().execute(ctx));

                if (null != split && i < columnMetas.size() - 1) {
                    line.append(split);
                }
            } catch (RdfFileException e) {
                throw new RdfFileException(
                        "rdf-file#RowRelationCodec.serialize serialize row=" + bmw.getBean()
                                + ", fileConfig=" + fileConfig + ", 将数据序列到文件出错. 错误列信息: columnMeta=" + columnMeta + ", field=" + ctx.field + ", errorMsg="
                                + e.getMessage(),
                        e, e.getErrorEnum());
            } catch (Throwable e) {
                throw new RdfFileException(
                        "rdf-file#RowRelationCodec.serialize row=" + bmw.getBean()
                                + ", fileConfig=" + fileConfig + ", 将数据序列到文件出错. 错误列信息: columnMeta=" + columnMeta + ", field=" + ctx.field,
                        e, RdfErrorEnum.SERIALIZE_ERROR);
            }
        }

        return  line.toString();
    }

    @Override
    public void deserialize(String line, RowCodecContext rccCtx) {
        FileConfig fileConfig = rccCtx.fileConfig;
        BeanMapWrapper bmw = rccCtx.bmw;
        RowDefinition rd = rccCtx.rd;
        List<FileColumnMeta> columnMetas = rccCtx.columnMetas;
        String[] columnValues = rccCtx.columnValues;

        int splitLength =  columnMetas.size();

        if (columnValues.length != splitLength) {
            throw new RdfFileException("rdf-file#RowRelationCodec.deserialize fileConfig="
                    + fileConfig + ", line=[" + line + "],模板定义列数=" + splitLength
                    + ", 实际列数=" + columnValues.length,
                    RdfErrorEnum.DESERIALIZE_ERROR);
        }

        int endIndex =  Math.min(columnValues.length, splitLength);

        for (int i = 0; i < endIndex; i++) {
            FileColumnMeta columnMeta = columnMetas.get(i);
            RdfFileFunctionSpi.FuncContext ctx = new RdfFileFunctionSpi.FuncContext();
            try {
                ctx.codecType = RdfFileFunctionSpi.CodecType.DESERIALIZE;
                ctx.field = columnValues[i];
                ctx.columnMeta = columnMeta;
                ctx.fileConfig = fileConfig;
                bmw.setProperty(columnMeta.getName(), rd.getOutput().execute(ctx));
            } catch (RdfFileException e) {
                throw new RdfFileException(
                        "rdf-file#RowRelationCodec.deserialize line=" + line + ", fileConfig="
                                + fileConfig + ", 将数据反序列到对象出错. 错误列信息:field=" + ctx.field + ", columnMeta=" + columnMeta + ", errorMsg="
                                + e.getMessage(),
                        e, e.getErrorEnum());
            } catch (Throwable e) {
                throw new RdfFileException("rdf-file#RowRelationCodec.deserialize line="
                        + line + ", fileConfig=" + fileConfig + ", 将数据反序列到对象出错. 错误列信息:field=" + ctx.field + ", columnMeta=" + columnMeta,
                        e, RdfErrorEnum.DESERIALIZE_ERROR);
            }
        }
    }
}
