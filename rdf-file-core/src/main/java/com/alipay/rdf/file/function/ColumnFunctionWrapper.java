package com.alipay.rdf.file.function;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.alipay.rdf.file.exception.RdfErrorEnum;
import com.alipay.rdf.file.exception.RdfFileException;
import com.alipay.rdf.file.loader.ExtensionLoader;
import com.alipay.rdf.file.loader.FormatLoader;
import com.alipay.rdf.file.meta.FileColumnMeta;
import com.alipay.rdf.file.meta.FileMeta;
import com.alipay.rdf.file.model.FileConfig;
import com.alipay.rdf.file.model.FileDataTypeEnum;
import com.alipay.rdf.file.protocol.ColumnLayoutEnum;
import com.alipay.rdf.file.protocol.RowDefinition;
import com.alipay.rdf.file.spi.RdfFileColumnTypeSpi;
import com.alipay.rdf.file.spi.RdfFileFormatSpi;
import com.alipay.rdf.file.spi.RdfFileFunctionSpi;
import com.alipay.rdf.file.util.RdfFileUtil;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 * 
 * @author hongwei.quhw
 * @version $Id: ColumnFunctionWrapper.java, v 0.1 2017年4月10日 下午3:09:50 hongwei.quhw Exp $
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class ColumnFunctionWrapper extends RdfFunction {
    public static final Map<String, ColumnRegEx> columnRegExs = new ConcurrentHashMap<String, ColumnRegEx>();

    private List<RdfFileFunctionSpi> rdfFunctions;

    public ColumnFunctionWrapper(List<RdfFileFunctionSpi> rdfFunctions, FileDataTypeEnum rowType) {
        this.rdfFunctions = rdfFunctions;
        this.rowType = rowType;
    }

    @Override
    public int rowsAffected(RowDefinition rd, FileMeta fileMeta) {
        if (!fileMeta.hasColumns(rowType)) {
            return 0;
        }

        if (ColumnLayoutEnum.horizontal.equals(rd.getColumnLayout())) {
            return 1;
        }

        if (ColumnLayoutEnum.vertical.equals(rd.getColumnLayout())) {
            return fileMeta.getColumns(rowType).size();
        }

        throw new RdfFileException(
            "ColumnFunctionWrapper对column的布局[" + rd.getColumnLayout() + "] 无法计算rowsAffected",
            RdfErrorEnum.FUNCTION_ERROR);
    }

    @Override
    public Object execute(FuncContext ctx) {
        if (CodecType.SERIALIZE.equals(ctx.codecType)) {
            return serialize(ctx.field, ctx.columnMeta, ctx.fileConfig, ctx.rowBean);
        } else if (CodecType.DESERIALIZE.equals(ctx.codecType)) {
            return deserialize((String) ctx.field, ctx.columnMeta, ctx.fileConfig);
        }
        return null;
    }

    private Object deserialize(String field, FileColumnMeta columnMeta, FileConfig fileConfig) {
        ColumnRegEx columnRegEx = getColumnRegEx(columnMeta);

        RdfFileFormatSpi columnFormat = FormatLoader
            .getColumnFormt(columnMeta.getFileMeta().getProtocol(), columnMeta.getType().getName());

        RdfFileUtil.assertNotNull(columnFormat,
            "rdf-file# protocol=" + columnMeta.getFileMeta().getProtocol() + " columnType="
                                                + columnMeta.getType().getName() + " 没有获取到format",
            RdfErrorEnum.EXTENSION_ERROR);

        field = columnFormat.deserialize(field, columnMeta, fileConfig);

        if (columnRegEx.isRegx) {
            Pattern pattern = Pattern.compile(columnRegEx.regEx);
            Matcher matcher = pattern.matcher(field);
            if (matcher.find()) {
                field = matcher.group(1);
            } else {
                throw new RdfFileException("字段内容[" + field + "]无法通过正则[" + columnRegEx.regEx + "]匹配",
                    RdfErrorEnum.DESERIALIZE_ERROR);
            }
        } else {
            field = columnRegEx.regEx;
        }

        RdfFileColumnTypeSpi columnTypeCodec = ExtensionLoader
            .getExtensionLoader(RdfFileColumnTypeSpi.class)
            .getExtension(columnMeta.getType().getName());
        RdfFileUtil.assertNotNull(columnTypeCodec,
            "没有type=" + columnMeta.getType().getName() + " 对应的类型codec");

        return columnTypeCodec.deserialize(field, columnMeta);
    }

    private String serialize(Object field, FileColumnMeta columnMeta, FileConfig fileConfig, Object rowBean) {
        RdfFileColumnTypeSpi columnTypeCodec = ExtensionLoader
            .getExtensionLoader(RdfFileColumnTypeSpi.class)
            .getExtension(columnMeta.getType().getName());
        RdfFileUtil.assertNotNull(columnTypeCodec,
            "没有type=" + columnMeta.getType().getName() + " 对应的类型codec");

        String value = columnTypeCodec.serialize(field, columnMeta);

        StringBuffer sb = new StringBuffer();
        for (RdfFileFunctionSpi rf : rdfFunctions) {
            FuncContext ctx = new FuncContext();
            ctx.field = value;
            ctx.columnMeta = columnMeta;
            ctx.codecType = CodecType.SERIALIZE;
            ctx.rowBean = rowBean;
            sb.append(rf.execute(ctx));
        }
        value = sb.toString();

        RdfFileFormatSpi columnFormat = FormatLoader
            .getColumnFormt(columnMeta.getFileMeta().getProtocol(), columnMeta.getType().getName());
        return columnFormat.serialize(value, columnMeta, fileConfig);
    }

    private ColumnRegEx getColumnRegEx(FileColumnMeta columnMeta) {
        String key = columnMeta.getFileMeta().getTemplatePath() + "-" + columnMeta.getDataType().name() + "-" + columnMeta.getName();
        ColumnRegEx columnRegEx = columnRegExs.get(key);
        if (null == columnRegEx) {
            columnRegEx = new ColumnRegEx();
            if (rdfFunctions.size() != 0) {
                StringBuffer sb = new StringBuffer();
                for (RdfFileFunctionSpi rf : rdfFunctions) {

                    if ("value".equals(rf.getExpression())) {
                        columnRegEx.isRegx = true;
                        sb.append("(.*)");
                    } else {
                        FuncContext ctx = new FuncContext();
                        ctx.columnMeta = columnMeta;
                        ctx.codecType = CodecType.DESERIALIZE;
                        Object ret = rf.execute(ctx);
                        sb.append(ret);
                    }
                }

                if (columnRegEx.isRegx) {
                    sb.insert(0, "^");
                    sb.append("$");
                }

                columnRegEx.regEx = sb.toString();
            }

            columnRegExs.put(key, columnRegEx);
        }
        return columnRegEx;
    }

    private static class ColumnRegEx {
        boolean isRegx;
        String  regEx;
    }
}
