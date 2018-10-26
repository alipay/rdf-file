/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.alipay.rdf.file.storage;

import java.io.IOException;
import java.io.InputStream;

import com.alipay.rdf.file.util.RdfFileLogUtil;
import com.alipay.rdf.file.util.SftpThreadContext;
import com.jcraft.jsch.ChannelSftp;

/**
 *
 * @author haofan.whf
 * @version $Id: SftpInputStream.java, v 0.1 2018年10月19日 下午6:19 haofan.whf Exp $
 */
public class SftpInputStream extends InputStream{

    private final InputStream is;

    private final ChannelSftp channelSftp;

    public SftpInputStream(InputStream is, ChannelSftp channelSftp){
        this.is = is;
        this.channelSftp = channelSftp;
    }

    @Override
    public int read() throws IOException {
        return is.read();
    }

    /**
     * @see java.io.InputStream#read(byte[])
     */
    @Override
    public int read(byte[] b) throws IOException {
        return is.read(b);
    }

    /**
     * @see java.io.InputStream#read(byte[], int, int)
     */
    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        return is.read(b, off, len);
    }

    /**
     * @see java.io.InputStream#skip(long)
     */
    @Override
    public long skip(long n) throws IOException {
        return is.skip(n);
    }

    /**
     * @see java.io.InputStream#available()
     */
    @Override
    public int available() throws IOException {
        return is.available();
    }

    /**
     * @see java.io.InputStream#close()
     */
    @Override
    public void close() throws IOException {
        try {
            is.close();
            channelSftp.disconnect();
            if(channelSftp.getSession() != null){
                channelSftp.getSession().disconnect();
            }
        } catch (Exception e){
            RdfFileLogUtil.common.warn("rdf-file#closeConnection fail", e);
        } finally {
            SftpThreadContext.clearChannelSftp();
        }
    }

    /**
     * @see java.io.InputStream#mark(int)
     */
    @Override
    public synchronized void mark(int readlimit) {
        is.mark(readlimit);
    }

    /**
     * @see java.io.InputStream#reset()
     */
    @Override
    public synchronized void reset() throws IOException {
        is.reset();
    }

    /**
     * @see java.io.InputStream#markSupported()
     */
    @Override
    public boolean markSupported() {
        return is.markSupported();
    }



}