package com.alipay.rdf.file.exception;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 * 
 * 组件异常类
 * 
 * @author hongwei.quhw
 * @version $Id: RdfFileException.java, v 0.1 2017年7月26日 下午7:37:57 hongwei.quhw Exp $
 */
public class RdfFileException extends RuntimeException {
    /**  */
    private static final long serialVersionUID = -6725254693900716337L;
    /**错误类型*/
    private RdfErrorEnum      errorEnum        = RdfErrorEnum.UNKOWN;

    public RdfFileException(String message, RdfErrorEnum errorEnum) {
        super(message);
        this.errorEnum = errorEnum;
    }

    public RdfFileException(String message, Throwable cause, RdfErrorEnum errorEnum) {
        super(message, cause);
        this.errorEnum = errorEnum;
    }

    public RdfFileException(Throwable cause, RdfErrorEnum errorEnum) {
        super(cause);
        this.errorEnum = errorEnum;
    }

    public RdfErrorEnum getErrorEnum() {
        return errorEnum;
    }
}
