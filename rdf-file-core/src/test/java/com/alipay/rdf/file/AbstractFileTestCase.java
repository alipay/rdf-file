/*
 * Alipay.com Inc.
 * Copyright (c) 2004-2021 All Rights Reserved.
 */
package com.alipay.rdf.file;

import com.alipay.rdf.file.model.FileDefaultConfig;
import com.alipay.rdf.file.util.TemporaryFolderUtil;
import com.alipay.rdf.file.util.TestLog;
import org.junit.After;
import org.junit.Before;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * @author wanhaofan
 * @version TestUtil.java, v 0.1 2021年09月26日 2:08 PM wanhaofan
 */
public class AbstractFileTestCase {

    public TemporaryFolderUtil tf = new TemporaryFolderUtil();

    @Before
    public void setUp() throws IOException {
        tf.create();
        new FileDefaultConfig().setCommonLog(new TestLog());
    }

    public final int getLastByteOfFile(String filePath) throws IOException {
        FileInputStream fis = null;

        try {
            fis = new FileInputStream(filePath);
            int prevByte = -1;
            int currentByte = fis.read();
            while(currentByte != -1){
                prevByte = currentByte;
                currentByte = fis.read();
            }
            return prevByte;
        } finally {
            if(fis != null){
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public final String createLocalFile(String fileName){
        String filePath = tf.getRoot().getAbsolutePath();
        return new File(filePath, fileName).getAbsolutePath();
    }

    @After
    public void after() {
        tf.delete();
    }

}