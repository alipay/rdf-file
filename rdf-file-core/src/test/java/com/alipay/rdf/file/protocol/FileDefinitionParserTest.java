package com.alipay.rdf.file.protocol;

import java.io.InputStream;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.alipay.rdf.file.function.BodyColumnFunction;
import com.alipay.rdf.file.function.ColumnFunctionWrapper;
import com.alipay.rdf.file.function.VariableFunction;
import com.alipay.rdf.file.loader.ResourceLoader;
import com.alipay.rdf.file.split.RowSplitByFixedlLength;
import com.alipay.rdf.file.split.RowSplitBySeparator;

public class FileDefinitionParserTest {

    @Test
    public void testParseFund() {
        InputStream is = ResourceLoader.getInputStream("META-INF/rdf-file/protocol/fund.xml");

        ProtocolDefinition pd = new FileDefinitionParser().parseFileDefinition(is);
        Assert.assertEquals("fund", pd.getName());
        Assert.assertEquals(RowSplitByFixedlLength.class.getName(),
            pd.getRowSplit().getClass().getName());

        List<RowDefinition> headRDs = pd.getHeads();
        Assert.assertTrue(headRDs.get(0).isColumnloop());
        Assert.assertEquals(ColumnLayoutEnum.vertical, headRDs.get(0).getColumnLayout());
        Assert.assertEquals(ColumnFunctionWrapper.class.getName(),
            headRDs.get(0).getOutput().getClass().getName());

        Assert.assertFalse(headRDs.get(1).isColumnloop());
        Assert.assertNull((headRDs.get(1).getColumnLayout()));
        Assert.assertEquals(BodyColumnFunction.class.getName(),
            headRDs.get(1).getOutput().getClass().getName());
        Assert.assertEquals("count", headRDs.get(1).getOutput().getExpression());

        Assert.assertFalse(headRDs.get(2).isColumnloop());
        Assert.assertNull(headRDs.get(2).getColumnLayout());
        Assert.assertEquals(BodyColumnFunction.class.getName(),
            headRDs.get(2).getOutput().getClass().getName());
        Assert.assertEquals("vertical", headRDs.get(2).getOutput().getExpression());

        Assert.assertFalse(headRDs.get(3).isColumnloop());
        Assert.assertNull(headRDs.get(3).getColumnLayout());
        Assert.assertEquals(VariableFunction.class.getName(),
            headRDs.get(3).getOutput().getClass().getName());

        List<RowDefinition> bodyRDs = pd.getBodys();
        Assert.assertEquals(1, bodyRDs.size());
        Assert.assertEquals(ColumnFunctionWrapper.class.getName(),
            bodyRDs.get(0).getOutput().getClass().getName());

        Assert.assertEquals(ColumnLayoutEnum.horizontal, bodyRDs.get(0).getColumnLayout());

        List<RowDefinition> tailRDs = pd.getTails();
        Assert.assertTrue(tailRDs.get(0).isColumnloop());
        Assert.assertEquals(ColumnLayoutEnum.horizontal, tailRDs.get(0).getColumnLayout());
        Assert.assertEquals(ColumnFunctionWrapper.class.getName(),
            tailRDs.get(0).getOutput().getClass().getName());
    }

    @Test
    public void testParseDE() {
        InputStream is = ResourceLoader.getInputStream("META-INF/rdf-file/protocol/de.xml");

        ProtocolDefinition pd = new FileDefinitionParser().parseFileDefinition(is);

        Assert.assertEquals("de", pd.getName());
        Assert.assertEquals(RowSplitBySeparator.class.getName(),
            pd.getRowSplit().getClass().getName());

        List<RowDefinition> headRDs = pd.getHeads();
        Assert.assertTrue(headRDs.get(0).isColumnloop());
        Assert.assertEquals(ColumnLayoutEnum.horizontal, headRDs.get(0).getColumnLayout());
        Assert.assertEquals(ColumnFunctionWrapper.class.getName(),
            headRDs.get(0).getOutput().getClass().getName());

        Assert.assertFalse(headRDs.get(1).isColumnloop());
        Assert.assertNull(headRDs.get(1).getColumnLayout());
        Assert.assertEquals(BodyColumnFunction.class.getName(),
            headRDs.get(1).getOutput().getClass().getName());

        List<RowDefinition> bodyRDs = pd.getBodys();
        Assert.assertEquals(1, bodyRDs.size());
        Assert.assertEquals(ColumnFunctionWrapper.class.getName(),
            bodyRDs.get(0).getOutput().getClass().getName());

        Assert.assertEquals(ColumnLayoutEnum.horizontal, bodyRDs.get(0).getColumnLayout());

        List<RowDefinition> tailRDs = pd.getTails();
        Assert.assertTrue(tailRDs.get(0).isColumnloop());
        Assert.assertEquals(ColumnLayoutEnum.horizontal, tailRDs.get(0).getColumnLayout());
        Assert.assertEquals(ColumnFunctionWrapper.class.getName(),
            tailRDs.get(0).getOutput().getClass().getName());
    }
}
