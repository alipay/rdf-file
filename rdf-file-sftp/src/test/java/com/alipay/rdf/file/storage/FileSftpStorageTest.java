package com.alipay.rdf.file.storage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.alipay.rdf.file.interfaces.FileFactory;
import com.alipay.rdf.file.interfaces.FileStorage;
import com.alipay.rdf.file.interfaces.FileStorage.FilePathFilter;
import com.alipay.rdf.file.model.FileInfo;
import com.alipay.rdf.file.model.StorageConfig;
import com.alipay.rdf.file.operation.AbstractSftpOperationTemplate;
import com.alipay.rdf.file.operation.SftpOperationFactory;
import com.alipay.rdf.file.operation.SftpOperationParamEnums;
import com.alipay.rdf.file.operation.SftpOperationResponse;
import com.alipay.rdf.file.operation.SftpOperationTypeEnums;
import com.alipay.rdf.file.sftp.SftpTestUtil;
import com.alipay.rdf.file.sftp.TestInitOperation;
import com.alipay.rdf.file.util.RdfFileUtil;
import com.alipay.rdf.file.sftp.TemporaryFolderUtil;

/**
 * 测试用例
 * 准备:
 * 1.创建sftp服务
 * 2.指定ROOT_PATH或者使用测试用例中默认的
 * 注意owner和group还有权限
 */
public class FileSftpStorageTest {

    TemporaryFolderUtil temporaryFolderUtil = new TemporaryFolderUtil();

    StorageConfig storageConfig;

    FileStorage fileStorage;

    AbstractSftpOperationTemplate<Boolean> initOperation = new TestInitOperation();

    String ROOT_PATH = "/files/testcase";

    String dir1 = "dir1";
    String dir2 = "dir2";

    String testFile1 = RdfFileUtil.combinePath(dir1, "test_file_1.txt");
    String testFile2 = RdfFileUtil.combinePath(dir2, "test_file_2.txt");
    String testFile3 = "test_file_3.txt";

    String remoteCopyDst = "copydir/test_file_1_cp.txt";

    String localTmpDir;
    String localDownloadDst;
    String localTmpFileName;

    String remoteUploadDir = "uploaddir";
    String remoteUploadFileName = "upload_test.txt";
    String remoteUploadDst = RdfFileUtil.combinePath(remoteUploadDir, remoteUploadFileName);

    String remoteRenameDir = "renamedir";
    String remoteRenameFileName = "rename_test.txt";
    String remoteRenameDst = RdfFileUtil.combinePath(remoteRenameDir, remoteRenameFileName);


    @Before
    public void setup(){
        storageConfig = SftpTestUtil.getStorageConfig();
        fileStorage = FileFactory.createStorage(storageConfig);
        try {
            temporaryFolderUtil.create();
        } catch (IOException e) {
            throw new RuntimeException("获取临时目录异常", e);
        }
        localTmpDir = temporaryFolderUtil.getRoot().getName();
        localDownloadDst = RdfFileUtil.combinePath(localTmpDir, "sftp/download_test.txt");
        localTmpFileName = RdfFileUtil.combinePath(localTmpDir, "sftp/localFile_1.txt");
    }

    @Test
    public void testAll() throws Exception{

        checkHealth();

        prepare();

        try{
            doAll();
        }finally {
            fileStorage.delete(ROOT_PATH);
        }
    }

    private void checkHealth(){
        AbstractSftpOperationTemplate<Boolean> healthCheckOperation
                = SftpOperationFactory.getOperation(SftpOperationTypeEnums.HEALTH_CHECK);
        FileSftpStorage fileSftpStorage = (FileSftpStorage)fileStorage;
        SftpOperationResponse<Boolean> response = healthCheckOperation
                .handle(fileSftpStorage.getUserInfo(), null);
        Assert.assertTrue(response.isSuccess());
        Assert.assertTrue(response.getData());
    }

    private void prepare() throws Exception{

        try{
            fileStorage.listAllFiles(ROOT_PATH);
        }catch (Exception e){
            System.out.println("开始设置ROOT_PATH");
            FileSftpStorage sftpStorage = (FileSftpStorage)fileStorage;
            Map<String, String> params = new HashMap<String, String>();
            params.put(SftpOperationParamEnums.TARGET_DIR.toString(), buildPath("dummy.txt"));
            SftpOperationResponse<Boolean> response = initOperation.handle(sftpStorage.getUserInfo(), params);
            if(!response.isSuccess()){
                throw new RuntimeException("设置ROOT_PATH失败,请尝试手动设置");
            }
        }

    }

    private void doAll() throws Exception{
        List<String> oldFiles = fileStorage.listAllFiles(ROOT_PATH);
        if(oldFiles != null && oldFiles.size() > 0){
            //删除老文件
            fileStorage.delete(ROOT_PATH);
        }
        System.out.println("createNewFile begin.");
        //构建测试文件
        fileStorage.createNewFile(buildPath(testFile1));
        //构建测试文件
        fileStorage.createNewFile(buildPath(testFile2));
        //构建测试文件
        fileStorage.createNewFile(buildPath(testFile3));
        System.out.println("createNewFile ok.");

        System.out.println("listAllFiles begin.");
        List<String> list = fileStorage.listAllFiles(ROOT_PATH);
        Assert.assertEquals(list.size(), 3);
        System.out.println("listAllFiles ok.");

        System.out.println("listFiles begin.");
        list = fileStorage.listFiles(ROOT_PATH);
        Assert.assertEquals(list.size(), 1);
        System.out.println("listFiles ok.");

        FilePathFilter filePathFilter = new FilePathFilter() {
            @Override
            public boolean accept(String file) {
                return file.equals(buildPath(testFile3));
            }
        };
        list = fileStorage.listAllFiles(ROOT_PATH, filePathFilter);
        Assert.assertEquals(list.size(), 1);

        System.out.println("copy begin.");
        fileStorage.copy(buildPath(testFile1),buildPath(remoteCopyDst));
        list = fileStorage.listAllFiles(ROOT_PATH);
        Assert.assertEquals(list.size(), 4);
        System.out.println("copy ok.");

        System.out.println("download begin.");
        fileStorage.download(buildPath(testFile1), localDownloadDst);
        File downloadFile = new File(localDownloadDst);
        Assert.assertTrue(downloadFile.exists());
        downloadFile.deleteOnExit();
        System.out.println("download ok.");

        System.out.println("upload begin.");
        File localTempFile = new File(localTmpFileName);
        localTempFile.deleteOnExit();
        FileOutputStream fos = new FileOutputStream(localTempFile);
        String testStr = "aaa";
        byte[] testStrByte = testStr.getBytes(Charset.forName("UTF-8"));
        fos.write(testStrByte);
        fos.flush();
        fileStorage.upload(localTmpFileName, buildPath(remoteUploadDst), false);
        FileInfo fileInfo = fileStorage.getFileInfo(buildPath(remoteUploadDst));
        Assert.assertEquals(fileInfo.getFileName(), remoteUploadFileName);
        Assert.assertEquals(fileInfo.getSize(), testStrByte.length);
        Assert.assertTrue(fileInfo.isExists());
        System.out.println("upload ok.");

        System.out.println("upload[override] begin.");
        fileStorage.upload(localDownloadDst, buildPath(remoteUploadDst), true);
        fileInfo = fileStorage.getFileInfo(buildPath(remoteUploadDst));
        Assert.assertEquals(fileInfo.getFileName(), remoteUploadFileName);
        Assert.assertEquals(fileInfo.getSize(), 0);
        Assert.assertTrue(fileInfo.isExists());

        filePathFilter = new FilePathFilter() {
            @Override
            public boolean accept(String file) {
                return file.equals(buildPath(remoteUploadDst));
            }
        };
        list = fileStorage.listFiles(buildPath(remoteUploadDir), filePathFilter);
        Assert.assertTrue(list.size() == 1);
        System.out.println("upload[override] ok.");

        System.out.println("rename begin.");
        fileStorage.rename(buildPath(remoteUploadDst), buildPath(remoteRenameDst));
        filePathFilter = new FilePathFilter() {
            @Override
            public boolean accept(String file) {
                return file.equals(buildPath(remoteRenameDst));
            }
        };
        list = fileStorage.listFiles(buildPath(remoteRenameDir), filePathFilter);
        Assert.assertTrue(list.size() == 1);
        System.out.println("rename ok.");
    }

    private String buildPath(String relativePath){
        return RdfFileUtil.combinePath(ROOT_PATH, relativePath);
    }

}