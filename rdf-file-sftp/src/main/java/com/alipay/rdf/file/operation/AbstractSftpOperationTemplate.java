/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.alipay.rdf.file.operation;

import java.util.Map;

import com.alipay.rdf.file.exception.RdfErrorEnum;
import com.alipay.rdf.file.exception.RdfFileException;
import com.alipay.rdf.file.interfaces.FileSftpStorageConstants;
import com.alipay.rdf.file.util.JschFactory;
import com.alipay.rdf.file.util.RdfFileLogUtil;
import com.alipay.rdf.file.util.SFTPUserInfo;
import com.alipay.rdf.file.util.SftpThreadContext;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

/**
 *
 * @author haofan.whf
 * @version $Id: AbstractSftpOperTemplate.java, v 0.1 2018年10月06日 下午3:44 haofan.whf Exp $
 */
public abstract class AbstractSftpOperationTemplate<T> {

    private SftpOperationTypeEnums operationType;

    protected abstract void initOperationType();

    /**
     * 实际业务处理
     * @param user
     * @param params
     * @return
     * @throws Exception
     */
    protected abstract SftpOperationResponse<T> doBusiness(SFTPUserInfo user
            , Map<SftpOperationParamEnums, String> params) throws Exception;

    private void initContext(){
        initOperationType();
    }

    /**
     * 检查参数，不抛出异常
     * @param user
     * @param params
     * @return
     */
    protected abstract boolean checkBeforeDoBiz(SFTPUserInfo user, Map<SftpOperationParamEnums, String> params);

    public SftpOperationResponse<T> handle(SFTPUserInfo user
            , Map<SftpOperationParamEnums, String> params){
        initContext();
        RdfFileLogUtil.common.info("rdf-file#sftpOperation."
                + this.operationType + ".request,params=" + params);

        // 申明ssh会话对象，sftp连接对象
        ChannelSftp sftp = null;
        SftpOperationResponse response = new SftpOperationResponse<T>();
        try {
            if(!checkBeforeDoBiz(user, params)){
                throw new RdfFileException("rdf-file#sftpOperation." + this.operationType
                        + ".checkParams fail,params=" + params, RdfErrorEnum.ILLEGAL_ARGUMENT);
            }
            sftp = openChannelSftp(user);
            response = doBusiness(user, params);
        } catch (Exception e){
            RdfFileLogUtil.common.warn("rdf-file#sftpOperation."
                    + this.operationType + "fail,params=" + params, e);
            response.setSuccess(false);
            response.setError(e);
        } finally {
            closeConnection(sftp, user);
        }
        RdfFileLogUtil.common.info("rdf-file#sftpOperation."
                + this.operationType + ".response,result=" + response + ",params=" + params);
        return response;
    }

    /**
     * 关闭连接
     *
     * @param sftp
     * @param user
     */
    private static void closeConnection(ChannelSftp sftp, SFTPUserInfo user) {
        try {
            sftp.disconnect();
            if(sftp.getSession() != null){
                sftp.getSession().disconnect();
            }
        } catch (Exception e) {
            RdfFileLogUtil.common.warn("rdf-file#closeConnection fail"
                    + ",user={" + user.toString(true, true) + "}", e);
        }finally {
            SftpThreadContext.clearChannelSftp();
        }
    }

    /**
     * 根据SFTP配置打开SFTP通道
     * @param user
     * @return
     * @throws JSchException
     */
    private static ChannelSftp openChannelSftp(SFTPUserInfo user) throws JSchException {

        RdfFileLogUtil.common.debug("rdf-file#SFTPHelper.openChannelSftp request"
                + ",user={" + user.toString(true, false) + "}");
        // 创建ssh会话
        Session ssh = JschFactory.openConnection(user);

        RdfFileLogUtil.common.debug("rdf-file#SFTPHelper.openChannelSftp create ssh success"
                + ",user={" + user.toString(true, false) + "}");

        // 打开sftp连接
        ChannelSftp channel = (ChannelSftp) ssh.openChannel(FileSftpStorageConstants.SFTP);

        if(channel == null){
            throw new RdfFileException("rdf-file#SFTPHelper.openChannelSftp get ChannelSftp fail.", RdfErrorEnum.UNKOWN);
        }

        SftpThreadContext.setChannelSftp(channel);

        channel.connect();

        RdfFileLogUtil.common.debug("rdf-file#SFTPHelper.openChannelSftp create channel success"
                + ",user={" + user.toString(true, false) + "}");
        return channel;
    }

    /**
     * Setter method for property operationType.
     *
     * @param operationType value to be assigned to property operationType
     */
    public void setOperationType(SftpOperationTypeEnums operationType) {
        this.operationType = operationType;
    }
}