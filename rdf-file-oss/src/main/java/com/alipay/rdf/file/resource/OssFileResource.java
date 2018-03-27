package com.alipay.rdf.file.resource;

import com.alipay.rdf.file.exception.RdfErrorEnum;
import com.alipay.rdf.file.exception.RdfFileException;
import com.alipay.rdf.file.interfaces.FileFactory;
import com.alipay.rdf.file.model.StorageConfig;
import com.alipay.rdf.file.storage.FileInnterStorage;
import com.alipay.rdf.file.util.RdfFileUtil;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 * 
 * 加载存储在oss上的文件
 * 
 * @author hongwei.quhw
 * @version $Id: OssFileResource.java, v 0.1 2017年8月22日 下午2:04:47 hongwei.quhw Exp $
 */
public class OssFileResource extends AbstractRdfResources {
    private FileInnterStorage fileStorage;

    @Override
    public void init(StorageConfig t) {
        super.init(t);
        RdfFileUtil.assertNotNull(storageConfig,
            "rdf-file#OssFileResource.resourceType=" + resourceType + ", 没有在默认配置中配置oss参数",
            RdfErrorEnum.ILLEGAL_ARGUMENT);
        this.fileStorage = (FileInnterStorage) FileFactory.createStorage(storageConfig);
    }

    @Override
    public RdfInputStream getInputStream(String path) {
        try {
            return new RdfInputStream(fileStorage.getInputStream(path));
        } catch (RdfFileException e) {
            if (RdfErrorEnum.NOT_EXSIT.equals(e.getErrorEnum())) {
                return null;
            }

            throw e;
        }
    }

}
