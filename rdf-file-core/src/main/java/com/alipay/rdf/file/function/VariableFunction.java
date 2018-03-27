package com.alipay.rdf.file.function;

import java.util.HashMap;
import java.util.Map;

import com.alipay.rdf.file.exception.RdfErrorEnum;
import com.alipay.rdf.file.exception.RdfFileException;
import com.alipay.rdf.file.loader.ExtensionLoader;
import com.alipay.rdf.file.loader.FormatLoader;
import com.alipay.rdf.file.loader.TemplateLoader;
import com.alipay.rdf.file.meta.FileMeta;
import com.alipay.rdf.file.spi.RdfFileColumnTypeSpi;
import com.alipay.rdf.file.spi.RdfFileFormatSpi;
import com.alipay.rdf.file.util.BeanMapWrapper;
import com.alipay.rdf.file.util.RdfFileUtil;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 * 
 * @author hongwei.quhw
 * @version $Id: VariableFunction.java, v 0.1 2017年4月10日 下午5:04:23 hongwei.quhw Exp $
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class VariableFunction extends RdfFunction {
    @Override
    public Object execute(FuncContext ctx) {
        FileMeta fileMeta = TemplateLoader.load(ctx.fileConfig);
        BeanMapWrapper bmw = new BeanMapWrapper(ctx.rowBean);

        String typeName = ctx.columnMeta.getType().getName();

        RdfFileFormatSpi columnFormat = FormatLoader.getColumnFormt(fileMeta.getProtocol(),
            typeName);
        RdfFileUtil.assertNotNull(columnFormat, "类型type=" + typeName + " 对应的format没有");
        RdfFileColumnTypeSpi columnTypeCodec = ExtensionLoader
            .getExtensionLoader(RdfFileColumnTypeSpi.class).getExtension(typeName);
        RdfFileUtil.assertNotNull(columnTypeCodec, "没有type=" + typeName + " 对应的类型codec");

        switch (ctx.codecType) {
            case SERIALIZE:
                Object value = bmw.getProperty(expression);
                String field = columnTypeCodec.serialize(value, ctx.columnMeta);
                return columnFormat.serialize(field, ctx.columnMeta, ctx.fileConfig);
            case DESERIALIZE:
                field = ctx.reader.readLine();
                field = columnFormat.deserialize(field, ctx.columnMeta, ctx.fileConfig);
                value = columnTypeCodec.deserialize(field, ctx.columnMeta);
                Map<String, Object> ret = new HashMap<String, Object>(1);
                ret.put(expression, value);
                return ret;
            default:
                throw new RdfFileException("rdf-file#不支持序列号反序列化类型" + ctx.codecType.name(),
                    RdfErrorEnum.UNSUPPORTED_OPERATION);
        }
    }
}
