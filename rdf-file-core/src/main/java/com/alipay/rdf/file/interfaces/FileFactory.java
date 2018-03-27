package com.alipay.rdf.file.interfaces;

import com.alipay.rdf.file.loader.FileSplitterLoader;
import com.alipay.rdf.file.loader.FileStorageLoader;
import com.alipay.rdf.file.loader.FileToolLoader;
import com.alipay.rdf.file.model.FileConfig;
import com.alipay.rdf.file.model.StorageConfig;
import com.alipay.rdf.file.spi.RdfFileMergerSpi;
import com.alipay.rdf.file.spi.RdfFileReaderSpi;
import com.alipay.rdf.file.spi.RdfFileSorterSpi;
import com.alipay.rdf.file.spi.RdfFileValidatorSpi;
import com.alipay.rdf.file.spi.RdfFileWriterSpi;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 *
 *                          _ooOoo_
 *                         o8888888o
 *                         88" . "88
 *                         (| -_- |)
 *                         O\  =  /O
 *                      ____/`---'\____
 *                    .'  \\|     |//  `.
 *                   /  \\|||  :  |||//  \
 *                  /  _||||| -:- |||||-  \
 *                  |   | \\\  -  /// |   |
 *                  | \_|  ''\---/''  |—/ |
 *                  \  .-\__  `-`  ___/-. /
 *                ___`. .'  /--.--\  `. . __
 *             ."" '<  `.___\_<|>_/___.'  >'"".
 *            | | :  `- \`.;`\ _ /`;.`/ - ` : | |
 *            \  \ `-.   \_ __\ /__ _/   .-` /  /
 *       ======`-.____`-.___\_____/___.-`____.-'======
 *                          `=---='
 *       ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
 *                    佛祖保佑       永无BUG
 *                
 * @author hongwei.quhw
 * @version $Id: FileFactory.java, v 0.1 2017年4月7日 下午2:55:37 hongwei.quhw Exp $
 */
public class FileFactory {

    /**
     * 创建文件读
     * 
     * @param fileConfig
     * @return
     */
    public static FileReader createReader(FileConfig fileConfig) {
        return FileToolLoader.loader(fileConfig, RdfFileReaderSpi.class);
    }

    /**
     * 创建文件写
     * 
     * @param fileConfig
     * @return
     */
    public static FileWriter createWriter(FileConfig fileConfig) {
        return FileToolLoader.loader(fileConfig, RdfFileWriterSpi.class);
    }

    /**
     * 创建文件合并工具
     * 
     * @return
     */
    public static FileMerger createMerger(FileConfig fileConfig) {
        return FileToolLoader.loader(fileConfig, RdfFileMergerSpi.class);
    }

    /**
     * 创建文件校验工具
     * 
     * @param fileConfig
     * @return
     */
    public static FileValidator createValidator(FileConfig fileConfig) {
        return FileToolLoader.loader(fileConfig, RdfFileValidatorSpi.class);
    }

    /**
     * 创建文件排序工具
     * 
     * @param fileConfig
     * @return
     */
    public static FileSorter createSorter(FileConfig fileConfig) {
        return FileToolLoader.loader(fileConfig, RdfFileSorterSpi.class);
    }

    /**
     * 创建文件存储
     * 
     * @param storageConfig
     * @return
     */
    public static FileStorage createStorage(StorageConfig storageConfig) {
        return FileStorageLoader.getFileStorage(storageConfig);
    }

    /**
     * 创建文件分片器
     * 
     * @param storageConfig
     * @return
     */
    public static FileSplitter createSplitter(StorageConfig storageConfig) {
        return FileSplitterLoader.getFileSplitter(storageConfig);
    }
}
