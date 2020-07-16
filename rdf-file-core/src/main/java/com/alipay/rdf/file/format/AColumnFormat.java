package com.alipay.rdf.file.format;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.alipay.rdf.file.exception.RdfErrorEnum;
import com.alipay.rdf.file.exception.RdfFileException;
import com.alipay.rdf.file.meta.FileColumnMeta;
import com.alipay.rdf.file.model.FileConfig;
import com.alipay.rdf.file.spi.RdfFileFormatSpi;
import com.alipay.rdf.file.util.RdfFileUtil;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 *
 * @author hongwei.quhw
 * @version $Id: AColumnFormat.java, v 0.1 2018年3月12日 下午4:17:36 hongwei.quhw Exp $
 */
public class AColumnFormat implements RdfFileFormatSpi {
    private static final Pattern DIGITAL_REG = Pattern.compile("^[0-9]*$");

    @Override
    public String serialize(String field, FileColumnMeta columnMeta, FileConfig fileConfig) {
        check(field, columnMeta);
        return RdfFileUtil.alignLeftBlank(field, columnMeta.getRange().getFirstAttr(),
            RdfFileUtil.getFileEncoding(fileConfig));
    }

    @Override
    public String deserialize(String field, FileColumnMeta columnMeta, FileConfig fileConfig) {
        field = field.trim();
        check(field, columnMeta);
        return field;
    }

    private void check(String field, FileColumnMeta columnMeta) {
        Matcher match = DIGITAL_REG.matcher(field);
        if (!match.matches()) {
            throw new RdfFileException(
                "rdf-file#字段" + columnMeta.getDesc() + "只能是数字字符类型, 实际是" + field,
                RdfErrorEnum.VALIDATE_ERROR);
        }
    }
}
