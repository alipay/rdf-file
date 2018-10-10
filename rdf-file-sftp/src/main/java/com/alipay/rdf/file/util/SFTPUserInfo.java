/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2012 All Rights Reserved.
 */
package com.alipay.rdf.file.util;

import java.util.Arrays;
import java.util.Map;
import java.util.Properties;

import com.alipay.rdf.file.enums.SftpAuthEnum;
import com.alipay.rdf.file.storage.SftpConfig;

/**
 * SFTP用户信息对象。
 * 
 * @author haofan.whf
 * @version $Id: SFTPUserInfo.java, v 0.1 2018-10-04 下午08:05:40 haofan.whf Exp
 */
public class SFTPUserInfo {

	/** FTP登录用户 */
	private String user;

	/** 密码 - 明文存储的密码 - 后续会逐渐删除 */
	private String pswd;

	/** 密码 - 经过kmi解密之后的 */
	private String securityPswd;

	/** 服务器地址 */
	private String host;

	/** 端口，缺省是21 */
	private int port = 21;

	/** 身份私钥文件 */
	private String identityFile;

	/** 验证类型: 0密码验证，1私钥验证, 2代表混合模式认证 ；必须指定，所以缺省是非法值 */
	private SftpAuthEnum authType = SftpAuthEnum.PASSWORD;

	private String privateKeyName;
	private byte[] prvkey;
	private String pwdForprvkey;
	private byte[] pubkey;
	private String strictHostKeyChecking;

	/** 是否使用代理 */
	private boolean useProxy = false;

	/** 代理host */
	private String proxyHost;

	/** 代理port */
	private int proxyPort;

	/** 代理user */
	private String proxyUser;

	/** 代理kmi解密后的密码 */
	private String proxySecurityPswd;

	/**
	 * 适用于需要补充session config的情况
	 * 比如对于高版本的ssh协议需要传入支持的kex
	 */
	private Properties extraSessionConfig;

	public SFTPUserInfo() {

	}

	public SFTPUserInfo(SftpConfig sftpConfig) {
		super();
		this.user = sftpConfig.getUserName();
		this.securityPswd = sftpConfig.getPassword();
		this.host = sftpConfig.getHost();
		this.port = sftpConfig.getPort();
		this.authType = sftpConfig.getAuthEnum();
		this.extraSessionConfig = sftpConfig.getExtraSessionConfig();
	}

	/**
	 * 获取密码
	 * @return
	 */
	public String getPassword(){
		return !RdfFileUtil.isBlank(this.getSecurityPswd())
				? this.getSecurityPswd() : this.getPswd();
	}

	/**
	 * Getter method for property <tt>authType</tt>.
	 * 
	 * @return property value of authType
	 */
	public SftpAuthEnum getAuthType() {
		return authType;
	}

	/**
	 * Setter method for property <tt>authType</tt>.
	 * 
	 * @param authType
	 *            value to be assigned to property authType
	 */
	public void setAuthType(SftpAuthEnum authType) {
		this.authType = authType;
	}

	/**
	 * Getter method for property <tt>user</tt>.
	 * 
	 * @return property value of user
	 */
	public String getUser() {
		return user;
	}

	/**
	 * Setter method for property <tt>user</tt>.
	 * 
	 * @param user
	 *            value to be assigned to property user
	 */
	public void setUser(String user) {
		this.user = user;
	}

	/**
	 * Getter method for property <tt>pswd</tt>.
	 * 
	 * @return property value of pswd
	 */
	public String getPswd() {
		return pswd;
	}

	/**
	 * Setter method for property <tt>pswd</tt>.
	 * 
	 * @param pswd
	 *            value to be assigned to property pswd
	 */
	public void setPswd(String pswd) {
		this.pswd = pswd;
	}

	/**
	 * Getter method for property <tt>host</tt>.
	 * 
	 * @return property value of host
	 */
	public String getHost() {
		return host;
	}

	/**
	 * Setter method for property <tt>host</tt>.
	 * 
	 * @param host
	 *            value to be assigned to property host
	 */
	public void setHost(String host) {
		this.host = host;
	}

	/**
	 * Getter method for property <tt>port</tt>.
	 * 
	 * @return property value of port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * Setter method for property <tt>port</tt>.
	 * 
	 * @param port
	 *            value to be assigned to property port
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * Getter method for property <tt>identityFile</tt>.
	 * 
	 * @return property value of identityFile
	 */
	public String getIdentityFile() {
		return identityFile;
	}

	/**
	 * Setter method for property <tt>identityFile</tt>.
	 * 
	 * @param identityFile
	 *            value to be assigned to property identityFile
	 */
	public void setIdentityFile(String identityFile) {
		this.identityFile = identityFile;
	}

	public String getSecurityPswd() {
		return securityPswd;
	}

	public void setSecurityPswd(String securityPswd) {
		this.securityPswd = securityPswd;
	}

	public String getPrivateKeyName() {
		return privateKeyName;
	}

	public void setPrivateKeyName(String privateKeyName) {
		this.privateKeyName = privateKeyName;
	}

	public byte[] getPrvkey() {
		return prvkey;
	}

	public void setPrvkey(byte[] prvkey) {
		this.prvkey = prvkey;
	}

	public String getPwdForprvkey() {
		return pwdForprvkey;
	}

	public void setPwdForprvkey(String pwdForprvkey) {
		this.pwdForprvkey = pwdForprvkey;
	}

	public byte[] getPubkey() {
		return pubkey;
	}

	public void setPubkey(byte[] pubkey) {
		this.pubkey = pubkey;
	}

	public String getStrictHostKeyChecking() {
		return strictHostKeyChecking;
	}

	public void setStrictHostKeyChecking(String strictHostKeyChecking) {
		this.strictHostKeyChecking = strictHostKeyChecking;
	}

	public boolean isUseProxy() {
		return useProxy;
	}

	public void setUseProxy(boolean useProxy) {
		this.useProxy = useProxy;
	}

	public String getProxyHost() {
		return proxyHost;
	}

	public void setProxyHost(String proxyHost) {
		this.proxyHost = proxyHost;
	}

	public int getProxyPort() {
		return proxyPort;
	}

	public void setProxyPort(int proxyPort) {
		this.proxyPort = proxyPort;
	}

	public String getProxyUser() {
		return proxyUser;
	}

	public void setProxyUser(String proxyUser) {
		this.proxyUser = proxyUser;
	}

	public String getProxySecurityPswd() {
		return proxySecurityPswd;
	}

	public void setProxySecurityPswd(String proxySecurityPswd) {
		this.proxySecurityPswd = proxySecurityPswd;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SFTPUserInfo [user=");
		builder.append(user);
		builder.append(", securityPswd=");
		builder.append(securityPswd);
		builder.append(", host=");
		builder.append(host);
		builder.append(", port=");
		builder.append(port);
		builder.append(", identityFile=");
		builder.append(identityFile);
		builder.append(", authType=");
		builder.append(authType);
		builder.append(", privateKeyName=");
		builder.append(privateKeyName);
		builder.append(", prvkey=");
		builder.append(Arrays.toString(prvkey));
		builder.append(", pwdForprvkey=");
		builder.append(pwdForprvkey);
		builder.append(", pubkey=");
		builder.append(Arrays.toString(pubkey));
		builder.append(", strictHostKeyChecking=");
		builder.append(strictHostKeyChecking);
		builder.append(", useProxy=");
		builder.append(useProxy);
		builder.append(", proxyHost=");
		builder.append(proxyHost);
		builder.append(", proxyPort=");
		builder.append(proxyPort);
		builder.append(", proxyUser=");
		builder.append(proxyUser);
		builder.append(", proxySecurityPswd=");
		builder.append(proxySecurityPswd);
		builder.append(", extraSessionConfig=");
		builder.append(extraSessionConfig);
		builder.append("]");
		return builder.toString();
	}

	/**
	 * @param sensitive true:脱敏 false:打印敏感信息
	 * @param simple true简单打印
	 * @return
	 */
	public String toString(boolean simple, boolean sensitive){
		StringBuilder builder = new StringBuilder();
		builder.append("SFTPUserSimpleInfo [user=");
		builder.append(user);
		builder.append(", host=");
		builder.append(host);
		builder.append(", port=");
		builder.append(port);
		if(!simple){
			builder.append(", authType=");
			builder.append(authType);
			builder.append(", strictHostKeyChecking=");
			builder.append(strictHostKeyChecking);
			builder.append(", useProxy=");
			builder.append(useProxy);
			builder.append(", proxyHost=");
			builder.append(proxyHost);
			builder.append(", proxyPort=");
			builder.append(proxyPort);
			builder.append(", proxyUser=");
			builder.append(proxyUser);
			builder.append(", extraSessionConfig=");
			builder.append(extraSessionConfig);
		}
		if(!sensitive){
			builder.append(", securityPswd=");
			builder.append(securityPswd);
			builder.append(", identityFile=");
			builder.append(identityFile);
			builder.append(", privateKeyName=");
			builder.append(privateKeyName);
			builder.append(", prvkey=");
			builder.append(Arrays.toString(prvkey));
			builder.append(", pwdForprvkey=");
			builder.append(pwdForprvkey);
			builder.append(", pubkey=");
			builder.append(Arrays.toString(pubkey));
			builder.append(", proxySecurityPswd=");
			builder.append(proxySecurityPswd);
		}
		builder.append("]");
		return builder.toString();
	}

	/**
	 * Getter method for property extraSessionConfig.
	 *
	 * @return property value of extraSessionConfig
	 */
	public Properties getExtraSessionConfig() {
		return extraSessionConfig;
	}

	/**
	 * Setter method for property extraSessionConfig.
	 *
	 * @param extraSessionConfig value to be assigned to property extraSessionConfig
	 */
	public void setExtraSessionConfig(Properties extraSessionConfig) {
		this.extraSessionConfig = extraSessionConfig;
	}
}