package com.alipay.rdf.file.storage;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;

import com.alipay.rdf.file.exception.RdfErrorEnum;
import com.alipay.rdf.file.exception.RdfFileException;
import com.alipay.rdf.file.util.RdfFileLogUtil;
import com.aliyun.oss.model.OSSObject;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 *
 * Oss 输入流
 *
 * @author hongwei.quhw
 * @version $Id: OssInputStream.java, v 0.1 2017年7月22日 下午10:53:37 hongwei.quhw Exp $
 */
public class OssInputStream extends InputStream {
    private InputStream is;
    private OSSObject   ossObject;

    public OssInputStream(OSSObject ossObject) {
        if (null != ossObject) {
            this.is = ossObject.getObjectContent();
            this.ossObject = ossObject;
        }
    }

    /**
     * @see java.io.InputStream#read()
     */
    @Override
    public int read() throws IOException {
        if (null == is) {
            return -1;
        }
        return is.read();
    }

    /**
     * @see java.io.InputStream#read(byte[])
     */
    @Override
    public int read(byte[] b) throws IOException {
        if (null == is) {
            return -1;
        }
        return is.read(b);
    }

    /**
     * @see java.io.InputStream#read(byte[], int, int)
     */
    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        if (null == is) {
            return -1;
        }
        return is.read(b, off, len);
    }

    /**
     * @see java.io.InputStream#skip(long)
     */
    @Override
    public long skip(long n) throws IOException {
        if (null == is) {
            return 0;
        }

        return is.skip(n);
    }

    /**
     * @see java.io.InputStream#available()
     */
    @Override
    public int available() throws IOException {
        if (null == is) {
            return 0;
        }
        return is.available();
    }

    /**
     * @see java.io.InputStream#close()
     */
    @Override
    public void close() throws IOException {
        if (null == ossObject) {
            return;
        }

        //低版本没有这个方法
        try {
            Method forcedClose = OSSObject.class.getDeclaredMethod("forcedClose");
            forcedClose.invoke(ossObject);
        } catch (NoSuchMethodException e) {
            if (RdfFileLogUtil.common.isWarn()) {
                RdfFileLogUtil.common
                    .warn("rdf-file#OssInputStream.close() ossObject中没有forcedClose方法", e);
            }
        } catch (Exception e) {
            throw new RdfFileException(
                "rdf-file#OssInputStream.close()  OssInputStream.ossObject forcedClose 失败 ", e,
                RdfErrorEnum.IO_ERROR);
        }

        is.close();
    }

    /**
     * @see java.io.InputStream#mark(int)
     */
    @Override
    public synchronized void mark(int readlimit) {
        if (null != is) {
            is.mark(readlimit);
        }
    }

    /**
     * @see java.io.InputStream#reset()
     */
    @Override
    public synchronized void reset() throws IOException {
        if (null != is) {
            is.reset();
        }
    }

    /**
     * @see java.io.InputStream#markSupported()
     */
    @Override
    public boolean markSupported() {
        if (null == is) {
            return false;
        }

        return is.markSupported();
    }

}
