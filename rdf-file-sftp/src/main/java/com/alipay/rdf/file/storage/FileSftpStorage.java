package com.alipay.rdf.file.storage;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Pattern;

import com.alipay.rdf.file.exception.RdfErrorEnum;
import com.alipay.rdf.file.exception.RdfFileException;
import com.alipay.rdf.file.interfaces.FileSftpStorageConstants;
import com.alipay.rdf.file.model.FileConfig;
import com.alipay.rdf.file.model.FileInfo;
import com.alipay.rdf.file.model.StorageConfig;
import com.alipay.rdf.file.operation.AbstractSftpOperationTemplate;
import com.alipay.rdf.file.operation.SftpFileEntry;
import com.alipay.rdf.file.operation.SftpOperationFactory;
import com.alipay.rdf.file.operation.SftpOperationParamEnums;
import com.alipay.rdf.file.operation.SftpOperationResponse;
import com.alipay.rdf.file.operation.SftpOperationTypeEnums;
import com.alipay.rdf.file.spi.RdfFileStorageSpi;
import com.alipay.rdf.file.util.RdfFileLogUtil;
import com.alipay.rdf.file.util.RdfFileUtil;
import com.alipay.rdf.file.util.SFTPUserInfo;
import com.jcraft.jsch.SftpATTRS;

/**
 * @author haofan.whf
 */
public class FileSftpStorage implements RdfFileStorageSpi {

	private SFTPUserInfo sftpUserInfo;

	private SftpConfig sftpConfig;

	@Override
	public void copy(String srcFile, String toFile) {

		Map<String, String> params = new HashMap<String, String>();

		params.put(SftpOperationParamEnums.SOURCE_FILE.toString(), srcFile);
		params.put(SftpOperationParamEnums.TARGET_FILE.toString(), toFile);
		params.put(SftpOperationParamEnums.LOCAL_TMP_PATH.toString(), sftpConfig.getLocalTmpPath());

		AbstractSftpOperationTemplate operationTemplate
				= SftpOperationFactory.getOperation(SftpOperationTypeEnums.COPY);

		SftpOperationResponse<Boolean> response = operationTemplate.handle(sftpUserInfo, params);

		if (!response.isSuccess()) {
			throw new RdfFileException("rdf-file#FileSftpStorage.copy,sftp copy fail"
					+ "，srcFile=" + srcFile + ",toFile=" + toFile, response.getError(), RdfErrorEnum.UNKOWN);
		}
	}

	@Override
	public void createNewFile(String fileFullPath) {

		Map<String, String> params = new HashMap<String, String>();

		params.put(SftpOperationParamEnums.TARGET_FILE.toString(), fileFullPath);

		AbstractSftpOperationTemplate operationTemplate
				= SftpOperationFactory.getOperation(SftpOperationTypeEnums.CREATE);

		SftpOperationResponse<Boolean> response = operationTemplate.handle(sftpUserInfo, params);

		if (!response.isSuccess()) {
			throw new RdfFileException("rdf-file#FileSftpStorage.createNewFile,sftp createNewFile fail"
					+ "，targetFile=" + fileFullPath , response.getError(), RdfErrorEnum.UNKOWN);
		}
	}

	@Override
	public void delete(String fileFullPath) {

		Map<String, String> params = new HashMap<String, String>();

		params.put(SftpOperationParamEnums.TARGET_FILE.toString(), fileFullPath);

		AbstractSftpOperationTemplate operationTemplate
				= SftpOperationFactory.getOperation(SftpOperationTypeEnums.DEL);

		SftpOperationResponse<Boolean> response = operationTemplate.handle(sftpUserInfo, params);

		if (!response.isSuccess()) {
			throw new RdfFileException("rdf-file#FileSftpStorage.del,sftp del fail"
					+ "，targetFile=" + fileFullPath , response.getError(), RdfErrorEnum.UNKOWN);
		}
	}

	@Override
	public void download(String srcFile, String toFile) {

		Map<String, String> params = new HashMap<String, String>();

		params.put(SftpOperationParamEnums.SOURCE_FILE.toString(), srcFile);
		params.put(SftpOperationParamEnums.TARGET_FILE.toString(), toFile);

		AbstractSftpOperationTemplate operationTemplate
				= SftpOperationFactory.getOperation(SftpOperationTypeEnums.DOWNLOAD);

		SftpOperationResponse<Boolean> response = operationTemplate.handle(sftpUserInfo, params);

		if (!response.isSuccess()) {
			throw new RdfFileException("rdf-file#FileSftpStorage.download,sftp download fail"
					+ "，srcFile=" + srcFile + ",toFile=" + toFile, response.getError(), RdfErrorEnum.UNKOWN);
		}
	}

	@Override
	public FileInfo getFileInfo(String filePath) {

		Map<String, String> params = new HashMap<String, String>();

		params.put(SftpOperationParamEnums.TARGET_FILE.toString(), filePath);

		AbstractSftpOperationTemplate operationTemplate
				= SftpOperationFactory.getOperation(SftpOperationTypeEnums.FILE_EXISTS);

		SftpOperationResponse<SftpATTRS> response = operationTemplate.handle(sftpUserInfo, params);

		FileInfo fileInfo = new FileInfo();

		if(!response.isSuccess()){
			throw new RdfFileException("rdf-file#FileSftpStorage.getFileInfo,sftp check file exists fail"
					+ "，fileName=" + filePath, response.getError(), RdfErrorEnum.UNKOWN);
		}

		SftpATTRS sftpATTRS = response.getData();

		fileInfo.setExists(sftpATTRS != null);
		fileInfo.setFileName(new File(filePath).getName());
		long size = !fileInfo.isExists() ? 0 : sftpATTRS.getSize();
		fileInfo.setSize(size);
		return fileInfo;
	}

	@Override
	public List<String> listAllFiles(String folderName, String[] regexs) {
		SftpOperationResponse<Vector<SftpFileEntry>> response = listFiles(folderName, true);

		return filterPaths(buildPathsFromFileEntries(response.getData()), regexs);
	}

	@Override
	public List<String> listAllFiles(String folderName, FilePathFilter... fileFilters) {
		SftpOperationResponse<Vector<SftpFileEntry>> response = listFiles(folderName, true);

		return filterPaths(buildPathsFromFileEntries(response.getData()), fileFilters);
	}

	@Override
	public List<String> listFiles(String folderName, String[] regexs) {
		SftpOperationResponse<Vector<SftpFileEntry>> response = listFiles(folderName, false);

		return filterPaths(buildPathsFromFileEntries(response.getData()), regexs);
	}

	@Override
	public List<String> listFiles(String folderName, FilePathFilter... fileFilters) {

		SftpOperationResponse<Vector<SftpFileEntry>> response = listFiles(folderName, false);

		return filterPaths(buildPathsFromFileEntries(response.getData()), fileFilters);
	}

	@Override
	public void rename(String srcFile, String toFile) {
		Map<String, String> params = new HashMap<String, String>();

		params.put(SftpOperationParamEnums.SOURCE_FILE.toString(), srcFile);
		params.put(SftpOperationParamEnums.TARGET_FILE.toString(), toFile);

		AbstractSftpOperationTemplate operationTemplate
				= SftpOperationFactory.getOperation(SftpOperationTypeEnums.RENAME);

		SftpOperationResponse<Boolean> response = operationTemplate.handle(sftpUserInfo, params);

		if(!response.isSuccess()){
			throw new RdfFileException("rdf-file#FileSftpStorage.rename,sftp rename file fail"
					+ "，srcFile=" + srcFile + ",toFile=" + toFile, response.getError(), RdfErrorEnum.UNKOWN);
		}
	}

	@Override
	public void upload(String srcFile, String toFile, boolean override) {

		boolean isExist = getFileInfo(toFile).isExists();

		boolean result = false;
		try {

			if (!isExist || override) {
				Map<String, String> params = new HashMap<String, String>();

				params.put(SftpOperationParamEnums.SOURCE_FILE.toString(), srcFile);
				params.put(SftpOperationParamEnums.TARGET_FILE.toString(), toFile);

				AbstractSftpOperationTemplate operationTemplate
						= SftpOperationFactory.getOperation(SftpOperationTypeEnums.UPLOAD);

				SftpOperationResponse<Boolean> response = operationTemplate.handle(sftpUserInfo, params);

				if(!response.isSuccess()){
					throw new RdfFileException("rdf-file#FileSftpStorage.upload,sftp upload file fail"
							+ "，srcFile=" + srcFile + ",toFile=" + toFile, response.getError(), RdfErrorEnum.UNKOWN);
				}
				result = response.getData();
			} else {
				RdfFileLogUtil.common.warn("rdf-file#FileSftpStorage.upload,file already exist,abort.toFile=" + toFile);

				result = true;
			}

		} catch (Exception e) {
			throw new RdfFileException("rdf-file#FileSftpStorage.upload,sftp upload file fail"
					+ "，srcFile=" + srcFile + ",toFile=" + toFile, e, RdfErrorEnum.UNKOWN);
		}

		if (!result) {
			throw new RdfFileException("rdf-file#FileSftpStorage.upload,sftp upload file fail"
					+ "，srcFile=" + srcFile + ",toFile=" + toFile, RdfErrorEnum.UNKOWN);
		}
	}

	@Override
	public InputStream getInputStream(String filename) {
		throw new RdfFileException("rdf-file#FileSftpStorage.getInputStream", RdfErrorEnum.UNSUPPORTED_OPERATION);
	}

	@Override
	public InputStream getInputStream(String filename, long start, long length) {
		throw new RdfFileException("rdf-file#FileSftpStorage.getInputStream", RdfErrorEnum.UNSUPPORTED_OPERATION);
	}

	@Override
	public InputStream getTailInputStream(FileConfig fileConfig) {
		throw new RdfFileException("rdf-file#FileSftpStorage.getTailInputStream", RdfErrorEnum.UNSUPPORTED_OPERATION);
	}

	@Override
	public void init(StorageConfig config) {
		SftpConfig sftpConfig = (SftpConfig) config.getParam(SftpConfig.SFTP_STORAGE_CONFIG_KEY);
		RdfFileUtil.assertNotNull(config, "rdf-file#StorageConfig中没有传递key="
						+ SftpConfig.SFTP_STORAGE_CONFIG_KEY + " 的SftpConfig对象参数",
				RdfErrorEnum.ILLEGAL_ARGUMENT);
		this.sftpConfig = sftpConfig;
		this.sftpUserInfo = new SFTPUserInfo(sftpConfig);
	}


	private SftpOperationResponse<Vector<SftpFileEntry>> listFiles(String folderName, boolean recursiveList){
		Map<String, String> params = new HashMap<String, String>();

		params.put(SftpOperationParamEnums.TARGET_DIR.toString(), folderName);
		params.put(SftpOperationParamEnums.RECURSIVE_LIST.toString(), recursiveList
				? FileSftpStorageConstants.T : FileSftpStorageConstants.F);

		AbstractSftpOperationTemplate operationTemplate
				= SftpOperationFactory.getOperation(SftpOperationTypeEnums.LIST_FILES);

		SftpOperationResponse<Vector<SftpFileEntry>> response = operationTemplate.handle(sftpUserInfo, params);

		if(!response.isSuccess()){
			throw new RdfFileException("rdf-file#FileSftpStorage.listFiles,sftp ls file fail"
					+ "，folderName=" + folderName, response.getError(), RdfErrorEnum.UNKOWN);
		}

		return response;
	}

	private List<String> filterPaths(List<String> paths, FilePathFilter... fileFilters){
		try {
			if (fileFilters == null || fileFilters.length == 0) {
				return paths;
			}
			List<String> result = new ArrayList<String>();
			for (String path : paths) {
				for (FilePathFilter filter : fileFilters) {
					if (filter.accept(path)) {
						result.add(path);
						break;
					}
				}
			}
			return result;
		} catch (Exception e) {
			throw new RdfFileException("rdf-file#FileSftpStorage.filterPath,filterPath fail"
					+ "，paths=" + paths, e, RdfErrorEnum.UNKOWN);
		}
	}

	private List<String> filterPaths(List<String> paths, String[] regexs){
		List<String> result = new ArrayList<String>();
		for (String filePath : paths) {
			String temp = "";
			if (filePath.endsWith("/")) {
				//文件夹
				temp = filePath.substring(0, filePath.length() - 1);
			} else {
				temp = filePath;
			}
			String name = temp.substring(temp.lastIndexOf("/") + 1, temp.length());
			for (String regex : regexs) {
				Pattern pattern = Pattern.compile(regex);
				if (pattern.matcher(name).matches()) {
					result.add(filePath);
					break;
				}
			}
		}
		return result;
	}

	private List<String> buildPathsFromFileEntries(Vector<SftpFileEntry> entries){
		List<String> paths = new ArrayList<String>();
		for (SftpFileEntry entry : entries) {
			paths.add(entry.getFullFileName());
		}
		return paths;
	}

	public SFTPUserInfo getUserInfo(){
		return this.sftpUserInfo;
	}


}
