package com.alipay.rdf.file.storage;

import java.lang.reflect.Field;
import java.util.Map;

import org.junit.After;
import org.junit.Before;

import com.alipay.rdf.file.loader.ProtocolLoader;
import com.alipay.rdf.file.model.StorageConfig;
import com.alipay.rdf.file.protocol.ProtocolDefinition;
import com.alipay.rdf.file.util.OssTestUtil;
import com.alipay.rdf.file.util.RdfFileLogUtil;
import com.alipay.rdf.file.util.TestLog;
import com.aliyun.oss.OSSClient;

/**
 * 
 * 
 * @author hongwei.quhw
 * @version $Id: AbstractOssSliceTest.java, v 0.1 2017年7月27日 下午7:22:10 hongwei.quhw Exp $
 */
public abstract class AbstractOssSliceTest {
    protected static final String             RDF_PATH = "rdf/rdf-file/test/slice/";
    protected Map<String, ProtocolDefinition> PD_CACHE = null;
    protected OSSClient                       ossClient;
    protected String                          bucketName;
    protected StorageConfig                   storageConfig;
    protected FileOssStorage                  ossStorage;
    protected OssFileSliceSplitter            splitter;

    @SuppressWarnings("unchecked")

    @Before
    public void setUp() throws Exception {
        Field field = ProtocolLoader.class.getDeclaredField("PD_CACHE");
        field.setAccessible(true);
        PD_CACHE = (Map<String, ProtocolDefinition>) field.get(ProtocolLoader.class);
        storageConfig = OssTestUtil.geStorageConfig();
        OssConfig config = (OssConfig) storageConfig.getParam(OssConfig.OSS_STORAGE_CONFIG_KEY);
        ossClient = new OSSClient(config.getEndpoint(), config.getAccessKeyId(),
            config.getAccessKeySecret());
        bucketName = config.getBucketName();

        ossStorage = new FileOssStorage();
        ossStorage.init(storageConfig);

        splitter = new OssFileSliceSplitter();
        splitter.init(ossStorage);

        RdfFileLogUtil.common = new TestLog();
    }

    @After
    public void after() {
        ossStorage.delete(RDF_PATH);
    }
}
