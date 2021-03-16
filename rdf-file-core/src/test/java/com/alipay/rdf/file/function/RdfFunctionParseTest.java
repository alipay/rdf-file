package com.alipay.rdf.file.function;

import com.alipay.rdf.file.exception.RdfErrorEnum;
import com.alipay.rdf.file.exception.RdfFileException;
import com.alipay.rdf.file.model.FileDataTypeEnum;
import com.alipay.rdf.file.spi.RdfFileFunctionSpi;
import org.junit.Assert;
import org.junit.Test;

/**
 * 函数解析测试
 * 
 * @author hongwei.quhw
 * @version $Id: RdfFunctionParseTest.java, v 0.1 2017年8月19日 下午1:46:50 hongwei.quhw Exp $
 */
public class RdfFunctionParseTest {

    @Test
    public void test() {
        String express = "${bodycolumn.vertical(name)}";
        RdfFileFunctionSpi func = RdfFunction.parse(express, FileDataTypeEnum.HEAD);
        Assert.assertTrue(func instanceof BodyColumnFunction);
        Assert.assertEquals("vertical", func.getExpression());
        Assert.assertEquals("name", func.getParams()[0]);

        try {
            express = "${bodycolumn.vertical}";
            func = RdfFunction.parse(express, FileDataTypeEnum.HEAD);
            Assert.assertEquals("vertical", func.getExpression());
            Assert.assertNull(func.getParams());
            Assert.fail();
        } catch (RdfFileException e) {
            Assert.assertEquals(RdfErrorEnum.FUNCTION_ERROR, e.getErrorEnum());
        }

        express = "${totalCount}";
        func = RdfFunction.parse(express, FileDataTypeEnum.HEAD);
        Assert.assertTrue(func instanceof VariableFunction);
        Assert.assertEquals("totalCount", func.getExpression());

        express = "totalCount";
        func = RdfFunction.parse(express, FileDataTypeEnum.HEAD);
        Assert.assertTrue(func instanceof ConstFunction);
        Assert.assertEquals("totalCount", func.getExpression());
    }
}
