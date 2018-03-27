package com.alipay.rdf.file.loader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.alipay.rdf.file.exception.RdfErrorEnum;
import com.alipay.rdf.file.exception.RdfFileException;
import com.alipay.rdf.file.meta.FileMeta;
import com.alipay.rdf.file.model.FileConfig;
import com.alipay.rdf.file.model.FileDataTypeEnum;
import com.alipay.rdf.file.model.FileDefaultConfig;
import com.alipay.rdf.file.protocol.FileDefinitionParser;
import com.alipay.rdf.file.protocol.ProtocolDefinition;
import com.alipay.rdf.file.protocol.RowDefinition;
import com.alipay.rdf.file.resource.RdfInputStream;
import com.alipay.rdf.file.util.RdfFileLogUtil;
import com.alipay.rdf.file.util.RdfFileUtil;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 * 
 * 协议定义加载器
 * 
 * @author hongwei.quhw
 * @version $Id: ProtocolLoader.java, v 0.1 2017年8月25日 下午3:51:37 hongwei.quhw Exp $
 */
public class ProtocolLoader {

    private static final Map<String, List<String>>       PROTOCOL_PROCESSOR_CACHE = new ConcurrentHashMap<String, List<String>>();

    private static final String                          RDF_PROTOCOL_SUBFIX      = ".xml";

    private static final Object                          LOCK                     = new Object();

    private static final Map<String, ProtocolDefinition> PD_CACHE                 = new ConcurrentHashMap<String, ProtocolDefinition>();

    public static ProtocolDefinition loadProtocol(String protocolName) {
        protocolName = RdfFileUtil.assertTrimNotBlank(protocolName);

        ProtocolDefinition pd = PD_CACHE.get(protocolName);

        if (null == pd) {
            synchronized (LOCK) {
                pd = PD_CACHE.get(protocolName);

                if (null == pd) {
                    String resourcePath = FileDefaultConfig.RDF_PROTOCOL_PATH
                                          + protocolName.toLowerCase() + RDF_PROTOCOL_SUBFIX;
                    RdfInputStream is = ResourceLoader.getInputStream(resourcePath);
                    if (null == is || !is.hasNext()) {
                        resourcePath = FileDefaultConfig.RDF_PROTOCOL_PATH
                                       + protocolName.toUpperCase() + RDF_PROTOCOL_SUBFIX;
                        is = ResourceLoader.getInputStream(resourcePath);
                    }
                    RdfFileUtil.assertNotNull(is, "资源 resourcePath=" + resourcePath
                                                  + "不存在, 协议protocol=" + protocolName + "没有被定义");

                    while (is.next()) {
                        pd = new FileDefinitionParser().parseFileDefinition(is);
                        if (PD_CACHE.containsKey(protocolName)) {
                            if (RdfFileLogUtil.common.isWarn()) {
                                RdfFileLogUtil.common
                                    .warn("rdf-file#ProtocolLoader.loadProtocol(protocol="
                                          + protocolName + ")重复定义了");
                            }
                        } else {
                            //加载协议默认处理器
                            parseProcessors(protocolName);
                            PD_CACHE.put(protocolName.toLowerCase(), pd);
                            PD_CACHE.put(protocolName.toUpperCase(), pd);
                        }
                    }
                }
            }
        }

        return pd;
    }

    public static List<String> loadAutoProcessors(String protocolName) {
        // 预加载一下
        loadProtocol(protocolName);

        return PROTOCOL_PROCESSOR_CACHE.get(protocolName);
    }

    private static void parseProcessors(String protocolName) {
        String processorPath = FileDefaultConfig.RDF_PROCESSOR_PATH + protocolName.toLowerCase();
        RdfInputStream is = ResourceLoader.getInputStream(processorPath);
        if (null == is) {
            processorPath = FileDefaultConfig.RDF_PROCESSOR_PATH + protocolName.toUpperCase();
            is = ResourceLoader.getInputStream(processorPath);
        }

        if (null == is || !is.hasNext()) {
            if (RdfFileLogUtil.common.isInfo()) {
                RdfFileLogUtil.common.info(
                    "rdf-file#ProtocolLoader protocol=" + protocolName + ", 没有对应的自动processor");
            }
            return;
        }

        while (is.next()) {
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new InputStreamReader(is, "utf-8"));

                String line = null;
                while ((line = reader.readLine()) != null) {
                    if (RdfFileUtil.isBlank(line)) {
                        continue;
                    }

                    List<String> processors = PROTOCOL_PROCESSOR_CACHE.get(protocolName);
                    if (null == processors) {
                        processors = new ArrayList<String>();
                        PROTOCOL_PROCESSOR_CACHE.put(protocolName.toLowerCase(), processors);
                        PROTOCOL_PROCESSOR_CACHE.put(protocolName.toUpperCase(), processors);
                    }

                    processors.add(line.trim());
                }

                if (RdfFileLogUtil.common.isInfo()) {
                    RdfFileLogUtil.common
                        .info("rdf-file#ProtocolLoader protocol=" + protocolName
                              + ", 加载的自动processor=" + PROTOCOL_PROCESSOR_CACHE.get(protocolName));
                }
            } catch (IOException e) {
                throw new RdfFileException(
                    "rdf-file#ProtocolLoader.loadProcessor protocol=" + protocolName, e,
                    RdfErrorEnum.IO_ERROR);
            } finally {
                if (null != reader) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        if (RdfFileLogUtil.common.isWarn()) {
                            RdfFileLogUtil.common.warn("ProtocolLoader.parseProcessors close error",
                                e);
                        }
                    }
                }
            }
        }

    }

    /**
     * 计算对应部位，涉及的行数
     * 
     * @param fileMeta
     * @param rowType
     * @return
     */
    public static int getRowsAfftected(FileConfig config, FileDataTypeEnum rowType) {
        FileMeta fileMeta = TemplateLoader.load(config);
        ProtocolDefinition pd = loadProtocol(fileMeta.getProtocol());
        List<RowDefinition> rds = null;
        if (FileDataTypeEnum.HEAD.equals(rowType)) {
            rds = pd.getHeads();
        } else if (FileDataTypeEnum.TAIL.equals(rowType)) {
            rds = pd.getTails();
        } else {
            throw new RdfFileException(fileMeta + "， 文件部位rowType=" + rowType.name() + ", 无法计算涉及的行数",
                RdfErrorEnum.ROWS_AFFECTED_ERROR);
        }

        int rowsAfftected = 0;
        for (RowDefinition rd : rds) {
            rowsAfftected += rd.getOutput().rowsAffected(rd, fileMeta);
        }

        return rowsAfftected;
    }

    /**
     * 获取行定义
     * 
     * @param protocol
     * @param rowType
     * @return
     */
    public static List<RowDefinition> getRowDefinitos(String protocol, FileDataTypeEnum rowType) {
        switch (rowType) {
            case HEAD:
                return loadProtocol(protocol).getHeads();
            case BODY:
                return loadProtocol(protocol).getBodys();
            case TAIL:
                return loadProtocol(protocol).getTails();
            default:
                throw new RdfFileException(
                    "rdf-file#ProtocolLoader.getRowDefinitos 不支持rowType=" + rowType.name(),
                    RdfErrorEnum.UNSUPPORTED_OPERATION);
        }
    }
}
