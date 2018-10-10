package com.alipay.rdf.file.sftp; /**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */

import com.alipay.rdf.file.interfaces.FileSftpStorageConstants;
import com.alipay.rdf.file.model.StorageConfig;
import com.alipay.rdf.file.storage.SftpConfig;

/**
 *
 * @author haofan.whf
 * @version $Id: com.alipay.rdf.file.sftp.SftpTestUtil.java, v 0.1 2018年10月09日 下午2:10 haofan.whf Exp $
 */
public class SftpTestUtil {


    public static StorageConfig getStorageConfig(){
        StorageConfig storageConfig = new StorageConfig(FileSftpStorageConstants.SFTP);
        SftpConfig sftpConfig = new SftpConfig();
        sftpConfig.setHost("localhost");
        sftpConfig.setPassword("123456");
        sftpConfig.setUserName("sftpuser");
        sftpConfig.setPort(22);

        sftpConfig.addExtraSessionConfig("kex", "diffie-hellman-group1-sha1");
        storageConfig.addParam(SftpConfig.SFTP_STORAGE_CONFIG_KEY, sftpConfig);
        return storageConfig;
    }

}