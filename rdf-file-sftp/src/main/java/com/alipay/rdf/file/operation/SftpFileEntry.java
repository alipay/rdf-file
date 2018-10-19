/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.alipay.rdf.file.operation;

import com.alipay.rdf.file.interfaces.FileSftpStorageConstants;
import com.alipay.rdf.file.util.RdfFileUtil;
import com.jcraft.jsch.ChannelSftp.LsEntry;

/**
 *
 * @author haofan.whf
 * @version $Id: SftpFileEntry.java, v 0.1 2018年10月09日 下午8:42 haofan.whf Exp $
 */
public class SftpFileEntry {

    /**
     * 文件全路径名称
     */
    private String fullFileName;

    /**
     * 文件ls信息
     */
    private LsEntry lsEntry;

    private SftpFileEntry(){}

    public static SftpFileEntry buildFileEntry(String fullFileName, LsEntry lsEntry){
        SftpFileEntry fileEntry = new SftpFileEntry();
        fileEntry.setFullFileName(fullFileName);
        fileEntry.setLsEntry(lsEntry);
        return fileEntry;
    }

    /**
     * 是否是目录
     * @return
     */
    public boolean isDir(){
        return this.getLsEntry().getAttrs().isDir();
    }

    /**
     * 是否是.
     */
    public boolean isCurrentDir(){
        return this.getLsEntry().getAttrs().isDir()
                && FileSftpStorageConstants.CURRENT_DIR.equals(this.getLsEntry().getFilename());
    }

    /**
     * 返回文件名带路径
     * @return
     */
    public String getFullFileName(){
        return this.fullFileName;
    }

    /**
     * 是否是..
     */
    public boolean isPrevDir(){
        return this.getLsEntry().getAttrs().isDir()
                && FileSftpStorageConstants.PREV_DIR.equals(this.getLsEntry().getFilename());
    }

    /**
     * Getter method for property lsEntry.
     *
     * @return property value of lsEntry
     */
    public LsEntry getLsEntry() {
        return lsEntry;
    }

    /**
     * Setter method for property lsEntry.
     *
     * @param lsEntry value to be assigned to property lsEntry
     */
    public void setLsEntry(LsEntry lsEntry) {
        this.lsEntry = lsEntry;
    }

    /**
     * Setter method for property fullFileName.
     *
     * @param fullFileName value to be assigned to property fullFileName
     */
    public void setFullFileName(String fullFileName) {
        this.fullFileName = fullFileName;
    }
}