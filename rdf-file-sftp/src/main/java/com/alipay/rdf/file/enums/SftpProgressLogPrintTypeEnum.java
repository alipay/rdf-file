/**
 * Alipay.com Inc. Copyright (c) 2004-2019 All Rights Reserved.
 */
package com.alipay.rdf.file.enums;

/**
 * 进度监控日志打印方式
 * @author iminright-ali
 * @version : SftpProgressLogPrintTypeEnum.java, v 0.1 2019年09月05日 11:14 iminright-ali Exp $
 */
public enum SftpProgressLogPrintTypeEnum {

    /**
     * 根据当前大小同步打印
     */
    SYNC,

    /**
     * 固定周期打印
     */
    FIXED_PERIOD,

}