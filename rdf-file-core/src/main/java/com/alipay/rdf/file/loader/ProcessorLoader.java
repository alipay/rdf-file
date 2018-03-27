package com.alipay.rdf.file.loader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alipay.rdf.file.exception.RdfErrorEnum;
import com.alipay.rdf.file.exception.RdfFileException;
import com.alipay.rdf.file.model.FileConfig;
import com.alipay.rdf.file.model.FileDefaultConfig;
import com.alipay.rdf.file.processor.ProcessorTypeEnum;
import com.alipay.rdf.file.resource.RdfInputStream;
import com.alipay.rdf.file.spi.RdfFileProcessorSpi;
import com.alipay.rdf.file.util.RdfFileLogUtil;
import com.alipay.rdf.file.util.RdfFileUtil;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 * 
 * 处理器加载
 * 
 * @author hongwei.quhw
 * @version $Id: ProcessorFactory.java, v 0.1 2017年4月7日 下午2:48:10 hongwei.quhw Exp $
 */
public class ProcessorLoader {
    private static final Object LOAD_DEFAULT_PROCESSOR_LOCK = new Object();
    private static final String DEFAULT_PROCESSOR_FILE_NAME = "default";

    private static List<String> DEFAULT_PROCESSORS;

    public static Map<ProcessorTypeEnum, List<RdfFileProcessorSpi>> loadByType(FileConfig fileConfig,
                                                                               ProcessorTypeEnum... processorTypes) {
        Map<ProcessorTypeEnum, List<RdfFileProcessorSpi>> processors = new HashMap<ProcessorTypeEnum, List<RdfFileProcessorSpi>>();

        // 默认处理器
        addProcessors(processors, loadDefaultProcessors(), processorTypes);

        // 文件协议（格式）关联处理器
        if (RdfFileUtil.isNotBlank(fileConfig.getTemplatePath())) {
            List<String> protocolProcessors = ProtocolLoader
                .loadAutoProcessors(TemplateLoader.load(fileConfig).getProtocol());
            if (null != protocolProcessors && protocolProcessors.size() > 0) {
                addProcessors(processors, protocolProcessors, processorTypes);
            }
        }

        // 用户指定processors
        addProcessors(processors, fileConfig.getProcessorKeys(), processorTypes);

        // 排序
        for (ProcessorTypeEnum type : processors.keySet()) {
            List<RdfFileProcessorSpi> processorList = processors.get(type);
            Collections.sort(processorList, new Comparator<RdfFileProcessorSpi>() {
                @Override
                public int compare(RdfFileProcessorSpi o1, RdfFileProcessorSpi o2) {
                    return o1.getOrder() - o2.getOrder();
                }
            });
        }

        return processors;
    }

    private static void addProcessors(Map<ProcessorTypeEnum, List<RdfFileProcessorSpi>> processors,
                                      List<String> processorNames,
                                      ProcessorTypeEnum... processorTypes) {
        for (String key : processorNames) {
            RdfFileProcessorSpi processor = ExtensionLoader
                .getExtensionLoader(RdfFileProcessorSpi.class).getNewExtension(key);

            for (ProcessorTypeEnum type : processorTypes) {
                if (processor.supportedTypes().contains(type)) {
                    List<RdfFileProcessorSpi> processorSpis = processors.get(type);
                    if (null == processorSpis) {
                        processorSpis = new ArrayList<RdfFileProcessorSpi>();
                        processors.put(type, processorSpis);
                    }

                    if (!processorSpis.contains(processor)) {
                        processorSpis.add(processor);
                    }
                }
            }
        }
    }

    private static List<String> loadDefaultProcessors() {
        if (null == DEFAULT_PROCESSORS) {
            synchronized (LOAD_DEFAULT_PROCESSOR_LOCK) {
                List<String> defaultProcessors = new ArrayList<String>();

                String processorPath = FileDefaultConfig.RDF_PROCESSOR_PATH
                                       + DEFAULT_PROCESSOR_FILE_NAME;
                RdfInputStream is = ResourceLoader.getInputStream(processorPath);

                if (null == is || !is.hasNext()) {
                    if (RdfFileLogUtil.common.isInfo()) {
                        RdfFileLogUtil.common
                            .info("rdf-file#ProcessorLoader.loadDefaultProcessors() processorPath="
                                  + processorPath + " 没有加载到默认processor");
                    }
                    DEFAULT_PROCESSORS = defaultProcessors;
                    return DEFAULT_PROCESSORS;
                }

                try {
                    while (is.next()) {
                        BufferedReader reader = new BufferedReader(
                            new InputStreamReader(is, "utf-8"));

                        try {
                            String line = null;
                            while ((line = reader.readLine()) != null) {
                                if (RdfFileUtil.isBlank(line)) {
                                    continue;
                                }

                                line = RdfFileUtil.trimNotNull(line);
                                if (defaultProcessors.contains(line)) {
                                    if (RdfFileLogUtil.common.isWarn()) {
                                        RdfFileLogUtil.common.warn(
                                            "rdf-file#ProcessorLoader.loadDefaultProcessor name = "
                                                                   + line + " 重复定义了");
                                    }
                                } else {
                                    defaultProcessors.add(line.trim());
                                }
                            }

                            if (RdfFileLogUtil.common.isInfo()) {
                                RdfFileLogUtil.common.info(
                                    "rdf-file#ProcessorLoader.loadDefaultProcessors() processorPath="
                                                           + processorPath + " 加载到默认processor="
                                                           + DEFAULT_PROCESSORS);
                            }
                        } finally {
                            reader.close();
                        }
                    }

                    DEFAULT_PROCESSORS = defaultProcessors;

                } catch (UnsupportedEncodingException e) {
                    throw new RdfFileException("rdf-file#ProcessorLoader.loadDefaultProcessor", e,
                        RdfErrorEnum.ENCODING_ERROR);
                } catch (IOException e) {
                    throw new RdfFileException("rdf-file#ProcessorLoader.loadDefaultProcessor", e,
                        RdfErrorEnum.IO_ERROR);
                }
            }
        }
        
        return DEFAULT_PROCESSORS;
    }
}
