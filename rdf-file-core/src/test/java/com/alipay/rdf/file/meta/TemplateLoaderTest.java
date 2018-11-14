package com.alipay.rdf.file.meta;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.alipay.rdf.file.exception.RdfErrorEnum;
import com.alipay.rdf.file.exception.RdfFileException;
import com.alipay.rdf.file.loader.TemplateLoader;
import com.alipay.rdf.file.model.FileDataTypeEnum;
import com.alipay.rdf.file.model.FileDefaultConfig;
import com.alipay.rdf.file.util.TestLog;

/**
 * 模板加载测试
 * 
 * @author hongwei.quhw
 * @version $Id: TemplateLoaderTest.java, v 0.1 2016-12-22 下午2:01:39 hongwei.quhw Exp $
 */
public class TemplateLoaderTest {

    @Before
    public void setUp() {
        new FileDefaultConfig().setCommonLog(new TestLog());
    }

    /**
     * 加载gbk编码的模板
     */
    @Test
    public void testTemplateEncoding() {
        FileMeta fileMeta = TemplateLoader.load("/meta/de-gbk.json", "GBK");
        checkTemplateEncoding(fileMeta);

        fileMeta = TemplateLoader.load("/meta/de-utf-8.json", "UTF-8");
        checkTemplateEncoding(fileMeta);
    }

    private void checkTemplateEncoding(FileMeta fileMeta) {
        List<FileColumnMeta> headMetas = fileMeta.getHeadColumns();
        Assert.assertEquals(2, headMetas.size());

        Assert.assertEquals("recordId", headMetas.get(0).getName());
        Assert.assertEquals("recordId", headMetas.get(0).getDesc());

        Assert.assertEquals("acctRef", headMetas.get(1).getName());
        Assert.assertEquals("测试", headMetas.get(1).getDesc());

        List<FileColumnMeta> bodyMetas = fileMeta.getBodyColumns();
        Assert.assertEquals(7, bodyMetas.size());

        Assert.assertEquals("mfundAccountNo", bodyMetas.get(2).getName());
        Assert.assertEquals("货币基金份额账户", bodyMetas.get(2).getDesc());
        Assert.assertEquals("BigDecimal", bodyMetas.get(2).getType().getName());

        Assert.assertEquals("date", bodyMetas.get(3).getName());
        Assert.assertEquals("日期类型", bodyMetas.get(3).getDesc());
        Assert.assertEquals("Date", bodyMetas.get(3).getType().getName());
        Assert.assertEquals("yyyy-MM-dd HH:mm:ss", bodyMetas.get(3).getType().getExtra());

        Assert.assertEquals("count", bodyMetas.get(4).getName());
        Assert.assertEquals("金额", bodyMetas.get(4).getDesc());
        Assert.assertEquals("BigDecimal", bodyMetas.get(4).getType().getName());
        Assert.assertNull(bodyMetas.get(4).getType().getExtra());
        Assert.assertEquals(10, bodyMetas.get(4).getRange().getFirstAttr());
        Assert.assertEquals(2, bodyMetas.get(4).getRange().getSecondAttr());

        Assert.assertEquals("name", bodyMetas.get(5).getName());
        Assert.assertEquals("姓名", bodyMetas.get(5).getDesc());
        Assert.assertEquals("String", bodyMetas.get(5).getType().getName());
        Assert.assertNull(bodyMetas.get(5).getType().getExtra());
        Assert.assertTrue(bodyMetas.get(5).isRequired());
        Assert.assertEquals("jack", bodyMetas.get(5).getDefaultValue());

        Assert.assertEquals("test", bodyMetas.get(6).getName());
        Assert.assertEquals("所有", bodyMetas.get(6).getDesc());
        Assert.assertEquals("String", bodyMetas.get(6).getType().getName());
        Assert.assertNull(bodyMetas.get(6).getType().getExtra());
        Assert.assertFalse(bodyMetas.get(6).isRequired());
        Assert.assertEquals("jack", bodyMetas.get(6).getDefaultValue());
        Assert.assertEquals(20, bodyMetas.get(6).getRange().getFirstAttr());
        Assert.assertEquals(23, bodyMetas.get(6).getRange().getSecondAttr());

        Assert.assertEquals("totalCount", fileMeta.getTotalCountKey());

    }

    @Test
    public void testSpecialcharacters() {
        FileMeta fileMeta = TemplateLoader.load("/meta/template1.json", "UTF-8");
        Assert.assertEquals("\r", fileMeta.getLineBreak());
        Assert.assertEquals("|@|", fileMeta.getColumnSplit());
        Assert.assertEquals("totalCount", fileMeta.getTotalCountKey());
        Assert.assertTrue(fileMeta.isStartWithSplit(FileDataTypeEnum.HEAD));
        Assert.assertTrue(fileMeta.isStartWithSplit(FileDataTypeEnum.BODY));
        Assert.assertTrue(fileMeta.isStartWithSplit(FileDataTypeEnum.TAIL));

    }

    @Test
    public void testLineBreak() {
        try {
            TemplateLoader.load("/meta/template2.json", "UTF-8");
        } catch (RdfFileException e) {
            Assert.assertEquals(RdfErrorEnum.UNSUPPORT_LINEBREAK, e.getErrorEnum());
        }
    }

    @Test
    public void testStartOrEndwithSplit() {
        FileMeta fileMeta = TemplateLoader.load("/meta/template3.json", "UTF-8");
        Assert.assertFalse(fileMeta.isStartWithSplit(FileDataTypeEnum.HEAD));
        Assert.assertTrue(fileMeta.isStartWithSplit(FileDataTypeEnum.BODY));
        Assert.assertTrue(fileMeta.isStartWithSplit(FileDataTypeEnum.TAIL));
        Assert.assertFalse(fileMeta.isEndWithSplit(FileDataTypeEnum.HEAD));
        Assert.assertFalse(fileMeta.isEndWithSplit(FileDataTypeEnum.BODY));
        Assert.assertFalse(fileMeta.isEndWithSplit(FileDataTypeEnum.TAIL));

    }

    @Test
    public void testSummary() {
        try {
            TemplateLoader.load("/meta/template4.json", "UTF-8");
            Assert.fail();
        } catch (RdfFileException e) {
            Assert.assertEquals(RdfErrorEnum.DUPLICATE_DEFINED, e.getErrorEnum());
        }

        FileMeta fileMeta = TemplateLoader.load("/meta/template5.json", "UTF-8");

        List<SummaryPairMeta> summaryPairs = fileMeta.getSummaryPairMetas();
        SummaryPairMeta summaryPair = summaryPairs.get(0);
        Assert.assertEquals("totalAmount", summaryPair.getSummaryKey());
        Assert.assertEquals("amount", summaryPair.getColumnKey());
        Assert.assertEquals(FileDataTypeEnum.HEAD, summaryPair.getSummaryDataType());

        summaryPair = summaryPairs.get(1);
        Assert.assertEquals("totalApplyNumber", summaryPair.getSummaryKey());
        Assert.assertEquals("applyNumber", summaryPair.getColumnKey());
        Assert.assertEquals(FileDataTypeEnum.TAIL, summaryPair.getSummaryDataType());
        System.out.println(summaryPairs);
    }

    /**
     * Date 格式需要制定format格式
     */
    @Test
    public void testDate() {
        try {
            TemplateLoader.load("/meta/date_wrong.json", "UTF-8");
            Assert.fail();
        } catch (Exception e) {
            Assert.assertEquals("日期类型必须指定format", e.getMessage());
        }
    }

    /**
     * 测试读取制定文件的编码格式
     */
    @Test
    public void testFileEncoding() {
        FileMeta fileMeta = TemplateLoader.load("/meta/fileconding.json", "UTF-8");
        Assert.assertEquals("UTF-8", fileMeta.getFileEncoding());
    }

    @Test
    public void testSummary_condition() {
        FileMeta fileMeta = TemplateLoader.load("/meta/template6.json", "UTF-8");

        StatisticPairMeta pair = fileMeta.getStatisticPairMetas().get(0);
    }
}
