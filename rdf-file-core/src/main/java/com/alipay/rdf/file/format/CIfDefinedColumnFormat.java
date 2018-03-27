package com.alipay.rdf.file.format;

import com.alipay.rdf.file.meta.FileColumnMeta;
import com.alipay.rdf.file.model.FileConfig;
import com.alipay.rdf.file.spi.RdfFileFormatSpi;
import com.alipay.rdf.file.util.RdfFileUtil;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 * 
 * 基金文件 序列化，反序列化工具, 这里并没有对数据进行校验
 * 
 * A 数字字符型，限于0—9 
 * C 字符型
 * N 数值型，其长度不包含小数点，可参与数值计算
 * 
 * 补位规则
 * a） 数字左补零右对齐，字符右补空格左对齐；
 * b） 字符不区分大小写
 * 
 * [20,0]
 * 如果定义定长属性属性格式化，否则按默认处理
 * 
 * @author quhongwei
 * @version $Id: FundCColumnFormat.java, v 0.1 2017年3月22日 下午5:04:45 quhongwei Exp $
 */
public class CIfDefinedColumnFormat extends RawFormat implements RdfFileFormatSpi {

    /** 
     * @see hongwei.quhw.file.format.RdfFileFormatSpi#serialize(java.lang.String, hongwei.quhw.file.meta.FileColumnMeta)
     */
    @Override
    public String serialize(String field, FileColumnMeta columnMeta, FileConfig fileConfig) {
        if (null != columnMeta.getRange()) {
            return RdfFileUtil.alignLeftBlank(field, columnMeta.getRange().getFirstAttr(),
                RdfFileUtil.getFileEncoding(fileConfig));
        }

        return super.serialize(field, columnMeta, fileConfig);
    }

    /** 
     * @see hongwei.quhw.file.format.RdfFileFormatSpi#deserialize(java.lang.String, hongwei.quhw.file.meta.FileColumnMeta)
     */
    @Override
    public String deserialize(String field, FileColumnMeta columnMeta, FileConfig fileConfig) {
        return field.trim();
    }

}
