package com.alipay.rdf.file.loader;

import java.util.Map;
import java.util.WeakHashMap;

import com.alipay.rdf.file.init.RdfInit;
import com.alipay.rdf.file.interfaces.FileStorage;
import com.alipay.rdf.file.model.StorageConfig;
import com.alipay.rdf.file.spi.RdfFileStorageSpi;
import com.alipay.rdf.file.util.RdfFileUtil;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 * 
 * @author hongwei.quhw
 * @version $Id: FileStorageLoader.java, v 0.1 2017年4月7日 下午4:16:50 hongwei.quhw Exp $
 */
public class FileStorageLoader {
    private static final Map<StorageConfig, FileStorage> STORAGE_CACHE = new WeakHashMap<StorageConfig, FileStorage>();

    private static final Object                          LOCK          = new Object();

    @SuppressWarnings("unchecked")
    public static FileStorage getFileStorage(StorageConfig storageConfig) {
        FileStorage fileStorage = STORAGE_CACHE.get(storageConfig);

        if (null == fileStorage) {
            synchronized (LOCK) {
                fileStorage = STORAGE_CACHE.get(storageConfig);

                if (null == fileStorage) {
                    fileStorage = ExtensionLoader.getExtensionLoader(RdfFileStorageSpi.class)
                        .getNewExtension(storageConfig.getStorageType());

                    RdfFileUtil.assertNotNull(fileStorage,
                        "文件存储 type=" + storageConfig.getStorageType() + ", 没有对应的实现");

                    //存储初始化
                    if (fileStorage instanceof RdfInit) {
                        ((RdfInit<StorageConfig>) fileStorage).init(storageConfig);
                    }

                    STORAGE_CACHE.put(storageConfig, fileStorage);
                }
            }
        }

        return fileStorage;
    }
}
