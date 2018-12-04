/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.alipay.rdf.file.resource;

import com.alipay.rdf.file.exception.RdfErrorEnum;
import com.alipay.rdf.file.exception.RdfFileException;
import com.alipay.rdf.file.interfaces.FileFactory;
import com.alipay.rdf.file.model.StorageConfig;
import com.alipay.rdf.file.storage.FileInnterStorage;
import com.alipay.rdf.file.util.RdfFileUtil;

/**
 *
 * @author haofan.whf
 * @version $Id: SftpFileResource.java, v 0.1 2018年10月19日 下午4:57 haofan.whf Exp $
 */
public class SftpFileResource extends AbstractRdfResources{

    private FileInnterStorage fileStorage;

    @Override
    public void init(StorageConfig t) {
        super.init(t);
        RdfFileUtil.assertNotNull(storageConfig,
                "rdf-file#SftpFileResource.resourceType=" + resourceType + ", 没有在默认配置中配置sftp参数",
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