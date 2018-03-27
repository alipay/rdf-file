package com.alipay.rdf.file.util;

import com.alipay.rdf.file.interfaces.LogCallback;

/**
 * 默认日志打印
 * 
 * @author hongwei.quhw
 * @version $Id: TestLog.java, v 0.1 2017年7月24日 下午8:39:17 hongwei.quhw Exp $
 */
public class TestLog implements LogCallback {

    @Override
    public boolean isDebug() {
        return true;
    }

    /** 
     * @see com.alipay.rdf.file.interfaces.LogCallback#isWarn()
     */
    @Override
    public boolean isWarn() {
        return true;
    }

    /** 
     * @see com.alipay.rdf.file.interfaces.LogCallback#isInfo()
     */
    @Override
    public boolean isInfo() {
        return true;
    }

    /** 
     * @see com.alipay.rdf.file.interfaces.LogCallback#info(java.lang.String)
     */
    @Override
    public void info(String msg) {
        System.out.println(msg);
    }

    /** 
     * @see com.alipay.rdf.file.interfaces.LogCallback#info(java.lang.String, java.lang.Throwable)
     */
    @Override
    public void info(String msg, Throwable throwable) {
        System.out.println(msg);
        throwable.printStackTrace();
    }

    /** 
     * @see com.alipay.rdf.file.interfaces.LogCallback#warn(java.lang.String)
     */
    @Override
    public void warn(String msg) {
        System.out.println(msg);
    }

    /** 
     * @see com.alipay.rdf.file.interfaces.LogCallback#warn(java.lang.String, java.lang.Throwable)
     */
    @Override
    public void warn(String msg, Throwable throwable) {
        System.out.println(msg);
        throwable.printStackTrace();
    }

    /** 
     * @see com.alipay.rdf.file.interfaces.LogCallback#debug(java.lang.String)
     */
    @Override
    public void debug(String msg) {
        System.out.println(msg);
    }

    /** 
     * @see com.alipay.rdf.file.interfaces.LogCallback#debug(java.lang.String, java.lang.Throwable)
     */
    @Override
    public void debug(String msg, Throwable throwable) {
        System.out.println(msg);
        throwable.printStackTrace();
    }

    /** 
     * @see com.alipay.rdf.file.interfaces.LogCallback#error(java.lang.String)
     */
    @Override
    public void error(String msg) {
        System.err.println(msg);
    }

    /** 
     * @see com.alipay.rdf.file.interfaces.LogCallback#error(java.lang.String, java.lang.Throwable)
     */
    @Override
    public void error(String msg, Throwable throwable) {
        System.err.println(msg);
        throwable.printStackTrace();
    }

}
