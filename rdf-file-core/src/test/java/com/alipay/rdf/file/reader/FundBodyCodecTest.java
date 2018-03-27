package com.alipay.rdf.file.reader;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;

import org.junit.Assert;
import org.junit.Test;

import com.alipay.rdf.file.codec.RowColumnHorizontalCodec;
import com.alipay.rdf.file.exception.RdfErrorEnum;
import com.alipay.rdf.file.exception.RdfFileException;
import com.alipay.rdf.file.loader.ProtocolLoader;
import com.alipay.rdf.file.model.FileConfig;
import com.alipay.rdf.file.model.FileDataTypeEnum;
import com.alipay.rdf.file.model.StorageConfig;
import com.alipay.rdf.file.protocol.ProtocolDefinition;
import com.alipay.rdf.file.util.BeanMapWrapper;
import com.alipay.rdf.file.util.DateUtil;

/**
 * 
 * @author hongwei.quhw
 * @version $Id: FundBodyCodecTest.java, v 0.1 2016-12-27 下午4:15:39 hongwei.quhw Exp $
 */
public class FundBodyCodecTest {

    @Test
    public void testDoSerialize() throws Exception {
        ProtocolDefinition pd = ProtocolLoader.loadProtocol("fund");
        BeanMapWrapper bmw = new BeanMapWrapper(HashMap.class);
        FileConfig fileConfig = new FileConfig("filePath", "/codec/test_codec.cfg",
            new StorageConfig("nas"));

        String line = RowColumnHorizontalCodec.serialize(bmw, fileConfig, pd.getBodys().get(0),
            null, FileDataTypeEnum.BODY);
        Assert.assertEquals("            000000000000000000000000            ", line);

        bmw.setProperty("FundCode", "");
        line = RowColumnHorizontalCodec.serialize(bmw, fileConfig, pd.getBodys().get(0), null,
            FileDataTypeEnum.BODY);
        Assert.assertEquals("            000000000000000000000000            ", line);

        bmw.setProperty("AvailableVol", new BigDecimal("4712.45"));
        line = RowColumnHorizontalCodec.serialize(bmw, fileConfig, pd.getBodys().get(0), null,
            FileDataTypeEnum.BODY);
        Assert.assertEquals("            471245000000000000000000            ", line);

        bmw.setProperty("AvailableVol", new BigDecimal("4712.45999999000000000000000000"));
        line = RowColumnHorizontalCodec.serialize(bmw, fileConfig, pd.getBodys().get(0), null,
            FileDataTypeEnum.BODY);
        Assert.assertEquals("            471245000000000000000000            ", line);

        bmw.setProperty("AvailableVol", new BigDecimal("4712.45111"));
        line = RowColumnHorizontalCodec.serialize(bmw, fileConfig, pd.getBodys().get(0), null,
            FileDataTypeEnum.BODY);
        Assert.assertEquals("            471245000000000000000000            ", line);
    }

    @Test
    public void testSerialize2() throws Exception {

        ProtocolDefinition pd = ProtocolLoader.loadProtocol("fund");
        BeanMapWrapper bmw = new BeanMapWrapper(HashMap.class);
        FileConfig fileConfig = new FileConfig("filePath", "/codec/test_codec.cfg",
            new StorageConfig("nas"));
        bmw.setProperty("AvailableVol", new BigDecimal("125"));
        bmw.setProperty("TransactionCfmDate", 253);
        bmw.setProperty("FundCode", "2中国");
        String line = RowColumnHorizontalCodec.serialize(bmw, fileConfig, pd.getBodys().get(0),
            null, FileDataTypeEnum.BODY);
        Assert.assertEquals("253   2中国 012500000000000000000000            ", line);

        bmw.setProperty("FundCode", "2中国a");
        line = RowColumnHorizontalCodec.serialize(bmw, fileConfig, pd.getBodys().get(0), null,
            FileDataTypeEnum.BODY);
        Assert.assertEquals("253   2中国a012500000000000000000000            ", line);

        bmw.setProperty("FundCode", " 中国a");
        line = RowColumnHorizontalCodec.serialize(bmw, fileConfig, pd.getBodys().get(0), null,
            FileDataTypeEnum.BODY);
        Assert.assertEquals("253   中国a 012500000000000000000000            ", line);

        bmw.setProperty("TransactionCfmDate", "253靠");
        line = RowColumnHorizontalCodec.serialize(bmw, fileConfig, pd.getBodys().get(0), null,
            FileDataTypeEnum.BODY);
        Assert.assertEquals("253靠 中国a 012500000000000000000000            ", line);
    }

    @Test
    public void testSerialize() throws Exception {
        ProtocolDefinition pd = ProtocolLoader.loadProtocol("fund");
        BeanMapWrapper bmw = new BeanMapWrapper(HashMap.class);
        FileConfig fileConfig = new FileConfig("filePath", "/codec/test_codec.cfg",
            new StorageConfig("nas"));
        bmw.setProperty("AvailableVol", new BigDecimal("125"));
        bmw.setProperty("TransactionCfmDate", 253);
        bmw.setProperty("FundCode", "2");
        String line = RowColumnHorizontalCodec.serialize(bmw, fileConfig, pd.getBodys().get(0),
            null, FileDataTypeEnum.BODY);
        Assert.assertEquals("253   2     012500000000000000000000            ", line);

        bmw.setProperty("AvailableVol", new BigDecimal("12.5"));
        line = RowColumnHorizontalCodec.serialize(bmw, fileConfig, pd.getBodys().get(0), null,
            FileDataTypeEnum.BODY);
        Assert.assertEquals("253   2     001250000000000000000000            ", line);

        bmw.setProperty("AvailableVol", new BigDecimal("1.25"));
        line = RowColumnHorizontalCodec.serialize(bmw, fileConfig, pd.getBodys().get(0), null,
            FileDataTypeEnum.BODY);
        Assert.assertEquals("253   2     000125000000000000000000            ", line);

        //超出小数部分截位
        bmw.setProperty("AvailableVol", new BigDecimal("0.125"));
        line = RowColumnHorizontalCodec.serialize(bmw, fileConfig, pd.getBodys().get(0), null,
            FileDataTypeEnum.BODY);
        Assert.assertEquals("253   2     000012000000000000000000            ", line);

        line = RowColumnHorizontalCodec.serialize(new BeanMapWrapper(HashMap.class), fileConfig,
            pd.getBodys().get(0), null, FileDataTypeEnum.BODY);
        Assert.assertEquals("            000000000000000000000000            ", line);
    }

    @Test
    public void testDeserialize2() {
        ProtocolDefinition pd = ProtocolLoader.loadProtocol("fund");
        BeanMapWrapper bmw = new BeanMapWrapper(HashMap.class);
        FileConfig fileConfig = new FileConfig("filePath", "/codec/test_codec.cfg",
            new StorageConfig("nas"));

        String line = "253   2中   012500000000000000000000            ";

        RowColumnHorizontalCodec.deserialize(bmw, fileConfig, line, pd.getBodys().get(0), null,
            FileDataTypeEnum.BODY);
        Assert.assertEquals("253", bmw.getProperty("TransactionCfmDate"));
        Assert.assertEquals(new BigDecimal(125), bmw.getProperty("AvailableVol"));
        Assert.assertEquals("2中", bmw.getProperty("FundCode"));

        line = "253   2中国b012500000000000000000000            ";
        RowColumnHorizontalCodec.deserialize(bmw, fileConfig, line, pd.getBodys().get(0), null,
            FileDataTypeEnum.BODY);
        Assert.assertEquals("253", bmw.getProperty("TransactionCfmDate"));
        Assert.assertEquals(new BigDecimal(125), bmw.getProperty("AvailableVol"));
        Assert.assertEquals("2中国b", bmw.getProperty("FundCode"));
    }

    @Test
    public void testDeserialize() {
        ProtocolDefinition pd = ProtocolLoader.loadProtocol("fund");
        BeanMapWrapper bmw = new BeanMapWrapper(HashMap.class);
        FileConfig fileConfig = new FileConfig("filePath", "/codec/test_codec.cfg",
            new StorageConfig("nas"));

        String line = "253   2     012500000000000000000000            ";
        RowColumnHorizontalCodec.deserialize(bmw, fileConfig, line, pd.getBodys().get(0), null,
            FileDataTypeEnum.BODY);
        Assert.assertEquals("253", bmw.getProperty("TransactionCfmDate"));
        Assert.assertEquals(new BigDecimal(125), bmw.getProperty("AvailableVol"));
        Assert.assertEquals("2", bmw.getProperty("FundCode"));

        //空
        line = "            000010000000000000000000            ";
        RowColumnHorizontalCodec.deserialize(bmw, fileConfig, line, pd.getBodys().get(0), null,
            FileDataTypeEnum.BODY);
        Assert.assertEquals(null, bmw.getProperty("TransactionCfmDate"));
        Assert.assertEquals(new BigDecimal("0.1"), bmw.getProperty("AvailableVol")); //有小数
        Assert.assertEquals(null, bmw.getProperty("FundCode"));
    }

    @Test
    public void testDigitalChar() throws Exception {
        ProtocolDefinition pd = ProtocolLoader.loadProtocol("fund");
        BeanMapWrapper bmw = new BeanMapWrapper(HashMap.class);
        FileConfig fileConfig = new FileConfig("filePath", "/codec/DigitalChar.cfg",
            new StorageConfig("nas"));

        String line = "123456";
        RowColumnHorizontalCodec.deserialize(bmw, fileConfig, line, pd.getBodys().get(0), null,
            FileDataTypeEnum.BODY);
        Assert.assertEquals(line, bmw.getProperty("testDigitalChar"));

        line = "12345 ";
        RowColumnHorizontalCodec.deserialize(bmw, fileConfig, line, pd.getBodys().get(0), null,
            FileDataTypeEnum.BODY);
        Assert.assertEquals("12345", bmw.getProperty("testDigitalChar"));

        line = "12i45 ";
        try {
            RowColumnHorizontalCodec.deserialize(bmw, fileConfig, line, pd.getBodys().get(0), null,
                FileDataTypeEnum.BODY);
            Assert.fail();
        } catch (RdfFileException e) {
            Assert.assertEquals(RdfErrorEnum.VALIDATE_ERROR, e.getErrorEnum());
        }

        bmw.setProperty("testDigitalChar", "123");
        line = RowColumnHorizontalCodec.serialize(bmw, fileConfig, pd.getBodys().get(0), null,
            FileDataTypeEnum.BODY);
        Assert.assertEquals("123   ", line);

        try {
            bmw.setProperty("testDigitalChar", "12o");
            RowColumnHorizontalCodec.serialize(bmw, fileConfig, pd.getBodys().get(0), null,
                FileDataTypeEnum.BODY);
            Assert.fail();
        } catch (RdfFileException e) {
            Assert.assertEquals(RdfErrorEnum.VALIDATE_ERROR, e.getErrorEnum());
        }
    }

    @Test
    public void testDate() throws Exception {
        ProtocolDefinition pd = ProtocolLoader.loadProtocol("fund");
        BeanMapWrapper bmw = new BeanMapWrapper(HashMap.class);
        FileConfig fileConfig = new FileConfig("filePath", "/codec/Date.cfg",
            new StorageConfig("nas"));

        String line = "201612  ";
        RowColumnHorizontalCodec.deserialize(bmw, fileConfig, line, pd.getBodys().get(0), null,
            FileDataTypeEnum.BODY);
        Assert.assertEquals("201612",
            DateUtil.format((Date) bmw.getProperty("testDate"), "yyyyMM"));

        Date date = DateUtil.parse("201612", "yyyyMM");
        bmw.setProperty("testDate", date);
        line = RowColumnHorizontalCodec.serialize(bmw, fileConfig, pd.getBodys().get(0), null,
            FileDataTypeEnum.BODY);
        Assert.assertEquals("201612  ", line);
    }

    @Test
    public void testLongAndInteger() throws Exception {
        ProtocolDefinition pd = ProtocolLoader.loadProtocol("fund");
        BeanMapWrapper bmw = new BeanMapWrapper(HashMap.class);
        FileConfig fileConfig = new FileConfig("filePath", "/codec/long&int.confg",
            new StorageConfig("nas"));

        String line = "0121098";
        RowColumnHorizontalCodec.deserialize(bmw, fileConfig, line, pd.getBodys().get(0), null,
            FileDataTypeEnum.BODY);
        Assert.assertEquals(new Long(121), bmw.getProperty("testLong"));
        Assert.assertEquals(new Integer(98), bmw.getProperty("testInteger"));

        bmw.setProperty("testLong", 12L);
        bmw.setProperty("testInteger", 5);
        line = RowColumnHorizontalCodec.serialize(bmw, fileConfig, pd.getBodys().get(0), null,
            FileDataTypeEnum.BODY);
        Assert.assertEquals("0012005", line);
    }
}
