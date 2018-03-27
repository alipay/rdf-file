package com.alipay.rdf.file.util;

import com.alipay.rdf.file.interfaces.LogCallback;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 * 
 * 日志工具类
 * 
 * @author hongwei.quhw
 * @version $Id: RdfLogUtil.java, v 0.1 2017年7月22日 下午11:22:46 hongwei.quhw Exp $
 */
public class RdfFileLogUtil {
    /**打印通用日志*/
    public static LogCallback common = EmptyLog.INSTANCE;

    private static class EmptyLog implements LogCallback {
        private static final EmptyLog INSTANCE = new EmptyLog();

        @Override
        public boolean isDebug() {
            return false;
        }

        /** 
         * @see com.alipay.rdf.file.interfaces.LogCallback#isWarn()
         */
        @Override
        public boolean isWarn() {
            return false;
        }

        /** 
         * @see com.alipay.rdf.file.interfaces.LogCallback#isInfo()
         */
        @Override
        public boolean isInfo() {
            return false;
        }

        @Override
        public void info(String msg) {
        }

        @Override
        public void info(String msg, Throwable throwable) {
        }

        @Override
        public void warn(String msg) {
        }

        @Override
        public void warn(String msg, Throwable throwable) {
        }

        @Override
        public void debug(String msg) {
        }

        @Override
        public void debug(String msg, Throwable throwable) {
        }

        @Override
        public void error(String msg) {
        }

        @Override
        public void error(String msg, Throwable throwable) {
        }
    }
}
