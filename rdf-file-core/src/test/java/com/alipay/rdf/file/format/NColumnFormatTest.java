package com.alipay.rdf.file.format;

import com.alipay.rdf.file.exception.RdfFileException;
import com.alipay.rdf.file.meta.FileColumnMeta;
import com.alipay.rdf.file.meta.FileColumnRangeMeta;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author: hongwei.quhwØ
 * @date: 2020-07-10 16:08
 */
public class NColumnFormatTest {
    private  NColumnFormat nColumnFormat = new NColumnFormat();

    @Test
    public void test1() {

        FileColumnRangeMeta range = new FileColumnRangeMeta(4, 2);

        FileColumnMeta colMeta = new FileColumnMeta(0, "test", "test", null, true, range, null, null, null);

        try {
            nColumnFormat.serialize("-12.22", colMeta, null);
        } catch (RdfFileException e) {
            Assert.assertEquals("数值1222补位后-1222长度5模板定义长度4", e.getMessage());
        }

        String value = nColumnFormat.serialize("-0.22", colMeta, null);
        Assert.assertEquals("-022", value);
        String ret = nColumnFormat.deserialize(value, colMeta, null);
        Assert.assertEquals("-0.22", ret);


        range = new FileColumnRangeMeta(4, 0);

        colMeta = new FileColumnMeta(0, "test", "test", null, true, range, null, null, null);

        try {
            nColumnFormat.serialize("-1222", colMeta, null);
        } catch (RdfFileException e) {
            Assert.assertEquals("数值1222补位后-1222长度5模板定义长度4", e.getMessage());
        }

         value = nColumnFormat.serialize("-2", colMeta, null);
        Assert.assertEquals("-002", value);
         ret = nColumnFormat.deserialize(value, colMeta, null);
        Assert.assertEquals("-2", ret);

    }
}
