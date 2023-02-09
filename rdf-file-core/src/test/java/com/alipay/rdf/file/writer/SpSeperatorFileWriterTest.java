package com.alipay.rdf.file.writer;

import com.alipay.rdf.file.interfaces.FileFactory;
import com.alipay.rdf.file.interfaces.FileReader;
import com.alipay.rdf.file.interfaces.FileWriter;
import com.alipay.rdf.file.model.FileConfig;
import com.alipay.rdf.file.model.StorageConfig;
import com.alipay.rdf.file.util.DateUtil;
import com.alipay.rdf.file.util.TemporaryFolderUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * sp 分隔符测试 写测试
 */
public class SpSeperatorFileWriterTest {
    TemporaryFolderUtil tf = new TemporaryFolderUtil();

    @Before
    public void setUp() throws IOException {
        tf.create();
    }

    @Test
    public void testWriter() throws Exception {
        String filePath = tf.getRoot().getAbsolutePath();
        System.out.println(filePath);

        FileConfig config = new FileConfig(new File(filePath, "test.txt").getAbsolutePath(),
                "/writer/template/sp_split.json", new StorageConfig("nas"));
        config.setLineBreak("\r");
        config.setFileEncoding("UTF-8");
        writeAndValide(config);
    }

    private void writeAndValide(FileConfig config) throws Exception {
        FileWriter fileWriter = FileFactory.createWriter(config);

        Map<String, Object> head = new HashMap<String, Object>();
        head.put("totalCount", 2);
        head.put("totalAmount", new BigDecimal("23.22"));
        fileWriter.writeHead(head);

        Map<String, Object> body = new HashMap<String, Object>();

        Date testDate = DateUtil.parse("2017-01-03 12:22:33", "yyyy-MM-dd HH:mm:ss");

        body.put("seq", "seq12345");
        body.put("instSeq", "303");
        body.put("gmtApply", testDate);
        body.put("date", testDate);
        body.put("dateTime", testDate);
        body.put("applyNumber", 12);
        body.put("amount", new BigDecimal("1.22"));
        body.put("age", new Integer(33));
        body.put("longN", new Long(33));
        body.put("bol", true);
        body.put("memo", "memo1");
        fileWriter.writeRow(body);

        testDate = DateUtil.parse("2016-02-03 12:22:33", "yyyy-MM-dd HH:mm:ss");

        body.put("seq", "seq234567");
        body.put("instSeq", "505");
        body.put("gmtApply", testDate);
        body.put("date", testDate);
        body.put("dateTime", testDate);
        body.put("applyNumber", 12);
        body.put("amount", new BigDecimal("1.09"));
        body.put("age", 66);
        body.put("longN", 125);
        body.put("bol", false);
        body.put("memo", "memo2");
        fileWriter.writeRow(body);

        fileWriter.close();

        FileReader fileReader = FileFactory.createReader(config);
        HashMap readHead = fileReader.readHead(HashMap.class);

        Assert.assertEquals("{totalAmount=23.22, totalCount=2}", readHead.toString());
        System.out.println(readHead);
        HashMap row1 = fileReader.readRow(HashMap.class);
        HashMap row2 = fileReader.readRow(HashMap.class);
        Assert.assertEquals("{date=Tue Jan 03 00:00:00 CST 2017, dateTime=Tue Jan 03 12:22:33 CST 2017, amount=1.22, instSeq=303, longN=33, memo=memo1, gmtApply=Tue Jan 03 12:22:33 CST 2017, seq=seq12345, age=33, bol=true, applyNumber=12}", row1.toString());
        Assert.assertEquals("{date=Wed Feb 03 00:00:00 CST 2016, dateTime=Wed Feb 03 12:22:33 CST 2016, amount=1.09, instSeq=505, longN=125, memo=memo2, gmtApply=Wed Feb 03 12:22:33 CST 2016, seq=seq234567, age=66, bol=false, applyNumber=12}", row2.toString());
        System.out.println(row1);
        System.out.println(row2);
    }

    @After
    public void after() {
        tf.delete();
    }
}
