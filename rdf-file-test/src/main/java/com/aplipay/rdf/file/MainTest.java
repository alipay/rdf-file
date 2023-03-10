package com.aplipay.rdf.file;

import com.alipay.rdf.file.interfaces.FileFactory;
import com.alipay.rdf.file.interfaces.FileReader;
import com.alipay.rdf.file.interfaces.FileWriter;
import com.alipay.rdf.file.model.FileConfig;
import com.alipay.rdf.file.model.StorageConfig;
import com.alipay.rdf.file.mysql.RealVisitor;
import com.alipay.rdf.file.mysql.MySqlLexer;
import com.alipay.rdf.file.mysql.MySqlParser;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.HashMap;
import java.util.Map;

public class MainTest {

    public static void main(String[] args) {

//        String s = "SUBSTR(abcd from 0 for 2)";
//        CharStream cs = CharStreams.fromString(s);
//        MySqlLexer lexer = new MySqlLexer(cs);
//        CommonTokenStream tokens = new CommonTokenStream(lexer);
//        MySqlParser parser = new MySqlParser(tokens);
//        ParseTree tree = parser.root();
//        RealVisitor visitor = new RealVisitor(new HashMap<String,Object>());
//        String ss = visitor.visit(tree);
//        System.out.println(ss);
//        return ;


        FileConfig readConfig = new FileConfig("/home/guanyu.iakwal7/code/rdf-file/rdf-file-test/src/main/resources/test-in",
                "read.json", new StorageConfig("nas"));
        FileReader reader = FileFactory.createReader(readConfig);

        FileConfig writeConfig = new FileConfig("/home/guanyu.iakwal7/code/rdf-file/rdf-file-test/src/main/resources/test-out",
                "write.json", new StorageConfig("nas"));
        FileWriter writer = FileFactory.createWriter(writeConfig);

        try {
            Map<String, Object> row = null;
            while (null != (row = reader.readRow(HashMap.class))) {
                writer.writeRow(row);
            }
            reader.close();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
