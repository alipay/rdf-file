package com.alipay.rdf.file.meta;

import com.alipay.rdf.file.loader.ExtensionLoader;
import com.alipay.rdf.file.spi.RdfFileColumnTypeSpi;
import com.alipay.rdf.file.type.DateType;
import com.alipay.rdf.file.util.RdfFileUtil;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 * 
 * Date:yyyyMMdd HH:mm:ss
 * 
 * typeEnum = Date
 * extra    = yyyyMMdd HH:mm:ss
 * 
 * @author hongwei.quhw
 * @version $Id: FileColumnTypeMeta.java, v 0.1 2016-12-20 下午4:19:12 hongwei.quhw Exp $
 */
public class FileColumnTypeMeta {

    private final String extra;

    private final String name;

    public FileColumnTypeMeta(String name, String extra) {
        this.extra = extra;
        this.name = name;
    }

    /**
     * Getter method for property <tt>name</tt>.
     * 
     * @return property value of name
     */
    public String getName() {
        return name;
    }

    /**
     * Getter method for property <tt>extra</tt>.
     * 
     * @return property value of extra
     */
    public String getExtra() {
        return extra;
    }

    public static FileColumnTypeMeta tryValueOf(String field, String extra) {
        @SuppressWarnings("rawtypes")
        RdfFileColumnTypeSpi columnTypeCodec = ExtensionLoader.getExtensionLoader(RdfFileColumnTypeSpi.class)
            .getExtension(field);
        if (null == columnTypeCodec) {
            return null;
        }

        if (columnTypeCodec instanceof DateType) {
            RdfFileUtil.assertTrimNotBlank(extra, "日期类型必须指定format");
        }

        return new FileColumnTypeMeta(field, extra);
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer("FileColumnTypeMeta[");
        sb.append("name=").append(name);
        if (RdfFileUtil.isNotBlank(extra)) {
            sb.append(",extra=" + extra);
        }
        sb.append("]");
        return sb.toString();
    }
}
