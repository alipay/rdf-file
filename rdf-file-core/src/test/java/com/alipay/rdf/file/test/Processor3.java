package com.alipay.rdf.file.test;

import java.util.ArrayList;
import java.util.List;

import com.alipay.rdf.file.processor.ProcessCotnext;
import com.alipay.rdf.file.processor.ProcessorTypeEnum;
import com.alipay.rdf.file.spi.RdfFileProcessorSpi;

public class Processor3 implements RdfFileProcessorSpi {

    @Override
    public void process(ProcessCotnext pc) {

    }

    @Override
    public int getOrder() {
        return 15;
    }

    @Override
    public List<ProcessorTypeEnum> supportedTypes() {
        List<ProcessorTypeEnum> types = new ArrayList<ProcessorTypeEnum>();
        types.add(ProcessorTypeEnum.AFTER_CLOSE_WRITER);
        return types;
    }

}
