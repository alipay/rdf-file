package com.alipay.rdf.file.sftp; /**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.alipay.rdf.file.enums.SftpProgressLogPrintTypeEnum;
import com.alipay.rdf.file.interfaces.FileSftpStorageConstants;
import com.alipay.rdf.file.loader.ResourceLoader;
import com.alipay.rdf.file.model.StorageConfig;
import com.alipay.rdf.file.storage.SftpConfig;
import com.alipay.rdf.file.util.RdfFileUtil;
import com.alipay.rdf.file.util.SFTPHelper;

/**
 *
 * @author haofan.whf
 * @version $Id: com.alipay.rdf.file.sftp.SftpTestUtil.java, v 0.1 2018年10月09日 下午2:10 haofan.whf Exp $
 */
public class SftpTestUtil {

    private static final Properties pp;

    static{
        InputStream is = ResourceLoader.getInputStream("classpath:sftpconfig.properties");
        pp = new Properties();
        try {
            pp.load(is);
        } catch (IOException e) {
            throw new RuntimeException("获取sftpconfig.properties文件失败", e);
        }
    }

    public static String getHomeDir(){
        String homedir = pp.getProperty("homedir");
        if(RdfFileUtil.isBlank(homedir)){
            throw new RuntimeException("获取homedir失败");
        }
        return homedir;
    }

    public static String combineHomeDir(String relativePath){
        return SFTPHelper.toSFTPPath(RdfFileUtil.combinePath(getHomeDir(), relativePath));
    }


    public static StorageConfig getStorageConfig() {

        String host = pp.getProperty("host");
        String password = pp.getProperty("password");
        String userName = pp.getProperty("username");
        String portStr = pp.getProperty("port");
        Integer port = 22;
        if(RdfFileUtil.isNotBlank(portStr)){
            port = Integer.valueOf(portStr);
        }

        if(RdfFileUtil.isBlank(host)
                || RdfFileUtil.isBlank(password)
                || RdfFileUtil.isBlank(userName)){
            throw new RuntimeException("sftpconfig.properties中host,password,username必填");
        }

        StorageConfig storageConfig = new StorageConfig(FileSftpStorageConstants.SFTP);
        SftpConfig sftpConfig = new SftpConfig();
        sftpConfig.setHost(host);
        sftpConfig.setPassword(password);
        sftpConfig.setUserName(userName);
        sftpConfig.setPort(port);

        sftpConfig.setProgressLogPrintTypeEnum(SftpProgressLogPrintTypeEnum.FIXED_PERIOD);
        sftpConfig.setProgressPrintLogPeriod(1);
        sftpConfig.addExtraSessionConfig("kex", "diffie-hellman-group1-sha1,diffie-hellman-group14-sha1,diffie-hellman-group-exchange-sha1,diffie-hellman-group-exchange-sha256");
        storageConfig.addParam(SftpConfig.SFTP_STORAGE_CONFIG_KEY, sftpConfig);
        return storageConfig;
    }

}