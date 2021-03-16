package com.alipay.rdf.file.reader;

import com.alipay.rdf.file.interfaces.FileFactory;
import com.alipay.rdf.file.interfaces.FileReader;
import com.alipay.rdf.file.interfaces.FileWriter;
import com.alipay.rdf.file.loader.TemplateLoader;
import com.alipay.rdf.file.meta.FileMeta;
import com.alipay.rdf.file.model.FileConfig;
import com.alipay.rdf.file.model.StorageConfig;
import com.alipay.rdf.file.util.DateUtil;
import com.alipay.rdf.file.util.TemporaryFolderUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * 
 * @author hongwei.quhw
 * @version $Id: FundIndexFileTest.java, v 0.1 2017年7月28日 下午2:29:05 hongwei.quhw Exp $
 */
public class FundIndexFileTest {
    private TemporaryFolderUtil temporaryFolder = new TemporaryFolderUtil();

    @Before
    public void setUp() throws Exception {
        temporaryFolder.create();
    }

    @Test
    public void testIndex() throws Exception {
        String filePath = temporaryFolder.getRoot().getAbsolutePath();
        //filePath = "/var/folders/pd/t0ck64755qb57z2_46lxz28c0000gn/T/-1521337822491676015";
        System.out.println(filePath);
        FileConfig config = new FileConfig(new File(filePath, "0.txt").getAbsolutePath(),
            "/reader/fund_index/fund_index_template.cfg", new StorageConfig("nas"));

        FileWriter writer = FileFactory.createWriter(config);
        FileMeta fileMeta = TemplateLoader.load(config);

        Map<String, Object> header = new HashMap<String, Object>();
        header.put(fileMeta.getTotalCountKey(), 3);
        header.put("msgRecipient", "xxx");
        Date today = new Date();
        header.put("sendDate", today);
        writer.writeHead(header);

        Map<String, Object> rows = new HashMap<String, Object>();
        rows.put("path", "aaa/xxx/ccc");
        writer.writeRow(rows);
        rows.put("path", "bbb/xxx/ccc");
        writer.writeRow(rows);
        rows.put("path", "ccc/xxx/ccc");
        writer.writeRow(rows);

        writer.writeTail(new HashMap<String, Object>());

        writer.close();

        //校验文件
        BufferedReader reader = new BufferedReader(
            new InputStreamReader(new FileInputStream(new File(filePath, "0.txt")), "UTF-8"));
        Assert.assertEquals("OFDCFIDX", reader.readLine());
        Assert.assertEquals("20  ", reader.readLine());
        Assert.assertEquals("H0       ", reader.readLine());
        Assert.assertEquals("xxx      ", reader.readLine());
        reader.readLine();
        Assert.assertEquals("003", reader.readLine());
        Assert.assertEquals("aaa/xxx/ccc", reader.readLine());
        Assert.assertEquals("bbb/xxx/ccc", reader.readLine());
        Assert.assertEquals("ccc/xxx/ccc", reader.readLine());
        Assert.assertEquals("OFDCFEND", reader.readLine());
        reader.close();

        FileReader fileReader = FileFactory.createReader(config);
        header = fileReader.readHead(HashMap.class);
        header.put(fileMeta.getTotalCountKey(), 3);
        header.put("msgRecipient", "xxx");
        header.put("sendDate", new Date());
        Assert.assertEquals(3, header.get(fileMeta.getTotalCountKey()));
        Assert.assertEquals("xxx", header.get("msgRecipient"));
        Assert.assertEquals(DateUtil.format(today, "yyyyMMdd"),
            DateUtil.format((Date) header.get("sendDate"), "yyyyMMdd"));

        Map<String, Object> row = fileReader.readRow(HashMap.class);
        Assert.assertEquals("aaa/xxx/ccc", row.get("path"));
        row = fileReader.readRow(HashMap.class);
        Assert.assertEquals("bbb/xxx/ccc", row.get("path"));
        row = fileReader.readRow(HashMap.class);
        Assert.assertEquals("ccc/xxx/ccc", row.get("path"));
        Assert.assertNull(fileReader.readRow(HashMap.class));

        Map<String, Object> tail = fileReader.readTail(HashMap.class);
        Assert.assertEquals("OFDCFEND", tail.get("fileEnd"));
    }

    @After
    public void after() {
        temporaryFolder.delete();
    }
}
