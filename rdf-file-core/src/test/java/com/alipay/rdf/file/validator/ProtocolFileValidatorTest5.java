package com.alipay.rdf.file.validator;

import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.alipay.rdf.file.interfaces.FileFactory;
import com.alipay.rdf.file.interfaces.FileValidator;
import com.alipay.rdf.file.model.FileConfig;
import com.alipay.rdf.file.model.StorageConfig;
import com.alipay.rdf.file.model.ValidateResult;
import com.alipay.rdf.file.util.TemporaryFolderUtil;

import junit.framework.Assert;

/**
 * 验证其test
 * 
 * @author hongwei.quhw
 * @version $Id: ProtocolFileValidatorTest.java, v 0.1 2017年8月17日 下午5:30:52 hongwei.quhw Exp $
 */
public class ProtocolFileValidatorTest5 {
    TemporaryFolderUtil tf = new TemporaryFolderUtil();

    @Before
    public void setUp() throws IOException {
        tf.create();
    }

    @Test
    public void test() {
        String filePath = File.class.getResource("/validator/data/de_all6.txt").getPath();
        FileConfig config = new FileConfig(filePath, "/validator/template/de6.json",
            new StorageConfig("nas"));

        FileValidator fileValidator = FileFactory.createValidator(config);

        ValidateResult result = fileValidator.validate();

        Assert.assertTrue(result.isSuccess());
        System.out.println(result.getErrorMsg());
    }

    @After
    public void after() {
        tf.delete();
    }
}
