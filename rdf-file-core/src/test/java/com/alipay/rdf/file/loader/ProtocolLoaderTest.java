package com.alipay.rdf.file.loader;

import com.alipay.rdf.file.function.ColumnFunctionWrapper;
import com.alipay.rdf.file.function.ColumnInfoFunction;
import com.alipay.rdf.file.function.VariableFunction;
import com.alipay.rdf.file.model.FileConfig;
import com.alipay.rdf.file.model.StorageConfig;
import com.alipay.rdf.file.processor.ProcessorTypeEnum;
import com.alipay.rdf.file.protocol.ColumnLayoutEnum;
import com.alipay.rdf.file.protocol.ProtocolDefinition;
import com.alipay.rdf.file.protocol.RowDefinition;
import com.alipay.rdf.file.spi.RdfFileProcessorSpi;
import com.alipay.rdf.file.split.RowSplitByFixedlLength;
import com.alipay.rdf.file.split.RowSplitBySeparator;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Map;

/**
 *
 *
 * @author hongwei.quhw
 * @version $Id: ProtocolLoaderTest.java, v 0.1 2017年4月7日 上午11:02:24 hongwei.quhw Exp $
 */
public class ProtocolLoaderTest {

    @Test
    public void testLoadFund() {
        ProtocolDefinition pd = ProtocolLoader.loadProtocol("fund");
        Assert.assertEquals("fund", pd.getName());
        Assert.assertEquals(RowSplitByFixedlLength.class.getName(),
                pd.getRowSplit().getClass().getName());

        List<RowDefinition> headRDs = pd.getHeads();
        Assert.assertTrue(headRDs.get(0).isColumnloop());
        Assert.assertEquals(ColumnLayoutEnum.vertical, headRDs.get(0).getColumnLayout());
        Assert.assertEquals(ColumnFunctionWrapper.class.getName(),
                headRDs.get(0).getOutput().getClass().getName());

        Assert.assertFalse(headRDs.get(1).isColumnloop());
        Assert.assertNull(headRDs.get(1).getColumnLayout());
        Assert.assertEquals(ColumnInfoFunction.class.getName(),
                headRDs.get(1).getOutput().getClass().getName());
        Assert.assertEquals("count", headRDs.get(1).getOutput().getExpression());

        Assert.assertFalse(headRDs.get(2).isColumnloop());
        Assert.assertNull(headRDs.get(2).getColumnLayout());
        Assert.assertEquals(ColumnInfoFunction.class.getName(),
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
    public void testLoadDE() {
        ProtocolDefinition pd = ProtocolLoader.loadProtocol("de");

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
        Assert.assertEquals(ColumnInfoFunction.class.getName(),
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

    @Test
    public void testOrder() {
        FileConfig fileConfig = new FileConfig("filePaht", "/reader/de/template/template1.json",
                new StorageConfig("nas"));
        fileConfig.addProcessorKey("processor1");
        fileConfig.addProcessorKey("processor2");
        fileConfig.addProcessorKey("processor3");
        Map<ProcessorTypeEnum, List<RdfFileProcessorSpi>> ret = ProcessorLoader
                .loadByType(fileConfig, ProcessorTypeEnum.AFTER_CLOSE_WRITER);

        List<RdfFileProcessorSpi> proces = ret.get(ProcessorTypeEnum.AFTER_CLOSE_WRITER);
        Assert.assertEquals(3, proces.size());
        Assert.assertEquals(0, proces.get(0).getOrder());
        Assert.assertEquals(10, proces.get(1).getOrder());
        Assert.assertEquals(15, proces.get(2).getOrder());
    }
}
