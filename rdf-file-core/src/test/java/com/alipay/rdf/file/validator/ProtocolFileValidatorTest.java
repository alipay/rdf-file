package com.alipay.rdf.file.validator;

import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.alipay.rdf.file.interfaces.FileFactory;
import com.alipay.rdf.file.interfaces.FileValidator;
import com.alipay.rdf.file.model.FileConfig;
import com.alipay.rdf.file.model.FileDefaultConfig;
import com.alipay.rdf.file.model.StorageConfig;
import com.alipay.rdf.file.model.ValidateResult;
import com.alipay.rdf.file.util.TemporaryFolderUtil;
import com.alipay.rdf.file.util.TestLog;

import junit.framework.Assert;

/**
 * 验证其test
 * 
 * @author hongwei.quhw
 * @version $Id: ProtocolFileValidatorTest.java, v 0.1 2017年8月17日 下午5:30:52 hongwei.quhw Exp $
 */
public class ProtocolFileValidatorTest {
    TemporaryFolderUtil tf = new TemporaryFolderUtil();

    @Before
    public void setUp() throws IOException {
        tf.create();
        new FileDefaultConfig().setCommonLog(new TestLog());
    }

    @Test
    public void test() {
        String filePath = File.class.getResource("/validator/data/de_all1.txt").getPath();
        FileConfig config = new FileConfig(filePath, "/validator/template/de1.json",
            new StorageConfig("nas"));

        FileValidator fileValidator = FileFactory.createValidator(config);

        ValidateResult result = fileValidator.validate();

        Assert.assertFalse(result.isSuccess());
        System.out.println(result.getErrorMsg());
    }

    /**
     * 加载行校验器验证
     */
    @Test
    public void test2() {
        String filePath = File.class.getResource("/validator/data/de_all1.txt").getPath();
        FileConfig config = new FileConfig(filePath, "/validator/template/de2.json",
            new StorageConfig("nas"));

        FileValidator fileValidator = FileFactory.createValidator(config);

        ValidateResult result = fileValidator.validate();

        Assert.assertFalse(result.isSuccess());
        Assert.assertEquals("校验错误啦seq=seq_1", result.getErrorMsg());
    }

    /**
     * 数据中必填字段验证
     */
    @Test
    public void test3() {
        String filePath = File.class.getResource("/validator/data/de_all2.txt").getPath();
        FileConfig config = new FileConfig(filePath, "/validator/template/de2.json",
            new StorageConfig("nas"));

        FileValidator fileValidator = FileFactory.createValidator(config);

        ValidateResult result = fileValidator.validate();

        Assert.assertFalse(result.isSuccess());
        Assert.assertTrue(result.getErrorMsg().indexOf("必填字段") > 0);
    }

    /**
     * 正确性
     */
    @Test
    public void test4() {
        String filePath = File.class.getResource("/validator/data/de_all3.txt").getPath();
        FileConfig config = new FileConfig(filePath, "/validator/template/de1.json",
            new StorageConfig("nas"));

        FileValidator fileValidator = FileFactory.createValidator(config);

        ValidateResult result = fileValidator.validate();

        Assert.assertTrue(result.isSuccess());
    }

    @After
    public void after() {
        tf.delete();
    }
}
