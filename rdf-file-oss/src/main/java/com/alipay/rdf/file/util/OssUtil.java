package com.alipay.rdf.file.util;

import java.io.IOException;
import java.io.InputStream;

import com.alipay.rdf.file.exception.RdfErrorEnum;
import com.alipay.rdf.file.exception.RdfFileException;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 *
 * 用 核心包的 com.alipay.rdf.file.util.RdfFileUtil#read(java.io.InputStream, int) 替换
 *
 * @author hongwei.quhw
 * @version $Id: OssUtil.java, v 0.1 2017年7月24日 下午7:07:41 hongwei.quhw Exp $
 */
@Deprecated
public class OssUtil {
    /**读取行尾默认buffer*/
    public static int OSS_READ_HEAD_BUFFER = 256;

    /**读取行尾默认buffer*/
    public static int OSS_READ_TAIL_BUFFER = 128;

    /**一行数据buffer*/
    public static int OSS_READ_LINE_BUEER  = 32;

    /**
     * 从输入流读取指定长度数据到byte[]
     *
     * @param is
     * @param length
     * @return
     * @throws IOException
     */
    public static byte[] read(InputStream is, int length) {
        try {
            byte[] bs = new byte[length];
            int ret = -1;
            int st = 0;
            int total = 0;
            int len = length;

            while ((ret = is.read(bs, st, len)) > 0) {
                st += ret;
                total += ret;
                int less = length - total;
                if (less < len) {
                    len = less;
                }
            }

            return bs;
        } catch (IOException e) {
            throw new RdfFileException("rdf-file#OssUtil.read 异常", e, RdfErrorEnum.IO_ERROR);
        } finally {
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    if (RdfFileLogUtil.common.isWarn()) {
                        RdfFileLogUtil.common.warn("rdf-file#OssUtil.close()", e);
                    }
                }
            }
        }
    }
}
