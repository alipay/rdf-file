package com.alipay.rdf.file.resource;

import java.io.InputStream;

import com.alipay.rdf.file.util.RdfFileUtil;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 *
 * @author hongwei.quhw
 * @version $Id: ClasspathRdfResource.java, v 0.1 2018年3月12日 下午4:24:04 hongwei.quhw Exp $
 */
public class ClasspathRdfResource extends AbstractRdfResources {

    @Override
    public RdfInputStream getInputStream(String path) {
        InputStream is = RdfFileUtil.getDefaultClassLoader().getResourceAsStream(path);

        if (null == is) {
            is = RdfFileUtil.class.getResourceAsStream(path);
        }

        if (null == is) {
            return null;
        }

        return new RdfInputStream(is);
    }

}