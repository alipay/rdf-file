/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.alipay.rdf.file.operation;

import java.util.Map;

import com.alipay.rdf.file.exception.RdfErrorEnum;
import com.alipay.rdf.file.exception.RdfFileException;
import com.alipay.rdf.file.util.RdfFileLogUtil;
import com.alipay.rdf.file.util.SFTPHelper;
import com.alipay.rdf.file.util.SFTPUserInfo;
import com.jcraft.jsch.ChannelSftp;

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
            sftp = SFTPHelper.openChannelSftp(user);
            response = doBusiness(user, params);
        } catch (Exception e){
            RdfFileLogUtil.common.warn("rdf-file#sftpOperation."
                    + this.operationType + "fail,params=" + params, e);
            response.setSuccess(false);
            response.setError(e);
        } finally {
            SFTPHelper.closeConnection(sftp, user);
        }
        RdfFileLogUtil.common.info("rdf-file#sftpOperation."
                + this.operationType + ".response,result=" + response + ",params=" + params);
        return response;
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