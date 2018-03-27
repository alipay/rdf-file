package com.alipay.rdf.file.storage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import com.alipay.rdf.file.exception.RdfErrorEnum;
import com.alipay.rdf.file.exception.RdfFileException;
import com.alipay.rdf.file.interfaces.FileFactory;
import com.alipay.rdf.file.interfaces.FileSplitter;
import com.alipay.rdf.file.model.FileConfig;
import com.alipay.rdf.file.model.FileInfo;
import com.alipay.rdf.file.model.FileSlice;
import com.alipay.rdf.file.model.StorageConfig;
import com.alipay.rdf.file.spi.RdfFileStorageSpi;
import com.alipay.rdf.file.util.RdfFileUtil;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 *
 * @author hongwei.quhw
 * @version $Id: FileNasStorage.java, v 0.1 2018年3月12日 下午4:27:11 hongwei.quhw Exp $
 */
public class FileNasStorage implements RdfFileStorageSpi {
    @Override
    public void init(StorageConfig t) {
    }

    /** 
     * @see com.alipay.rdf.file.storage.FileInnterStorage#getInputStream(java.lang.String)
     */
    @Override
    public InputStream getInputStream(String filename) {
        try {
            return new FileInputStream(new File(filename));
        } catch (FileNotFoundException e) {
            throw new RdfFileException("rdf-file#FileNasStorage(file=" + filename + ") 文件不存在。", e,
                RdfErrorEnum.NOT_EXSIT);
        }
    }

    /** 
     * @see com.alipay.rdf.file.storage.FileInnterStorage#getInputStream(java.lang.String, long, long)
     */
    @Override
    public InputStream getInputStream(String filename, long start, long length) {
        return new BoundedInputStream(new File(filename), start, length);
    }

    /** 
     * @see com.alipay.rdf.file.storage.FileInnterStorage#getTailInputStream(com.alipay.rdf.file.model.FileConfig)
     */
    @Override
    public InputStream getTailInputStream(FileConfig fileConfig) {
        FileSplitter fileSplitter = FileFactory.createSplitter(fileConfig.getStorageConfig());
        FileSlice fileSlice = fileSplitter.getTailSlice(fileConfig);
        if (null == fileSlice) {
            return null;
        }
        return getInputStream(fileConfig.getFilePath(), fileSlice.getStart(),
            fileSlice.getLength());
    }

    /** 
     * @see com.alipay.rdf.file.interfaces.FileStorage#createNewFile(java.lang.String)
     */
    @Override
    public void createNewFile(String filePath) {
        File file = new File(filePath);
        if (file.exists() && !file.delete()) {
            throw new RdfFileException("创建NAS文件时文件已存在并且删除失败！", RdfErrorEnum.IO_ERROR);
        }
        File parent = file.getParentFile();
        if (!parent.exists() && !parent.mkdirs() && !parent.exists()) {
            throw new RdfFileException("创建NAS文件目录失败！", RdfErrorEnum.IO_ERROR);
        }
        try {
            if (!file.createNewFile()) {
                throw new RdfFileException("创建NAS文件失败！", RdfErrorEnum.IO_ERROR);
            }
        } catch (IOException e) {
            throw new RdfFileException("创建NAS文件失败！", e, RdfErrorEnum.IO_ERROR);
        }
    }

    /** 
     * @see com.alipay.rdf.file.interfaces.FileStorage#getFileInfo(java.lang.String)
     */
    @Override
    public FileInfo getFileInfo(String filePath) {
        File file = new File(filePath);
        FileInfo fileInfo = new FileInfo();
        fileInfo.setFileName(file.getName());
        fileInfo.setExists(file.exists());
        fileInfo.setSize(file.length());
        fileInfo.setLastModifiedDate(new Date(file.lastModified()));
        return fileInfo;
    }

    /** 
     * @see com.alipay.rdf.file.interfaces.FileStorage#listFiles(java.lang.String, java.lang.String[])
     */
    @Override
    public List<String> listFiles(String folderName, String[] regexs) {
        return listFilesWithRegexs(folderName, false, regexs);
    }

    /** 
     * @see com.alipay.rdf.file.interfaces.FileStorage#listFiles(java.lang.String, com.alipay.rdf.file.interfaces.FileStorage.FilePathFilter[])
     */
    @Override
    public List<String> listFiles(String folderName, FilePathFilter... fileFilters) {
        return listFileWithFilter(folderName, false, fileFilters);
    }

    /** 
     * @see com.alipay.rdf.file.interfaces.FileStorage#listAllFiles(java.lang.String, java.lang.String[])
     */
    @Override
    public List<String> listAllFiles(String folderName, String[] regexs) {
        return listFilesWithRegexs(folderName, true, regexs);
    }

    /** 
     * @see com.alipay.rdf.file.interfaces.FileStorage#listAllFiles(java.lang.String, com.alipay.rdf.file.interfaces.FileStorage.FilePathFilter[])
     */
    @Override
    public List<String> listAllFiles(String folderName, FilePathFilter... fileFilters) {
        return listFileWithFilter(folderName, true, fileFilters);
    }

    /** 
     * @see com.alipay.rdf.file.interfaces.FileStorage#download(java.lang.String, java.lang.String)
     */
    @Override
    public void download(String srcFile, String toFile) {
        copyFile(srcFile, toFile);
    }

    /** 
     * @see com.alipay.rdf.file.interfaces.FileStorage#upload(java.lang.String, java.lang.String, boolean)
     */
    @Override
    public void upload(String srcFile, String toFile, boolean override) {
        if (!override) {
            if (new File(toFile).exists()) {
                return;
            }
        }

        copyFile(srcFile, toFile);
    }

    /** 
     * @see com.alipay.rdf.file.interfaces.FileStorage#rename(java.lang.String, java.lang.String)
     */
    @Override
    public void rename(String srcFile, String toName) {
        File fromFile = new File(srcFile);
        File toFile = new File(toName);
        //确定源文件/目录的存在
        if (!fromFile.exists()) {
            return;
        }

        boolean success = false;

        RuntimeException e = null;

        // 多次尝试，防止文件已经存在
        int times = 5;
        while (times-- >= 0) {
            //先保证目标文件父目录存在
            File parent = toFile.getParentFile();
            parent.mkdirs();
            if (!parent.exists()) {
                e = new RdfFileException(
                    "rdf-file#目标目录或文件的父目录不存在且无法创建，目标文件：" + toFile.getAbsolutePath(),
                    RdfErrorEnum.IO_ERROR);
                continue;
            }

            //再保证目标目录、文件不存在
            if (toFile.exists()) {
                recursiveDelete(toFile);
            }
            if (toFile.exists()) {
                //删除后仍然存在，证明无法删除，重试
                e = new RdfFileException("rdf-file#目标目录或文件存在且无法清除：" + toFile.getAbsolutePath(),
                    RdfErrorEnum.IO_ERROR);
                continue;
            }

            // 将文件移动到目标文件
            if (fromFile.renameTo(toFile)) {
                success = true;
                break;
            }
        }
        if (success) {
            //清除历史异常
            e = null;
        } else if (e != null) {
            throw e;
        } else {
            throw new RdfFileException("rdf-file#移动目录失败,源文件：" + fromFile.getAbsolutePath(),
                RdfErrorEnum.IO_ERROR);
        }
    }

    /**
     * 迭代删除文件夹,如果删不了则抛出RuntimeException
     *
     * @param file
     */
    private void recursiveDelete(File file) {
        if (file == null || !file.exists()) {
            return;
        }
        File[] subfiles = file.listFiles();
        if (subfiles != null) {
            for (File each : subfiles) {
                recursiveDelete(each);
            }
        }
        file.delete();
        if (file.exists()) {
            throw new RdfFileException("rdf-file#无法删除文件/文件夹" + file.getAbsolutePath(),
                RdfErrorEnum.IO_ERROR);
        }
    }

    /** 
     * @see com.alipay.rdf.file.interfaces.FileStorage#copy(java.lang.String, java.lang.String)
     */
    @Override
    public void copy(String srcFile, String toFile) {
        copyFile(srcFile, toFile);
    }

    /** 
     * @see com.alipay.rdf.file.interfaces.FileStorage#delete(java.lang.String)
     */
    @Override
    public void delete(String fullPath) {
        recursiveDelete(new File(fullPath));
    }

    /**
     * 将文件或文件夹复制到目标位置
     * @param srcPath
     * @param destPath
     */
    private static void copyFile(String srcPath, String destPath) {
        File srcFile = new File(srcPath);
        File destFile = new File(destPath);

        if (!srcFile.exists()) {
            return;
        }

        if (RdfFileUtil.equals(srcPath, destPath)) {
            // 复制到自身,直接返回
            return;
        }

        if (srcFile.isDirectory()) {
            // 两者都是目录, 首先建立目录
            destFile.mkdirs();
            RdfFileUtil.copyDirectoryToDirectory(srcFile, destFile);
        } else {
            RdfFileUtil.copyFile(srcFile, destFile);
        }
    }

    /**
     * helper method for adding regexs check when listing files
     * 
     * @param folderName
     * @param all
     * @param regexs
     * @return
     */
    private List<String> listFilesWithRegexs(String folderName, boolean all, String[] regexs) {
        List<String> paths = listFilesHandler(folderName, all);
        if (regexs == null || regexs.length == 0) {
            return paths;
        }
        List<String> result = new ArrayList<String>();
        for (String filePath : paths) {
            String name = new File(filePath).getName();
            for (String regex : regexs) {
                Pattern pattern = Pattern.compile(regex);
                if (pattern.matcher(name).matches()) {
                    result.add(filePath);
                    break;
                }
            }
        }
        return result;
    }

    /**
     * helper method for adding filter check when listing files
     * 
     * @param folderName
     * @param all
     * @param fileFilters
     * @return
     */
    private List<String> listFileWithFilter(String folderName, boolean all,
                                            FilePathFilter... fileFilters) {
        List<String> paths = listFilesHandler(folderName, all);

        if (fileFilters == null || fileFilters.length == 0) {
            return paths;
        }
        List<String> result = new ArrayList<String>();
        for (String path : paths) {
            for (FilePathFilter filter : fileFilters) {
                if (filter.accept(path)) {
                    result.add(path);
                    break;
                }
            }
        }
        return result;
    }

    /**
     * handler method for listing files
     * 
     * @param folderName
     * @param all
     * @return
     */
    private List<String> listFilesHandler(String folderName, boolean all) {
        File[] files = new File(folderName).listFiles();
        List<String> paths = new ArrayList<String>(null == files ? 0 : files.length);
        if (null == files) {
            return paths;
        }
        for (File file : files) {
            if (all) {
                if (file.isDirectory()) {
                    paths.addAll(listFilesHandler(file.getAbsolutePath(), all));
                } else {
                    paths.add(file.getAbsolutePath());
                }
            } else {
                paths.add(file.getAbsolutePath());
            }
        }
        return paths;
    }

    /**
     * 使用RandomAccessFile来访问文件
     * 
     * @author hongwei.quhw
     * @version $Id: FileNasStorage.java, v 0.1 2017年4月7日 下午3:18:29 hongwei.quhw Exp $
     */
    public static class BoundedInputStream extends InputStream {
        private final RandomAccessFile randomAccessFile;
        private long                   remaining;

        /**
         * 只访问文件种指定范围之内的数据
         * @param file      文件
         * @param offset    起始的位置
         * @param length    数据的长度
         * @throws IOException
         */
        public BoundedInputStream(File file, long offset, long length) {
            this.remaining = length;
            // 打开文件, 跳转到指定的位置
            try {
                this.randomAccessFile = new RandomAccessFile(file, "r");
                this.randomAccessFile.seek(offset);
            } catch (FileNotFoundException e) {
                throw new RdfFileException(
                    "rdf-file#BoundedInputStream file=" + file.getAbsolutePath() + " 文件不存在", e,
                    RdfErrorEnum.NOT_EXSIT);
            } catch (IOException e) {
                throw new RdfFileException(
                    "rdf-file#BoundedInputStream file=" + file.getAbsolutePath() + " io 异常", e,
                    RdfErrorEnum.IO_ERROR);
            }
        }

        /** 
         * @see java.io.InputStream#read()
         */
        @Override
        public int read() {
            if (this.remaining <= 0) {
                return -1;
            }

            this.remaining--;
            try {
                return this.randomAccessFile.readByte();
            } catch (IOException e) {
                throw new RdfFileException("rdf-file#BoundedInputStream read() io 异常", e,
                    RdfErrorEnum.IO_ERROR);
            }
        }

        /** 
         * @see java.io.InputStream#read(byte[], int, int)
         */
        @Override
        public int read(byte[] b, int off, int len) {
            if (this.remaining <= 0) {
                return -1;
            }

            if (len <= 0) {
                return 0;
            }

            if (len > this.remaining) {
                len = (int) this.remaining;
            }
            int ret;
            try {
                ret = this.randomAccessFile.read(b, off, len);
            } catch (IOException e) {
                throw new RdfFileException("rdf-file#BoundedInputStream read() io 异常", e,
                    RdfErrorEnum.IO_ERROR);
            }

            if (ret > 0) {
                this.remaining -= ret;
            }
            return ret;
        }

        /** 
         * @see java.io.InputStream#close()
         */
        @Override
        public void close() {
            try {
                this.randomAccessFile.close();
            } catch (IOException e) {
                throw new RdfFileException("rdf-file#BoundedInputStream close() io 异常", e,
                    RdfErrorEnum.IO_ERROR);
            }
        }

        /** 
         * @see java.io.InputStream#available()
         */
        @Override
        public int available() {
            return (int) this.remaining;
        }

        @Override
        public long skip(long size) {
            try {
                long skip = size;
                if (remaining < size) {
                    skip = remaining;
                }

                int ret = randomAccessFile.skipBytes((int) skip);

                this.remaining -= ret;

            } catch (IOException e) {
                throw new RdfFileException("rdf-file#BoundedInputStream randomAccessFile.skipBytes",
                    e, RdfErrorEnum.IO_ERROR);
            }
            return 0;
        }

        public long getFilePointer() throws IOException {
            return randomAccessFile.getFilePointer();
        }

        public void seek(long pos) throws IOException {
            long current = randomAccessFile.getFilePointer();
            this.remaining += (current - pos);
            randomAccessFile.seek(pos);
        }
    }
}
