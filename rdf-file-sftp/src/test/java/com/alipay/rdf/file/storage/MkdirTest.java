/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.alipay.rdf.file.storage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.alipay.rdf.file.interfaces.FileFactory;
import com.alipay.rdf.file.interfaces.FileStorage;
import com.alipay.rdf.file.model.FileInfo;
import com.alipay.rdf.file.model.StorageConfig;
import com.alipay.rdf.file.sftp.SftpTestUtil;
import com.alipay.rdf.file.sftp.TemporaryFolderUtil;
import com.alipay.rdf.file.util.RdfFileUtil;

/**
 *
 * @author haofan.whf
 * @version $Id: MkdirTest.java, v 0.1 2018年11月12日 下午10:40 haofan.whf Exp $
 */
public class MkdirTest {

    TemporaryFolderUtil temporaryFolderUtil = new TemporaryFolderUtil();

    StorageConfig storageConfig;

    FileStorage fileStorage;

    String ROOT_PATH = "/files/testcase";

    String localTmpDir;
    String localTmpFileName;

    String remoteUploadDir = "uploaddir";
    String remoteUploadFileName = "upload_test.txt";
    String remoteUploadDst = RdfFileUtil.combinePath(remoteUploadDir, remoteUploadFileName);

    //@Before
    //public void setup(){
    //    storageConfig = SftpTestUtil.getStorageConfig();
    //    fileStorage = FileFactory.createStorage(storageConfig);
    //    try {
    //        temporaryFolderUtil.create();
    //    } catch (IOException e) {
    //        throw new RuntimeException("获取临时目录异常", e);
    //    }
    //    localTmpDir = temporaryFolderUtil.getRoot().getName();
    //    localTmpFileName = RdfFileUtil.combinePath(localTmpDir, "sftp/localFile_1.txt");
    //}
    //
    //@Test
    //public void testA() throws Exception{
    //    System.out.println("upload begin.");
    //    File localTempFile = new File("/Users/iminright-ali/aa.txt");
    //    fileStorage.upload("/Users/iminright-ali/aa.txt", buildPath(remoteUploadDst), false);
    //    System.out.println("upload ok.");
    //}
    //
    //
    //private String buildPath(String relativePath){
    //    return RdfFileUtil.combinePath(ROOT_PATH, relativePath);
    //}
}