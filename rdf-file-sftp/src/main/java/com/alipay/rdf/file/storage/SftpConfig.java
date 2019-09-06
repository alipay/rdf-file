/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.alipay.rdf.file.storage;

import com.alipay.rdf.file.enums.SftpAuthEnum;
import com.alipay.rdf.file.enums.SftpProgressLogPrintTypeEnum;
import com.alipay.rdf.file.util.RdfFileUtil;

import java.util.Properties;

/**
 *
 * @author haofan.whf
 * @version $Id: SftpConfig.java, v 0.1 2018年10月06日 下午11:53 haofan.whf Exp $
 */
public class SftpConfig {

    public static final String SFTP_STORAGE_CONFIG_KEY = "sftp_storage_config";

    public static final String DEFAULT_LOCAL_TMP_PATH  = System.getProperty("java.io.tmpdir");

    private String             password;

    private String             userName;

    private Integer            port                    = 21;

    private String             host;

    private Properties         extraSessionConfig      = new Properties();

    private SftpAuthEnum       authEnum                = SftpAuthEnum.PASSWORD;

    private String             localTmpPath;

    /**
     * 进度监控日志打印周期
     */
    private int                progressPrintLogPeriod          = 3;

    private SftpProgressLogPrintTypeEnum  progressLogPrintTypeEnum  = SftpProgressLogPrintTypeEnum.SYNC;

    public void addExtraSessionConfig(String key, String value) {
        this.extraSessionConfig.put(key, value);
    }

    public Properties getExtraSessionConfig() {
        return this.extraSessionConfig;
    }

    public String getLocalTmpPath() {
        return RdfFileUtil.isNotBlank(localTmpPath) ? localTmpPath : DEFAULT_LOCAL_TMP_PATH;
    }

    /**
     * Setter method for property localTmpPath.
     *
     * @param localTmpPath value to be assigned to property localTmpPath
     */
    public void setLocalTmpPath(String localTmpPath) {
        this.localTmpPath = localTmpPath;
    }

    /**
     * Getter method for property password.
     *
     * @return property value of password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Setter method for property password.
     *
     * @param password value to be assigned to property password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Getter method for property userName.
     *
     * @return property value of userName
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Setter method for property userName.
     *
     * @param userName value to be assigned to property userName
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * Getter method for property port.
     *
     * @return property value of port
     */
    public Integer getPort() {
        return port;
    }

    /**
     * Setter method for property port.
     *
     * @param port value to be assigned to property port
     */
    public void setPort(Integer port) {
        this.port = port;
    }

    /**
     * Getter method for property host.
     *
     * @return property value of host
     */
    public String getHost() {
        return host;
    }

    /**
     * Setter method for property host.
     *
     * @param host value to be assigned to property host
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * Getter method for property authEnum.
     *
     * @return property value of authEnum
     */
    public SftpAuthEnum getAuthEnum() {
        return authEnum;
    }

    /**
     * Setter method for property authEnum.
     *
     * @param authEnum value to be assigned to property authEnum
     */
    public void setAuthEnum(SftpAuthEnum authEnum) {
        this.authEnum = authEnum;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((host == null) ? 0 : host.hashCode());
        result = prime * result + ((password == null) ? 0 : password.hashCode());
        result = prime * result + ((port == null) ? 0 : port.hashCode());
        result = prime * result + ((userName == null) ? 0 : userName.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SftpConfig other = (SftpConfig) obj;
        if (host == null) {
            if (other.host != null)
                return false;
        } else if (!host.equals(other.host))
            return false;
        if (password == null) {
            if (other.password != null)
                return false;
        } else if (!password.equals(other.password))
            return false;
        if (port == null) {
            if (other.port != null)
                return false;
        } else if (!port.equals(other.port))
            return false;
        if (userName == null) {
            if (other.userName != null)
                return false;
        } else if (!userName.equals(other.userName))
            return false;
        return true;
    }

    public SftpProgressLogPrintTypeEnum getProgressLogPrintTypeEnum() {
        return progressLogPrintTypeEnum;
    }

    public void setProgressLogPrintTypeEnum(SftpProgressLogPrintTypeEnum progressLogPrintTypeEnum) {
        this.progressLogPrintTypeEnum = progressLogPrintTypeEnum;
    }

    public int getProgressPrintLogPeriod() {
        return progressPrintLogPeriod;
    }

    public void setProgressPrintLogPeriod(int progressPrintLogPeriod) {
        this.progressPrintLogPeriod = progressPrintLogPeriod;
    }
}