package com.alipay.rdf.file.resource;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import com.alipay.rdf.file.exception.RdfErrorEnum;
import com.alipay.rdf.file.exception.RdfFileException;
import com.alipay.rdf.file.util.RdfFileUtil;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 * 
 * classpath 或者 classpath jar包中资源
 * 
 * 路径是文件夹， 加载文件夹中文件资源
 * 
 * @author hongwei.quhw
 * @version $Id: ClassLoaderJarsRdfResouces.java, v 0.1 2017年8月7日 上午10:43:07 hongwei.quhw Exp $
 */
public class ClasspathDirRdfResouces extends AbstractRdfResources {

    @Override
    public RdfInputStream getInputStream(String path) {
        try {
            Enumeration<URL> urls = RdfFileUtil.getDefaultClassLoader().getResources(path);

            if (null == urls || !urls.hasMoreElements()) {
                return null;
            }

            List<InputStream> streams = new ArrayList<InputStream>();
            while (urls.hasMoreElements()) {
                streams.add(urls.nextElement().openStream());
            }

            return new RdfInputStream(streams);
        } catch (IOException e) {
            throw new RdfFileException(
                "rdf-file#ClasspathDirRdfResouces.getInputStream(path=" + path + ") 出错", e,
                RdfErrorEnum.IO_ERROR);
        }
    }
}
