/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.alipay.rdf.file.operation;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import com.alipay.rdf.file.exception.RdfErrorEnum;
import com.alipay.rdf.file.exception.RdfFileException;
import com.alipay.rdf.file.interfaces.FileSftpStorageConstants;
import com.alipay.rdf.file.util.RdfFileLogUtil;
import com.alipay.rdf.file.util.SFTPHelper;
import com.alipay.rdf.file.util.SFTPLogMonitor;
import com.alipay.rdf.file.util.SFTPUserInfo;
import com.alipay.rdf.file.util.SftpThreadContext;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpATTRS;

/**
 *
 * @author haofan.whf
 * @version $Id: SftpOperationCollection.java, v 0.1 2018年10月06日 下午4:03 haofan.whf Exp $
 */
public class SftpOperationFactory {


    public static AbstractSftpOperationTemplate getOperation(SftpOperationTypeEnums operationType){
        switch (operationType){
            case COPY:
                return COPY_OPERATION;
            case RENAME:
                return RENAME_OPERATION;
            case UPLOAD:
                return UPLOAD_OPERATION;
            case DOWNLOAD:
                return DOWNLOAD_OPERATION;
            case LIST_FILES:
                return LIST_FILES_OPERATION;
            case FILE_EXISTS:
                return FILE_EXISTS_OPERATION;
            case DEL:
                return DEL_OPERATION;
            case CREATE:
                return CREATE_OPERATION;
            default:
                throw new RdfFileException("rdf-file#SftpOperationFactory.getOperation("
                        + operationType + ")unsupport operation type", RdfErrorEnum.UNSUPPORTED_OPERATION);
        }
    }



    /**
     * 通过SFTP上传文件。
     *
     * SFTPUserInfo
     *   SFTP用户信息
     * SftpOperationTypeEnums.UPLOAD
     * SftpOperationParamEnums.SOURCE_FILE
     *   待上传的本地文件
     * SftpOperationParamEnums.TARGET_FILE
     *   SFTP服务器上的文件
     */
    private static final AbstractSftpOperationTemplate UPLOAD_OPERATION
         = new AbstractSftpOperationTemplate<Boolean>() {
        @Override
        protected void initOperationType() {
            this.setOperationType(SftpOperationTypeEnums.UPLOAD);
        }

        @Override
        protected SftpOperationResponse<Boolean> doBusiness(SFTPUserInfo user
                , Map<SftpOperationParamEnums, String> params) throws Exception{
            SftpOperationResponse<Boolean> response = new SftpOperationResponse<Boolean>();
            String localFile = params.get(SftpOperationParamEnums.SOURCE_FILE);
            String remoteFile = params.get(SftpOperationParamEnums.TARGET_FILE);
            ChannelSftp sftp = SftpThreadContext.getChannelSftp();
            SFTPHelper.createFTPDirIfnotExist(sftp, remoteFile);
            SFTPLogMonitor progressMonitor = new SFTPLogMonitor();
            sftp.put(localFile, remoteFile, progressMonitor);
            response.setSuccess(true);
            response.setData(true);
            return response;
        }

        @Override
        protected boolean checkBeforeDoBiz(SFTPUserInfo user, Map<SftpOperationParamEnums, String> params) {
            return params.containsKey(SftpOperationParamEnums.SOURCE_FILE)
                    && params.containsKey(SftpOperationParamEnums.TARGET_FILE);
        }
    };

    /**
     * 修改文件名。
     *
     * SFTPUserInfo
     *   SFTP用户信息
     * SftpOperationTypeEnums.RENAME
     * SftpOperationParamEnums.SOURCE_FILE
     *   待修改文件名的文件
     * SftpOperationParamEnums.TARGET_FILE
     *   要修改的文件名
     */
    private static final AbstractSftpOperationTemplate RENAME_OPERATION
            = new AbstractSftpOperationTemplate<Boolean>() {

        @Override
        protected void initOperationType() {
            this.setOperationType(SftpOperationTypeEnums.RENAME);
        }

        @Override
        protected SftpOperationResponse<Boolean> doBusiness(SFTPUserInfo user
                , Map<SftpOperationParamEnums, String> params) throws Exception{
            SftpOperationResponse<Boolean> response = new SftpOperationResponse<Boolean>();

            ChannelSftp sftp = SftpThreadContext.getChannelSftp();
            String fileName = params.get(SftpOperationParamEnums.SOURCE_FILE);
            String newFileName = params.get(SftpOperationParamEnums.TARGET_FILE);
            // 获取文件属性,如果获取不到或者获取异常,则认为文件不存在
            SftpATTRS sftpATTRS = sftp.stat(fileName);

            if (sftpATTRS == null) {
                throw new RdfFileException("file not exists", RdfErrorEnum.NOT_EXSIT);
            } else if (sftpATTRS.isDir() || sftpATTRS.isLink()) {
                throw new RdfFileException("dirs or links are not support rename", RdfErrorEnum.UNSUPPORTED_OPERATION);
            } else {

                // 如果新的目标文件目录不存在则创建一个
                SFTPHelper.createFTPDirIfnotExist(sftp, newFileName);

                // 重命名目标文件
                sftp.rename(fileName, newFileName);
                response.setSuccess(true);
                response.setData(true);
                return response;
            }
        }

        @Override
        protected boolean checkBeforeDoBiz(SFTPUserInfo user, Map<SftpOperationParamEnums, String> params) {
            return params.containsKey(SftpOperationParamEnums.SOURCE_FILE)
                    && params.containsKey(SftpOperationParamEnums.TARGET_FILE);
        }
    };

    /**
     * 通过SFTP下载文件。
     *
     * SFTPUserInfo
     *   SFTP用户信息
     * SftpOperationTypeEnums.DOWNLOAD
     * SftpOperationParamEnums.SOURCE_FILE
     *   SFTP服务器上的文件
     * SftpOperationParamEnums.TARGET_FILE
     *   待下载的本地文件
     */
    private static final AbstractSftpOperationTemplate DOWNLOAD_OPERATION
            = new AbstractSftpOperationTemplate<Boolean>() {

        @Override
        protected void initOperationType() {
            this.setOperationType(SftpOperationTypeEnums.DOWNLOAD);
        }

        @Override
        protected SftpOperationResponse<Boolean> doBusiness(SFTPUserInfo user
                , Map<SftpOperationParamEnums, String> params) throws Exception{
            SftpOperationResponse<Boolean> response = new SftpOperationResponse<Boolean>();

            ChannelSftp sftp = SftpThreadContext.getChannelSftp();
            String remoteFile = params.get(SftpOperationParamEnums.SOURCE_FILE);
            String localFile = params.get(SftpOperationParamEnums.TARGET_FILE);

            // 下载前先创建本地目录，如失败则直接返回
            SFTPHelper.createLocalDirIfnotExist(localFile);
            // 执行文件下载，并将进度输出到日志文件中
            SFTPLogMonitor progressMonitor = new SFTPLogMonitor();

            sftp.get(remoteFile, localFile, progressMonitor);
            response.setData(true);
            response.setSuccess(true);
            return response;
        }

        @Override
        protected boolean checkBeforeDoBiz(SFTPUserInfo user, Map<SftpOperationParamEnums, String> params) {
            return params.containsKey(SftpOperationParamEnums.SOURCE_FILE)
                    && params.containsKey(SftpOperationParamEnums.TARGET_FILE);
        }
    };

    /**
     * copy文件。
     *
     * SFTPUserInfo
     *   SFTP用户信息
     * SftpOperationTypeEnums.COPY
     * SftpOperationParamEnums.SOURCE_FILE
     * SftpOperationParamEnums.TARGET_FILE
     * 先下载到本地临时目录再上传
     */
    private static final AbstractSftpOperationTemplate COPY_OPERATION
            = new AbstractSftpOperationTemplate<Boolean>() {

        @Override
        protected void initOperationType() {
            this.setOperationType(SftpOperationTypeEnums.COPY);
        }

        @Override
        protected SftpOperationResponse<Boolean> doBusiness(SFTPUserInfo user
                , Map<SftpOperationParamEnums, String> params) throws Exception{
            SftpOperationResponse<Boolean> response = new SftpOperationResponse<Boolean>();

            String localTmpPath = params.get(SftpOperationParamEnums.LOCAL_TMP_PATH);
            String srcFile = params.get(SftpOperationParamEnums.SOURCE_FILE);
            String targetFile = params.get(SftpOperationParamEnums.TARGET_FILE);

            String localTmpFileName = localTmpPath + "/" + new File(srcFile).getName();

            Map<SftpOperationParamEnums, String> downloadOperationParams
                    = new HashMap<SftpOperationParamEnums, String>();
            downloadOperationParams.put(SftpOperationParamEnums.TARGET_FILE, localTmpFileName);
            downloadOperationParams.put(SftpOperationParamEnums.SOURCE_FILE, srcFile);
            DOWNLOAD_OPERATION.doBusiness(user, downloadOperationParams);

            Map<SftpOperationParamEnums, String> uploadOperationParams
                    = new HashMap<SftpOperationParamEnums, String>();
            uploadOperationParams.put(SftpOperationParamEnums.TARGET_FILE, targetFile);
            uploadOperationParams.put(SftpOperationParamEnums.SOURCE_FILE, localTmpFileName);
            UPLOAD_OPERATION.doBusiness(user, uploadOperationParams);

            try{
                File localTmpFile = new File(localTmpFileName);

                localTmpFile.delete();
            }catch (Exception e){
                RdfFileLogUtil.common.warn("copy operation delete tmp file fail,ignored", e);
            }

            response.setData(true);
            response.setSuccess(true);
            return response;
        }

        @Override
        protected boolean checkBeforeDoBiz(SFTPUserInfo user, Map<SftpOperationParamEnums, String> params) {
            return params.containsKey(SftpOperationParamEnums.SOURCE_FILE)
                    && params.containsKey(SftpOperationParamEnums.TARGET_FILE)
                    && params.containsKey(SftpOperationParamEnums.LOCAL_TMP_PATH);
        }
    };


    /**
     * 判断文件是否存在。
     *
     * SFTPUserInfo
     *   SFTP用户信息
     * SftpOperationTypeEnums.FILE_EXISTS
     * SftpOperationParamEnums.TARGET_FILE
     */
    private static final AbstractSftpOperationTemplate FILE_EXISTS_OPERATION
            = new AbstractSftpOperationTemplate<SftpATTRS>() {

        @Override
        protected void initOperationType() {
            this.setOperationType(SftpOperationTypeEnums.FILE_EXISTS);
        }

        @Override
        protected SftpOperationResponse<SftpATTRS> doBusiness(SFTPUserInfo user
                , Map<SftpOperationParamEnums, String> params) throws Exception{
            SftpOperationResponse<SftpATTRS> response = new SftpOperationResponse<SftpATTRS>();
            String remoteFile = params.get(SftpOperationParamEnums.TARGET_FILE);
            ChannelSftp sftp = SftpThreadContext.getChannelSftp();
            try{
                // 获取文件属性,如果获取不到或者获取异常,则认为文件不存在
                SftpATTRS sftpATTRS = sftp.stat(remoteFile);
                response.setData(sftpATTRS);
            }catch (Exception e){
                response.setData(null);
            }
            response.setSuccess(true);
            return response;
        }

        @Override
        protected boolean checkBeforeDoBiz(SFTPUserInfo user, Map<SftpOperationParamEnums, String> params) {
            return params.containsKey(SftpOperationParamEnums.TARGET_FILE);
        }
    };


    /**
     * 指定全路径创建文件。
     *
     * SFTPUserInfo
     *   SFTP用户信息
     * SftpOperationTypeEnums.CREATE
     * SftpOperationParamEnums.TARGET_FILE
     */
    private static final AbstractSftpOperationTemplate CREATE_OPERATION
            = new AbstractSftpOperationTemplate<Boolean>() {

        @Override
        protected void initOperationType() {
            this.setOperationType(SftpOperationTypeEnums.CREATE);
        }

        @Override
        protected SftpOperationResponse<Boolean> doBusiness(SFTPUserInfo user
                , Map<SftpOperationParamEnums, String> params) throws Exception{
            SftpOperationResponse<Boolean> response = new SftpOperationResponse<Boolean>();
            String targetFile = params.get(SftpOperationParamEnums.TARGET_FILE);
            ChannelSftp sftp = SftpThreadContext.getChannelSftp();
            SFTPHelper.createFTPDirIfnotExist(sftp, targetFile);
            sftp.put(new ByteArrayInputStream(FileSftpStorageConstants.EMPTY_STRING.getBytes()), targetFile);
            response.setSuccess(true);
            response.setData(true);
            return response;
        }

        @Override
        protected boolean checkBeforeDoBiz(SFTPUserInfo user, Map<SftpOperationParamEnums, String> params) {
            return params.containsKey(SftpOperationParamEnums.TARGET_FILE);
        }
    };

    /**
     * 删除文件/目录。
     * 如果目录下存在文件则删除目录下的文件最后删除目录
     * SFTPUserInfo
     *   SFTP用户信息
     * SftpOperationTypeEnums.DEL
     * SftpOperationParamEnums.TARGET_FILE
     */
    private static final AbstractSftpOperationTemplate DEL_OPERATION
            = new AbstractSftpOperationTemplate<Boolean>() {

        @Override
        protected void initOperationType() {
            this.setOperationType(SftpOperationTypeEnums.DEL);
        }

        @Override
        protected SftpOperationResponse<Boolean> doBusiness(SFTPUserInfo user
                , Map<SftpOperationParamEnums, String> params) throws Exception{
            SftpOperationResponse<Boolean> response = new SftpOperationResponse<Boolean>();
            String target = params.get(SftpOperationParamEnums.TARGET_FILE);
            ChannelSftp sftp = SftpThreadContext.getChannelSftp();
            SftpATTRS sftpATTRS = null;
            boolean isExists = false;
            try{
                // 获取文件属性,如果获取不到或者获取异常,则认为文件不存在
                sftpATTRS = sftp.stat(target);
                isExists = true;
            }catch (Exception e){
                //文件不存在那么删除也失败
                response.setData(false);
                isExists = false;
            }
            if(!isExists){
                return response;
            }
            if(sftpATTRS.isDir()){
                SFTPHelper.removeDir(sftp, target);
            }else{
                sftp.rm(target);
            }
            response.setData(true);
            response.setSuccess(true);
            return response;
        }

        @Override
        protected boolean checkBeforeDoBiz(SFTPUserInfo user, Map<SftpOperationParamEnums, String> params) {
            return params.containsKey(SftpOperationParamEnums.TARGET_FILE);
        }
    };

    /**
     * 列出sftp目录下的所有文件
     *
     * SFTPUserInfo
     *   SFTP用户信息
     * SftpOperationTypeEnums.LIST_FILES
     * SftpOperationParamEnums.TARGET_DIR
     */
    private static final AbstractSftpOperationTemplate LIST_FILES_OPERATION
            = new AbstractSftpOperationTemplate<Vector<SftpFileEntry>>() {

        @Override
        protected void initOperationType() {
            this.setOperationType(SftpOperationTypeEnums.LIST_FILES);
        }

        @Override
        protected SftpOperationResponse<Vector<SftpFileEntry>> doBusiness(SFTPUserInfo user
                , Map<SftpOperationParamEnums, String> params) throws Exception{
            SftpOperationResponse<Vector<SftpFileEntry>> response = new SftpOperationResponse<Vector<SftpFileEntry>>();
            String targetDir = params.get(SftpOperationParamEnums.TARGET_DIR);
            boolean recursiveList = FileSftpStorageConstants.T
                    .equals(params.get(SftpOperationParamEnums.RECURSIVE_LIST));
            ChannelSftp sftp = SftpThreadContext.getChannelSftp();

            Vector<SftpFileEntry> fileEntries = SFTPHelper.listFiles(sftp, targetDir, recursiveList);

            response.setSuccess(true);
            response.setData(fileEntries);
            return response;
        }

        @Override
        protected boolean checkBeforeDoBiz(SFTPUserInfo user, Map<SftpOperationParamEnums, String> params) {
            return params.containsKey(SftpOperationParamEnums.TARGET_DIR);
        }
    };


}