package com.alipay.rdf.file.function;

import com.alipay.rdf.file.codec.ColumnInfoHorizontalCodec;
import com.alipay.rdf.file.codec.ColumnInfoVerticalCodec;
import com.alipay.rdf.file.exception.RdfErrorEnum;
import com.alipay.rdf.file.exception.RdfFileException;
import com.alipay.rdf.file.loader.ExtensionLoader;
import com.alipay.rdf.file.loader.FormatLoader;
import com.alipay.rdf.file.loader.TemplateLoader;
import com.alipay.rdf.file.meta.FileMeta;
import com.alipay.rdf.file.model.FileDataTypeEnum;
import com.alipay.rdf.file.protocol.RowDefinition;
import com.alipay.rdf.file.spi.RdfFileColumnTypeSpi;
import com.alipay.rdf.file.spi.RdfFileFormatSpi;
import com.alipay.rdf.file.util.RdfFileUtil;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 *
 * 字段信息序列化反序列化函数
 *
 * 参考fund.xml协议
 *
 *  1. ${columnInfo.count(head,body)}           表示在文件头部，计算body的字段数
 *  2. ${columnInfo.vertical(head,body,name)}   表示在文件头部，每个body字段名作为一行
 *  3. ${columnInfo.horizontal(tail,tail,name)} 表示在文件尾部，所有文件尾字段名作为一行数据
 *
 *  函数第一个参数代表函数在文件中的作用域
 *  函数第二个参数代表对文件数据模板中哪个部分的字段进行信息处理
 *  函数第三个参数代表对文件数据模板中name或者desc字段处理
 *
 * @author quhongwei
 * @version : ColumnInfoFunction.java, v 0.1 2019年11月29日 15:50 quhongwei Exp $
 */
public class ColumnInfoFunction extends RdfFunction {
    @Override
    public void checkParams() {
        if (("horizontal".equals(expression) || "vertical".equals(expression)) && (null == params || params.length != 3)) {
            throw new RdfFileException("rdf-file#ColumnInfoFunction.checkParams() 指定的参数应该为三个", RdfErrorEnum.FUNCTION_ERROR);
        } else if ("count".equals(expression) && (null == params || params.length != 2)) {
            throw new RdfFileException("rdf-file#ColumnInfoFunction.checkParams() 指定的参数应该为两个", RdfErrorEnum.FUNCTION_ERROR);
        }
    }

    @Override
    public int rowsAffected(RowDefinition rd, FileMeta fileMeta) {
        if ("horizontal".equals(expression)) {
            return 1;
        } else if ("vertical".equals(expression)) {
            return getColumnSize(fileMeta);
        } else if ("count".equals(expression)) {
            return 1;
        } else {
            throw new RdfFileException(
                    "rdf-file#ColumnInfoFunction" + expression + ", 无法计算rowsAffected",
                    RdfErrorEnum.UNSUPPORTED_OPERATION);
        }
    }

    public void horizontal(FuncContext ctx) {
        FileDataTypeEnum layoutType = FileDataTypeEnum.valueOf(params[0].toUpperCase());
        FileDataTypeEnum dataType = FileDataTypeEnum.valueOf(params[1].toUpperCase());
        if (CodecType.SERIALIZE.equals(ctx.codecType)) {
            ColumnInfoHorizontalCodec.serialize(layoutType, dataType, ctx.fileConfig, ctx.writer, params[2]);

        } else if (CodecType.DESERIALIZE.equals(ctx.codecType)) {
            ColumnInfoHorizontalCodec.deserialize(layoutType, dataType, ctx.fileConfig, ctx.reader, params[2]);
        }
    }

    public void vertical(FuncContext ctx) {
        FileDataTypeEnum layoutType = FileDataTypeEnum.valueOf(params[0].toUpperCase());
        FileDataTypeEnum dataType = FileDataTypeEnum.valueOf(params[1].toUpperCase());

        if (CodecType.SERIALIZE.equals(ctx.codecType)) {
            ColumnInfoVerticalCodec.serialize(layoutType, dataType, ctx.fileConfig, ctx.writer, params[2]);
        } else if (CodecType.DESERIALIZE.equals(ctx.codecType)) {
            ColumnInfoVerticalCodec.deserialize(layoutType, dataType, ctx.fileConfig, ctx.reader, params[2]);
        }
    }

    public void count(FuncContext ctx) {
        FileMeta fileMeta = TemplateLoader.load(ctx.fileConfig);
        String typeName = ctx.columnMeta.getType().getName();

        RdfFileFormatSpi columnFormat = FormatLoader.getColumnFormt(fileMeta.getProtocol(),
                typeName);
        RdfFileUtil.assertNotNull(columnFormat, "类型type=" + typeName + " 对应的format没有");
        RdfFileColumnTypeSpi columnTypeCodec = ExtensionLoader
                .getExtensionLoader(RdfFileColumnTypeSpi.class).getExtension(typeName);
        RdfFileUtil.assertNotNull(columnTypeCodec, "没有type=" + typeName + " 对应的类型codec");

        FileDataTypeEnum layoutType = FileDataTypeEnum.valueOf(params[0].toUpperCase());
        boolean startWithSplit = null != RdfFileUtil.getRowSplit(ctx.fileConfig) && fileMeta.isStartWithSplit(layoutType);
        boolean endtWithSplit = null != RdfFileUtil.getRowSplit(ctx.fileConfig) && fileMeta.isEndWithSplit(layoutType);

        switch (ctx.codecType) {
            case SERIALIZE:
                StringBuilder line = new StringBuilder();
                if (startWithSplit) {
                    line.append(RdfFileUtil.getRowSplit(ctx.fileConfig));
                }
                line.append(getColumnSize(fileMeta));
                if (endtWithSplit) {
                    line.append(RdfFileUtil.getRowSplit(ctx.fileConfig));
                }
                ctx.writer.writeLine(columnFormat.serialize(line.toString(), ctx.columnMeta, ctx.fileConfig));
                break;
            case DESERIALIZE:
                String value = ctx.reader.readLine();
                if (startWithSplit) {
                    value = value.substring(RdfFileUtil.getRowSplit(ctx.fileConfig).length());
                }
                if (endtWithSplit) {
                    value = value.substring(0, value.length() - RdfFileUtil.getRowSplit(ctx.fileConfig).length());
                }
                Object field = columnTypeCodec.deserialize(value, ctx.columnMeta);
                RdfFileUtil.assertEquals(field.toString(),
                        String.valueOf(fileMeta.getBodyColumns().size()));
                break;
            default:
                throw new RdfFileException("不支持序列化反序列化类型" + ctx.codecType.name(),
                        RdfErrorEnum.UNSUPPORTED_OPERATION);
        }
    }

    private int getColumnSize(FileMeta fileMeta) {
        FileDataTypeEnum dataType = FileDataTypeEnum.valueOf(params[1].toUpperCase());
        switch (dataType) {
            case HEAD:
                return fileMeta.getHeadColumns().size();
            case BODY:
                return fileMeta.getBodyColumns().size();
            case TAIL:
                return fileMeta.getTailColumns().size();
            default:
                throw new RdfFileException(
                        "rdf-file#ColumnInfoFunction.rowsAffected dateType=" + dataType.name(),
                        RdfErrorEnum.UNSUPPORTED_OPERATION);
        }
    }

}