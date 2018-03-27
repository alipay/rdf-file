package com.alipay.rdf.file.spi;

import java.util.List;
import java.util.Map;

import com.alipay.rdf.file.interfaces.FileReader;
import com.alipay.rdf.file.interfaces.FileWriter;
import com.alipay.rdf.file.meta.FileColumnMeta;
import com.alipay.rdf.file.meta.FileMeta;
import com.alipay.rdf.file.model.FileConfig;
import com.alipay.rdf.file.model.FileDataTypeEnum;
import com.alipay.rdf.file.processor.ProcessorTypeEnum;
import com.alipay.rdf.file.protocol.RowDefinition;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 * 
 * 函数扩展spi
 * 
 * @author hongwei.quhw
 * @version $Id: RdfFileFunctionSpi.java, v 0.1 2017年8月19日 下午2:59:30 hongwei.quhw Exp $
 */
public interface RdfFileFunctionSpi {
    /**
     * 获取函数表达式
     * */
    String getExpression();

    /**
     * 设置函数表达式
     * 
     * @param expression
     */
    void setExpression(String expression);

    /**
     * 获取执行函数方法参数
     * 
     * @return
     */
    String[] getParams();

    /**
     * 设置执行函数方法参数
     * 
     * @param params
     */
    void setParams(String[] params);

    /**
     * 检查函数执行参数
     */
    void checkParams();

    /**
     * 执行函数
     * 
     * @param ctx
     * @return
     */
    Object execute(FuncContext ctx);

    /**
     * 获取函数执行所在的数据类型
     * 
     * @return
     */
    FileDataTypeEnum getRowType();

    /**
     * 设置函数执行所在的数据类型
     * 
     * @param rowType
     */
    void setRowType(FileDataTypeEnum rowType);

    /**
     * 此函数操作涉及的行数
     * 
     */
    int rowsAffected(RowDefinition rd, FileMeta fileMeta);

    /**
     *  函数执行上下文
     */
    public static class FuncContext {
        public String                                            protocolName;
        public CodecType                                         codecType;
        public Object                                            rowBean;
        public FileWriter                                        writer;
        public FileReader                                        reader;
        public Object                                            field;
        public FileColumnMeta                                    columnMeta;
        public FileConfig                                        fileConfig;
        public Map<ProcessorTypeEnum, List<RdfFileProcessorSpi>> processors;
    }

    /**
     * 函数执行编码类型
     * 
     * @author hongwei.quhw
     * @version $Id: RdfFileFunctionSpi.java, v 0.1 2017年8月19日 下午3:02:09 hongwei.quhw Exp $
     */
    public enum CodecType {
                           SERIALIZE, DESERIALIZE;
    }
}
