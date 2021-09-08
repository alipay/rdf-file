package com.alipay.rdf.file.util;

import com.alipay.rdf.file.exception.RdfErrorEnum;
import com.alipay.rdf.file.exception.RdfFileException;
import com.alipay.rdf.file.loader.ProtocolLoader;
import com.alipay.rdf.file.loader.TemplateLoader;
import com.alipay.rdf.file.meta.FileMeta;
import com.alipay.rdf.file.model.FileConfig;
import com.alipay.rdf.file.model.FileDefaultConfig;

import java.io.*;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 * <p>
 * 文件组件工具类
 *
 * @author hongwei.quhw
 * @version $Id: RdfUtil.java, v 0.1 2017年8月8日 上午11:29:42 hongwei.quhw Exp $
 */
public class RdfFileUtil {
    public static final String EMPTY = "";

    private static final int BUF_SIZE = 8192;

    private static final String AMPERSAND = "&";

    private static final String EQUALS = "=";

    public static final String QUESTION = "?";

    public static String trimNotNull(String text) {
        if (null == text) {
            return null;
        }

        return text.trim();
    }

    public static void assertNotBlank(String text) {
        if (null == text || 0 == text.trim().length()) {
            throw new RdfFileException("字符串不能为空", RdfErrorEnum.ILLEGAL_ARGUMENT);
        }
    }

    public static void assertNotBlank(String text, String errorMsg) {
        if (null == text || 0 == text.trim().length()) {
            throw new RdfFileException(errorMsg, RdfErrorEnum.ILLEGAL_ARGUMENT);
        }
    }

    public static void assertNotBlank(String text, String errorMsg, RdfErrorEnum errorCode) {
        if (null == text || 0 == text.trim().length()) {
            throw new RdfFileException(errorMsg, errorCode);
        }
    }

    public static boolean isBlank(String text) {
        if (null == text || 0 == text.trim().length()) {
            return true;
        }

        return false;
    }

    public static boolean isNotBlank(String text) {
        return !isBlank(text);
    }

    public static void assertNull(Object obj, String errorMsg) {
        if (null != obj) {
            throw new RdfFileException(errorMsg, RdfErrorEnum.ILLEGAL_ARGUMENT);
        }
    }

    public static void assertNull(Object obj, String errorMsg, RdfErrorEnum errorCode) {
        if (null != obj) {
            throw new RdfFileException(errorMsg, errorCode);
        }
    }

    public static void assertNotNull(Object obj, String errorMsg) {
        if (null == obj) {
            throw new RdfFileException(errorMsg, RdfErrorEnum.ILLEGAL_ARGUMENT);
        }
    }

    public static void assertNotNull(Object obj, String errorMsg, RdfErrorEnum errorCode) {
        if (null == obj) {
            throw new RdfFileException(errorMsg, errorCode);
        }
    }

    public static void assertEquals(String str1, String str2) {
        if (str1 == null && str2 != null) {
            throw new RdfFileException("rdf-file#字符串不相等 str1 == null, str2 == " + str2,
                    RdfErrorEnum.ILLEGAL_ARGUMENT);
        }

        if (!str1.equals(str2)) {
            throw new RdfFileException("rdf-file#字符串不相等 str1 == " + str1 + ", str2 == " + str2,
                    RdfErrorEnum.ILLEGAL_ARGUMENT);
        }
    }

    public static void assertTrue(boolean bol, String str2, RdfErrorEnum errorcode) {
        if (!bol) {
            throw new RdfFileException(str2, errorcode);
        }
    }

    public static String assertTrimNotBlank(String text) {
        if (null == text || 0 == text.trim().length()) {
            throw new RdfFileException("rdf-file#字符串不能为空", RdfErrorEnum.ILLEGAL_ARGUMENT);
        }

        return text.trim();
    }

    public static String assertTrimNotBlank(String text, String errorMsg) {
        if (null == text || 0 == text.trim().length()) {
            throw new RdfFileException(errorMsg, RdfErrorEnum.ILLEGAL_ARGUMENT);
        }

        return text.trim();
    }

    public static String assertTrimNotBlank(String text, String errorMsg, RdfErrorEnum errorCode) {
        if (null == text || 0 == text.trim().length()) {
            throw new RdfFileException(errorMsg, errorCode);
        }

        return text.trim();
    }

    public static boolean equals(String str1, String str2) {
        if (null == str1) {
            return str2 == null;
        }

        return str1.equals(str2);
    }

    public static ClassLoader getDefaultClassLoader() {
        ClassLoader cl = null;
        try {
            cl = Thread.currentThread().getContextClassLoader();
        } catch (Throwable ex) {
            // Cannot access thread context ClassLoader - falling back to system class loader...
        }
        if (cl == null) {
            // No thread context class loader -> use class loader of this class.
            cl = RdfFileUtil.class.getClassLoader();
        }
        return cl;
    }

    public static String safeReadFully(InputStream is, String encoding) {
        InputStreamReader rdr = null;
        try {
            rdr = new InputStreamReader(is, encoding);

            final char[] buffer = new char[BUF_SIZE];
            int bufferLength = 0;
            StringBuffer textBuffer = null;
            while (bufferLength != -1) {
                bufferLength = rdr.read(buffer);
                if (bufferLength > 0) {
                    textBuffer = (textBuffer == null) ? new StringBuffer() : textBuffer;
                    textBuffer.append(new String(buffer, 0, bufferLength));
                }
            }
            return (textBuffer == null) ? "" : textBuffer.toString();
        } catch (UnsupportedEncodingException e) {
            throw new RdfFileException(e, RdfErrorEnum.UNKOWN);
        } catch (IOException e) {
            throw new RdfFileException(e, RdfErrorEnum.UNKOWN);
        } finally {
            try {
                if (null != rdr) {
                    rdr.close();
                }
            } catch (IOException e) {
                if (RdfFileLogUtil.common.isWarn()) {
                    RdfFileLogUtil.common.warn("TemplateLoader reader.close 错误", e);
                }
            }
        }
    }

    public static Object newInstance(String clazz) {
        try {
            Class<?> cl = getDefaultClassLoader().loadClass(clazz);
            return cl.newInstance();
        } catch (ClassNotFoundException e) {
            throw new RdfFileException("类" + clazz + "不存在", e, RdfErrorEnum.INSTANTIATION_ERROR);
        } catch (InstantiationException e) {
            throw new RdfFileException("类" + clazz + "实例化对象出错", e,
                    RdfErrorEnum.INSTANTIATION_ERROR);
        } catch (IllegalAccessException e) {
            throw new RdfFileException("类" + clazz + "实例化对象出错", e,
                    RdfErrorEnum.INSTANTIATION_ERROR);
        }
    }

    public static String alignRight(String str, int size, char padChar) {
        return alignRight(str, size, padChar, false);
    }

    /**
     * 基金格式中对数值补位
     *
     * @param str
     * @param size
     * @param padChar
     * @return
     */
    public static String alignRight(String str, int size, char padChar, boolean negate) {
        int alignSize = size;

        if (negate) {
            alignSize--;
        }

        String val = alignRight(str, alignSize, String.valueOf(padChar));

        if (negate) {
            val = "-" + val;
        }

        if (val.length() != size) {
            throw new RdfFileException(
                    "数值" + str + "补位后" + val + "长度" + val.length() + "模板定义长度" + size,
                    RdfErrorEnum.ILLEGAL_ARGUMENT);
        }

        return val;
    }

    public static String alignRight(String str, int size, String padStr) {
        if (str == null) {
            return null;
        }

        if ((padStr == null) || (padStr.length() == 0)) {
            padStr = " ";
        }

        int padLen = padStr.length();
        int strLen = str.length();
        int pads = size - strLen;

        if (pads <= 0) {
            return str;
        }

        if (pads == padLen) {
            return padStr.concat(str);
        } else if (pads < padLen) {
            return padStr.substring(0, pads).concat(str);
        } else {
            char[] padding = new char[pads];
            char[] padChars = padStr.toCharArray();

            for (int i = 0; i < pads; i++) {
                padding[i] = padChars[i % padLen];
            }

            return new String(padding).concat(str);
        }
    }

    /**
     * 左对齐，填充空
     * <p>
     * 中文字符按字节计算
     *
     * @param obj
     * @param size
     * @return
     */
    public static String alignLeftBlank(Object obj, int size, String encoding) {
        if (null == obj) {
            return alignLeft("", size, " ");
        }

        return alignLeftBlank(String.valueOf(obj).trim(), size, encoding);
    }

    /**
     * 左对齐，填充空
     * <p>
     * 中文字符按字节计算
     * <p>
     * 拷贝之StringUtil
     *
     * @param str
     * @param size
     * @return
     */
    public static String alignLeftBlank(String str, int size, String encoding) {
        if (null == str) {
            return alignLeft("", size, " ");
        }

        try {
            int len = str.trim().getBytes(encoding).length;

            if (len > size) {
                throw new RdfFileException("字符串[" + str + "]超过了模板定义的长度" + size + "无法补位",
                        RdfErrorEnum.FORMAT_ERROR);
            }

            int pads = size - len;
            if (pads <= 0) {
                return str;
            }

            String padStr = " ";

            int padLen = padStr.length();

            if (pads <= 0) {
                return str;
            }

            char[] padding = new char[pads];
            char[] padChars = padStr.toCharArray();

            for (int i = 0; i < pads; i++) {
                padding[i] = padChars[i % padLen];
            }

            return str.concat(new String(padding));

        } catch (UnsupportedEncodingException e) {
            throw new RdfFileException(str + " 编码出错", e, RdfErrorEnum.FORMAT_ERROR);
        }
    }

    public static String alignLeft(String str, int size, String padStr) {
        if (str == null) {
            return null;
        }

        if ((padStr == null) || (padStr.length() == 0)) {
            padStr = " ";
        }

        int padLen = padStr.length();
        int strLen = str.length();
        int pads = size - strLen;

        if (pads <= 0) {
            return str;
        }

        if (pads == padLen) {
            return str.concat(padStr);
        } else if (pads < padLen) {
            return str.concat(padStr.substring(0, pads));
        } else {
            char[] padding = new char[pads];
            char[] padChars = padStr.toCharArray();

            for (int i = 0; i < pads; i++) {
                padding[i] = padChars[i % padLen];
            }

            return str.concat(new String(padding));
        }
    }

    public static String[] split(String str, String separator) {
        if (null == separator) {
            throw new RdfFileException("rdf-file# split 分隔符为空", RdfErrorEnum.ILLEGAL_ARGUMENT);
        }

        if (null == str) {
            return null;
        }

        int len = str.length();

        if (len == 0) {
            return new String[0];
        }

        List<String> subStrings = new ArrayList<String>();
        int separatorLength = separator.length();
        int beg = 0;
        int end = 0;
        while (end < len) {
            end = str.indexOf(separator, beg);

            if (end > -1) {
                if (end > beg) {
                    subStrings.add(str.substring(beg, end));
                } else {
                    subStrings.add(EMPTY);
                }
                beg = end + separatorLength;
            } else {
                subStrings.add(str.substring(beg));
                end = len;
            }
        }

        return subStrings.toArray(new String[subStrings.size()]);
    }

    public static String getFileEncoding(FileConfig fileConfig) {
        if (isNotBlank(fileConfig.getFileEncoding())) {
            return fileConfig.getFileEncoding().trim();
        }

        if (isNotBlank(fileConfig.getTemplatePath())) {
            FileMeta fileMeta = TemplateLoader.load(fileConfig.getTemplatePath(),
                    fileConfig.getTemplateEncoding());
            if (isNotBlank(fileMeta.getFileEncoding())) {
                return fileMeta.getFileEncoding();
            }
        }
        return FileDefaultConfig.DEFAULT_FILE_ENCONDIG;
    }

    /**
     * 获取换行符配置
     *
     * @param fileConfig
     * @return
     */
    public static String getLineBreak(FileConfig fileConfig) {
        // 用户指定
        if ("\r\n".equals(fileConfig.getLineBreak()) || "\n".equals(fileConfig.getLineBreak())
                || "\r".equals(fileConfig.getLineBreak())) {
            return fileConfig.getLineBreak();
        } else if (isNotBlank(fileConfig.getLineBreak())) {
            throw new RdfFileException(
                    "rdf-file#fileConfig.getLineBreak() = " + fileConfig.getLineBreak() + " 不是有效的换行符",
                    RdfErrorEnum.UNSUPPORT_LINEBREAK);
        }
        // 模板配置
        if (isNotBlank(fileConfig.getTemplatePath())) {
            FileMeta fileMeta = TemplateLoader.load(fileConfig.getTemplatePath(),
                    fileConfig.getTemplateEncoding());
            if ("\r\n".equals(fileMeta.getLineBreak()) || "\n".equals(fileMeta.getLineBreak())
                    || "\r".equals(fileMeta.getLineBreak())) {
                return fileMeta.getLineBreak();
            } else if (isNotBlank(fileMeta.getLineBreak())) {
                throw new RdfFileException(
                        "rdf-file#fileMeta.getLineBreak() = " + fileMeta.getLineBreak() + " 不是有效的换行符",
                        RdfErrorEnum.UNSUPPORT_LINEBREAK);
            }

        }
        // 全局默认值
        return FileDefaultConfig.DEFAULT_LINE_BREAK;
    }

    public static void copyDirectoryToDirectory(File srcDir, File destDir) {
        assertNotNull(srcDir, "rdf-file#Source must not be null", RdfErrorEnum.ILLEGAL_ARGUMENT);
        assertNotNull(destDir, "rdf-file#Destination must not be null",
                RdfErrorEnum.ILLEGAL_ARGUMENT);
        if (destDir.exists() && destDir.isDirectory() == false) {
            throw new RdfFileException("rdf-file#Destination '" + destDir + "' is not a directory",
                    RdfErrorEnum.ILLEGAL_ARGUMENT);
        }

        copyDirectory(srcDir, new File(destDir, srcDir.getName()), true);
    }

    public static void copyDirectory(File srcDir, File destDir, boolean preserveFileDate) {
        copyDirectory(srcDir, destDir, null, preserveFileDate);
    }

    public static void copyDirectory(File srcDir, File destDir, FileFilter filter,
                                     boolean preserveFileDate) {
        assertNotNull(srcDir, "rdf-file#Source must not be null", RdfErrorEnum.ILLEGAL_ARGUMENT);
        assertNotNull(destDir, "rdf-file#Destination must not be null",
                RdfErrorEnum.ILLEGAL_ARGUMENT);
        if (srcDir.exists() == false) {
            throw new RdfFileException("rdf-file#Source '" + srcDir + "' does not exist",
                    RdfErrorEnum.IO_ERROR);
        }
        if (srcDir.isDirectory() == false) {
            throw new RdfFileException(
                    "rdf-file#Source '" + srcDir + "' exists but is not a directory",
                    RdfErrorEnum.IO_ERROR);
        }
        try {
            if (srcDir.getCanonicalPath().equals(destDir.getCanonicalPath())) {
                throw new RdfFileException("rdf-file#Source '" + srcDir + "' and destination '"
                        + destDir + "' are the same",
                        RdfErrorEnum.IO_ERROR);
            }

            // Cater for destination being directory within the source directory (see IO-141)
            List<String> exclusionList = null;
            if (destDir.getCanonicalPath().startsWith(srcDir.getCanonicalPath())) {
                File[] srcFiles = filter == null ? srcDir.listFiles() : srcDir.listFiles(filter);
                if (srcFiles != null && srcFiles.length > 0) {
                    exclusionList = new ArrayList<String>(srcFiles.length);
                    for (File srcFile : srcFiles) {
                        File copiedFile = new File(destDir, srcFile.getName());
                        exclusionList.add(copiedFile.getCanonicalPath());
                    }
                }
            }
            doCopyDirectory(srcDir, destDir, filter, preserveFileDate, exclusionList);
        } catch (IOException e) {
            throw new RdfFileException(
                    "rdf-file#Source '" + srcDir + "' and destination '" + destDir + "'", e,
                    RdfErrorEnum.IO_ERROR);
        }
    }

    private static void doCopyDirectory(File srcDir, File destDir, FileFilter filter,
                                        boolean preserveFileDate, List<String> exclusionList) {
        // recurse
        File[] srcFiles = filter == null ? srcDir.listFiles() : srcDir.listFiles(filter);
        // null if abstract pathname does not denote a directory, or if an I/O error occurs
        assertNotNull(srcFiles, "rdf-fiel#Failed to list contents of " + srcDir,
                RdfErrorEnum.IO_ERROR);

        if (destDir.exists()) {
            if (destDir.isDirectory() == false) {
                throw new RdfFileException(
                        "rdf-fiel#Destination '" + destDir + "' exists but is not a directory",
                        RdfErrorEnum.IO_ERROR);
            }
        } else {
            if (!destDir.mkdirs() && !destDir.isDirectory()) {
                throw new RdfFileException(
                        "rdf-file#Destination '" + destDir + "' directory cannot be created",
                        RdfErrorEnum.IO_ERROR);
            }
        }
        if (destDir.canWrite() == false) {
            throw new RdfFileException(
                    "rdf-file#Destination '" + destDir + "' cannot be written to",
                    RdfErrorEnum.IO_ERROR);
        }
        for (File srcFile : srcFiles) {
            File dstFile = new File(destDir, srcFile.getName());
            try {
                if (exclusionList == null || !exclusionList.contains(srcFile.getCanonicalPath())) {
                    if (srcFile.isDirectory()) {
                        doCopyDirectory(srcFile, dstFile, filter, preserveFileDate, exclusionList);
                    } else {
                        doCopyFile(srcFile, dstFile, preserveFileDate);
                    }
                }
            } catch (IOException e) {
                throw new RdfFileException("rdf-file# srcFile=" + srcFile.getAbsolutePath()
                        + ", dstFile=" + dstFile.getAbsolutePath(),
                        e, RdfErrorEnum.IO_ERROR);
            }
        }

        // Do this last, as the above has probably affected directory metadata
        if (preserveFileDate) {
            destDir.setLastModified(srcDir.lastModified());
        }
    }

    private static final long FILE_COPY_BUFFER_SIZE = 1024 * 1024 * 30;

    private static void doCopyFile(File srcFile, File destFile, boolean preserveFileDate) {
        if (destFile.exists() && destFile.isDirectory()) {
            throw new RdfFileException(
                    "rdf-file#doCopyFile Destination '" + destFile + "' exists but is a directory",
                    RdfErrorEnum.IO_ERROR);
        }

        FileInputStream fis = null;
        FileOutputStream fos = null;
        FileChannel input = null;
        FileChannel output = null;
        try {
            fis = new FileInputStream(srcFile);
            fos = new FileOutputStream(destFile);
            input = fis.getChannel();
            output = fos.getChannel();
            long size = input.size();
            long pos = 0;
            long count = 0;
            while (pos < size) {
                count = size - pos > FILE_COPY_BUFFER_SIZE ? FILE_COPY_BUFFER_SIZE : size - pos;
                pos += output.transferFrom(input, pos, count);
            }
        } catch (FileNotFoundException e) {
            throw new RdfFileException("rdf-file# Failed to copy full contents from '" + srcFile
                    + "' to '" + destFile + "' 文件不存在",
                    e, RdfErrorEnum.IO_ERROR);
        } catch (IOException e) {
            throw new RdfFileException("rdf-file# Failed to copy full contents from '" + srcFile
                    + "' to '" + destFile + "' io error",
                    e, RdfErrorEnum.IO_ERROR);
        } finally {
            if (null != output) {
                try {
                    output.close();
                } catch (IOException e) {
                    if (RdfFileLogUtil.common.isWarn()) {
                        RdfFileLogUtil.common
                                .warn("rdf-file# Failed to copy full contents from '" + srcFile
                                                + "' to '" + destFile + "' output.close() error",
                                        e);
                    }
                }
            }
            if (null != fos) {
                try {
                    fos.close();
                } catch (IOException e) {
                    if (RdfFileLogUtil.common.isWarn()) {
                        RdfFileLogUtil.common
                                .warn("rdf-file# Failed to copy full contents from '" + srcFile
                                                + "' to '" + destFile + "' fos.close() error",
                                        e);
                    }
                }
            }
            if (null != input) {
                try {
                    input.close();
                } catch (IOException e) {
                    if (RdfFileLogUtil.common.isWarn()) {
                        RdfFileLogUtil.common
                                .warn("rdf-file# Failed to copy full contents from '" + srcFile
                                                + "' to '" + destFile + "' input.close() error",
                                        e);
                    }
                }
            }
            if (null != fis) {
                try {
                    fis.close();
                } catch (IOException e) {
                    if (RdfFileLogUtil.common.isWarn()) {
                        RdfFileLogUtil.common
                                .warn("rdf-file# Failed to copy full contents from '" + srcFile
                                                + "' to '" + destFile + "' fis.close() error",
                                        e);
                    }
                }
            }
        }

        if (srcFile.length() != destFile.length()) {
            throw new RdfFileException("rdf-file# Failed to copy full contents from '" + srcFile
                    + "' to '" + destFile + "'",
                    RdfErrorEnum.IO_ERROR);
        }
        if (preserveFileDate) {
            destFile.setLastModified(srcFile.lastModified());
        }
    }

    public static void copyFile(File srcFile, File destFile) {
        copyFile(srcFile, destFile, true);
    }

    public static void copyFile(File srcFile, File destFile, boolean preserveFileDate) {
        assertNotNull(srcFile, "rdf-file#Source must not be null", RdfErrorEnum.ILLEGAL_ARGUMENT);
        assertNotNull(destFile, "rdf-file#Destination must not be null",
                RdfErrorEnum.ILLEGAL_ARGUMENT);

        if (srcFile.exists() == false) {
            throw new RdfFileException("rdf-file#Source '" + srcFile + "' does not exist",
                    RdfErrorEnum.NOT_EXSIT);
        }
        if (srcFile.isDirectory()) {
            throw new RdfFileException(
                    "rdf-file#Source '" + srcFile + "' exists but is a directory",
                    RdfErrorEnum.ILLEGAL_ARGUMENT);
        }
        try {
            if (srcFile.getCanonicalPath().equals(destFile.getCanonicalPath())) {
                throw new RdfFileException("rdf-fiel#Source '" + srcFile + "' and destination '"
                        + destFile + "' are the same",
                        RdfErrorEnum.ILLEGAL_ARGUMENT);
            }
        } catch (IOException e) {
            throw new RdfFileException(
                    "rdf-file#Source '" + srcFile + "' and destination '" + destFile + "'", e,
                    RdfErrorEnum.IO_ERROR);
        }
        File parentFile = destFile.getParentFile();
        if (parentFile != null) {
            if (!parentFile.mkdirs() && !parentFile.isDirectory()) {
                throw new RdfFileException(
                        "rdf-file#Destination '" + parentFile + "' directory cannot be created",
                        RdfErrorEnum.IO_ERROR);
            }
        }
        if (destFile.exists() && destFile.canWrite() == false) {
            throw new RdfFileException(
                    "rdf-file#Destination '" + destFile + "' exists but is read-only",
                    RdfErrorEnum.IO_ERROR);
        }

        doCopyFile(srcFile, destFile, preserveFileDate);
    }

    /**
     * 组装文件路径
     *
     * @param pathOrFilename
     * @return
     */
    public static String combinePath(String... pathOrFilename) {
        return FileNameUtils.normalize(FileNameUtils.join(pathOrFilename, File.separator));
    }

    /**
     * 从配置获取分隔符
     *
     * @return
     */
    public static String getColumnSplit(FileConfig fileConfig) {
        if (null != fileConfig.getColumnSplit()) {
            return fileConfig.getColumnSplit();
        }

        return TemplateLoader.load(fileConfig).getColumnSplit();
    }

    /**
     * 从协议定义获取分隔符
     *
     * @param fileConfig
     * @return
     */
    public static String getRowSplit(FileConfig fileConfig) {
        return ProtocolLoader.loadProtocol(TemplateLoader.load(fileConfig).getProtocol())
                .getRowSplit().getSplit(fileConfig);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static boolean compare(Object left, Object right) {
        if (null == left) {
            if (null == right) {
                return true;
            } else {
                return false;
            }
        }

        if (!left.getClass().getName().equals(right.getClass().getName())) {
            left = new BigDecimal(left.toString());
            right = new BigDecimal(right.toString());
        }

        if (left instanceof Comparable) {
            if (((Comparable) left).compareTo((Comparable) right) == 0) {
                return true;
            } else {
                return false;
            }
        }

        return left.equals(right);
    }

    public static Method findMethod(Class<?> clazz, String name) {
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            if (name.equals(method.getName())) {
                return method;
            }
        }
        return null;
    }

    /**
     * 格式化字节
     * si true:based on 1000, false:based on 1024
     *
     * @param bytes
     * @param si
     * @return
     */
    public static String humanReadableByteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

    public static Map<String, String> parsePathParams(String path) {
        Map<String, String> params = new HashMap<String, String>();

        if (isBlank(path)) {
            return params;
        }

        int idx = path.indexOf(QUESTION);
        if (idx < 0) {
            return params;
        }

        path = path.substring(idx + 1);
        String[] pairs = path.split(AMPERSAND);
        for (String pair : pairs) {
            String[] param = split(pair, EQUALS);
            if (param.length == 2) {
                params.put(param[0], param[1]);
            } else {
                throw new RdfFileException("path=" + path + ", parseParams format error, it has not valid param pairs ", RdfErrorEnum.ILLEGAL_ARGUMENT);
            }
        }

        return params;
    }

    /**
     * 从输入流读取指定长度数据到byte[]
     *
     * @param is
     * @return
     * @throws IOException
     */
    public static byte[] read(InputStream is) {
        try {
            byte[] bs = new byte[1024];
            int readSize = 0;
            ByteArrayOutputStream bao = new ByteArrayOutputStream();
            while ((readSize = is.read(bs)) != -1) {
                bao.write(bs, 0, readSize);
            }
            return bao.toByteArray();
        } catch (IOException e) {
            throw new RdfFileException("rdf-file#RdfFileBytesUtil.read 异常", e, RdfErrorEnum.IO_ERROR);
        } finally {
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    if (RdfFileLogUtil.common.isWarn()) {
                        RdfFileLogUtil.common.warn("rdf-file#RdfFileBytesUtil.close()", e);
                    }
                }
            }
        }
    }

    public static String getRowCodecMode(FileConfig fileConfig) {
        if (isNotBlank(fileConfig.getRowCodecMode())) {
            return fileConfig.getRowCodecMode();
        }

        FileMeta fileMeta = TemplateLoader.load(fileConfig);
        return fileMeta.getRowCodecMode();
    }

    public static <T> T getParam(FileConfig fileConfig, String key, T defaultValue) {
        FileMeta fileMeta = TemplateLoader.load(fileConfig);
        String val = fileMeta.getParams().get(key);
        if (isNotBlank(val)) {
            return (T) val.trim();
        }
        return defaultValue;
    }

    public static boolean isRelationCodecMode(FileConfig fileConfig) {
        return "relation".equals(getRowCodecMode(fileConfig));
    }

    public static boolean isRelationReadRowCompatibility(FileConfig fileConfig) {
        if (null != fileConfig.getRelationReadRowCompatibility()) {
            return fileConfig.getRelationReadRowCompatibility();
        }

        FileMeta fileMeta = TemplateLoader.load(fileConfig);
        if (null != fileMeta.getRelationReadRowCompatibility()) {
            return fileMeta.getRelationReadRowCompatibility();
        }

        return false;
    }
}
