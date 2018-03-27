/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2017 All Rights Reserved.
 */
package com.alipay.rdf.file.storage;

import java.io.File;
import java.util.List;

import org.junit.Test;

import com.alipay.rdf.file.storage.FileNasStorage;

import junit.framework.Assert;

/**
 * nas 存储
 * 
 * @author hongwei.quhw
 * @version $Id: FileNasStorageTest.java, v 0.1 2017年8月22日 下午5:29:20 hongwei.quhw Exp $
 */
public class FileNasStorageTest {

    private static FileNasStorage storage = new FileNasStorage();

    @Test
    public void testListFilesWithRegexs() {
        List<String> paths = storage.listFiles(File.class.getResource("/storage").getPath(),
            new String[] { "folder1*" });
        Assert.assertEquals(1, paths.size());
    }

    @Test
    public void testListAllFilesWithRegexs() {
        List<String> paths = storage.listAllFiles(File.class.getResource("/storage").getPath(),
            new String[] { "11.txt" });
        Assert.assertEquals(1, paths.size());
    }

}
