package com.alipay.rdf.file.loader;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

import com.alipay.rdf.file.init.RdfInit;
import com.alipay.rdf.file.interfaces.FileSplitter;
import com.alipay.rdf.file.interfaces.FileStorage;
import com.alipay.rdf.file.model.StorageConfig;
import com.alipay.rdf.file.spi.RdfFileSplitterSpi;
import com.alipay.rdf.file.util.RdfFileUtil;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 *
 * @author hongwei.quhw
 * @version $Id: FileSplitterLoader.java, v 0.1 2018年3月12日 下午4:20:17 hongwei.quhw Exp $
 */
public class FileSplitterLoader {
    private static final Map<StorageConfig, FileSplitter> STORAGE_CACHE = Collections
        .synchronizedMap(new WeakHashMap<StorageConfig, FileSplitter>());

    private static final Object                           LOCK          = new Object();

    @SuppressWarnings("unchecked")
    public static FileSplitter getFileSplitter(StorageConfig storageConfig) {
        FileSplitter fileSplitter = STORAGE_CACHE.get(storageConfig);

        if (null == fileSplitter) {
            synchronized (LOCK) {
                fileSplitter = STORAGE_CACHE.get(storageConfig);

                if (null == fileSplitter) {
                    fileSplitter = ExtensionLoader.getExtensionLoader(RdfFileSplitterSpi.class)
                        .getNewExtension(storageConfig.getStorageType());

                    RdfFileUtil.assertNotNull(
                        fileSplitter,
                        "rdf-file#FileSplitterLoader.getFileSplitter(storageConfig="
                                      + storageConfig.getStorageType() + ") 文件分割器 type="
                                      + storageConfig.getStorageType() + ", 没有对应的实现");

                    //文件分割器初始化
                    if (fileSplitter instanceof RdfInit) {
                        FileStorage fileStorage = FileStorageLoader.getFileStorage(storageConfig);
                        ((RdfInit<FileStorage>) fileSplitter).init(fileStorage);
                    }

                    STORAGE_CACHE.put(storageConfig, fileSplitter);
                }
            }
        }

        return fileSplitter;
    }
}
