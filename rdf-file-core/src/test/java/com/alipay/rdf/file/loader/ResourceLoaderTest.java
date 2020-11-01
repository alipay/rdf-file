/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2020 All Rights Reserved.
 */
package com.alipay.rdf.file.loader;

import com.alipay.rdf.file.model.FileDefaultConfig;
import com.alipay.rdf.file.model.StorageConfig;
import com.alipay.rdf.file.resource.TestResource;
import com.alipay.rdf.file.spi.RdfFileResourceSpi;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.Map;

import static com.alipay.rdf.file.loader.ResourceLoader.getInputStream;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 *
 * @author quhongwei
 * @version : ResourceLoaderTest.java, v 0.1 2020年11月01日 09:19 quhongwei Exp $
 */
public class ResourceLoaderTest {

    @Test
    public void testGetInputStreamResourceKey() throws  Exception{

        FileDefaultConfig.DEFAULT_FILE_PARAMS.remove("key1");
        FileDefaultConfig.DEFAULT_FILE_PARAMS.remove("key1");
        FileDefaultConfig.DEFAULT_FILE_PARAMS.remove("testResource");

        Field field = ResourceLoader.class.getDeclaredField("RESOURCE_CACHE");
        field.setAccessible(true);

        Map<String, RdfFileResourceSpi> resourceMap = ( Map<String, RdfFileResourceSpi>)field.get(null);

        getInputStream("aa/bb/cc/dd.json");
        Assert.assertNotNull(resourceMap.get("classpath"));

        getInputStream("classpath:aa/bb/cc/dd.json");
        Assert.assertNotNull(resourceMap.get("classpath"));

        getInputStream("classpath:aa/bb/cc/dd.json?resourceKey=hzconfig");
        Assert.assertNotNull(resourceMap.get("classpath"));
        Assert.assertNotNull(resourceMap.get("classpathhzconfig"));

        getInputStream("classpath:aa/bb/cc/dd.json?resourceKey=shconfig");
        Assert.assertNotNull(resourceMap.get("classpath"));
        Assert.assertNotNull(resourceMap.get("classpathshconfig"));

        getInputStream("classpath:aa/bb/cc/dd.json?resourceKey=shconfig&xx=dd");
        Assert.assertNotNull(resourceMap.get("classpath"));
        Assert.assertNotNull(resourceMap.get("classpathhzconfig"));
        Assert.assertNotNull(resourceMap.get("classpathshconfig"));


        TestResource.TestInputStream testInputStream = (TestResource.TestInputStream)ResourceLoader.getInputStream("testResource:aa/bb/cc/dd.json");

        Assert.assertNotNull(resourceMap.get("classpath"));
        Assert.assertNotNull(resourceMap.get("classpathhzconfig"));
        Assert.assertNotNull(resourceMap.get("classpathshconfig"));
        Assert.assertNotNull(resourceMap.get("testResource"));

        Assert.assertEquals("testResource", testInputStream.getResourceType());
        Assert.assertEquals("aa/bb/cc/dd.json", testInputStream.getPath());
        Assert.assertNull( testInputStream.getConfig());

        FileDefaultConfig.DEFAULT_FILE_PARAMS.put("testResource", new StorageConfig("nas") {
            {
                addParam("type", "testResource");
            }
        });

        FileDefaultConfig.DEFAULT_FILE_PARAMS.put("key1", new StorageConfig("nas") {
            {
                addParam("type", "key1");
            }
        });

        FileDefaultConfig.DEFAULT_FILE_PARAMS.put("key2", new StorageConfig("nas") {
            {
                addParam("type", "key2");
            }
        });

        testInputStream = (TestResource.TestInputStream)ResourceLoader.getInputStream("testResource:aa/bb/cc/dd.json?resourceKey=key1");
        Assert.assertNotNull(resourceMap.get("classpath"));
        Assert.assertNotNull(resourceMap.get("classpathhzconfig"));
        Assert.assertNotNull(resourceMap.get("classpathshconfig"));
        Assert.assertNotNull(resourceMap.get("testResource"));
        Assert.assertNotNull(resourceMap.get("testResourcekey1"));

        RdfFileResourceSpi pre = resourceMap.get("testResourcekey1");

        // 重复看看缓存
        testInputStream = (TestResource.TestInputStream)ResourceLoader.getInputStream("testResource:aa/bb/cc/dd.json?resourceKey=key1");
        Assert.assertNotNull(resourceMap.get("classpath"));
        Assert.assertNotNull(resourceMap.get("classpathhzconfig"));
        Assert.assertNotNull(resourceMap.get("classpathshconfig"));
        Assert.assertNotNull(resourceMap.get("testResource"));
        Assert.assertNotNull(resourceMap.get("testResourcekey1"));
        Assert.assertEquals("key1", testInputStream.getConfig().getParam("type"));
        Assert.assertEquals(pre, resourceMap.get("testResourcekey1"));

        testInputStream = (TestResource.TestInputStream)ResourceLoader.getInputStream("testResource:aa/bb/cc/dd.json?resourceKey=key2");
        Assert.assertNotNull(resourceMap.get("classpath"));
        Assert.assertNotNull(resourceMap.get("classpathhzconfig"));
        Assert.assertNotNull(resourceMap.get("classpathshconfig"));
        Assert.assertNotNull(resourceMap.get("testResource"));
        Assert.assertNotNull(resourceMap.get("testResourcekey1"));
        Assert.assertNotNull(resourceMap.get("testResourcekey2"));
        Assert.assertEquals("key2", testInputStream.getConfig().getParam("type"));

        testInputStream = (TestResource.TestInputStream)ResourceLoader.getInputStream("testResource:aa/bb/cc/dd.json?resourceKey=key3");
        Assert.assertNotNull(resourceMap.get("classpath"));
        Assert.assertNotNull(resourceMap.get("classpathhzconfig"));
        Assert.assertNotNull(resourceMap.get("classpathshconfig"));
        Assert.assertNotNull(resourceMap.get("testResource"));
        Assert.assertNotNull(resourceMap.get("testResourcekey1"));
        Assert.assertNotNull(resourceMap.get("testResourcekey2"));
        Assert.assertNotNull(resourceMap.get("testResourcekey3"));
        Assert.assertEquals("testResource", testInputStream.getConfig().getParam("type"));

        FileDefaultConfig.DEFAULT_FILE_PARAMS.remove("key1");
        FileDefaultConfig.DEFAULT_FILE_PARAMS.remove("key2");
        FileDefaultConfig.DEFAULT_FILE_PARAMS.remove("testResource");

    }
}