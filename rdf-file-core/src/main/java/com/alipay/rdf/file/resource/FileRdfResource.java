package com.alipay.rdf.file.resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import com.alipay.rdf.file.exception.RdfErrorEnum;
import com.alipay.rdf.file.exception.RdfFileException;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 * 
 * @author hongwei.quhw
 * @version $Id: FileRdfResource.java, v 0.1 2017年3月31日 下午9:39:27 hongwei.quhw Exp $
 */
public class FileRdfResource extends AbstractRdfResources {

    /** 
     * @see com.alipay.rdf.file.spi.RdfFileResourceSpi#getInputStream(java.lang.String)
     */
    @Override
    public RdfInputStream getInputStream(String path) {
        try {
            if (new File(path).exists()) {
                return new RdfInputStream(new FileInputStream(path));
            } else {
                return null;
            }
        } catch (FileNotFoundException e) {
            throw new RdfFileException(e, RdfErrorEnum.RESOURCE_ERROR);
        }
    }

}
