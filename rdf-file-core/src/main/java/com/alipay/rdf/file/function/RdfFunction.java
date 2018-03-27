package com.alipay.rdf.file.function;

import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.alipay.rdf.file.exception.RdfErrorEnum;
import com.alipay.rdf.file.exception.RdfFileException;
import com.alipay.rdf.file.loader.ExtensionLoader;
import com.alipay.rdf.file.meta.FileMeta;
import com.alipay.rdf.file.model.FileDataTypeEnum;
import com.alipay.rdf.file.protocol.RowDefinition;
import com.alipay.rdf.file.spi.RdfFileFunctionSpi;
import com.alipay.rdf.file.util.RdfFileLogUtil;
import com.alipay.rdf.file.util.RdfFileUtil;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 * 
 * @author hongwei.quhw
 * @version $Id: RdfFunction.java, v 0.1 2017年4月5日 下午7:27:32 hongwei.quhw Exp $
 */
public abstract class RdfFunction implements RdfFileFunctionSpi {
    protected FileDataTypeEnum rowType;

    protected String           expression;

    protected String[]         params;

    @Override
    public void checkParams() {
    }

    public String[] getParams() {
        return params;
    }

    public void setParams(String[] params) {
        this.params = params;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public Object execute(FuncContext ctx) {
        try {
            Method method = getClass().getDeclaredMethod(expression, FuncContext.class);
            return method.invoke(this, ctx);
        } catch (Exception e) {
            throw new RdfFileException("执行[" + getClass().getName() + "." + expression + "]方法是出错",
                e, RdfErrorEnum.FUNCTION_ERROR);
        }
    }

    /**
     *  此函数涉及数据行数, 默认影响一行
     * 
     * @return
     */
    public int rowsAffected(RowDefinition rd, FileMeta fileMeta) {
        return 1;
    }

    public FileDataTypeEnum getRowType() {
        return rowType;
    }

    public void setRowType(FileDataTypeEnum rowType) {
        this.rowType = rowType;
    }

    private static final Pattern PATTERN        = Pattern.compile("^\\$\\{(.*)\\}$");

    private static final Pattern METHOD_PATTERN = Pattern
        .compile("^([a-zA-Z]+)\\(([a-zA-Z,]*)\\)$");

    public static RdfFileFunctionSpi parse(String text, FileDataTypeEnum rowType) {
        if (RdfFileLogUtil.common.isInfo()) {
            RdfFileLogUtil.common
                .info("rdf-file# parse function text=" + text + ", rowType=" + rowType.name());
        }

        text = RdfFileUtil.assertTrimNotBlank(text);
        Matcher matcher = PATTERN.matcher(text);

        RdfFileFunctionSpi rdfFunction = null;

        if (matcher.find()) {
            String expr = matcher.group(1);
            expr = RdfFileUtil.assertTrimNotBlank(expr, "rdf-file#协议中配置的表达没有内容：" + text,
                RdfErrorEnum.FUNCTION_ERROR);
            String[] exprsArray = RdfFileUtil.split(expr, ".");
            if (exprsArray.length == 1) {
                rdfFunction = new VariableFunction();
                rdfFunction.setExpression(expr);
                rdfFunction.setRowType(rowType);
            } else if (exprsArray.length == 2) {
                rdfFunction = ExtensionLoader.getExtensionLoader(RdfFileFunctionSpi.class)
                    .getNewExtension(exprsArray[0]);

                Matcher methodMatcher = METHOD_PATTERN.matcher(exprsArray[1]);
                if (methodMatcher.find()) {
                    rdfFunction.setExpression(methodMatcher.group(1));
                    String params = methodMatcher.group(2);
                    if (RdfFileUtil.isNotBlank(params)) {
                        if (RdfFileLogUtil.common.isInfo()) {
                            RdfFileLogUtil.common
                                .info("rdf-file# parse function text=" + text + ", rowType="
                                      + rowType.name() + ", parse method params=" + params);
                        }
                        rdfFunction.setParams(params.split(","));
                    }
                } else {
                    throw new RdfFileException("rdf-file#协议中配置的表达有误：" + text + ", 对象方法配置错误",
                        RdfErrorEnum.FUNCTION_ERROR);
                }

                rdfFunction.setRowType(rowType);
            } else {
                throw new RdfFileException("rdf-file#协议中配置的表达有误：" + text + ", 目前只支持访问对象自己属性方法",
                    RdfErrorEnum.FUNCTION_ERROR);
            }
        } else {
            rdfFunction = new ConstFunction();
            rdfFunction.setExpression(text);
            rdfFunction.setRowType(rowType);
        }

        //校验参数
        rdfFunction.checkParams();
        return rdfFunction;
    }

}
