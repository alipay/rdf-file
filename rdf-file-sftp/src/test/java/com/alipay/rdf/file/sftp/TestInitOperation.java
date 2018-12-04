/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.alipay.rdf.file.sftp;

import java.util.Map;

import com.alipay.rdf.file.operation.AbstractSftpOperationTemplate;
import com.alipay.rdf.file.operation.SftpOperationParamEnums;
import com.alipay.rdf.file.operation.SftpOperationResponse;
import com.alipay.rdf.file.util.SFTPHelper;
import com.alipay.rdf.file.util.SFTPUserInfo;
import com.alipay.rdf.file.util.SftpThreadContext;

/**
 *
 * @author haofan.whf
 * @version $Id: TestInitOperation.java, v 0.1 2018年10月10日 下午12:40 haofan.whf Exp $
 */
public class TestInitOperation extends AbstractSftpOperationTemplate<Boolean>{
    @Override
    protected void initOperationType() {
    }

    @Override
    protected SftpOperationResponse<Boolean> doBusiness(SFTPUserInfo user
            , Map<String, String> params) throws Exception {
        SftpOperationResponse<Boolean> response = new SftpOperationResponse<Boolean>();
        SFTPHelper.createFTPDirIfnotExist(SftpThreadContext.getChannelSftp()
                , params.get(SftpOperationParamEnums.TARGET_DIR.toString()));
        response.setData(true);
        response.setSuccess(true);
        return response;
    }

    @Override
    protected boolean checkBeforeDoBiz(SFTPUserInfo user, Map<String, String> params) {
        return params.containsKey(SftpOperationParamEnums.TARGET_DIR.toString());
    }
}