package com.alipay.rdf.file.loader;

import java.io.IOException;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import com.alipay.rdf.file.exception.RdfErrorEnum;
import com.alipay.rdf.file.exception.RdfFileException;
import com.alipay.rdf.file.model.FileDefaultConfig;
import com.alipay.rdf.file.resource.RdfInputStream;
import com.alipay.rdf.file.spi.RdfFileColumnTypeSpi;
import com.alipay.rdf.file.spi.RdfFileFormatSpi;
import com.alipay.rdf.file.util.RdfFileLogUtil;
import com.alipay.rdf.file.util.RdfFileUtil;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 * 
 * @author hongwei.quhw
 * @version $Id: FormatLoader.java, v 0.1 2017年4月6日 上午11:38:17 hongwei.quhw Exp $
 */
public class FormatLoader {

    private static final String                        RDF_FORMAT_SUBFIX      = ".properties";

    private static final Object                        LOCK                   = new Object();

    private static final Map<String, TypeFormatHolder> TYPEFORMATHOLDER_CACHE = new ConcurrentHashMap<String, TypeFormatHolder>();

    public static RdfFileFormatSpi getColumnFormt(String protocolName, String columnType) {

        TypeFormatHolder typeFormatHolder = TYPEFORMATHOLDER_CACHE.get(protocolName);
        if (null == typeFormatHolder) {
            synchronized (LOCK) {
                typeFormatHolder = TYPEFORMATHOLDER_CACHE.get(protocolName);
                if (null == typeFormatHolder) {
                    typeFormatHolder = loadMapping(protocolName);
                }
            }
        }

        return typeFormatHolder.getColumnFormat(protocolName, columnType);
    }

    private static TypeFormatHolder loadMapping(String protocolName) {
        String fileName = FileDefaultConfig.RDF_FORMAT_PATH + protocolName.toLowerCase()
                          + RDF_FORMAT_SUBFIX;

        try {
            RdfInputStream is = ResourceLoader.getInputStream(fileName);
            if (null == is || !is.hasNext()) {
                fileName = FileDefaultConfig.RDF_FORMAT_PATH + protocolName.toUpperCase()
                           + RDF_FORMAT_SUBFIX;
                is = ResourceLoader.getInputStream(fileName);
            }

            TypeFormatHolder typeFormatHolder = null;

            if (null == is || !is.hasNext()) {
                typeFormatHolder = new RawTypeFormatHoler();
                if (RdfFileLogUtil.common.isInfo()) {
                    RdfFileLogUtil.common.info("rdf-file#FormatLoader.loadMapping(protocol="
                                               + protocolName + " 没有绑定格式化配置)");
                }

            } else {
                Properties all = new Properties();
                while (is.next()) {
                    Properties properties = new Properties();
                    properties.load(is);
                    for (Object key : properties.keySet()) {
                        if (all.contains(key)) {
                            if (RdfFileLogUtil.common.isWarn()) {
                                RdfFileLogUtil.common
                                    .warn("rdf-file#FormatLoader.loadMapping(protocol"
                                          + protocolName + ") 重复定义 key=" + key + ", value="
                                          + properties.getProperty((String) key));
                            }
                        } else {
                            all.put(key, properties.get((String) key));
                        }
                    }
                }

                if (all.isEmpty()) {
                    typeFormatHolder = new RawTypeFormatHoler();
                } else {
                    typeFormatHolder = new CommonTypeFormatHolder(all);
                }

                if (RdfFileLogUtil.common.isInfo()) {
                    RdfFileLogUtil.common.info("rdf-file#FormatLoader.loadMapping(protocol="
                                               + protocolName + ")  vlaues= " + all);
                }

            }

            TYPEFORMATHOLDER_CACHE.put(protocolName, typeFormatHolder);

            return typeFormatHolder;

        } catch (IOException e) {
            throw new RdfFileException("rdf-file#loadMapping protocolName=" + protocolName + " 失败",
                e, RdfErrorEnum.IO_ERROR);
        }

    }

    private static interface TypeFormatHolder {
        RdfFileFormatSpi getColumnFormat(String protocolName, String columnType);
    }

    private static class RawTypeFormatHoler implements TypeFormatHolder {
        public RawTypeFormatHoler() {
        }

        @Override
        public RdfFileFormatSpi getColumnFormat(String protocolName, String columnType) {
            RdfFileFormatSpi format = ExtensionLoader.getExtensionLoader(RdfFileFormatSpi.class)
                .getExtension("raw");

            RdfFileUtil
                .assertNotNull(format,
                    "rdf-file#FormatLoader protocolName=" + protocolName + ", columnType="
                                       + columnType + " 没有对象得raw format实现",
                    RdfErrorEnum.EXTENSION_ERROR);

            return format;

        }
    }

    private static class CommonTypeFormatHolder implements TypeFormatHolder {
        private final Properties properties;

        @SuppressWarnings("rawtypes")
        public CommonTypeFormatHolder(Properties properties) {
            this.properties = new Properties();

            Enumeration<?> names = properties.propertyNames();
            while (names.hasMoreElements()) {
                String name = (String) names.nextElement();
                Class<RdfFileColumnTypeSpi> clazz = ExtensionLoader
                    .getExtensionLoader(RdfFileColumnTypeSpi.class).getExtensionClass(name);
                RdfFileUtil.assertNotNull(clazz, "name=" + name + " 找不到对应的ColumnTypeCodec实现");
                List<String> aliasList = ExtensionLoader
                    .getExtensionLoader(RdfFileColumnTypeSpi.class).getExtensionAlias(clazz);
                for (String alias : aliasList) {
                    this.properties.put(alias, properties.get(name));
                }
            }
        }

        @Override
        public RdfFileFormatSpi getColumnFormat(String protocolName, String columnType) {
            String formatnName = RdfFileUtil.assertTrimNotBlank(properties.getProperty(columnType),
                "字段类型 type=" + columnType + ", 没有配置对应的Fromat");
            RdfFileFormatSpi format = ExtensionLoader.getExtensionLoader(RdfFileFormatSpi.class)
                .getExtension(formatnName);

            RdfFileUtil
                .assertNotNull(format,
                    "rdf-file#FormatLoader protocolName=" + protocolName + ", columnType="
                                       + columnType + " 没有对象得format实现",
                    RdfErrorEnum.EXTENSION_ERROR);

            return format;
        }
    }
}
