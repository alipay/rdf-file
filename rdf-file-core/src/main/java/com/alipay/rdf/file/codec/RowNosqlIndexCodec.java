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

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @Author: hongwei.quhw 2021/6/27 3:48 下午
 */
public class RowNosqlIndexCodec implements RdfFileRowCodecSpi {
    private static String INDEX_PLACE_KEY = "rowCodecIndex";
    // (start|end) 代表在 (第一个字段|最后一个字段)
    private static String INDEX_START = "start";
    private static String INDEX_END = "end";

    @Override
    public String serialize(RowCodecContext rccCtx) {
        BeanMapWrapper bmw = rccCtx.bmw;
        List<FileColumnMeta> columnMetas = rccCtx.columnMetas;

        StringBuffer index = new StringBuffer();
        StringBuffer line = new StringBuffer();
        String split = RdfFileUtil.getRowSplit(rccCtx.fileConfig);

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
                    line.append(value);
                    index.append(columnMeta.getColIndex());

                    if (null != split && i < columnMetas.size() - 1) {
                        line.append(split);
                        index.append(",");
                    }
                }
            } catch (RdfFileException e) {
                throw new RdfFileException(
                        "rdf-file#RowNosqlIndexCodec.serialize serialize row=" + bmw.getBean()
                                + ", fileConfig=" + rccCtx.fileConfig + ", 将数据序列到文件出错. 错误列信息: columnMeta=" + columnMeta + ", field=" + ctx.field + ", errorMsg="
                                + e.getMessage(),
                        e, e.getErrorEnum());
            } catch (Throwable e) {
                throw new RdfFileException(
                        "rdf-file#RowNosqlIndexCodec.serialize row=" + bmw.getBean()
                                + ", fileConfig=" + rccCtx.fileConfig + ", 将数据序列到文件出错. 错误列信息: columnMeta=" + columnMeta + ", field=" + ctx.field,
                        e, RdfErrorEnum.SERIALIZE_ERROR);
            }
        }

        // 序列化索引字段
        String indexPlace = RdfFileUtil.getParam(rccCtx.fileConfig, INDEX_PLACE_KEY, INDEX_END);
        if (RdfFileUtil.isBlank(indexPlace) || INDEX_END.equalsIgnoreCase(indexPlace)) {
            index.insert(0, "(").append(")");
            line.append(split).append(index.toString());
        } else if (INDEX_START.equalsIgnoreCase(indexPlace)) {
            index.insert(0, "(").append(")");
            line.insert(0, split).insert(0, index.toString());
        } else {
            throw new RdfFileException("rdf-file#RowNosqlIndexCodec.serialize fileConfig=" + rccCtx.fileConfig + ", 配置的索引字段存放位置的值不正确，应该是[start]或者[end]， 实际是[" + indexPlace + "]",
                    RdfErrorEnum.SERIALIZE_ERROR);
        }

        return  line.toString();
    }

    @Override
    public void deserialize(String line, RowCodecContext rccCtx) {
        FileConfig fileConfig = rccCtx.fileConfig;
        BeanMapWrapper bmw = rccCtx.bmw;
        RowDefinition rd = rccCtx.rd;
        String[] columnValues = rccCtx.columnValues;

        // 反序列化索引字段
        String indexs[] = null;
        String indexPlace = RdfFileUtil.getParam(rccCtx.fileConfig, INDEX_PLACE_KEY, INDEX_END);
        if (RdfFileUtil.isBlank(indexPlace) || INDEX_END.equalsIgnoreCase(indexPlace)) {
            indexs = columnValues[columnValues.length - 1].split(",");
        } else if (INDEX_START.equalsIgnoreCase(indexPlace)) {
            indexs = columnValues[0].split(",");
        } else {
            throw new RdfFileException("rdf-file#RowNosqlIndexCodec.deserialize fileConfig=" + rccCtx.fileConfig + ", 配置的索引字段存放位置的值不正确，应该是[start]或者[end]， 实际是[" + indexPlace + "]",
                    RdfErrorEnum.SERIALIZE_ERROR);
        }

        List<FileColumnMeta> columnMetas = new ArrayList<FileColumnMeta>(indexs.length);
        for (String index : indexs) {
            for (FileColumnMeta columnMeta : rccCtx.columnMetas) {
                if (index.equals(String.valueOf(columnMeta.getColIndex()))) {
                    columnMetas.add(columnMeta);
                }
            }
        }

        // TODO 字段在之前就已经分格了， 那索引还需要()吗？

        for (int i = 0; i < columnMetas.size(); i++) {
            RdfFileFunctionSpi.FuncContext ctx = new RdfFileFunctionSpi.FuncContext();
            FileColumnMeta columnMeta = columnMetas.get(i);
            try {
                ctx.codecType = RdfFileFunctionSpi.CodecType.DESERIALIZE;
                ctx.field = columnValues[i + 1];
                ctx.columnMeta = columnMeta;
                ctx.fileConfig = fileConfig;
                bmw.setProperty(columnMeta.getName(), rd.getOutput().execute(ctx));
            } catch (RdfFileException e) {
                throw new RdfFileException(
                        "rdf-file#RowNosqlIndexCodec.deserialize line=" + line + ", fileConfig="
                                + fileConfig + ", 将数据反序列到对象出错. 错误列信息:field=" + ctx.field + ", columnMeta=" + columnMeta + ", errorMsg="
                                + e.getMessage(),
                        e, e.getErrorEnum());
            } catch (Throwable e) {
                throw new RdfFileException("rdf-file#RowNosqlIndexCodec.deserialize line="
                        + line + ", fileConfig=" + fileConfig + ", 将数据反序列到对象出错. 错误列信息:field=" + ctx.field + ", columnMeta=" + columnMeta,
                        e, RdfErrorEnum.DESERIALIZE_ERROR);
            }
        }
    }

}
