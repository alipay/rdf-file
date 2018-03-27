package com.alipay.rdf.file.protocol;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.alipay.rdf.file.exception.RdfErrorEnum;
import com.alipay.rdf.file.exception.RdfFileException;
import com.alipay.rdf.file.util.RdfFileLogUtil;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 *
 * @author hongwei.quhw
 * @version $Id: NodeletParser.java, v 0.1 2018年3月12日 下午4:23:35 hongwei.quhw Exp $
 */
public class NodeletParser {
    private static String        DDD    = "http://apache.org/xml/features/disallow-doctype-decl";

    private Map<String, Nodelet> letmap = new HashMap<String, Nodelet>();

    public void addNodelet(String xpath, Nodelet nodelet) {
        letmap.put(xpath, nodelet);
    }

    public void parse(InputStream inputStream) {
        Document doc = createDocument(inputStream);
        parse(doc.getDocumentElement());
    }

    public void parse(Element ele) {
        Path path = new Path();
        processNodelet(ele, "/");
        process(ele, path);
    }

    private void process(Element ele, Path path) {
        path.add(ele.getNodeName());
        processNodelet(ele, path.toString());

        NodeList children = ele.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node node = children.item(i);
            if (node instanceof Element) {
                process((Element) node, path);
            }
        }

        path.remove();
    }

    private void processNodelet(Element ele, String xpath) {
        Nodelet nodelet = letmap.get(xpath);
        if (null != nodelet) {
            nodelet.process(xpath, ele);
        }
    }

    private Document createDocument(InputStream inputStream) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            try {
                factory.setFeature(DDD, true);
            } catch (Throwable e) {
                RdfFileLogUtil.common.warn("rdf-file#factory.setFeature(DDD, true)", e);
            }
            DocumentBuilder builder = factory.newDocumentBuilder();
            return builder.parse(new InputSource(inputStream));
        } catch (Exception e) {
            throw new RdfFileException(e, RdfErrorEnum.PROTOCOL_DEFINE_ERROR);
        }
    }

    private static class Path {
        private List<String> nodeList = new ArrayList<String>();

        public Path() {
        }

        public void add(String node) {
            nodeList.add(node);
        }

        public void remove() {
            nodeList.remove(nodeList.size() - 1);
        }

        public String toString() {
            StringBuffer buffer = new StringBuffer("/");
            for (int i = 0; i < nodeList.size(); i++) {
                buffer.append(nodeList.get(i));
                if (i < nodeList.size() - 1) {
                    buffer.append("/");
                }
            }
            return buffer.toString();
        }
    }
}
