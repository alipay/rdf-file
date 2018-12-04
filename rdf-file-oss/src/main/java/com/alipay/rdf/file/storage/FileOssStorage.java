package com.alipay.rdf.file.storage;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import com.alipay.rdf.file.exception.RdfErrorEnum;
import com.alipay.rdf.file.exception.RdfFileException;
import com.alipay.rdf.file.interfaces.FileFactory;
import com.alipay.rdf.file.model.FileConfig;
import com.alipay.rdf.file.model.FileInfo;
import com.alipay.rdf.file.model.FileSlice;
import com.alipay.rdf.file.model.StorageConfig;
import com.alipay.rdf.file.spi.RdfFileSplitterSpi;
import com.alipay.rdf.file.spi.RdfFileStorageSpi;
import com.alipay.rdf.file.util.RdfFileLogUtil;
import com.alipay.rdf.file.util.RdfFileUtil;
import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSErrorCode;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.AppendObjectRequest;
import com.aliyun.oss.model.AppendObjectResult;
import com.aliyun.oss.model.CompleteMultipartUploadRequest;
import com.aliyun.oss.model.GetObjectRequest;
import com.aliyun.oss.model.InitiateMultipartUploadRequest;
import com.aliyun.oss.model.InitiateMultipartUploadResult;
import com.aliyun.oss.model.ListObjectsRequest;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.OSSObjectSummary;
import com.aliyun.oss.model.ObjectListing;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PartETag;
import com.aliyun.oss.model.UploadFileRequest;
import com.aliyun.oss.model.UploadPartCopyRequest;
import com.aliyun.oss.model.UploadPartCopyResult;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 * 
 * oss 存储操作
 * @author hongwei.quhw
 * @version $Id: FileOssStorage.java, v 0.1 2017年4月7日 下午3:56:04 hongwei.quhw Exp $
 */
public class FileOssStorage implements RdfFileStorageSpi {
    private OssConfig ossConfig;
    private OSSClient client;

    @Override
    public void init(StorageConfig storageConfig) {
        OssConfig config = (OssConfig) storageConfig.getParam(OssConfig.OSS_STORAGE_CONFIG_KEY);
        RdfFileUtil.assertNotNull(config, "rdf-file#StorageConfig中没有传递key="
                                          + OssConfig.OSS_STORAGE_CONFIG_KEY + " 的OssConfig对象参数",
            RdfErrorEnum.ILLEGAL_ARGUMENT);
        this.client = new OSSClient(config.getEndpoint(), config.getAccessKeyId(),
            config.getAccessKeySecret(), config.getClientConfiguration());
        if (!client.doesBucketExist(config.getBucketName())) {
            client.createBucket(config.getBucketName());
        }
        this.ossConfig = config;
    }

    /** 
     * @see com.alipay.rdf.file.storage.FileInnterStorage#getInputStream(java.lang.String)
     */
    @Override
    public InputStream getInputStream(String filePath) {
        filePath = toOSSPath(filePath);
        if (isExist(filePath)) {
            OSSObject object = client.getObject(ossConfig.getBucketName(), filePath);
            return new OssInputStream(object);
        } else {
            throw new RdfFileException(
                "rdf-file#FileOssStorage.getInputStream(filePath=" + filePath + "), 文件不存在",
                RdfErrorEnum.NOT_EXSIT);
        }
    }

    /** 
     * @see com.alipay.rdf.file.storage.FileInnterStorage#getInputStream(java.lang.String, long, long)
     */
    @Override
    public InputStream getInputStream(String filePath, long start, long length) {
        if (length <= 0) {
            throw new RdfFileException("rdf-file#FileOssStorage.getInputStream(filePath=" + filePath
                                       + ", start=" + start + ", length=" + length + "获取数据长度必须大于零",
                RdfErrorEnum.ILLEGAL_ARGUMENT);
        }

        filePath = toOSSPath(filePath);
        if (isExist(filePath)) {
            GetObjectRequest getObjectRequest = new GetObjectRequest(ossConfig.getBucketName(),
                filePath);
            getObjectRequest.setRange(start, start + length - 1);
            OSSObject object = client.getObject(getObjectRequest);
            return new OssInputStream(object);
        } else {
            throw new RdfFileException("rdf-file#FileOssStorage.getInputStream(filePath=" + filePath
                                       + ", start=" + start + ", length=" + length + ")",
                RdfErrorEnum.NOT_EXSIT);
        }
    }

    /** 
     * @see com.alipay.rdf.file.storage.FileInnterStorage#getTailInputStream(com.alipay.rdf.file.model.FileConfig)
     */
    @Override
    public InputStream getTailInputStream(FileConfig fileConfig) {
        RdfFileSplitterSpi fileSplitter = (RdfFileSplitterSpi) FileFactory
            .createSplitter(fileConfig.getStorageConfig());
        FileSlice fileSlice = fileSplitter.getTailSlice(fileConfig);
        if (null == fileSlice) {
            return null;
        }
        return getInputStream(fileConfig.getFilePath(), fileSlice.getStart(),
            fileSlice.getLength());
    }

    /** 
     * @see com.alipay.rdf.file.interfaces.FileStorage#createNewFile(java.lang.String)
     */
    @Override
    public void createNewFile(String filePath) {
        filePath = toOSSPath(filePath);

        ObjectMetadata objectMeta = new ObjectMetadata();
        byte[] buffer = new byte[0];
        ByteArrayInputStream in = new ByteArrayInputStream(buffer);
        objectMeta.setContentLength(0);
        try {
            client.putObject(ossConfig.getBucketName(), filePath, in, objectMeta);
        } catch (Exception e) {
            throw new RdfFileException("rdf-file#创建OSS文件异常！", e, RdfErrorEnum.IO_ERROR);
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                if (RdfFileLogUtil.common.isWarn()) {
                    RdfFileLogUtil.common.warn("rdf-file#创建OSS文件时关闭流异常！", e);
                }
            }
        }
    }

    /** 
     * @see com.alipay.rdf.file.interfaces.FileStorage#getFileInfo(java.lang.String)
     */
    @Override
    public FileInfo getFileInfo(String filePath) {
        filePath = toOSSPath(filePath);
        FileInfo fileInfo = new FileInfo();
        try {
            ObjectMetadata metaData = client.getObjectMetadata(ossConfig.getBucketName(), filePath);
            fileInfo.setFileName(new File(filePath).getName());
            fileInfo.setLastModifiedDate(metaData.getLastModified());
            fileInfo.setSize(metaData.getContentLength());
            fileInfo.setExists(true);
            fileInfo.getMetadata().putAll(metaData.getRawMetadata());
            fileInfo.getUserMetadata().putAll(metaData.getUserMetadata());
        } catch (OSSException e) {
            if (OSSErrorCode.NO_SUCH_KEY.equals(e.getErrorCode())) {
                fileInfo.setExists(false);
            } else {
                throw e;
            }
        }
        return fileInfo;
    }

    /** 
     * @see com.alipay.rdf.file.interfaces.FileStorage#listFiles(java.lang.String, java.lang.String[])
     */
    @Override
    public List<String> listFiles(String folderName, String[] regexs) {
        return listFilesWithRegex(folderName, regexs, false);
    }

    /** 
     * @see com.alipay.rdf.file.interfaces.FileStorage#listFiles(java.lang.String, com.alipay.rdf.file.interfaces.FileStorage.FilePathFilter[])
     */
    @Override
    public List<String> listFiles(String folderName, FilePathFilter... fileFilters) {
        return listFilesWithFilter(folderName, false, fileFilters);
    }

    /**
     * helper method for adding FilePathFilter check when listing files
     * 
     * @param filePaths
     * @param fileFilters
     * @return
     */
    private List<String> listFilesWithFilter(String folderName, boolean all,
                                             FilePathFilter... fileFilters) {
        List<String> filePaths = listFilesHandler(folderName, null, all);
        if (fileFilters == null || fileFilters.length == 0) {
            return filePaths;
        }
        List<String> result = new ArrayList<String>();
        for (String path : filePaths) {
            for (FilePathFilter filter : fileFilters) {
                if (filter.accept(path)) {
                    result.add(path);
                    break;
                }
            }
        }
        return result;
    }

    /** 
     * @see com.alipay.rdf.file.interfaces.FileStorage#listAllFiles(java.lang.String, java.lang.String[])
     */
    @Override
    public List<String> listAllFiles(String folderName, String[] regexs) {
        return listFilesWithRegex(folderName, regexs, true);
    }

    /** 
     * @see com.alipay.rdf.file.interfaces.FileStorage#listAllFiles(java.lang.String, com.alipay.rdf.file.interfaces.FileStorage.FilePathFilter[])
     */
    @Override
    public List<String> listAllFiles(String folderName, FilePathFilter... fileFilters) {
        return listFilesWithFilter(folderName, true, fileFilters);
    }

    /** 
     * @see com.alipay.rdf.file.interfaces.FileStorage#download(java.lang.String, java.lang.String)
     */
    @Override
    public void download(String srcOSSPath, String toFile) {
        srcOSSPath = toOSSPath(srcOSSPath);
        List<String> fileNames = listAllFiles(srcOSSPath);
        if (isExist(srcOSSPath)) {
            fileNames.add(srcOSSPath);
        } else {
            throw new RdfFileException("rdf-file# oss donwLoad srcOSSPath=" + srcOSSPath + " 不存在",
                RdfErrorEnum.NOT_EXSIT);
        }
        String temp = "";
        for (String name : fileNames) {
            if (!name.equals(srcOSSPath)) {
                toFile = toFile.replaceAll("\\\\", "/");
                if (toFile.endsWith("/")) {
                    toFile = toFile.substring(0, toFile.length() - 1);
                }
                String subFileName = name.substring(name.indexOf(srcOSSPath) + srcOSSPath.length());
                if (subFileName.startsWith("/")) {
                    subFileName = subFileName.substring(1);
                }
                temp = toFile + "/" + subFileName;
            } else {
                //srcOSSPath为单个文件，不为文件夹
                temp = toFile;
            }
            downloadFile(this.client, this.ossConfig.getBucketName(), name, temp);
        }
    }

    /** 
     * @see com.alipay.rdf.file.interfaces.FileStorage#upload(java.lang.String, java.lang.String, boolean)
     */
    @Override
    public void upload(String srcFile, String toFile, boolean override) {
        File file = new File(srcFile);
        if (!file.exists()) {
            throw new RdfFileException("rdf-file#上传至OSS时本地文件不存在！ srcPath=" + srcFile,
                RdfErrorEnum.NOT_EXSIT);
        }
        srcFile = file.getAbsolutePath().replaceAll("\\\\", "/");
        toFile = toOSSPath(toFile);

        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null && files.length > 0) {
                for (File f : files) {
                    String temp = f.getAbsolutePath().replaceAll("\\\\", "/");
                    if (!RdfFileUtil.equals(srcFile, toFile)) {
                        if (toFile.endsWith("/")) {
                            toFile = toFile.substring(0, toFile.length() - 1);
                        }
                        temp = toFile + "/" + f.getName();
                    }
                    upload(f.getAbsolutePath(), temp, override);
                }
            }
        } else {
            if (file.length() < ossConfig.getOssBigFileSize()) {
                uploadFile(file, toFile, override, null);
            } else {
                uploadBigFile(srcFile, toFile, override, null);
            }
        }
    }

    /** 
     * @see com.alipay.rdf.file.interfaces.FileStorage#rename(java.lang.String, java.lang.String)
     */
    @Override
    public void rename(String srcFile, String toFile) {
        srcFile = toOSSPath(srcFile);
        toFile = toOSSPath(toFile);
        //若目标路径为空，则移动源文件到目标路径处，若存在，则覆盖
        if (isExist(toFile)) {
            delete(toFile);
        }
        copy(srcFile, toFile);

        // 删除原文件
        delete(srcFile);
    }

    /** 
     * @see com.alipay.rdf.file.interfaces.FileStorage#copy(java.lang.String, java.lang.String)
     */
    @Override
    public void copy(String srcFile, String toFile) {
        copy(ossConfig.getBucketName(), srcFile, ossConfig.getBucketName(), toFile);
    }

    /** 
     * @see com.alipay.rdf.file.interfaces.FileStorage#delete(java.lang.String)
     */
    @Override
    public void delete(String fullPath) {
        fullPath = toOSSPath(fullPath);
        try {
            List<String> fileNames = listAllFiles(fullPath);
            if (fileNames.size() > 0) {
                for (String name : fileNames) {
                    client.deleteObject(ossConfig.getBucketName(), name);
                }
            } else {
                client.deleteObject(ossConfig.getBucketName(), fullPath);
            }
        } catch (Exception e) {
            throw new RdfFileException("删除OSS文件异常", e, RdfErrorEnum.IO_ERROR);
        }
    }

    /**
     * 拷贝到另一个bucket
     * 
     * @param srcFile
     * @param toBucketName
     * @param toFile
     */
    public void copy(String srcBucketName, String srcFile, String toBucketName, String toFile) {
        srcFile = toOSSPath(srcFile);
        toFile = toOSSPath(toFile);

        FileInfo fileInfo = getFileInfo(srcFile);
        if (fileInfo.getSize() < ossConfig.getOssBigFileSize()) {
            client.copyObject(srcBucketName, srcFile, toBucketName, toFile);
        } else {
            copyBigFile(srcBucketName, srcFile, toBucketName, toFile, fileInfo);
        }
    }

    private void copyBigFile(String srcBucketName, String srcFile, String toBucketName,
                             String toFile, FileInfo fileInfo) {
        long contentLength = fileInfo.getSize();
        // 分片大小，10MB
        long partSize = 1024 * 1024 * 10;
        // 计算分块数目
        int partCount = (int) (contentLength / partSize);
        if (contentLength % partSize != 0) {
            partCount++;
        }

        // 初始化拷贝任务
        InitiateMultipartUploadRequest initiateMultipartUploadRequest = new InitiateMultipartUploadRequest(
            toBucketName, toFile);
        InitiateMultipartUploadResult initiateMultipartUploadResult = client
            .initiateMultipartUpload(initiateMultipartUploadRequest);
        String uploadId = initiateMultipartUploadResult.getUploadId();

        // 分片拷贝
        List<PartETag> partETags = new ArrayList<PartETag>();
        for (int i = 0; i < partCount; i++) {
            // 计算每个分块的大小
            long skipBytes = partSize * i;
            long size = partSize < contentLength - skipBytes ? partSize : contentLength - skipBytes;
            // 创建UploadPartCopyRequest
            UploadPartCopyRequest uploadPartCopyRequest = new UploadPartCopyRequest(srcBucketName,
                srcFile, toBucketName, toFile);
            uploadPartCopyRequest.setUploadId(uploadId);
            uploadPartCopyRequest.setPartSize(size);
            uploadPartCopyRequest.setBeginIndex(skipBytes);
            uploadPartCopyRequest.setPartNumber(i + 1);
            UploadPartCopyResult uploadPartCopyResult = client
                .uploadPartCopy(uploadPartCopyRequest);
            // 将返回的PartETag保存到List中
            partETags.add(uploadPartCopyResult.getPartETag());
        }

        // 提交分片拷贝任务
        CompleteMultipartUploadRequest completeMultipartUploadRequest = new CompleteMultipartUploadRequest(
            toBucketName, toFile, uploadId, partETags);
        client.completeMultipartUpload(completeMultipartUploadRequest);
    }

    /**
     * 追加上传
     */
    public void appendUploadFile(String srcFile, String ossFilePath) {
        File file = new File(srcFile);
        if (!file.exists()) {
            throw new RdfFileException("rdf-file#上传至OSS时本地文件不存在！ srcPath=" + srcFile,
                RdfErrorEnum.NOT_EXSIT);
        }
        ossFilePath = toOSSPath(ossFilePath);

        long position = 0;
        FileInfo fileInfo = getFileInfo(ossFilePath);
        if (fileInfo.isExists()) {
            position = fileInfo.getSize();
        }

        if (position + file.length() > ossConfig.getOssAppendSizeLimit()) {
            throw new RdfFileException(
                "rdf-file# oss append upload file srcFile=" + srcFile + " size=" + file.length()
                                       + ", ossFile=" + ossFilePath + " size=" + position
                                       + ", 总和大于append大小限制=" + ossConfig.getOssAppendSizeLimit(),
                RdfErrorEnum.VALIDATE_ERROR);
        }

        AppendObjectRequest appendObjectRequest = new AppendObjectRequest(ossConfig.getBucketName(),
            ossFilePath, file);
        appendObjectRequest.setPosition(position);

        AppendObjectResult result = client.appendObject(appendObjectRequest);

        if (RdfFileLogUtil.common.isInfo()) {
            RdfFileLogUtil.common
                .info("rdf-file#FileOssStorage.appendUploadFile(srcFile=" + srcFile
                      + ",ossFilePath=" + ossFilePath + ") 上传objectCRC=" + result.getObjectCRC());
        }
    }

    /**
     * 上传一个文件至指定路径
     * 
     * @param file
     * @param ossFilePath
     * @param override
     * @param MD5           可为空
     */
    private void uploadFile(File file, String ossFilePath, boolean override, String MD5) {
        ossFilePath = toOSSPath(ossFilePath);
        //若目标路径已存在文件，且不覆盖，则停止上传
        if (!override && isExist(ossFilePath)) {
            return;
        }
        //若覆盖，则先删除目标路径下的文件
        if (override) {
            client.deleteObject(ossConfig.getBucketName(), ossFilePath);
        }

        InputStream fileStream = null;
        try {
            fileStream = new FileInputStream(file);
        } catch (FileNotFoundException e1) {
            throw new RdfFileException("rdf-file#上传文件出现异常，file路径为" + file.getAbsolutePath(),
                RdfErrorEnum.NOT_EXSIT);
        }
        ObjectMetadata meta = new ObjectMetadata();
        if (RdfFileUtil.isNotBlank(MD5)) {
            meta.setContentMD5(MD5);
        }
        try {
            client.putObject(ossConfig.getBucketName(), ossFilePath, fileStream, meta);
        } finally {
            if (null != fileStream) {
                try {
                    fileStream.close();
                } catch (IOException e) {
                    //忽略异常
                    if (RdfFileLogUtil.common.isWarn()) {
                        RdfFileLogUtil.common
                            .warn("rdf-file#oss upload file close io error ignore.", e);
                    }
                }
            }
        }
    }

    /**
     * 上传一个大文件至oss指定路径
     * 
     * @param srcPath
     * @param ossFilePath
     * @param override
     * @param MD5
     */
    private void uploadBigFile(String srcPath, String ossFilePath, boolean override, String MD5) {
        ossFilePath = toOSSPath(ossFilePath);
        //若目标路径已存在文件，且不需要覆盖，则停止上传
        if (!override && isExist(ossFilePath)) {
            return;
        }
        //若需要覆盖，则先删除目标路径下的文件
        if (override) {
            client.deleteObject(ossConfig.getBucketName(), ossFilePath);
        }

        UploadFileRequest uploadFileRequest = new UploadFileRequest(ossConfig.getBucketName(),
            ossFilePath);

        uploadFileRequest.setUploadFile(srcPath);
        uploadFileRequest.setTaskNum(5);
        uploadFileRequest.setPartSize(10 * 1024 * 1024);
        uploadFileRequest.setEnableCheckpoint(true);

        if (RdfFileUtil.isNotBlank(MD5)) {
            ObjectMetadata meta = new ObjectMetadata();
            meta.setContentMD5(MD5);
            uploadFileRequest.setObjectMetadata(meta);
        }

        try {
            client.uploadFile(uploadFileRequest);
        } catch (Throwable e) {
            throw new RdfFileException("rdf-file#oss上传文件出现异常", e, RdfErrorEnum.IO_ERROR);
        }
    }

    /**
     * download one file
     * 
     * @param client
     * @param bucketName
     * @param filename
     * @param localFilename
     */
    private void downloadFile(OSSClient client, String bucketName, String filename,
                              String localFilename) {
        filename = this.toOSSPath(filename);
        GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName, filename);
        File localFile = new File(localFilename);
        if (!localFile.getParentFile().exists()) {
            localFile.getParentFile().mkdirs();
        }
        client.getObject(getObjectRequest, localFile);
    }

    /**
     * helper method for adding regex check when listing files
     * 
     * @param folderName
     * @param regexs
     * @param all
     * @return
     */
    private List<String> listFilesWithRegex(String folderName, String[] regexs, boolean all) {
        List<String> filePaths = listFilesHandler(folderName, null, all);
        if (regexs == null || regexs.length == 0) {
            return filePaths;
        }
        List<String> result = new ArrayList<String>();
        for (String filePath : filePaths) {
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

    /**
     * handler method for listing files
     * 
     * @param folderName
     * @param marker
     * @param all
     * @param fileFilters
     * @return
     */
    private List<String> listFilesHandler(String folderName, String marker, boolean all) {
        folderName = toOSSPath(folderName);
        if (!folderName.endsWith("/")) {
            folderName += "/";
        }
        List<String> filePaths = new ArrayList<String>();
        ListObjectsRequest request = new ListObjectsRequest();
        request.setPrefix(folderName);
        request.setBucketName(ossConfig.getBucketName());
        if (!all) {
            request.setDelimiter("/");
        }
        if (RdfFileUtil.isNotBlank(marker)) {
            request.setMarker(marker);
        }
        ObjectListing result = client.listObjects(request);
        for (OSSObjectSummary object : result.getObjectSummaries()) {
            if (all && object.getKey().endsWith("/")) {
                continue;
            }
            String path = object.getKey();
            if (RdfFileUtil.equals(folderName, path)) {
                continue;
            }
            if (!RdfFileUtil.isBlank(path)) {
                filePaths.add(path);
            }
        }
        if (!all) {
            for (String commonPrefix : result.getCommonPrefixes()) {
                filePaths.add(commonPrefix);
            }
        }
        if (result.isTruncated()) {
            filePaths.addAll(listFilesHandler(folderName, result.getNextMarker(), all));
        }
        return filePaths;

    }

    /**
     * 判断oss某路径下是否存在文件
     * 
     * @param ossFilePath
     * @return
     */
    private boolean isExist(String ossFilePath) throws OSSException, ClientException {
        ossFilePath = toOSSPath(ossFilePath);
        try {
            return client.doesObjectExist(ossConfig.getBucketName(), ossFilePath);
        } catch (OSSException e) {
            if (e.getErrorCode() == OSSErrorCode.NO_SUCH_BUCKET
                || e.getErrorCode() == OSSErrorCode.NO_SUCH_KEY) {
                return false;
            }
            throw e;
        }
    }

    /**
     * oss 存储路径不能以/开始
     * 
     * @param filePath
     * @return
     */
    private String toOSSPath(String filePath) {
        filePath = filePath.replaceAll("\\\\", "/");

        if (filePath.startsWith("/")) {
            filePath = filePath.substring(1);
        }
        return filePath;
    }
}
