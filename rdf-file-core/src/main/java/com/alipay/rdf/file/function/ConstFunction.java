package com.alipay.rdf.file.function;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 * 
 * 常量函数
 * 
 * @author hongwei.quhw
 * @version $Id: ConstFunction.java, v 0.1 2017年4月5日 下午7:36:38 hongwei.quhw Exp $
 */
public class ConstFunction extends RdfFunction {
    @Override
    public String execute(FuncContext ctx) {
        return expression;
    }
}
