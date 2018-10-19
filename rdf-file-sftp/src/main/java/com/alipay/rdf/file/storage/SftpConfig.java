/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.alipay.rdf.file.storage;

import java.util.Properties;

import com.alipay.rdf.file.enums.SftpAuthEnum;
import com.alipay.rdf.file.util.RdfFileUtil;

/**
 *
 * @author haofan.whf
 * @version $Id: SftpConfig.java, v 0.1 2018年10月06日 下午11:53 haofan.whf Exp $
 */
public class SftpConfig {

    public static final String SFTP_STORAGE_CONFIG_KEY = "sftp_storage_config";

    public static final String DEFAULT_LOCAL_TMP_PATH = "/tmp/rdf-file/sftp";

    private String password;

    private String userName;

    private Integer port = 21;

    private String host;

    private Properties extraSessionConfig = new Properties();

    private SftpAuthEnum authEnum = SftpAuthEnum.PASSWORD;

    private String localTmpPath;

    public void addExtraSessionConfig(String key, String value){
        this.extraSessionConfig.put(key, value);
    }

    public Properties getExtraSessionConfig(){
        return this.extraSessionConfig;
    }


    public String getLocalTmpPath(){
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
}