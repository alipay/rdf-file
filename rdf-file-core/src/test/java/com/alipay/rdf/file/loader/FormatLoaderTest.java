package com.alipay.rdf.file.loader;

import com.alipay.rdf.file.format.AColumnFormat;
import com.alipay.rdf.file.format.CColumnFormat;
import com.alipay.rdf.file.format.NColumnFormat;
import com.alipay.rdf.file.format.RawFormat;
import com.alipay.rdf.file.spi.RdfFileFormatSpi;
import org.junit.Assert;
import org.junit.Test;

/**
 * 
 * @author hongwei.quhw
 * @version $Id: FormatLoaderTest.java, v 0.1 2017年4月7日 上午10:55:26 hongwei.quhw Exp $
 */
public class FormatLoaderTest {

    @Test
    public void testFormatLoader() {
        RdfFileFormatSpi format = FormatLoader.getColumnFormt("de", "String");
        Assert.assertEquals(RawFormat.class.getName(), format.getClass().getName());

        format = FormatLoader.getColumnFormt("fund", "String");
        Assert.assertEquals(CColumnFormat.class.getName(), format.getClass().getName());

        format = FormatLoader.getColumnFormt("fund", "string");
        Assert.assertEquals(CColumnFormat.class.getName(), format.getClass().getName());

        format = FormatLoader.getColumnFormt("fund", "DigitalChar");
        Assert.assertEquals(AColumnFormat.class.getName(), format.getClass().getName());

        format = FormatLoader.getColumnFormt("fund", "digitalChar");
        Assert.assertEquals(AColumnFormat.class.getName(), format.getClass().getName());

        format = FormatLoader.getColumnFormt("fund", "digitalchar");
        Assert.assertEquals(AColumnFormat.class.getName(), format.getClass().getName());

        format = FormatLoader.getColumnFormt("fund", "double");
        Assert.assertEquals(NColumnFormat.class.getName(), format.getClass().getName());

        format = FormatLoader.getColumnFormt("fund", "Double");
        Assert.assertEquals(NColumnFormat.class.getName(), format.getClass().getName());
    }
}
