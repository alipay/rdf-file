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
 * 多模板
 * 
 * 不带头尾文件校验
 * 
 * @author hongwei.quhw
 * @version $Id: ProtocolFileValidatorTest.java, v 0.1 2017年8月17日 下午5:30:52 hongwei.quhw Exp $
 */
public class ProtocolFileValidatorTest4 {
    TemporaryFolderUtil tf = new TemporaryFolderUtil();

    @Before
    public void setUp() throws IOException {
        tf.create();
        new FileDefaultConfig().setCommonLog(new TestLog());
    }

    @Test
    public void test() {
        String filePath = File.class.getResource("/validator/data/test2.txt").getPath();
        FileConfig config = new FileConfig(filePath, "/validator/template/de5.json",
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
