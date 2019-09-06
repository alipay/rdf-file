/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.alipay.rdf.file.operation;

/**
 *
 * @author haofan.whf
 * @version $Id: SftpOperationParamEnums.java, v 0.1 2018年10月06日 下午4:12 haofan.whf Exp $
 */
public enum SftpOperationParamEnums {

    /**
     * upload时作为本地文件
     * rename时作为源文件
     */
    SOURCE_FILE,
    /**
     * upload时作为远程文件
     * rename时作为目标文件
     */
    TARGET_FILE,

    /**
     * 目录
     */
    TARGET_DIR,

    /**
     * 是否递归listfile
     */
    RECURSIVE_LIST,

    /**
     * 是否在框架层面关闭连接
     * 如果选择否注意手动关闭
     */
    DO_NOT_CLOSE_CONNECTION,

    ;

}