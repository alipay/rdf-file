package com.alipay.rdf.file.function;

import com.alipay.rdf.file.mysql.RealVisitor;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 *
 * @author hongwei.quhw
 * @version $Id: ColumnFunction.java, v 0.1 2018年3月12日 下午4:18:13 hongwei.quhw Exp $
 */
public class ColumnFunction extends RdfFunction {


    public String desc(FuncContext ctx) {
        return ctx.columnMeta.getDesc();
    }

    public Object value(FuncContext ctx) {
        if (ctx.columnMeta.getGenerateRule() != null) {
            RealVisitor visitor = new RealVisitor(ctx.rowBean);
            return visitor.visit(ctx.columnMeta.getGenerateRule());
        }
        return ctx.field;
    }

}
