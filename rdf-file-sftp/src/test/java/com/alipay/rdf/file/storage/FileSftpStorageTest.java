package com.alipay.rdf.file.storage;

import com.alipay.rdf.file.interfaces.FileFactory;
import com.alipay.rdf.file.interfaces.FileStorage;
import com.alipay.rdf.file.interfaces.FileStorage.FilePathFilter;
import com.alipay.rdf.file.model.FileInfo;
import com.alipay.rdf.file.model.StorageConfig;
import com.alipay.rdf.file.operation.*;
import com.alipay.rdf.file.sftp.SftpTestUtil;
import com.alipay.rdf.file.sftp.TemporaryFolderUtil;
import com.alipay.rdf.file.sftp.TestInitOperation;
import com.alipay.rdf.file.util.RdfFileLogUtil;
import com.alipay.rdf.file.util.RdfFileUtil;
import com.alipay.rdf.file.util.SFTPHelper;
import com.alipay.rdf.file.util.SystemPrintLog;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    String ROOT_PATH = SftpTestUtil.combineHomeDir("testcase");

    String dir1 = "dir1";
    String dir2 = "dir2";

    String testFile1 = SFTPHelper.toSFTPPath(RdfFileUtil.combinePath(dir1, "test_file_1.txt"));
    String testFile2 = SFTPHelper.toSFTPPath(RdfFileUtil.combinePath(dir2, "test_file_2.txt"));
    String testFile3 = "test_file_3.txt";

    String remoteCopyDst = "copydir/test_file_1_cp.txt";

    String localTmpDir;
    String localDownloadDst;
    String localTmpFileName;

    String remoteUploadDir = "uploaddir";
    String remoteUploadFileName = "upload_test.txt";
    String remoteUploadDst = SFTPHelper.toSFTPPath(RdfFileUtil.combinePath(remoteUploadDir, remoteUploadFileName));

    String remoteRenameDir = "renamedir";
    String remoteRenameFileName = "rename_test.txt";
    String remoteRenameDst = SFTPHelper.toSFTPPath(RdfFileUtil.combinePath(remoteRenameDir, remoteRenameFileName));


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
        RdfFileLogUtil.common = new SystemPrintLog();
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
                .handle(fileSftpStorage.getUserInfo(), null, null);
        Assert.assertTrue(response.isSuccess());
        Assert.assertTrue(response.getData());
    }

    private void prepare() throws Exception{

        try{
            fileStorage.listAllFiles(ROOT_PATH);
        }catch (Exception e){
            System.out.println("开始设置ROOT_PATH");
            FileSftpStorage sftpStorage = (FileSftpStorage)fileStorage;
            Map<String, Object> params = new HashMap<String, Object>();
            params.put(SftpOperationParamEnums.TARGET_DIR.toString(), buildPath("dummy.txt"));
            SftpOperationResponse<Boolean> response = initOperation.handle(sftpStorage.getUserInfo(), params, null);
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
        int fileSize = 100 * 1000 * 1000;
        createLocalFile(localTempFile, fileSize);
        fileStorage.upload(localTmpFileName, buildPath(remoteUploadDst), false);
        FileInfo fileInfo = fileStorage.getFileInfo(buildPath(remoteUploadDst));
        Assert.assertEquals(fileInfo.getFileName(), remoteUploadFileName);
        Assert.assertEquals(fileInfo.getSize(), fileSize);
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

        System.out.println("rename[targetfile exists] begin.");
        fileStorage.rename(buildPath(testFile3), buildPath(remoteRenameDst));
        filePathFilter = new FilePathFilter() {
            @Override
            public boolean accept(String file) {
                return file.equals(buildPath(remoteRenameDst));
            }
        };
        list = fileStorage.listFiles(buildPath(remoteRenameDir), filePathFilter);
        Assert.assertTrue(list.size() == 1);
        System.out.println("rename[targetfile exists] ok.");
    }

    private String buildPath(String relativePath){
        return RdfFileUtil.combinePath(ROOT_PATH, relativePath);
    }


    public static void createLocalFile(File file, long length) throws IOException{
        long start = System.currentTimeMillis();
        RandomAccessFile r = null;
        try {
            r = new RandomAccessFile(file, "rw");
            r.setLength(length);
        } finally{
            if (r != null) {
                try {
                    r.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}