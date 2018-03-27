package com.alipay.rdf.file.validate;

import java.util.ArrayList;
import java.util.List;

import com.alipay.rdf.file.exception.RdfErrorEnum;
import com.alipay.rdf.file.exception.RdfFileException;
import com.alipay.rdf.file.interfaces.RowValidator;
import com.alipay.rdf.file.interfaces.RowValidator.RowValidatorContext;
import com.alipay.rdf.file.loader.TemplateLoader;
import com.alipay.rdf.file.meta.FileMeta;
import com.alipay.rdf.file.model.FileConfig;
import com.alipay.rdf.file.model.FileDataTypeEnum;
import com.alipay.rdf.file.model.ValidateResult;
import com.alipay.rdf.file.processor.ProcessCotnext;
import com.alipay.rdf.file.processor.ProcessorTypeEnum;
import com.alipay.rdf.file.spi.RdfFileProcessorSpi;
import com.alipay.rdf.file.util.BeanMapWrapper;
import com.alipay.rdf.file.util.RdfFileConstants;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 * 
 * 行校验器
 * 
 * @author hongwei.quhw
 * @version $Id: RowValidateProcessor.java, v 0.1 2017年8月4日 下午9:51:53 hongwei.quhw Exp $
 */
public class BodyRowValidator implements RdfFileProcessorSpi {
    @Override
    public List<ProcessorTypeEnum> supportedTypes() {
        List<ProcessorTypeEnum> types = new ArrayList<ProcessorTypeEnum>();
        types.add(ProcessorTypeEnum.BEFORE_SERIALIZE_ROW);
        types.add(ProcessorTypeEnum.AFTER_DESERIALIZE_ROW);
        return types;
    }

    @Override
    public void process(ProcessCotnext pc) {
        if (!FileDataTypeEnum.BODY.equals(pc.getBizData(RdfFileConstants.ROW_TYPE))) {
            return;
        }

        FileConfig fileConfig = pc.getFileConfig();
        BeanMapWrapper row = (BeanMapWrapper) pc.getBizData(RdfFileConstants.DATA);

        List<RowValidator> validators = fileConfig.getRowValidators();
        for (RowValidator validator : validators) {
            validator.validateRow(new RowValidatorContext(fileConfig, row));
        }

        FileMeta fileMeta = TemplateLoader.load(fileConfig);
        validators = fileMeta.getValidators();
        for (RowValidator validator : validators) {
            ValidateResult ret = validator.validateRow(new RowValidatorContext(fileConfig, row));
            if (!ret.isSuccess()) {
                throw new RdfFileException(ret.getErrorMsg(), ret.getEx(),
                    RdfErrorEnum.VALIDATE_ERROR);
            }
        }
    }

    @Override
    public int getOrder() {
        return 0;
    }

}
