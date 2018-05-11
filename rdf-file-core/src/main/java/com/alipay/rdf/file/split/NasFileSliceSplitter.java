/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2016 All Rights Reserved.
 */
package com.alipay.rdf.file.split;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
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
import com.alipay.rdf.file.model.FileSlice;
import com.alipay.rdf.file.spi.RdfFileSplitterSpi;
import com.alipay.rdf.file.storage.FileNasStorage.BoundedInputStream;
import com.alipay.rdf.file.util.RdfFileLogUtil;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 * 
 * nas 存储实现
 * 
 * @author hongwei.quhw
 * @version $Id: NasFileSliceSplitter.java, v 0.1 2017年7月31日 下午4:07:39 hongwei.quhw Exp $
 */
public class NasFileSliceSplitter implements RdfFileSplitterSpi {

    @Override
    public void init(FileStorage fileStorage) {
    }

    /** 
     * @see com.alipay.rdf.file.FileSplitter#split(java.lang.String, int)
     */
    @Override
    public List<FileSlice> split(String path, int sliceSize) {
        File file = new File(path);
        if (!file.exists()) {
            throw new RdfFileException("rdf-file#NasFileSliceSplitter.split(path=" + path
                                       + ", sliceSize=" + sliceSize + ") 文件不存在",
                RdfErrorEnum.NOT_EXSIT);
        }

        return split(file, new FileSlice(path, FileDataTypeEnum.UNKOWN, 0, file.length()),
            sliceSize);
    }

    private List<FileSlice> split(File file, FileSlice fileSlice, int sliceSize) {
        if (fileSlice.getLength() < 0 || sliceSize <= 0 || fileSlice.getEnd() > file.length()) {
            throw new RdfFileException("rdf-file#NasFileSliceSplitter.split(fileSlice=" + fileSlice
                                       + ", sliceSize=" + sliceSize
                                       + ") fileSlice.getLength()必须大于等于零，sliceSize必须大于零， fileSlice.getEnd() 不能大于 file.length()",
                RdfErrorEnum.ILLEGAL_ARGUMENT);
        }

        List<FileSlice> fileSlices = new ArrayList<FileSlice>();
        String filePath = fileSlice.getFilePath();

        long length = file.length();
        if (length == 0) {
            RdfFileLogUtil.common
                .info("rdf-file#NasFileSliceSplitter.split path=" + filePath + ", start=0, end=0");
            FileSlice slice = new FileSlice(filePath, fileSlice.getFileDataType(), 0, 0);
            fileSlices.add(slice);
            return fileSlices;
        } else if (RdfFileLogUtil.common.isDebug()) {
            RdfFileLogUtil.common.debug("rdf-file#NasFileSliceSplitter.split fileSize=" + length);
        }

        //文件体大小小于分片大小
        if (fileSlice.getLength() <= sliceSize) {
            fileSlices.add(fileSlice);
            if (RdfFileLogUtil.common.isDebug()) {
                RdfFileLogUtil.common
                    .debug("rdf-file#NasFileSliceSplitter.split fileSlice.getLength()="
                           + fileSlice.getLength() + " <= sliceSize=" + sliceSize);
            }
            return fileSlices;
        }

        BoundedInputStream raf = null;
        try {
            long rangeStart = fileSlice.getStart();
            boolean eof = false;
            raf = new BoundedInputStream(file, rangeStart, fileSlice.getLength());

            while (!eof) {
                raf.skip(sliceSize);
                boolean eol = false;
                while (!eol && !eof) {
                    switch (raf.read()) {
                        case -1:
                            eof = true;
                            break;
                        case '\n':
                            eol = true;
                            break;
                        case '\r':
                            eol = true;
                            long cur = raf.getFilePointer();
                            if ((raf.read()) != '\n') {
                                raf.seek(cur);
                            }
                            break;
                        default:
                            break;
                    }
                }
                if (rangeStart != raf.getFilePointer()) {
                    FileSlice slice = new FileSlice(filePath, fileSlice.getFileDataType(),
                        rangeStart, raf.getFilePointer());
                    fileSlices.add(slice);
                    if (RdfFileLogUtil.common.isDebug()) {
                        RdfFileLogUtil.common
                            .debug("rdf-file#NasFileSliceSplitter.split add slice slice=" + slice);
                    }
                    rangeStart = raf.getFilePointer();
                }
            }
        } catch (FileNotFoundException e) {
            throw new RdfFileException(
                "rdf-file#NasFileSliceSplitter.split file=" + file.getAbsolutePath() + " 文件不存在", e,
                RdfErrorEnum.NOT_EXSIT);
        } catch (IOException e) {
            throw new RdfFileException("rdf-file#NasFileSliceSplitter.split io 异常", e,
                RdfErrorEnum.NOT_EXSIT);
        } finally {
            if (raf != null) {
                raf.close();
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
        RdfFileLogUtil.common.info(
            "rdf-file#OssFileSliceSplitter.getHeadSlice(fileConfig.filePath=" + filePath + ")");

        File file = new File(filePath);
        if (!file.exists()) {
            throw new RdfFileException("rdf-file#filePath=" + filePath + ", 不存在文件",
                RdfErrorEnum.NOT_EXSIT);
        }

        long length = file.length();
        if (0 == length) {
            RdfFileLogUtil.common.warn(
                "rdf-file#NasFileSliceSplitter.getHeadSlice filePath=" + filePath + ", size=0");
            return new FileSlice(filePath, FileDataTypeEnum.HEAD, 0, 0);
        } else {
            RdfFileLogUtil.common
                .debug("rdf-file#NasFileSliceSplitter.getHeadSlice fileSize=" + length);
        }

        if (!fileConfig.isPartial() && (fileConfig.getFileDataType() == FileDataTypeEnum.BODY
                                        || fileConfig.getFileDataType() == FileDataTypeEnum.TAIL)) {
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

        RandomAccessFile raf = null;
        int headLineCount = 0;
        int count = 0;
        boolean next = true;
        try {
            raf = new RandomAccessFile(file, "r");
            raf.seek(0);

            while (next) {
                count++;
                int b = raf.read();
                switch (b) {
                    case '\n':
                    case '\r':
                        long cur = raf.getFilePointer();
                        int pre = raf.read();
                        if (pre == -1 || pre != '\n') {
                            headLineCount++;
                            if (RdfFileLogUtil.common.isDebug()) {
                                RdfFileLogUtil.common.debug(
                                    "rdf-file#NasFileSliceSplitter.getHeadSlice headLineCount++, linebreak="
                                                            + (b == '\r' ? "\\r" : "\\n"));
                            }
                        }

                        if (headLineCount == headRowsAffected) {
                            next = false;
                            break;
                        }

                        raf.seek(cur);

                        break;
                    case -1:
                        count--;
                        next = false;
                        if (RdfFileLogUtil.common.isWarn()) {
                            RdfFileLogUtil.common
                                .warn("rdf-file#NasFileSliceSplitter.getHeadSlice char count="
                                      + count + ", read() == -1");
                        }
                        break;
                    default:
                        break;
                }
            }
        } catch (FileNotFoundException e) {
            throw new RdfFileException("rdf-file#NasFileSliceSplitter.getHeadSlice file="
                                       + file.getAbsolutePath() + " 文件不存在",
                e, RdfErrorEnum.NOT_EXSIT);
        } catch (IOException e) {
            throw new RdfFileException("rdf-file#NasFileSliceSplitter.getHeadSlice io 异常", e,
                RdfErrorEnum.NOT_EXSIT);
        } finally {
            if (null != raf) {
                try {
                    raf.close();
                } catch (IOException e) {
                    if (RdfFileLogUtil.common.isWarn()) {
                        RdfFileLogUtil.common
                            .warn("rdf-file#NasFileSliceSplitter.getHeadSlice close 异常", e);
                    }
                }
            }
        }

        if (RdfFileLogUtil.common.isInfo()) {
            RdfFileLogUtil.common.info("rdf-file#NasFileSliceSplitter.getHeadSlice filePath="
                                       + filePath + " ,char count=" + count);
        }

        return new FileSlice(filePath, FileDataTypeEnum.HEAD, 0, count);
    }

    /** 
     * @see com.alipay.rdf.file.interfaces.FileSplitter#getBodySlice(com.alipay.rdf.file.model.FileConfig)
     */
    @Override
    public FileSlice getBodySlice(FileConfig fileConfig) {
        String filePath = fileConfig.getFilePath();
        RdfFileLogUtil.common
            .info("rdf-file#NasFileSliceSplitter.getBodySlice filePath=" + filePath);
        File file = new File(filePath);
        if (!file.exists()) {
            throw new RdfFileException(
                "rdf-file#NasFileSliceSplitter.getBodySlice filePath=" + filePath + ", 不存在文件",
                RdfErrorEnum.NOT_EXSIT);
        }

        long length = file.length();
        if (length == 0) {
            RdfFileLogUtil.common.info("rdf-file#NasFileSliceSplitter.getBodySlice filePath="
                                       + filePath + ", start=0, end=0");
            return new FileSlice(filePath, FileDataTypeEnum.BODY, 0, 0);
        } else if (RdfFileLogUtil.common.isDebug()) {
            RdfFileLogUtil.common
                .debug("rdf-file#NasFileSliceSplitter.getBodySlice fileSize=" + length);
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
                "rdf-file#NasFileSliceSplitter.getBodySlice start=" + start + ", end=" + end);
        }

        return new FileSlice(filePath, FileDataTypeEnum.BODY, start, end);
    }

    /** 
     * @see com.alipay.rdf.file.interfaces.FileSplitter#getBodySlices(com.alipay.rdf.file.model.FileConfig, int)
     */
    @Override
    public List<FileSlice> getBodySlices(FileConfig fileConfig, int sliceSize) {
        String filePath = fileConfig.getFilePath();
        RdfFileLogUtil.common.info("rdf-file#NasFileSliceSplitter.getBodySlices(filePath="
                                   + filePath + ", sliceSize=" + sliceSize + ")");

        File file = new File(filePath);
        if (!file.exists()) {
            throw new RdfFileException(
                "rdf-file#NasFileSliceSplitter.getBodySlice filePath=" + filePath + ", 不存在文件",
                RdfErrorEnum.NOT_EXSIT);
        }

        FileSlice slice = null;
        if (fileConfig.isPartial()) {
            slice = new FileSlice(filePath, fileConfig.getFileDataType(), fileConfig.getOffset(),
                fileConfig.getOffset() + fileConfig.getLength());
        } else {
            slice = getBodySlice(fileConfig);
        }

        return split(file, slice, sliceSize);
    }

    /** 
     * @see com.alipay.rdf.file.interfaces.FileSplitter#getTailSlice(com.alipay.rdf.file.model.FileConfig)
     */
    @Override
    public FileSlice getTailSlice(FileConfig fileConfig) {
        String filePath = fileConfig.getFilePath();
        if (RdfFileLogUtil.common.isInfo()) {
            RdfFileLogUtil.common
                .info("rdf-file#NasFileSliceSplitter.getTailSlice filePath=" + filePath);
        }

        File file = new File(filePath);
        if (!file.exists()) {
            throw new RdfFileException(
                "rdf-file#NasFileSliceSplitter.getTailSlice  path=" + filePath + " 不存在文件",
                RdfErrorEnum.NOT_EXSIT);
        }

        long length = file.length();
        //文件内容为空
        if (length == 0) {
            if (RdfFileLogUtil.common.isInfo()) {
                RdfFileLogUtil.common.info(
                    "rdf-file#NasFileSliceSplitter.getTailSlice filePath=" + filePath + ", size=0");
            }
            return new FileSlice(filePath, FileDataTypeEnum.TAIL, 0, 0);
        } else if (RdfFileLogUtil.common.isDebug()) {
            RdfFileLogUtil.common
                .debug("rdf-file#NasFileSliceSplitter.getTailSlice file size=" + length);
        }

        if (!fileConfig.isPartial() && (fileConfig.getFileDataType() == FileDataTypeEnum.BODY
                                        || fileConfig.getFileDataType() == FileDataTypeEnum.HEAD)) {
            return new FileSlice(fileConfig.getFilePath(), FileDataTypeEnum.TAIL, length, length);
        }

        FileMeta fileMeta = TemplateLoader.load(fileConfig.getTemplatePath(),
            fileConfig.getTemplateEncoding());
        if (!fileMeta.hasTail()) {
            throw new RdfFileException(
                "rdf-file#文件模板template=" + fileConfig.getTemplatePath() + ", 没有定义尾",
                RdfErrorEnum.TAIL_NOT_DEFINED);
        }

        int tailRowsAffected = ProtocolLoader.getRowsAfftected(fileConfig, FileDataTypeEnum.TAIL);
        if (tailRowsAffected == 0) {
            if (RdfFileLogUtil.common.isInfo()) {
                RdfFileLogUtil.common
                    .info("rdf-file#NasFileSliceSplitter.getTailSlice tailRowsAfftected="

                          + tailRowsAffected);
            }
            return new FileSlice(filePath, FileDataTypeEnum.TAIL, length, length);
        } else if (RdfFileLogUtil.common.isDebug()) {
            RdfFileLogUtil.common.debug(
                "rdf-file#NasFileSliceSplitter.getTailSlice tailRowsAfftected=" + tailRowsAffected);
        }

        RandomAccessFile raf = null;
        int tailLineCount = 0;
        int count = 0;
        long p = length - 1;
        boolean next = true;

        try {
            raf = new RandomAccessFile(file, "r");

            //文件尾没有以换行符结束
            raf.seek(p);
            int last = raf.read();
            if (last != '\n' && last != '\r') {
                tailLineCount++;
                if (RdfFileLogUtil.common.isDebug()) {
                    RdfFileLogUtil.common.debug(
                        "rdf-file#OssFileSliceSplitter.getTailSlice file is not end with \\n or \\r");
                }
            }

            while (next) {
                count++;
                raf.seek(p--);
                int b = raf.read();
                switch (b) {
                    case '\n':
                    case '\r':
                        if (tailLineCount == tailRowsAffected) {
                            count--;
                            next = false;
                            break;
                        }

                        if (p < 0) {
                            next = false;
                            if (RdfFileLogUtil.common.isDebug()) {
                                RdfFileLogUtil.common.debug(
                                    "rdf-file#NasFileSliceSplitter.getTailSlice p < 0, linebreak="
                                                            + (b == '\r' ? "\\r" : "\\n"));
                            }
                            break;
                        }

                        raf.seek(p);
                        int pre = raf.read();
                        if (pre != '\r' && pre != '\n') {
                            tailLineCount++;
                            if (RdfFileLogUtil.common.isDebug()) {
                                RdfFileLogUtil.common.debug(
                                    "rdf-file#NasFileSliceSplitter.getTailSlice tailLineCount++, linebreak="
                                                            + (b == '\r' ? "\\r" : "\\n"));
                            }
                        }

                        break;
                    case -1:
                        count--;
                        next = false;
                        if (RdfFileLogUtil.common.isWarn()) {
                            RdfFileLogUtil.common
                                .warn("rdf-file#NasFileSliceSplitter.getTailSlice filePath="
                                      + filePath + " ,char count=" + count + ", read() == -1");
                        }
                        break;
                    default:
                        break;
                }

                if (p < 0) {
                    next = false;
                    if (RdfFileLogUtil.common.isDebug()) {
                        RdfFileLogUtil.common
                            .debug("rdf-file#NasFileSliceSplitter.getTailSlice normal p < 0");
                    }
                }
            }
        } catch (FileNotFoundException e) {
            throw new RdfFileException("rdf-file#NasFileSliceSplitter.getTailSlice file="
                                       + file.getAbsolutePath() + " 文件不存在",
                e, RdfErrorEnum.NOT_EXSIT);
        } catch (IOException e) {
            throw new RdfFileException("rdf-file#NasFileSliceSplitter.getTailSlice io 异常", e,
                RdfErrorEnum.NOT_EXSIT);
        } finally {
            if (null != raf) {
                try {
                    raf.close();
                } catch (IOException e) {
                    if (RdfFileLogUtil.common.isWarn()) {
                        RdfFileLogUtil.common
                            .warn("rdf-file#NasFileSliceSplitter.getTailSlice close 异常", e);
                    }
                }
            }
        }

        long start = length - count;
        if (RdfFileLogUtil.common.isInfo()) {
            RdfFileLogUtil.common
                .info("rdf-file#NasFileSliceSplitter.getTailSlice filePath=" + filePath
                      + " ,char count=" + count + ", start=" + start + ", length=" + length);
        }

        return new FileSlice(filePath, FileDataTypeEnum.TAIL, start, length);
    }
}
