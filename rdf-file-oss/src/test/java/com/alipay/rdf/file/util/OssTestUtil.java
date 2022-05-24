package com.alipay.rdf.file.util;

import com.alipay.rdf.file.exception.RdfErrorEnum;
import com.alipay.rdf.file.exception.RdfFileException;
import com.alipay.rdf.file.interfaces.FileOssStorageContants;
import com.alipay.rdf.file.loader.ResourceLoader;
import com.alipay.rdf.file.model.StorageConfig;
import com.alipay.rdf.file.storage.OssConfig;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class OssTestUtil {

    public static StorageConfig geStorageConfig() {
        try {
            InputStream is = ResourceLoader.getInputStream("classpath:ossconfig.properties");
            Properties pp = new Properties();
            pp.load(is);

            String bucketName = pp.getProperty("bucketName");
            String endpoint = pp.getProperty("endpoint");
            String accessKeyId = pp.getProperty("accessKeyId");
            String accessKeySecret = pp.getProperty("accessKeySecret");

            if (RdfFileUtil.isBlank(bucketName) || RdfFileUtil.isBlank(endpoint)
                || RdfFileUtil.isBlank(accessKeyId) || RdfFileUtil.isBlank(accessKeySecret)) {
                throw new RdfFileException("rdf-dal#请在测试资源下的ossconfig.properties文件中填入oss账户信息",
                    RdfErrorEnum.ILLEGAL_ARGUMENT);
            }

            OssConfig ossConfig = new OssConfig(bucketName, endpoint, accessKeyId, accessKeySecret);

            StorageConfig storageConfig = new StorageConfig(FileOssStorageContants.STORAGE_OSS);
            storageConfig.addParam(OssConfig.OSS_STORAGE_CONFIG_KEY, ossConfig);

            return storageConfig;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static StorageConfig geStorageConfigByURL() {
        try {
            InputStream is = ResourceLoader.getInputStream("classpath:ossconfig.properties");
            Properties pp = new Properties();
            pp.load(is);

            String bucketName = pp.getProperty("bucketName");
            String connectionURL = pp.getProperty("connectionURL");

            if (RdfFileUtil.isBlank(bucketName) || RdfFileUtil.isBlank(connectionURL)) {
                throw new RdfFileException("rdf-dal#请在测试资源下的ossconfig.properties文件中填入oss账户信息",
                        RdfErrorEnum.ILLEGAL_ARGUMENT);
            }

            OssConfig ossConfig = new OssConfig(bucketName, connectionURL, "url");

            StorageConfig storageConfig = new StorageConfig(FileOssStorageContants.STORAGE_OSS);
            storageConfig.addParam(OssConfig.OSS_STORAGE_CONFIG_KEY, ossConfig);

            return storageConfig;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
