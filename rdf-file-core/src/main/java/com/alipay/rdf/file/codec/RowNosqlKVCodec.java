package com.alipay.rdf.file.codec;

import com.alipay.rdf.file.exception.RdfErrorEnum;
import com.alipay.rdf.file.exception.RdfFileException;
import com.alipay.rdf.file.meta.FileColumnMeta;
import com.alipay.rdf.file.model.FileConfig;
import com.alipay.rdf.file.protocol.RowDefinition;
import com.alipay.rdf.file.spi.RdfFileFunctionSpi;
import com.alipay.rdf.file.spi.RdfFileRowCodecSpi;
import com.alipay.rdf.file.util.BeanMapWrapper;
import com.alipay.rdf.file.util.RdfFileUtil;

import java.util.List;

/**
 * @Author: hongwei.quhw 2021/6/27 3:48 下午
 */
public class RowNosqlKVCodec implements RdfFileRowCodecSpi {
    private static final String KV_SPLIT_KEY = "rowCodecKVSplit";
    private static final String DEFAULT_KV_SPLIT = ":";

    @Override
    public String serialize(RowCodecContext rccCtx) {
        BeanMapWrapper bmw = rccCtx.bmw;
        List<FileColumnMeta> columnMetas = rccCtx.columnMetas;

        StringBuffer line = new StringBuffer();
        String lineSplit = RdfFileUtil.getRowSplit(rccCtx.fileConfig);
        String kvSplit = RdfFileUtil.getParam(rccCtx.fileConfig, KV_SPLIT_KEY, DEFAULT_KV_SPLIT);

        for (int i = 0; i < columnMetas.size(); i++) {
            FileColumnMeta columnMeta = columnMetas.get(i);
            RdfFileFunctionSpi.FuncContext ctx = new RdfFileFunctionSpi.FuncContext();
            try {
                ctx.codecType = RdfFileFunctionSpi.CodecType.SERIALIZE;
                ctx.field = bmw.getProperty(columnMeta.getName());
                // 对非空字段进行序列化
                if (null != ctx.field) {
                    ctx.columnMeta = columnMeta;
                    ctx.fileConfig = rccCtx.fileConfig;
                    String value= (String) rccCtx.rd.getOutput().execute(ctx);
                    line.append(columnMeta.getName() + kvSplit + value);

                    if (null != lineSplit && i < columnMetas.size() - 1) {
                        line.append(lineSplit);
                    }
                }
            } catch (RdfFileException e) {
                throw new RdfFileException(
                        "rdf-file#RowNosqlKVCodec.serialize serialize row=" + bmw.getBean()
                                + ", fileConfig=" + rccCtx.fileConfig + ", 将数据序列到文件出错. 错误列信息: columnMeta=" + columnMeta + ", field=" + ctx.field + ", errorMsg="
                                + e.getMessage(),
                        e, e.getErrorEnum());
            } catch (Throwable e) {
                throw new RdfFileException(
                        "rdf-file#RowNosqlKVCodec.serialize row=" + bmw.getBean()
                                + ", fileConfig=" + rccCtx.fileConfig + ", 将数据序列到文件出错. 错误列信息: columnMeta=" + columnMeta + ", field=" + ctx.field,
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
        String kvSplit = RdfFileUtil.getParam(fileConfig, KV_SPLIT_KEY, DEFAULT_KV_SPLIT);

        for (String columnPair : columnValues) {
            int idx = columnPair.indexOf(kvSplit);
            String metaName = columnPair.substring(0, idx);
            String value = columnPair.substring(idx);

            RdfFileFunctionSpi.FuncContext ctx = new RdfFileFunctionSpi.FuncContext();
            FileColumnMeta columnMeta = getFileColumnMeta(metaName, columnMetas);
            try {
                ctx.codecType = RdfFileFunctionSpi.CodecType.DESERIALIZE;
                ctx.field = value;
                ctx.columnMeta = columnMeta;
                ctx.fileConfig = fileConfig;
                bmw.setProperty(columnMeta.getName(), rd.getOutput().execute(ctx));
            } catch (RdfFileException e) {
                throw new RdfFileException(
                        "rdf-file#RowNosqlKVCodec.deserialize line=" + line + ", fileConfig="
                                + fileConfig + ", 将数据反序列到对象出错. 错误列信息:field=" + ctx.field + ", columnMeta=" + columnMeta + ", errorMsg="
                                + e.getMessage(),
                        e, e.getErrorEnum());
            } catch (Throwable e) {
                throw new RdfFileException("rdf-file#RowNosqlKVCodec.deserialize line="
                        + line + ", fileConfig=" + fileConfig + ", 将数据反序列到对象出错. 错误列信息:field=" + ctx.field + ", columnMeta=" + columnMeta,
                        e, RdfErrorEnum.DESERIALIZE_ERROR);
            }
        }
    }

    private FileColumnMeta getFileColumnMeta(String columnName, List<FileColumnMeta> columnMetas) {
        for (FileColumnMeta columnMeta : columnMetas) {
            if (columnName.equals(columnMeta.getName())) {
                return columnMeta;
            }
        }

        return null;
    }
}
