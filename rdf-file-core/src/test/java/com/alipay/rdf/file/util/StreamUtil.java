package com.alipay.rdf.file.util;

import java.io.IOException;
import java.io.InputStream;

import com.alipay.rdf.file.exception.RdfErrorEnum;
import com.alipay.rdf.file.exception.RdfFileException;

/**
 * 
 * 
 * @author hongwei.quhw
 * @version $Id: OssUtil.java, v 0.1 2017年7月24日 下午7:07:41 hongwei.quhw Exp $
 */
public class StreamUtil {
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
            throw new RdfFileException("rdf-file#StreamUtil read error", RdfErrorEnum.IO_ERROR);
        } finally {
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    if (RdfFileLogUtil.common.isWarn()) {
                        RdfFileLogUtil.common.warn("rdf-file#StreamUtil close error");
                    }
                }
            }
        }
    }
}
