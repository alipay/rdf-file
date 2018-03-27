package com.alipay.rdf.file.interfaces;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 * 
 * rdf-file-core 存储常量
 * 
 * @StorageConfig storageConfig = new StorageConfig(FileCoreStorageContants.STORAGE_NAS);
 * @author hongwei.quhw
 * @version $Id: CoreExtensionContants.java, v 0.1 2017年8月24日 下午2:03:16 hongwei.quhw Exp $
 */
public interface FileCoreStorageContants {
    //------------   存储类型 nas和local内部实现一样-----------
    /**nas 存储*/
    public static final String STORAGE_NAS   = "nas";
    /**本地磁盘存储*/
    public static final String STORAGE_LOCAL = "local";

}
