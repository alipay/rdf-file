/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2016 All Rights Reserved.
 */
package com.alipay.rdf.file.storage;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.alipay.rdf.file.exception.RdfErrorEnum;
import com.alipay.rdf.file.exception.RdfFileException;
import com.alipay.rdf.file.interfaces.FileStorage;
import com.alipay.rdf.file.loader.ProtocolLoader;
import com.alipay.rdf.file.loader.TemplateLoader;
import com.alipay.rdf.file.meta.FileMeta;
import com.alipay.rdf.file.model.FileConfig;
import com.alipay.rdf.file.model.FileDataTypeEnum;
import com.alipay.rdf.file.model.FileInfo;
import com.alipay.rdf.file.model.FileSlice;
import com.alipay.rdf.file.spi.RdfFileSplitterSpi;
import com.alipay.rdf.file.util.OssUtil;
import com.alipay.rdf.file.util.RdfFileLogUtil;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 * 
 * @author hongwei.quhw
 * @version $Id: OssFileSliceSplitter.java, v 0.1 2017年5月9日 下午4:26:38 hongwei.quhw Exp $
 */
public class OssFileSliceSplitter implements RdfFileSplitterSpi {

    /**
     * OSS Storage
     */
    private FileOssStorage storage;

    @Override
    public void init(FileStorage fileStorage) {
        this.storage = (FileOssStorage) fileStorage;
    }

    /** 
     * @see com.alipay.rdf.file.FileSplitter#split(java.lang.String, int)
     */
    @Override
    public List<FileSlice> split(String filePath, int sliceSize) {
        if (RdfFileLogUtil.common.isInfo()) {
            RdfFileLogUtil.common.info("rdf-file#OssFileSliceSplitter.split(filePath=" + filePath
                                       + ", sliceSize=" + sliceSize + ")");
        }

        FileInfo fileInfo = storage.getFileInfo(filePath);
        if (!fileInfo.isExists()) {
            throw new RdfFileException(
                "rdf-file#OssFileSliceSplitter.split filePath=" + filePath + "), 文件不存在文件",
                RdfErrorEnum.NOT_EXSIT);
        }

        if (RdfFileLogUtil.common.isDebug()) {
            RdfFileLogUtil.common
                .debug("rdf-file#OssFileSliceSplitter.split(filePath=" + filePath + ", sliceSize="
                       + sliceSize + ")  fileSize=" + fileInfo.getSize());
        }

        return split(fileInfo,
            new FileSlice(filePath, FileDataTypeEnum.UNKOWN, 0, fileInfo.getSize()), sliceSize);
    }

    private List<FileSlice> split(FileInfo fileInfo, FileSlice fileSlice, int sliceSize) {
        String filePath = fileSlice.getFilePath();

        if (fileSlice.getLength() < 0 || sliceSize <= 0) {
            throw new RdfFileException("rdf-file#OssFileSliceSplitter.split(fileSlice=" + fileSlice
                                       + ", sliceSize=" + sliceSize + ") 分片大小不小于且sliceSize必须大于零",
                RdfErrorEnum.ILLEGAL_ARGUMENT);
        }

        long start = fileSlice.getStart();
        long end = fileSlice.getEnd();
        long length = fileSlice.getLength();

        if (end > fileInfo.getSize()) {
            throw new RdfFileException("rdf-file#OssFileSliceSplitter.split(fileSlice=" + fileSlice
                                       + ") 分片结束位置大于文件结束位置 fileSize=" + fileInfo.getSize(),
                RdfErrorEnum.ILLEGAL_ARGUMENT);
        }

        List<FileSlice> fileSlices = new ArrayList<FileSlice>();

        if (length <= sliceSize) {
            fileSlices.add(fileSlice);
            return fileSlices;
        }

        //缓存读取的数组
        byte[] bs = null;
        //数组下标
        int idx = 0;
        // 分片开始位置
        long sliceStart = start;
        //分片结束位置
        long sliceEnd = sliceStart + sliceSize;
        //往后读取的buffer
        int bufferSize = OssUtil.OSS_READ_LINE_BUEER;
        //跳出循环
        boolean next = true;
        // 是否行尾
        boolean eof = false;
        while (next) {
            if (null == bs) {
                if ((sliceEnd + bufferSize) > end) {
                    bufferSize = (int) (end - sliceEnd);

                    if (RdfFileLogUtil.common.isDebug()) {
                        RdfFileLogUtil.common
                            .debug("rdf-file#OssFileSliceSplitter.split (sliceEnd + bufferSize)="
                                   + bufferSize + " > end=" + end);
                    }
                }

                if (bufferSize <= 0) {
                    if (end > sliceStart) {
                        FileSlice slice = new FileSlice(filePath, fileSlice.getFileDataType(),
                            sliceStart, end);
                        fileSlices.add(slice);

                        if (RdfFileLogUtil.common.isDebug()) {
                            RdfFileLogUtil.common.debug(
                                "rdf-file#OssFileSliceSplitter.split bufferSize <= 0 fileSlice="
                                                        + slice);
                        }
                    }
                    next = false;
                    break;
                }

                if (RdfFileLogUtil.common.isDebug()) {
                    RdfFileLogUtil.common
                        .debug("rdf-file#OssFileSliceSplitter.split read bytes start=" + sliceEnd
                               + ", length=" + bufferSize);
                }

                InputStream is = storage.getInputStream(filePath, sliceEnd, bufferSize);
                bs = OssUtil.read(is, bufferSize);

                if (RdfFileLogUtil.common.isDebug()) {
                    RdfFileLogUtil.common
                        .debug("rdf-file#OssFileSliceSplitter.split read bytes size=" + bs.length);
                    RdfFileLogUtil.common
                        .debug("rdf-file#OssFileSliceSplitter.split read bytes content:\r\n"
                               + new String(bs));
                }
            }

            sliceEnd++;
            switch (bs[idx++]) {
                case '\n':
                    FileSlice slice = new FileSlice(filePath, fileSlice.getFileDataType(),
                        sliceStart, sliceEnd);
                    if (RdfFileLogUtil.common.isDebug()) {
                        RdfFileLogUtil.common
                            .debug("rdf-file#OssFileSliceSplitter.split \\n fileSlice=" + slice);
                    }
                    fileSlices.add(slice);
                    sliceStart = sliceEnd;
                    sliceEnd += sliceSize;
                    eof = true;
                    break;
                case '\r':
                    //往后读一个字符看看是不是\n
                    if (idx < bs.length) {
                        if (bs[idx] != '\n') {
                            slice = new FileSlice(filePath, fileSlice.getFileDataType(), sliceStart,
                                sliceEnd);
                            if (RdfFileLogUtil.common.isDebug()) {
                                RdfFileLogUtil.common.debug(
                                    "rdf-file#OssFileSliceSplitter.split \\r bs[idx] != \\n fileSlice="
                                                            + slice);
                            }
                            fileSlices.add(slice);
                            sliceStart = sliceEnd;
                            sliceEnd += sliceSize;
                            eof = true;
                        }
                    } else if (sliceEnd == end) {
                        slice = new FileSlice(filePath, fileSlice.getFileDataType(), sliceStart,
                            sliceEnd);
                        if (RdfFileLogUtil.common.isDebug()) {
                            RdfFileLogUtil.common.debug(
                                "rdf-file#OssFileSliceSplitter.split \\r (sliceEnd == end) fileSlice="
                                                        + slice);
                        }
                        fileSlices.add(slice);
                        sliceStart = sliceEnd;
                        sliceEnd += sliceSize;
                        eof = true;
                    } else {
                        // oss 往后读一个字符
                        if (RdfFileLogUtil.common.isDebug()) {
                            RdfFileLogUtil.common.debug(
                                "rdf-file#OssFileSliceSplitter.split \\r oss read next char ");
                        }
                        InputStream is = storage.getInputStream(filePath, sliceEnd, 1);
                        try {
                            if (is.read() != '\n') {
                                slice = new FileSlice(filePath, fileSlice.getFileDataType(),
                                    sliceStart, sliceEnd);
                                if (RdfFileLogUtil.common.isDebug()) {
                                    RdfFileLogUtil.common.debug(
                                        "rdf-file#OssFileSliceSplitter.split oss read next char fileSlice="
                                                                + slice);
                                }
                                fileSlices.add(slice);
                                sliceStart = sliceEnd;
                                sliceEnd += sliceSize;
                                eof = true;
                            }
                        } catch (IOException e) {
                            throw new RdfFileException("rdf-file#OssFileSliceSplitter.split read()",
                                e, RdfErrorEnum.IO_ERROR);
                        } finally {
                            if (null != is) {
                                try {
                                    is.close();
                                } catch (IOException e) {
                                    if (RdfFileLogUtil.common.isWarn()) {
                                        RdfFileLogUtil.common.warn(
                                            "rdf-file#OssFileSliceSplitter.split is.close() eroor",
                                            e);
                                    }
                                }
                            }
                        }
                    }
            }

            if (eof || idx == bs.length) {
                int left = bs.length - idx;
                if (left > 0) {
                    sliceEnd += left;
                    if (RdfFileLogUtil.common.isDebug()) {
                        RdfFileLogUtil.common
                            .debug("rdf-file#OssFileSliceSplitter.split left=" + left + " > 0");
                    }
                }
                bs = null;
                idx = 0;
                eof = false;
            }
        }

        return fileSlices;

    }

    /** 
     * @see com.alipay.rdf.file.interfaces.FileSplitter#getHeadSlice(com.alipay.rdf.file.model.FileConfig)
     */
    @Override
    public FileSlice getHeadSlice(FileConfig fileConfig) {
        String filePath = fileConfig.getFilePath();
        RdfFileLogUtil.common
            .info("rdf-file#OssFileSliceSplitter.getHeadSlice filePath=" + filePath);

        FileInfo fileInfo = storage.getFileInfo(filePath);
        if (!fileInfo.isExists()) {
            throw new RdfFileException("rdf-file#filePath=" + filePath + ", 不存在文件",
                RdfErrorEnum.NOT_EXSIT);
        }

        if (0 == fileInfo.getSize()) {
            RdfFileLogUtil.common.warn(
                "rdf-file#OssFileSliceSplitter.getHeadSlice filePath=" + filePath + ", size=0");
            return new FileSlice(filePath, FileDataTypeEnum.HEAD, 0, 0);
        } else {
            RdfFileLogUtil.common
                .debug("rdf-file#OssFileSliceSplitter.getHeadSlice fileSize=" + fileInfo.getSize());
        }

        if (fileConfig.getFileDataType() == FileDataTypeEnum.BODY
            || fileConfig.getFileDataType() == FileDataTypeEnum.TAIL) {
            return new FileSlice(fileConfig.getFilePath(), FileDataTypeEnum.HEAD, 0, 0);
        }

        FileMeta fileMeta = TemplateLoader.load(fileConfig.getTemplatePath(),
            fileConfig.getTemplateEncoding());
        if (!fileMeta.hasHead()) {
            throw new RdfFileException(
                "rdf-file#文件模板template=" + fileConfig.getTemplatePath() + ", 没有定义头",
                RdfErrorEnum.HEAD_NOT_DEFINED);
        }

        int headRowsAffected = ProtocolLoader.getRowsAfftected(fileConfig, FileDataTypeEnum.HEAD);
        if (headRowsAffected == 0) {
            RdfFileLogUtil.common
                .info("rdf-file#OssFileSliceSplitter.getHeadSlice file headRowsAfftected="
                      + headRowsAffected);
            return new FileSlice(filePath, FileDataTypeEnum.HEAD, 0, 0);
        } else {
            RdfFileLogUtil.common
                .debug("rdf-file#OssFileSliceSplitter.getHeadSlice file headRowsAfftected="
                       + headRowsAffected);
        }

        long bufferSize = OssUtil.OSS_READ_HEAD_BUFFER;
        long size = fileInfo.getSize();
        if (size < bufferSize) {
            bufferSize = size;
        }
        long start = 0;

        RdfFileLogUtil.common.debug(
            "rdf-file#OssFileSliceSplitter.getHeadSlice start=" + start + ", length=" + bufferSize);
        InputStream is = storage.getInputStream(filePath, start, bufferSize);
        byte[] bs = OssUtil.read(is, (int) bufferSize);
        RdfFileLogUtil.common
            .debug("rdf-file#OssFileSliceSplitter.getHeadSlice read bytes size=" + bs.length);
        RdfFileLogUtil.common.debug(
            "rdf-file#OssFileSliceSplitter.getHeadSlice read bytes content:\r\n" + new String(bs));

        int headLineCount = 0;
        boolean next = true;
        int count = 0;
        int idx = 0;
        while (next) {
            count++;
            switch (bs[idx++]) {
                case '\n':
                    headLineCount++;
                    if (headLineCount == headRowsAffected) {
                        next = false;
                        break;
                    }

                    if (idx == bufferSize) {
                        RdfFileLogUtil.common.debug(
                            "rdf-file#OssFileSliceSplitter.getHeadSlice (idx == bufferSize) start="
                                                    + bufferSize + ", left=" + bufferSize);
                        start = start + bufferSize;
                        long left = size - start;
                        RdfFileLogUtil.common.debug(
                            "rdf-file#OssFileSliceSplitter.getHeadSlice (idx == bufferSize) start="
                                                    + start + ", left=" + left);
                        if (left == 0) {
                            next = false;
                            break;
                        }

                        if (left < bufferSize) {
                            bufferSize = left;
                            RdfFileLogUtil.common.debug(
                                "rdf-file#OssFileSliceSplitter.getHeadSlice (idx == bufferSize) (left < bufferSize) start="
                                                        + start + ", length=" + bufferSize);
                        } else {
                            RdfFileLogUtil.common.debug(
                                "rdf-file#OssFileSliceSplitter.getHeadSlice (idx == bufferSize) start="
                                                        + start + ", length=" + bufferSize);
                        }

                        is = storage.getInputStream(filePath, start, bufferSize);
                        bs = OssUtil.read(is, (int) bufferSize);
                        RdfFileLogUtil.common.debug(
                            "rdf-file#OssFileSliceSplitter.getHeadSlice (idx == bufferSize) char count="
                                                    + count);
                        RdfFileLogUtil.common.debug(
                            "rdf-file#OssFileSliceSplitter.getHeadSlice (idx == bufferSize) read bytes content:\r\n"
                                                    + new String(bs));
                        idx = 0;
                    }
                    break;
                case '\r':
                    if (idx < bufferSize) {
                        if (bs[idx] != '\n') {
                            headLineCount++;
                        }
                    } else if (count == size) {
                        headLineCount++;
                        next = false;
                    } else {
                        RdfFileLogUtil.common.debug(
                            "rdf-file#OssFileSliceSplitter.getHeadSlice (idx == bufferSize) start="
                                                    + bufferSize + ", left=" + bufferSize);
                        start = start + bufferSize;
                        long left = size - start;
                        RdfFileLogUtil.common.debug(
                            "rdf-file#OssFileSliceSplitter.getHeadSlice (idx == bufferSize) start="
                                                    + start + ", left=" + left);
                        if (left == 0) {
                            next = false;
                            break;
                        }

                        if (left < bufferSize) {
                            bufferSize = left;
                            RdfFileLogUtil.common.debug(
                                "rdf-file#OssFileSliceSplitter.getHeadSlice (idx == bufferSize) (left < bufferSize) start="
                                                        + start + ", length=" + bufferSize);
                        } else {
                            RdfFileLogUtil.common.debug(
                                "rdf-file#OssFileSliceSplitter.getHeadSlice (idx == bufferSize) start="
                                                        + start + ", length=" + bufferSize);
                        }

                        is = storage.getInputStream(filePath, start, bufferSize);
                        bs = OssUtil.read(is, (int) bufferSize);
                        RdfFileLogUtil.common.debug(
                            "rdf-file#OssFileSliceSplitter.getHeadSlice (idx == bufferSize) char count="
                                                    + count);
                        RdfFileLogUtil.common.debug(
                            "rdf-file#OssFileSliceSplitter.getHeadSlice (idx == bufferSize) read bytes content:\r\n"
                                                    + new String(bs));

                        if (bs[0] != '\n') {
                            headLineCount++;
                        }

                        idx = 0;
                    }

                    if (headLineCount == headRowsAffected) {
                        next = false;
                    }
                    break;
                default:
                    if (idx == bufferSize) {
                        RdfFileLogUtil.common.debug(
                            "rdf-file#OssFileSliceSplitter.getHeadSlice (idx == bufferSize) start="
                                                    + bufferSize + ", left=" + bufferSize);
                        start = start + bufferSize;
                        long left = size - start;
                        RdfFileLogUtil.common.debug(
                            "rdf-file#OssFileSliceSplitter.getHeadSlice (idx == bufferSize) start="
                                                    + start + ", left=" + left);
                        if (left == 0) {
                            next = false;
                            break;
                        }

                        if (left < bufferSize) {
                            bufferSize = left;
                            RdfFileLogUtil.common.debug(
                                "rdf-file#OssFileSliceSplitter.getHeadSlice (idx == bufferSize) (left < bufferSize) start="
                                                        + start + ", length=" + bufferSize);
                        } else {
                            RdfFileLogUtil.common.debug(
                                "rdf-file#OssFileSliceSplitter.getHeadSlice (idx == bufferSize) start="
                                                        + start + ", length=" + bufferSize);
                        }

                        is = storage.getInputStream(filePath, start, bufferSize);
                        bs = OssUtil.read(is, (int) bufferSize);
                        RdfFileLogUtil.common.debug(
                            "rdf-file#OssFileSliceSplitter.getHeadSlice (idx == bufferSize) char count="
                                                    + count);
                        RdfFileLogUtil.common.debug(
                            "rdf-file#OssFileSliceSplitter.getHeadSlice (idx == bufferSize) read bytes content:\r\n"
                                                    + new String(bs));
                        idx = 0;
                    }
                    break;
            }

        }

        RdfFileLogUtil.common.info("rdf-file#OssFileSliceSplitter.getHeadSlice filePath=" + filePath
                                   + ", start=0, length=" + count);

        return new FileSlice(fileConfig.getFilePath(), FileDataTypeEnum.HEAD, 0, count);
    }

    /** 
     * @see com.alipay.rdf.file.interfaces.FileSplitter#getBodySlice(com.alipay.rdf.file.model.FileConfig)
     */
    @Override
    public FileSlice getBodySlice(FileConfig fileConfig) {
        String filePath = fileConfig.getFilePath();
        RdfFileLogUtil.common
            .info("rdf-file#OssFileSliceSplitter.getBodySlice filePath=" + filePath);
        FileInfo fileInfo = storage.getFileInfo(filePath);
        if (!fileInfo.isExists()) {
            throw new RdfFileException("rdf-file#filePath=" + filePath + ", 不存在文件",
                RdfErrorEnum.NOT_EXSIT);
        }

        long length = fileInfo.getSize();
        if (length == 0) {
            RdfFileLogUtil.common.info("rdf-file#OssFileSliceSplitter.getBodySlice filePath="
                                       + filePath + ", start=0, end=0");
            return new FileSlice(filePath, FileDataTypeEnum.BODY, 0, 0);
        } else if (RdfFileLogUtil.common.isDebug()) {
            RdfFileLogUtil.common
                .debug("rdf-file#OssFileSliceSplitter.getTailSlice fileSize=" + length);
        }

        long start = 0;
        long end = length;
        try {
            FileSlice headSlice = getHeadSlice(fileConfig);
            start = headSlice.getEnd();
        } catch (RdfFileException e) {
            if (!RdfErrorEnum.HEAD_NOT_DEFINED.equals(e.getErrorEnum())) {
                throw e;
            }
        }

        try {
            FileSlice tailSlice = getTailSlice(fileConfig);
            end = tailSlice.getStart();
        } catch (RdfFileException e) {
            if (!RdfErrorEnum.TAIL_NOT_DEFINED.equals(e.getErrorEnum())) {
                throw e;
            }
        }

        if (RdfFileLogUtil.common.isDebug()) {
            RdfFileLogUtil.common.debug(
                "rdf-file#OssFileSliceSplitter.getBodySlice start=" + start + ", end=" + end);
        }

        return new FileSlice(filePath, FileDataTypeEnum.BODY, start, end);
    }

    /** 
     * @see com.alipay.rdf.file.interfaces.FileSplitter#getBodySlices(com.alipay.rdf.file.model.FileConfig, int)
     */
    @Override
    public List<FileSlice> getBodySlices(FileConfig fileConfig, int sliceSize) {
        String filePath = fileConfig.getFilePath();
        RdfFileLogUtil.common.info("rdf-file#OssFileSliceSplitter.getBodySlices(filePath="
                                   + filePath + ", sliceSize=" + sliceSize + ")");
        FileInfo fileInfo = storage.getFileInfo(filePath);
        if (!fileInfo.isExists()) {
            throw new RdfFileException(
                "rdf-file#OssFileSliceSplitter.getBodySlices filePath=" + filePath + "), 文件不存在文件",
                RdfErrorEnum.NOT_EXSIT);
        }

        return split(fileInfo, getBodySlice(fileConfig), sliceSize);
    }

    @Override
    public FileSlice getTailSlice(FileConfig fileConfig) {
        String filePath = fileConfig.getFilePath();
        if (RdfFileLogUtil.common.isInfo()) {
            RdfFileLogUtil.common
                .info("rdf-file#OssFileSliceSplitter.getTailSlice filePath=" + filePath);
        }
        FileInfo fileInfo = storage.getFileInfo(filePath);
        if (!fileInfo.isExists()) {
            throw new RdfFileException("rdf-file#filePath=" + filePath + ", 不存在文件",
                RdfErrorEnum.NOT_EXSIT);
        }

        /**文件大小*/
        long length = fileInfo.getSize();
        if (length == 0) {
            if (RdfFileLogUtil.common.isInfo()) {
                RdfFileLogUtil.common.info(
                    "rdf-file#OssFileSliceSplitter.getTailSlice filePath=" + filePath + ", size=0");
            }
            return new FileSlice(filePath, FileDataTypeEnum.TAIL, 0, 0);
        } else if (RdfFileLogUtil.common.isDebug()) {
            RdfFileLogUtil.common
                .debug("rdf-file#OssFileSliceSplitter.getTailSlice file size=" + length);
        }

        if (fileConfig.getFileDataType() == FileDataTypeEnum.BODY
            || fileConfig.getFileDataType() == FileDataTypeEnum.HEAD) {
            return new FileSlice(fileConfig.getFilePath(), FileDataTypeEnum.TAIL, length, length);
        }

        FileMeta fileMeta = TemplateLoader.load(fileConfig.getTemplatePath(),
            fileConfig.getTemplateEncoding());
        if (!fileMeta.hasTail()) {
            throw new RdfFileException(
                "rdf-file#文件模板template=" + fileConfig.getTemplatePath() + ", 没有定义尾",
                RdfErrorEnum.TAIL_NOT_DEFINED);
        }

        int tailRowsAfftected = ProtocolLoader.getRowsAfftected(fileConfig, FileDataTypeEnum.TAIL);
        if (tailRowsAfftected == 0) {
            if (RdfFileLogUtil.common.isInfo()) {
                RdfFileLogUtil.common
                    .info("rdf-file#OssFileSliceSplitter.getTailSlice tailRowsAfftected="

                          + tailRowsAfftected);
            }
            return new FileSlice(filePath, FileDataTypeEnum.TAIL, length, length);
        } else if (RdfFileLogUtil.common.isDebug()) {
            RdfFileLogUtil.common
                .debug("rdf-file#OssFileSliceSplitter.getTailSlice tailRowsAfftected="
                       + tailRowsAfftected);
        }

        int bufferSize = OssUtil.OSS_READ_TAIL_BUFFER;
        long start = length - bufferSize;
        if (length < bufferSize) {
            bufferSize = (int) length;
            start = 0;
        }

        if (RdfFileLogUtil.common.isDebug()) {
            RdfFileLogUtil.common.debug("rdf-file#OssFileSliceSplitter.getTailSlice start=" + start
                                        + ", bufferSize=" + bufferSize);
        }

        InputStream is = storage.getInputStream(filePath, start, bufferSize);
        byte[] bs = OssUtil.read(is, bufferSize);

        if (RdfFileLogUtil.common.isDebug()) {
            RdfFileLogUtil.common
                .debug("rdf-file#OssFileSliceSplitter.getTailSlice read bytes size=" + bs.length);
            RdfFileLogUtil.common
                .debug("rdf-file#OssFileSliceSplitter.getTailSlice read bytes content:\r\n"
                       + new String(bs));
        }

        int idx = bufferSize - 1;
        int tailLineCount = 0;
        int count = 0;

        //文件尾没有以换行符结束
        if (bs[idx] != '\n' && bs[idx] != '\r') {
            tailLineCount++;

            if (RdfFileLogUtil.common.isDebug()) {
                RdfFileLogUtil.common.debug(
                    "rdf-file#OssFileSliceSplitter.getTailSlice file is not end with \\n or \\r");
            }
        }

        boolean next = true;
        while (next) {
            count++;

            byte b = bs[idx];
            switch (b) {
                case '\n':
                case '\r':
                    if (tailLineCount == tailRowsAfftected) {
                        count--;
                        next = false;
                        break;
                    }

                    int p = idx - 1;
                    if (p < 0) {
                        if (start > bufferSize) {
                            start -= bufferSize;
                            if (RdfFileLogUtil.common.isDebug()) {
                                RdfFileLogUtil.common
                                    .debug("rdf-file#OssFileSliceSplitter.getTailSlice char="
                                           + (b == '\n' ? "\\n" : "\\r") + " start=" + start
                                           + " > bufferSize=" + bufferSize + " and char count="
                                           + count);
                            }
                        } else if (start == 0) {
                            next = false;
                            RdfFileLogUtil.common
                                .debug("rdf-file#OssFileSliceSplitter.getTailSlice char="
                                       + (b == '\n' ? "\\n" : "\\r")
                                       + " start == 0  and char count=" + count);
                            break;
                        } else {
                            bufferSize = (int) start;
                            start = 0;
                            RdfFileLogUtil.common
                                .debug("rdf-file#OssFileSliceSplitter.getTailSlice char="
                                       + (b == '\n' ? "\\n" : "\\r") + " !(start=" + start
                                       + " > bufferSize=" + bufferSize + ")  and char count="
                                       + count);
                        }

                        if (bufferSize < 0) {
                            throw new RdfFileException("出错啦", RdfErrorEnum.UNKOWN);
                        }

                        idx = bufferSize;
                        is = storage.getInputStream(filePath, start, bufferSize);
                        bs = OssUtil.read(is, bufferSize);

                        p = idx - 1;
                    }

                    if (bs[p] != '\r' && bs[p] != '\n') {
                        tailLineCount++;
                    }

                    break;

                default:
                    p = idx - 1;
                    if (p < 0) {
                        if (start > bufferSize) {
                            start -= bufferSize;

                            if (RdfFileLogUtil.common.isDebug()) {
                                RdfFileLogUtil.common.debug(
                                    "rdf-file#OssFileSliceSplitter.getTailSlice default start="
                                                            + start + " > bufferSize=" + bufferSize
                                                            + "  and char count=" + count);
                            }
                        } else if (start == 0) {
                            next = false;

                            if (RdfFileLogUtil.common.isDebug()) {
                                RdfFileLogUtil.common.debug(
                                    "rdf-file#OssFileSliceSplitter.getTailSlice default start == 0  and char count="
                                                            + count);
                            }

                            break;
                        } else {
                            bufferSize = (int) start;
                            start = 0;

                            if (RdfFileLogUtil.common.isDebug()) {
                                RdfFileLogUtil.common.debug(
                                    "rdf-file#OssFileSliceSplitter.getTailSlice default !(start="
                                                            + start + " > bufferSize=" + bufferSize
                                                            + ")  and char count=" + count);
                            }
                        }

                        if (bufferSize < 0) {
                            throw new RdfFileException("出错啦", RdfErrorEnum.UNKOWN);
                        }

                        idx = bufferSize;
                        is = storage.getInputStream(filePath, start, bufferSize);
                        bs = OssUtil.read(is, bufferSize);
                    }

                    break;
            }

            idx--;
        }

        start = length - count;

        if (RdfFileLogUtil.common.isInfo()) {
            RdfFileLogUtil.common
                .info("rdf-file#OssFileSliceSplitter.getTailSlice filePath=" + filePath
                      + " ,char count=" + count + ", start=" + start + ", length=" + length);
        }
        return new FileSlice(filePath, FileDataTypeEnum.TAIL, start, length);
    }
}
