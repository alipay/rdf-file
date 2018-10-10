/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.alipay.rdf.file.enums;

/**
 *
 * @author haofan.whf
 * @version $Id: SftpAuthEnum.java, v 0.1 2018年10月02日 下午7:08 haofan.whf Exp $
 */
public enum SftpAuthEnum {

    PASSWORD(0, "密码验证"),
    IDENTITY(1, "私钥验证"),
    MIX(2, "混合模式认证"),

    ;


    private final int code;
    private final String desc;

    SftpAuthEnum(int code, String desc){
        this.code = code;
        this.desc = desc;
    }


}