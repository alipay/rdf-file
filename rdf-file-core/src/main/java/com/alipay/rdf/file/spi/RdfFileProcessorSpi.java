package com.alipay.rdf.file.spi;

import java.util.List;

import com.alipay.rdf.file.processor.ProcessCotnext;
import com.alipay.rdf.file.processor.ProcessorTypeEnum;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 *
 * @author hongwei.quhw
 * @version $Id: RdfFileProcessorSpi.java, v 0.1 2018年3月12日 下午4:25:52 hongwei.quhw Exp $
 */
public interface RdfFileProcessorSpi {

    void process(ProcessCotnext pc);

    int getOrder();

    List<ProcessorTypeEnum> supportedTypes();
}
