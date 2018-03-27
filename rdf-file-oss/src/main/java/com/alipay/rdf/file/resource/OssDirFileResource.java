package com.alipay.rdf.file.resource;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.alipay.rdf.file.exception.RdfErrorEnum;
import com.alipay.rdf.file.interfaces.FileFactory;
import com.alipay.rdf.file.interfaces.FileStorage;
import com.alipay.rdf.file.model.StorageConfig;
import com.alipay.rdf.file.storage.FileInnterStorage;
import com.alipay.rdf.file.util.RdfFileLogUtil;
import com.alipay.rdf.file.util.RdfFileUtil;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 * 
 * 加载存储在oss上的文件夹下的文件
 * 
 * @author hongwei.quhw
 * @version $Id: OssFileResource.java, v 0.1 2017年8月22日 下午2:04:47 hongwei.quhw Exp $
 */
public class OssDirFileResource extends AbstractRdfResources {
    private FileStorage fileStorage;

    @Override
    public void init(StorageConfig t) {
        super.init(t);
        RdfFileUtil.assertNotNull(storageConfig,
            "rdf-file#OssFileResource.resourceType=" + resourceType + ", 没有在默认配置中配置oss参数",
            RdfErrorEnum.ILLEGAL_ARGUMENT);
        this.fileStorage = FileFactory.createStorage(storageConfig);
    }

    @Override
    public RdfInputStream getInputStream(String path) {

        List<String> paths = fileStorage.listAllFiles(path);
        if (null == paths || paths.isEmpty()) {
            if (RdfFileLogUtil.common.isWarn()) {
                RdfFileLogUtil.common
                    .warn("rdf-file#OssDirFileResource.getInputStream(path=" + path + ")没有加载到资源");
            }
            return null;
        } else {
            if (RdfFileLogUtil.common.isInfo()) {
                RdfFileLogUtil.common.info("rdf-file#OssDirFileResource.getInputStream(path=" + path
                                           + "),  加载的resources=" + paths);
            }
        }

        List<InputStream> streams = new ArrayList<InputStream>(paths.size());
        FileInnterStorage innterStorage = (FileInnterStorage) fileStorage;
        for (String filePath : paths) {
            streams.add(innterStorage.getInputStream(filePath));
        }

        return new RdfInputStream(streams);
    }

}
