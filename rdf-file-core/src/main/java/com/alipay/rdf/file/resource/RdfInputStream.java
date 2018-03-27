package com.alipay.rdf.file.resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.alipay.rdf.file.exception.RdfErrorEnum;
import com.alipay.rdf.file.util.RdfFileUtil;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 * 
 * rdf-file封装的inputstream
 * 
 * @author hongwei.quhw
 * @version $Id: GroupInputStream.java, v 0.1 2017年8月7日 上午11:31:08 hongwei.quhw Exp $
 */
public class RdfInputStream extends InputStream {
    private Iterator<InputStream> streamIter;

    private InputStream           currentStream;

    public RdfInputStream(InputStream is) {
        RdfFileUtil.assertNotNull(is, "rdf-file#new RdfInputStream(is = null)",
            RdfErrorEnum.ILLEGAL_ARGUMENT);
        this.currentStream = is;
        List<InputStream> streams = new ArrayList<InputStream>(1);
        streams.add(is);
        this.streamIter = streams.iterator();
    }

    public RdfInputStream(List<InputStream> streams) {
        RdfFileUtil.assertNotNull(streams, "rdf-file#new RdfInputStream(is = null)",
            RdfErrorEnum.ILLEGAL_ARGUMENT);
        this.streamIter = streams.iterator();
    }

    public boolean hasNext() {
        if (null == streamIter) {
            return false;
        }

        return streamIter.hasNext();
    }

    public boolean next() {
        if (null == streamIter) {
            return false;
        }

        boolean hasNext = streamIter.hasNext();
        if (hasNext) {
            currentStream = streamIter.next();
        }

        return hasNext;
    }

    @Override
    public int read() throws IOException {
        return currentStream.read();
    }

    @Override
    public int read(byte[] b) throws IOException {
        return currentStream.read(b);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        return currentStream.read(b, off, len);
    }

    @Override
    public long skip(long n) throws IOException {
        return currentStream.skip(n);
    }

    @Override
    public int available() throws IOException {
        return currentStream.available();
    }

    @Override
    public void close() throws IOException {
        currentStream.close();
    }

    @Override
    public synchronized void mark(int readlimit) {
        currentStream.mark(readlimit);
    }

    @Override
    public synchronized void reset() throws IOException {
        currentStream.reset();
    }

    @Override
    public boolean markSupported() {
        return currentStream.markSupported();
    }
}
