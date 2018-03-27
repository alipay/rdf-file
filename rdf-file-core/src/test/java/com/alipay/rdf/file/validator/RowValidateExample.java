package com.alipay.rdf.file.validator;

import com.alipay.rdf.file.interfaces.RowValidator;
import com.alipay.rdf.file.model.ValidateResult;
import com.alipay.rdf.file.util.BeanMapWrapper;
import com.alipay.rdf.file.util.RdfFileUtil;

/**
 * 示例
 * 
 * @author hongwei.quhw
 * @version $Id: RowValidateExample.java, v 0.1 2017年8月17日 下午8:04:33 hongwei.quhw Exp $
 */
public class RowValidateExample implements RowValidator {

    @Override
    public ValidateResult validateRow(RowValidatorContext context) {
        ValidateResult ret = new ValidateResult();
        BeanMapWrapper bmw = context.getRow();
        if (RdfFileUtil.equals("seq_1", (String) bmw.getProperty("seq"))) {
            ret.fail("校验错误啦seq=" + bmw.getProperty("seq"));
        }

        return ret;
    }

}
