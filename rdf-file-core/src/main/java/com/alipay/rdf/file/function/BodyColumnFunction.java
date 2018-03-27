package com.alipay.rdf.file.function;

import java.io.IOException;

import com.alipay.rdf.file.codec.BodyColumnHorizontalCodec;
import com.alipay.rdf.file.codec.BodyColumnVerticalCodec;
import com.alipay.rdf.file.exception.RdfErrorEnum;
import com.alipay.rdf.file.exception.RdfFileException;
import com.alipay.rdf.file.loader.ExtensionLoader;
import com.alipay.rdf.file.loader.FormatLoader;
import com.alipay.rdf.file.loader.TemplateLoader;
import com.alipay.rdf.file.meta.FileMeta;
import com.alipay.rdf.file.protocol.RowDefinition;
import com.alipay.rdf.file.spi.RdfFileColumnTypeSpi;
import com.alipay.rdf.file.spi.RdfFileFormatSpi;
import com.alipay.rdf.file.util.RdfFileUtil;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 * 
 * body 字段
 * 
 * @author hongwei.quhw
 * @version $Id: BodyColumnFunction.java, v 0.1 2017年8月19日 下午1:21:38 hongwei.quhw Exp $
 */
@SuppressWarnings("rawtypes")
public class BodyColumnFunction extends RdfFunction {
    @Override
    public void checkParams() {
        if (("horizontal".equals(expression) || "vertical".equals(expression))
            && (null == params || params.length != 1)) {
            throw new RdfFileException("rdf-file#BodyColumnFunction.checkParams() 指定的参数应该为一个",
                RdfErrorEnum.FUNCTION_ERROR);
        }
    }

    public void horizontal(FuncContext ctx) throws IOException {
        if (CodecType.SERIALIZE.equals(ctx.codecType)) {
            BodyColumnHorizontalCodec.serialize(ctx.rowBean, ctx.fileConfig, ctx.writer, params[0]);

        } else if (CodecType.DESERIALIZE.equals(ctx.codecType)) {
            BodyColumnHorizontalCodec.deserialize(null, ctx.fileConfig, ctx.reader, params[0]);
        }
    }

    public void vertical(FuncContext ctx) throws IOException {
        if (CodecType.SERIALIZE.equals(ctx.codecType)) {
            BodyColumnVerticalCodec.serialize(ctx.rowBean, ctx.fileConfig, ctx.writer, params[0]);
        } else if (CodecType.DESERIALIZE.equals(ctx.codecType)) {
            BodyColumnVerticalCodec.deserialize(null, ctx.fileConfig, ctx.reader, params[0]);
        }
    }

    public void count(FuncContext ctx) throws IOException {
        FileMeta fileMeta = TemplateLoader.load(ctx.fileConfig);
        String typeName = ctx.columnMeta.getType().getName();

        RdfFileFormatSpi columnFormat = FormatLoader.getColumnFormt(fileMeta.getProtocol(),
            typeName);
        RdfFileUtil.assertNotNull(columnFormat, "类型type=" + typeName + " 对应的format没有");
        RdfFileColumnTypeSpi columnTypeCodec = ExtensionLoader
            .getExtensionLoader(RdfFileColumnTypeSpi.class).getExtension(typeName);
        RdfFileUtil.assertNotNull(columnTypeCodec, "没有type=" + typeName + " 对应的类型codec");

        switch (ctx.codecType) {
            case SERIALIZE:
                String value = String.valueOf(fileMeta.getBodyColumns().size());
                ctx.writer.writeLine(columnFormat.serialize(value, ctx.columnMeta, ctx.fileConfig));
                break;
            case DESERIALIZE:
                value = ctx.reader.readLine();
                Object field = columnTypeCodec.deserialize(value, ctx.columnMeta);
                RdfFileUtil.assertEquals(field.toString(),
                    String.valueOf(fileMeta.getBodyColumns().size()));
                break;
            default:
                throw new RdfFileException("不支持序列号反序列化类型" + ctx.codecType.name(),
                    RdfErrorEnum.UNSUPPORTED_OPERATION);
        }
    }

    @Override
    public int rowsAffected(RowDefinition rd, FileMeta fileMeta) {
        if ("horizontal".equals(expression)) {
            return 1;
        } else if ("vertical".equals(expression)) {
            return fileMeta.getBodyColumns().size();
        } else if ("count".equals(expression)) {
            return 1;
        } else {
            throw new RdfFileException(
                "rdf-file#BodyColumnFunction函数针对方法" + expression + ", 无法计算rowsAffected",
                RdfErrorEnum.UNSUPPORTED_OPERATION);
        }
    }
}
