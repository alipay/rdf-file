/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2012 All Rights Reserved.
 */
package com.alipay.rdf.file.util;

import java.util.Properties;

import com.alipay.rdf.file.exception.RdfErrorEnum;
import com.alipay.rdf.file.exception.RdfFileException;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.ProxyHTTP;
import com.jcraft.jsch.Session;

/**
 * SSH连接工厂。
 * 
 * @author haofan.whf
 * @version $Id: JschFactory.java, v 0.1 2018-10-4 下午20:20:50 haofan.whf Exp $
 */
public class JschFactory {
    
    /**
     * 连接sftp服务器
     * @param user
     * @return
     */
    public static Session openConnection(SFTPUserInfo user) {
        if(RdfFileUtil.isBlank(user.getUser())
                || RdfFileUtil.isBlank(user.getHost())
                || user.getPort() < 0){
            throw new RdfFileException("rdf-file#JschFactory.openConnection,userName/host不能为空,port不小于0"
                    , RdfErrorEnum.ILLEGAL_ARGUMENT);
        }

        Session session = null;

        try {
            switch (user.getAuthType()){
                case PASSWORD:
                    session = JschFactory.connectByPasswd(user);
                    break;
                case IDENTITY:
                    session = JschFactory.connectByIdentity(user);
                    break;
                case MIX:
                    session = JschFactory.connect(user);
                    break;
                default:
                    throw new RdfFileException("rdf-file#JschFactory不支持的AuthType" + user.getAuthType()
                            ,RdfErrorEnum.UNSUPPORTED_OPERATION);
            }
        }catch (JSchException e){
            throw new RdfFileException("rdf-file#JschFactory.openConnection异常,user=" + user
                    , e, RdfErrorEnum.UNKOWN);
        }

        if (session == null) {
            throw new RdfFileException("rdf-file#JschFactory.openConnection异常,user="
                    + user.toString(true, true)
                    ,RdfErrorEnum.UNKOWN);
        }

        if (user.isUseProxy()) {
            ProxyHTTP proxyHTTP = new ProxyHTTP(user.getProxyHost(), user.getProxyPort());
            if (RdfFileUtil.isNotBlank(user.getProxyUser()) && RdfFileUtil.isNotBlank(user.getProxySecurityPswd())) {
                proxyHTTP.setUserPasswd(user.getProxyUser(), user.getProxySecurityPswd());
            }
            session.setProxy(proxyHTTP);
        }
        if(user.getExtraSessionConfig() != null
                && user.getExtraSessionConfig().size() > 0){
            session.setConfig(user.getExtraSessionConfig());
        }

        try {
            session.connect();
        }catch (JSchException e){
            throw new RdfFileException("rdf-file#JschFactory.openConnection异常,user="
                    + user.toString(true, true)
                    , e, RdfErrorEnum.UNKOWN);
        }

        return session;
    }

    /**
     * 通过用户名和密码认证，发起ssh连接。
     * @param  user      FTP登录用户名
     * @return Session   ssh会话
     * @throws JSchException
     */
    private static Session connectByPasswd(SFTPUserInfo user) throws JSchException {

        if(RdfFileUtil.isBlank(user.getPassword())){
            throw new RdfFileException("rdf-file#JschFactory.connectByPasswd密码不能为空"
                    , RdfErrorEnum.ILLEGAL_ARGUMENT);
        }

        JSch jsch = new JSch();
        Session sshSession = jsch.getSession(user.getUser(), user.getHost(), user.getPort());
        sshSession.setPassword(user.getPassword());

        Properties sshConfig = new Properties();
        sshConfig.put("StrictHostKeyChecking", "no");
        sshSession.setConfig(sshConfig);
        return sshSession;
    }

    /**
     * 通过用户名和私钥认证，发起ssh连接。
     * 
     * @param user           FTP登录用户
     * @return
     * @throws JSchException 
     */
    private static Session connectByIdentity(SFTPUserInfo user) throws JSchException {
        if(RdfFileUtil.isBlank(user.getIdentityFile())){
            throw new RdfFileException("rdf-file#JschFactory.connectByPasswd私钥文件不能为空"
                    , RdfErrorEnum.ILLEGAL_ARGUMENT);
        }
        Properties sshConfig = new Properties();
        sshConfig.put("StrictHostKeyChecking", "no");
        JSch.setConfig(sshConfig);
        JSch jsch = new JSch();
        jsch.addIdentity(user.getIdentityFile());
        Session sshSession = jsch.getSession(user.getUser(), user.getHost(), user.getPort());
        return sshSession;
    }

    /**
     * 通过私钥进行认证，如果密码不为空，则使用私钥及密码双重认证
     * @param user
     * @return
     * @throws JSchException
     */
    private static Session connect(SFTPUserInfo user) throws JSchException {
        if(RdfFileUtil.isBlank(user.getPrivateKeyName())
                || user.getPrvkey() == null
                || user.getPubkey() == null){
            throw new RdfFileException("rdf-file#JschFactory.connect私钥名称/内容,公钥内容不能为空"
                    , RdfErrorEnum.ILLEGAL_ARGUMENT);
        }
        Properties sshConfig = new Properties();
        if (RdfFileUtil.isBlank(user.getStrictHostKeyChecking())) {
            sshConfig.put("StrictHostKeyChecking", "no");
        } else {
            sshConfig.put("StrictHostKeyChecking", user.getStrictHostKeyChecking());
        }
        JSch.setConfig(sshConfig);
        JSch jsch = new JSch();

        byte[] pwdForPrvKeyBytes = null;
        if (RdfFileUtil.isNotBlank(user.getPwdForprvkey())) {
            pwdForPrvKeyBytes = user.getPwdForprvkey().getBytes();
        }

        jsch.addIdentity(user.getPrivateKeyName(), user.getPrvkey(), user.getPubkey(), pwdForPrvKeyBytes);

        Session sshSession = jsch.getSession(user.getUser(), user.getHost(), user.getPort());
        //如果密码不为空则设置密码
        if (RdfFileUtil.isNotBlank(user.getPassword())) {
            sshSession.setPassword(user.getPassword());
        }
        return sshSession;
    }

}
