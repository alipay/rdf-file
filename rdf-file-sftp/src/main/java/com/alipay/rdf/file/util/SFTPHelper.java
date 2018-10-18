/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2012 All Rights Reserved.
 */
package com.alipay.rdf.file.util;

import java.io.File;
import java.util.Vector;

import org.apache.commons.io.FilenameUtils;

import com.alipay.rdf.file.exception.RdfErrorEnum;
import com.alipay.rdf.file.exception.RdfFileException;
import com.alipay.rdf.file.operation.SftpFileEntry;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.SftpException;

/**
 * SFTP文件传输帮助类。
 * 
 * <p>
 * 通过JSch实现SFTP文件上传和下载功能。
 * </p>
 * 
 * @author haofan.whf
 * @version $Id: SFTPHelper.java, v 0.1 2018-10-4 下午20:04:06 haofan.whf Exp $
 */
public class SFTPHelper {


	/**
	 * 创建本地目录，如果目录不存在。
	 * 
	 * @param file
	 * @return true表示创建成功或目录已存在，false表示失败
	 */
	public static boolean createLocalDirIfnotExist(String file) {
		boolean success = false;
		// 取文件的路径
		String path = FilenameUtils.getFullPath(file);
		try {
			File dir = new File(path);
			// 目录不存在，则创建之。
			if (!dir.exists()) {
				dir.mkdirs();
			}
			success = true;
		} catch (Throwable e) {
			RdfFileLogUtil.common.warn("rdf-file#SFTPHelper.createLocalDirIfnotExist异常,"
					+ ",file={" + file + "}");
			success = false;
		}
		return success;
	}

	/**
	 * 创建FTP目录，如果目录不存在。
	 *
	 * @param sftp
	 * @throws SftpException
	 */
	public static void createFTPDirIfnotExist(ChannelSftp sftp, String file) {
		// 取出文件目录
		String dir = FilenameUtils.getFullPath(file);

		if (RdfFileUtil.isNotBlank(dir)) {
			mkdirs(sftp, dir, 3);
		}
	}

	/**
	 * mkdir -p dir
	 */
	private static void mkdirs(ChannelSftp sftp, String dir, int retryTime){
		String separator = "/"; // File.separator;
		String[] names = dir.split(separator);
		if (names != null && names.length > 0) {
			String parent = separator;
			boolean success = false;
			while (!success && --retryTime >= 0) {
				String current = null;
				success = true;
				for (int i = 0; i < names.length; i++) {
					if (RdfFileUtil.isNotBlank(names[i])) {
						current = parent + names[i] + separator;
						parent = current;
						try{
							mkdirIfNotExist(sftp, current);
						}catch (SftpException e){
							RdfFileLogUtil.common.warn("rdf-file#SFTPHelper.mkdirs"
									+ "创建SFTP目录失败，3秒后尝试再次创建，剩余创建尝试次数：{" + retryTime + "}"
									+ ",current={" + current + "},dir={" + dir + "}", e);
							success = false;
							try {
								Thread.sleep(3000);
							} catch (InterruptedException e1) {
								RdfFileLogUtil.common.warn("rdf-file#SFTPHelper.mkdirs"
										+ "等待中断dir={" + dir + "}", e1);
							}
						}
					}
				}
			}
			if(!success){
				throw new RdfFileException("rdf-file#SFTPHelper.mkdirs,创建SFTP目录彻底失败,dir={" + dir + "}"
						, RdfErrorEnum.UNKOWN);
			}
		}
	}

	/**
	 * 当前路径下创建文件夹
	 * @param sftp
	 * @param currentPath
	 * @throws SftpException
	 */
	private synchronized static void mkdirIfNotExist(ChannelSftp sftp, String currentPath) throws SftpException{
		try {
			sftp.cd(currentPath);
		} catch (SftpException e) {
			// 如目录不存在，则创建之
			try {
				sftp.mkdir(currentPath);
				RdfFileLogUtil.common.info("rdf-file#SFTPHelper"
						+ ".mkdirIfNotExist,create path={" + currentPath + "} success.");
			} catch (SftpException e1) {
				RdfFileLogUtil.common.warn("rdf-file#SFTPHelper"
						+ ".mkdirIfNotExist,create path={" + currentPath + "} failed.");
				throw e1;
			}
		}
	}

	/**
	 *
	 * @param sftp
	 * @param dir
	 * @param recursiveList  递归查询
	 * @return
	 * @throws SftpException
	 */
	public static Vector<SftpFileEntry> listFiles(ChannelSftp sftp, String dir, boolean recursiveList) throws SftpException {
		Vector<SftpFileEntry> fileEntries = new Vector<SftpFileEntry>();
		listAllFiles(sftp, dir, fileEntries, recursiveList);
		return fileEntries;
	}

	private static void listAllFiles(ChannelSftp sftp, String dir, Vector<SftpFileEntry> fileEntries, boolean recursiveList) throws SftpException {
		// 如果参数不是目录则抛出异常
		if (!sftp.stat(dir).isDir()) {
			throw new RdfFileException("target is not dir, target=" + dir, RdfErrorEnum.UNSUPPORTED_OPERATION);
		}
		Vector<LsEntry> currentLsEntryVec = sftp.ls(dir);
		for(LsEntry lsEntry : currentLsEntryVec){
			SftpFileEntry sftpFileEntry = SftpFileEntry
					.buildFileEntry(RdfFileUtil.combinePath(dir, lsEntry.getFilename()), lsEntry);
			if(sftpFileEntry.isDir()){
				if(recursiveList && (!sftpFileEntry.isCurrentDir() && !sftpFileEntry.isPrevDir())) {
					listAllFiles(sftp, sftpFileEntry.getFullFileName(), fileEntries, true);
				}
			}else{
				fileEntries.add(sftpFileEntry);
			}
		}
	}

	/**
	 * 递归删除目录下的所有目录和文件
	 * @param sftp
	 * @param dir
	 * @throws SftpException
	 */
	public static void removeDir(ChannelSftp sftp, String dir) throws SftpException {
		doRemove(sftp, dir);
	}

	private static void doRemove(ChannelSftp sftp, String dir) throws SftpException{
		// 如果参数不是目录则抛出异常
		if (!sftp.stat(dir).isDir()) {
			throw new RdfFileException("target is not dir, target=" + dir, RdfErrorEnum.UNSUPPORTED_OPERATION);
		}
		Vector<LsEntry> currentLsEntryVec = sftp.ls(dir);
		for(LsEntry lsEntry : currentLsEntryVec){
			SftpFileEntry sftpFileEntry = SftpFileEntry
					.buildFileEntry(RdfFileUtil.combinePath(dir, lsEntry.getFilename()), lsEntry);
			if(sftpFileEntry.isDir()){
				if((!sftpFileEntry.isCurrentDir() && !sftpFileEntry.isPrevDir())){
					doRemove(sftp, sftpFileEntry.getFullFileName());
				}
			}else{
				sftp.rm(sftpFileEntry.getFullFileName());
			}
		}
		sftp.rmdir(dir);
	}

}
