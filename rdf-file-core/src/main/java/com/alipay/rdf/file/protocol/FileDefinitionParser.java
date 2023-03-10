package com.alipay.rdf.file.protocol;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.alipay.rdf.file.exception.RdfErrorEnum;
import com.alipay.rdf.file.exception.RdfFileException;
import com.alipay.rdf.file.function.ColumnFunctionWrapper;
import com.alipay.rdf.file.function.RdfFunction;
import com.alipay.rdf.file.loader.ExtensionLoader;
import com.alipay.rdf.file.meta.FileColumnMeta;
import com.alipay.rdf.file.meta.FileColumnRangeMeta;
import com.alipay.rdf.file.meta.FileColumnTypeMeta;
import com.alipay.rdf.file.model.FileDataTypeEnum;
import com.alipay.rdf.file.spi.RdfFileFunctionSpi;
import com.alipay.rdf.file.spi.RdfFileRowSplitSpi;
import com.alipay.rdf.file.util.RdfFileConstants;
import com.alipay.rdf.file.util.RdfFileUtil;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 * 
 * @author hongwei.quhw
 * @version $Id: XmlFileDefinitionParser.java, v 0.1 2017年4月1日 上午11:36:16 hongwei.quhw Exp $
 */
public class FileDefinitionParser {
    private NodeletParser      parser = new NodeletParser();

    private ProtocolDefinition pd     = new ProtocolDefinition();

    public FileDefinitionParser() {
        parser.addNodelet("/protocol", new Nodelet() {
            @Override
            public void process(String xpath, Element ele) {
                String protocolName = RdfFileUtil.assertTrimNotBlank(ele.getAttribute("name"));
                pd.setName(protocolName);

                String rowSplitRef = RdfFileConstants.DEFAULT_ROW_SPLIT;
                if (RdfFileUtil.isNotBlank(ele.getAttribute("rowsplit"))) {
                    rowSplitRef = RdfFileUtil.assertTrimNotBlank(ele.getAttribute("rowsplit"));
                }
                RdfFileRowSplitSpi rowSplit = ExtensionLoader
                    .getExtensionLoader(RdfFileRowSplitSpi.class).getExtension(rowSplitRef);
                RdfFileUtil.assertNotNull(rowSplit,
                    "文件protocol=" + protocolName + "中定义的rowsplit=" + rowSplitRef + "找不到对应的服务实现");
                pd.setRowSplit(rowSplit);
            }
        });

        parser.addNodelet("/protocol/head/row", new Nodelet() {
            @Override
            public void process(String xpath, Element node) {
                parseRow(xpath, node, pd.getHeads(), FileDataTypeEnum.HEAD);
            }
        });

        parser.addNodelet("/protocol/body/row", new Nodelet() {
            @Override
            public void process(String xpath, Element node) {
                parseRow(xpath, node, pd.getBodys(), FileDataTypeEnum.BODY);
            }
        });

        parser.addNodelet("/protocol/tail/row", new Nodelet() {
            @Override
            public void process(String xpath, Element node) {
                parseRow(xpath, node, pd.getTails(), FileDataTypeEnum.TAIL);
            }
        });

        parser.addNodelet("/protocol/head/row/column", new Nodelet() {
            @Override
            public void process(String xpath, Element node) {
                parseRowColumn(xpath, node, pd.getHeads(), FileDataTypeEnum.HEAD);
            }
        });

        parser.addNodelet("/protocol/body/row/column", new Nodelet() {
            @Override
            public void process(String xpath, Element node) {
                parseRowColumn(xpath, node, pd.getBodys(), FileDataTypeEnum.BODY);
            }
        });

        parser.addNodelet("/protocol/tail/row/column", new Nodelet() {
            @Override
            public void process(String xpath, Element node) {
                parseRowColumn(xpath, node, pd.getTails(), FileDataTypeEnum.TAIL);
            }
        });
    }

    private void parseRow(String xpath, Element node, List<RowDefinition> rowDefinitions,
                          FileDataTypeEnum rowType) {
        String output = node.getAttribute("output");
        RowDefinition rd = new RowDefinition();
        rowDefinitions.add(rd);

        if (RdfFileUtil.isNotBlank(output)) {
            rd.setOutput(RdfFunction.parse(output.trim(), rowType));
        } else {
            String columnloop = node.getAttribute("columnloop");
            if (RdfFileUtil.isNotBlank(columnloop)) {
                rd.setColumnloop(Boolean.parseBoolean(columnloop.trim()));
            } else {
                rd.setColumnloop(RdfFileConstants.DEFAULT_COLUMN_LOOP);
            }

            String columnLayout = node.getAttribute("columnLayout");
            if (RdfFileUtil.isNotBlank(columnLayout)) {
                rd.setColumnLayout(ColumnLayoutEnum.getColumnLayoutByName(columnLayout.trim()));
            } else {
                rd.setColumnLayout(RdfFileConstants.DEFAULT_COLUMN_LAYOUT);
            }
        }

        String type = RdfFileUtil.trimNotNull(node.getAttribute("type"));
        if (RdfFileUtil.isNotBlank(type)) {
            FileColumnTypeMeta columnTypeMeta = null;
            FileColumnRangeMeta columnRangeMeta = null;
            String[] fileds = type.split("\\|");
            for (int i = 0; i < fileds.length; i++) {
                String field = fileds[i];
                int semicolonIndex = field.indexOf(":");
                String extra = null;
                if (semicolonIndex > 0) {
                    extra = field.substring(semicolonIndex + 1);
                    field = field.substring(0, semicolonIndex);
                }

                FileColumnTypeMeta typeMeta = FileColumnTypeMeta.tryValueOf(field, extra);
                if (null != typeMeta) {
                    columnTypeMeta = typeMeta;
                }
                FileColumnRangeMeta rangeMeta = FileColumnRangeMeta.tryValueOf(field, extra);
                if (null != rangeMeta) {
                    columnRangeMeta = rangeMeta;
                }
            }

            FileColumnMeta columnMeta = new FileColumnMeta(-1, output, output, columnTypeMeta,
                false, columnRangeMeta, null, null, rowType, null);
            rd.setColumnMeta(columnMeta);
        }
    }

    private void parseRowColumn(String xpath, Element node, List<RowDefinition> rowDefinitions,
                                FileDataTypeEnum rowType) {
        NodeList nl = node.getChildNodes();
        List<RdfFileFunctionSpi> rfs = new ArrayList<RdfFileFunctionSpi>();
        for (int i = 0; i < nl.getLength(); i++) {
            Node nd = nl.item(i);
            if (!(nd instanceof Element)) {
                continue;
            }

            Element outputEle = (Element) nd;
            RdfFileUtil.assertEquals(outputEle.getTagName(), "output");

            rfs.add(RdfFunction.parse(RdfFileUtil.assertTrimNotBlank(outputEle.getTextContent()),
                rowType));
        }

        RowDefinition rd = rowDefinitions.get(rowDefinitions.size() - 1);
        if (rfs.size() > 0) {
            rd.setOutput(new ColumnFunctionWrapper(rfs, rowType));
        } else {
            throw new RdfFileException(xpath + " 下面没有配置output节点",
                RdfErrorEnum.PROTOCOL_DEFINE_ERROR);
        }
    }

    public ProtocolDefinition parseFileDefinition(InputStream is) {
        RdfFileUtil.assertNotNull(is, "XmlFileDefinitionParser InputStream 为空");

        parser.parse(is);

        return pd;
    }

}
