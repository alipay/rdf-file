package com.alipay.rdf.file.codec;

import com.alipay.rdf.file.exception.RdfErrorEnum;
import com.alipay.rdf.file.exception.RdfFileException;
import com.alipay.rdf.file.meta.FileColumnMeta;
import com.alipay.rdf.file.model.FileConfig;
import com.alipay.rdf.file.protocol.RowDefinition;
import com.alipay.rdf.file.spi.RdfFileFunctionSpi;
import com.alipay.rdf.file.util.BeanMapWrapper;
import com.alipay.rdf.file.util.RdfFileUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 行首或者行尾添加行元数据信息
 * colMeta(idx:1,3,5)
 * 默认实现是数据与数据定义模板的字段映射索引
 * 默认放置于行尾
 *
 * @Author: hongwei.quhw 2021/6/27 3:48 下午
 */
public class RowNosqlIndexCodec extends AbstractRowCodec {
    private static final String INDEX_PLACE_KEY = "rowCodecIndex";
    // (start|end) 代表在 (第一个字段|最后一个字段)
    private static final String INDEX_START = "start";
    private static final String INDEX_END = "end";
    private static final String COL_META_START = "colMeta(idx:";
    private static final int COL_META_START_LENGTH = COL_META_START.length();
    private static final String COL_META_END = ")";

    @Override
    public String serialize(RowCodecContext rccCtx) {
        BeanMapWrapper bmw = rccCtx.bmw;
        List<FileColumnMeta> columnMetas = rccCtx.columnMetas;

        StringBuffer colMeta = new StringBuffer();
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
                    String value = (String) rccCtx.rd.getOutput().execute(ctx);
                    line.append(value);
                    colMeta.append(columnMeta.getColIndex());

                    if (null != split && i < columnMetas.size() - 1) {
                        line.append(split);
                        colMeta.append(",");
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
        colMeta.insert(0, COL_META_START).append(COL_META_END);
        if (RdfFileUtil.isBlank(indexPlace) || INDEX_END.equalsIgnoreCase(indexPlace)) {
            line.append(split).append(colMeta.toString());
        } else if (INDEX_START.equalsIgnoreCase(indexPlace)) {
            line.insert(0, split).insert(0, colMeta.toString());
        } else {
            throw new RdfFileException("rdf-file#RowNosqlIndexCodec.serialize fileConfig=" + rccCtx.fileConfig + ", 配置的索引字段存放位置的值不正确，应该是[start]或者[end]， 实际是[" + indexPlace + "]",
                    RdfErrorEnum.SERIALIZE_ERROR);
        }

        return line.toString();
    }

    @Override
    public String preDeserialize(String line, RowCodecContext rccCtx) {
        String indexPlace = RdfFileUtil.getParam(rccCtx.fileConfig, INDEX_PLACE_KEY, INDEX_END);
        String split = RdfFileUtil.getRowSplit(rccCtx.fileConfig);
        String colMeta;
        if (RdfFileUtil.isBlank(indexPlace) || INDEX_END.equalsIgnoreCase(indexPlace)) {
            int endIdx = line.indexOf(split);
            colMeta = line.substring(endIdx + split.length());
            line = line.substring(0, endIdx);
        } else if (INDEX_START.equalsIgnoreCase(indexPlace)) {
            int startIdx = line.lastIndexOf(split);
            colMeta = line.substring(0, startIdx);
            line = line.substring(startIdx + split.length());
        } else {
            throw new RdfFileException("rdf-file#RowNosqlIndexCodec.preDeserialize fileConfig=" + rccCtx.fileConfig + ", 配置的索引字段存放位置的值不正确，应该是[start]或者[end]， 实际是[" + indexPlace + "]",
                    RdfErrorEnum.SERIALIZE_ERROR);
        }

        String idxMeta = colMeta.substring(COL_META_START_LENGTH, colMeta.length() - 1);

        if (RdfFileUtil.isNotBlank(idxMeta)) {
            String[] idxes = idxMeta.split(",");
            Integer[] idxArray = new Integer[idxes.length];
            for (int i = 0; i < idxes.length; i++) {
                idxArray[i] = Integer.parseInt(idxes[i]);
            }

            rccCtx.ext = idxArray;
        }

        return line;
    }

    @Override
    public void deserialize(String line, RowCodecContext rccCtx) {
        FileConfig fileConfig = rccCtx.fileConfig;
        BeanMapWrapper bmw = rccCtx.bmw;
        RowDefinition rd = rccCtx.rd;
        String[] columnValues = rccCtx.columnValues;

        // 反序列化索引字段
        String idxArray[] = (String[]) rccCtx.ext;

        List<FileColumnMeta> columnMetas = new ArrayList<FileColumnMeta>(idxArray.length);
        for (String index : idxArray) {
            for (FileColumnMeta columnMeta : rccCtx.columnMetas) {
                if (index.equals(String.valueOf(columnMeta.getColIndex()))) {
                    columnMetas.add(columnMeta);
                }
            }
        }

        for (int i = 0; i < columnMetas.size(); i++) {
            RdfFileFunctionSpi.FuncContext ctx = new RdfFileFunctionSpi.FuncContext();
            FileColumnMeta columnMeta = columnMetas.get(i);
            try {
                ctx.codecType = RdfFileFunctionSpi.CodecType.DESERIALIZE;
                ctx.field = columnValues[i];
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
