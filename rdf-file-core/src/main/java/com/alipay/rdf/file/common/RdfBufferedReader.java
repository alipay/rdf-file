package com.alipay.rdf.file.common;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.Map;

import com.alipay.rdf.file.model.FileConfig;
import com.alipay.rdf.file.processor.ProcessExecutor;
import com.alipay.rdf.file.processor.ProcessExecutor.BizData;
import com.alipay.rdf.file.processor.ProcessorTypeEnum;
import com.alipay.rdf.file.spi.RdfFileProcessorSpi;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 * 
 * @author hongwei.quhw
 * @version $Id: RdfBufferedReader.java, v 0.1 2017年7月22日 下午11:32:53 hongwei.quhw Exp $
 */
class RdfBufferedReader extends Reader {

    private InputStreamReader                                       in;

    private char                                                    cb[];
    private int                                                     nChars;

    private int                                                     nextChar;

    /** If the next character is a line feed, skip it */
    private boolean                                                 skipLF                    = false;

    private static final int                                        defaultCharBufferSize     = 8192;
    private static final int                                        defaultExpectedLineLength = 80;

    /** 已读数据大小 */
    private Long                                                    readedSize;

    /** 文件是否读取完成 */
    private boolean                                                 isReadEnd                 = false;

    private final Map<ProcessorTypeEnum, List<RdfFileProcessorSpi>> processors;

    private final FileConfig                                        fileConfig;

    /**
     * @param in
     * @param validateType
     */
    public RdfBufferedReader(InputStreamReader in, FileConfig fileConfig,
                             Map<ProcessorTypeEnum, List<RdfFileProcessorSpi>> processors) {
        super(in);
        this.in = in;
        cb = new char[defaultCharBufferSize];
        nextChar = nChars = 0;
        this.processors = processors;
        this.fileConfig = fileConfig;

    }

    /**
     * Reads a line of text.  A line is considered to be terminated by any one
     * of a line feed ('\n'), a carriage return ('\r'), or a carriage return
     * followed immediately by a linefeed.
     *
     * @param      ignoreLF  If true, the next '\n' will be skipped
     *
     * @return     A String containing the contents of the line, not including
     *             any line-termination characters, or null if the end of the
     *             stream has been reached
     * 
     * @see        java.io.LineNumberReader#readLine()
     *
     * @exception  IOException  If an I/O error occurs
     */
    public String readLine() throws IOException {
        StringBuffer s = null;
        int startChar;

        synchronized (lock) {
            ensureOpen();

            for (;;) {

                if (nextChar >= nChars) {
                    fill();
                }
                if (nextChar >= nChars) { /* EOF */
                    if (s != null && s.length() > 0) {
                        String str = s.toString();
                        validate(str.getBytes(in.getEncoding()));
                        return str;
                    } else {
                        isReadEnd = true;
                        return null;
                    }
                }
                boolean eol = false;
                char c = 0;
                int i;

                /* Skip a leftover '\n', if necessary */
                if (skipLF && (cb[nextChar] == '\n')) {
                    nextChar++;
                    validate("\n".getBytes(in.getEncoding()));
                }
                skipLF = false;

                for (i = nextChar; i < nChars; i++) {
                    c = cb[i];
                    if ((c == '\n') || (c == '\r')) {
                        eol = true;
                        break;
                    }
                }

                startChar = nextChar;
                nextChar = i;

                if (eol) {
                    String str;
                    if (s == null) {
                        str = new String(cb, startChar, i - startChar);
                    } else {
                        s.append(cb, startChar, i - startChar);
                        str = s.toString();
                    }
                    nextChar++;
                    validate((str + c).getBytes(in.getEncoding()));
                    if (c == '\r') {
                        skipLF = true;
                    }
                    return str;
                }

                if (s == null) {
                    s = new StringBuffer(defaultExpectedLineLength);
                }
                s.append(cb, startChar, i - startChar);
            }
        }
    }

    private void fill() throws IOException {
        int n;
        do {
            n = in.read(cb, 0, cb.length);
        } while (n == 0);
        if (n > 0) {
            nChars = n;
            nextChar = 0;
        }
    }

    /**
     * 计算校验数据
     * 
     * @param bytes
     */
    private void validate(byte[] bytes) {
        if (null == processors || processors.size() == 0) {
            return;
        }

        ProcessExecutor.execute(ProcessorTypeEnum.AFTER_READ_BYTES, processors, fileConfig,
            new BizData("inputByte", bytes));
    }

    /** Checks to make sure that the stream has not been closed */
    private void ensureOpen() throws IOException {
        if (in == null) {
            throw new IOException("Stream closed");
        }
    }

    /** 
     * @see java.io.Reader#read(char[], int, int)
     */
    @Override
    public int read(char[] cbuf, int off, int len) throws IOException {
        throw new IOException("没有实现，请检查代码！");
    }

    /** 
     * @see java.io.Reader#close()
     */
    @Override
    public void close() throws IOException {
        synchronized (lock) {
            if (in == null) {
                return;
            }
            in.close();
            in = null;
            cb = null;
        }
    }

    /**
     * Getter method for property <tt>readedSize</tt>.
     * 
     * @return property value of readedSize
     */
    public Long getReadedSize() {
        return readedSize;
    }

    /**
     * Getter method for property <tt>isReadEnd</tt>.
     * 
     * @return property value of isReadEnd
     */
    public boolean isReadEnd() {
        return isReadEnd;
    }
}
