package com.alipay.rdf.file.resource;

import org.junit.Test;

import com.alipay.rdf.file.loader.ExtensionLoader;
import com.alipay.rdf.file.spi.RdfFileResourceSpi;

public class RdfResourceTest {

    @Test
    public void testResource() {
        RdfFileResourceSpi fileResource = ExtensionLoader.getExtensionLoader(RdfFileResourceSpi.class)
            .getExtension("file");

        System.out.println(fileResource.getClass().getName());

        fileResource = ExtensionLoader.getExtensionLoader(RdfFileResourceSpi.class)
            .getExtension("classpath");

        System.out.println(fileResource.getClass().getName());

    }
}
