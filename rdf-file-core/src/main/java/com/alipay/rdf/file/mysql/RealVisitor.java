package com.alipay.rdf.file.mysql;

import java.util.Map;

import org.antlr.v4.runtime.tree.RuleNode;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

public class RealVisitor extends MySqlBaseVisitor<String> {

    private Object rowBean;

    public RealVisitor(Object rowBean) {
        super();
        this.rowBean = rowBean;
    }

    public String getField(String key) {
        return ((Map<String, Object>) rowBean).get(key).toString();
    };

    @Override
    public String visitSimpleId(MySqlParser.SimpleIdContext ctx) {
        if (ctx.ID() != null) {
            return getField(ctx.ID().getText());
        }
        throw new RuntimeException("unknown column name ");
    }

    @Override
    public String visitTerminal(TerminalNode node) {
        return node.getText();
    }


    @Override
    public String visitSubstrFunctionCall(MySqlParser.SubstrFunctionCallContext ctx) {
        String source = this.visitChildren(ObjectUtils.firstNonNull(ctx.sourceExpression, ctx.sourceString));
        String fromDec = this.visitChildren(ObjectUtils.firstNonNull(ctx.fromExpression, ctx.fromDecimal));
        String forDec = this.visitChildren(ObjectUtils.firstNonNull(ctx.forExpression, ctx.forDecimal));
        int fromInt = Integer.parseInt(fromDec), forInt = Integer.parseInt(forDec);
        return StringUtils.substring(source, fromInt, fromInt + forInt);
    }

    @Override
    protected String aggregateResult(String aggregate, String nextResult) {
        if (nextResult != null) {
            return nextResult;
        }
        return aggregate;
    }

}
