package com.alipay.rdf.file.validator;

import com.alipay.rdf.file.interfaces.FileFactory;
import com.alipay.rdf.file.interfaces.FileValidator;
import com.alipay.rdf.file.model.FileConfig;
import com.alipay.rdf.file.model.FileDefaultConfig;
import com.alipay.rdf.file.model.StorageConfig;
import com.alipay.rdf.file.model.ValidateResult;
import com.alipay.rdf.file.util.TemporaryFolderUtil;
import com.alipay.rdf.file.util.TestLog;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

/**
 * 条件汇总 & 条件统计
 * 
 * @author hongwei.quhw
 * @version $Id: ProtocolFileValidatorTest.java, v 0.1 2017年8月17日 下午5:30:52 hongwei.quhw Exp $
 */
public class ProtocolFileValidatorTest2 {
    TemporaryFolderUtil tf = new TemporaryFolderUtil();

    @Before
    public void setUp() throws IOException {
        tf.create();
        new FileDefaultConfig().setCommonLog(new TestLog());
    }

    @Test
    public void test() {
        String filePath = File.class.getResource("/validator/data/de_all4.txt").getPath();
        FileConfig config = new FileConfig(filePath, "/validator/template/de3.json",
            new StorageConfig("nas"));

        FileValidator fileValidator = FileFactory.createValidator(config);

        ValidateResult result = fileValidator.validate();

        System.out.println(result.getErrorMsg());
        Assert.assertTrue(result.isSuccess());
    }

    @Test
    public void test2() {
        String filePath = File.class.getResource("/validator/data/de_all5.txt").getPath();
        FileConfig config = new FileConfig(filePath, "/validator/template/de3.json",
            new StorageConfig("nas"));

        FileValidator fileValidator = FileFactory.createValidator(config);

        ValidateResult result = fileValidator.validate();

        System.out.println(result.getErrorMsg());
        Assert.assertTrue(result.isSuccess());
    }

    @After
    public void after() {
        tf.delete();
    }
}
