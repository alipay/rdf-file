package com.alipay.rdf.file.processor;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 *
 * @author hongwei.quhw
 * @version $Id: ProcessorTypeEnum.java, v 0.1 2018年3月12日 下午4:23:10 hongwei.quhw Exp $
 */
public enum ProcessorTypeEnum {

    AFTER_CLOSE_WRITER,

    AFTER_WRITE_BYTES,

    AFTER_WRITE_HEAD,

    BEFORE_WRITE_ROW,

    AFTER_WRITE_ROW,

    AFTER_WRITE_TAIL,

    AFTER_CLOSE_READER,

    AFTER_READ_BYTES,

    AFTER_READ_HEAD,

    BEFORE_READ_ROW,

    AFTER_READ_ROW,

    AFTER_READ_TAIL,

    BEFORE_SERIALIZE_ROW,

    AFTER_SERIALIZE_ROW,

    BEFORE_DESERIALIZE_ROW,

    AFTER_DESERIALIZE_ROW,

    BEFORE_CREATE_WRITER,

    BEFORE_CREATE_READER;

}
