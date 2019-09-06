/**
 * Alipay.com Inc. Copyright (c) 2004-2019 All Rights Reserved.
 */
package com.alipay.rdf.file.util;

import com.alipay.rdf.file.interfaces.LogCallback;

import java.util.Date;

/**
 *
 * @author iminright-ali
 * @version : SystemPrintLog.java, v 0.1 2019年09月05日 14:54 iminright-ali Exp $
 */
public class SystemPrintLog implements LogCallback {
    @Override
    public boolean isDebug() {
        return true;
    }

    @Override
    public boolean isWarn() {
        return true;
    }

    @Override
    public boolean isInfo() {
        return true;
    }

    @Override
    public void info(String msg) {
        System.out.println(new Date() + ":::" + msg);
    }

    @Override
    public void info(String msg, Throwable throwable) {
        System.out.println(new Date() + ":::" + msg);
        System.out.println(throwable);
    }

    @Override
    public void warn(String msg) {
        System.out.println(new Date() + ":::" + msg);

    }

    @Override
    public void warn(String msg, Throwable throwable) {
        System.out.println(new Date() + ":::" + msg);
        System.out.println(throwable);
    }

    @Override
    public void debug(String msg) {
        System.out.println(msg);
    }

    @Override
    public void debug(String msg, Throwable throwable) {
        System.out.println(new Date() + ":::" + msg);
        System.out.println(throwable);
    }

    @Override
    public void error(String msg) {
        System.out.println(new Date() + ":::" + msg);
    }

    @Override
    public void error(String msg, Throwable throwable) {
        System.out.println(new Date() + ":::" + msg);
        System.out.println(throwable);
    }
}