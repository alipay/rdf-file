package com.alipay.rdf.file.exception;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 * 
 * 组件异常错误枚举
 * 
 * @author hongwei.quhw
 * @version $Id: RdfErrorEnum.java, v 0.1 2017年7月26日 下午7:35:38 hongwei.quhw Exp $
 */
public enum RdfErrorEnum {
                          UNKOWN("未知异常"),

                          EMPTY("内容为空"),

                          HEAD_NOT_DEFINED("没有定义头"),

                          BODY_NOT_DEFINED("没有定义文件内容"),

                          COLUMN_NOT_DEFINED("字段没有定义"),

                          TAIL_NOT_DEFINED("没有定义尾"),

                          PROTOCOL_DEFINE_ERROR("协议布局模板定义错误"),

                          NOT_EXSIT("不存在"),

                          ILLEGAL_ARGUMENT("非法参数"),

                          DATA_ERROR("数据错误"),

                          SERIALIZE_ERROR("序列化错误"),

                          DESERIALIZE_ERROR("反序列化错误"),

                          VALIDATE_ERROR("校验异常"),

                          UNSUPPORTED_OPERATION("不支持操作"),

                          ENCODING_ERROR("编码问题"),

                          IO_ERROR("IO问题"),

                          SUMMARY_DISNABLE("汇总功能没有开启"),

                          SUMMARY_DEFINED_ERROR("汇总字段定义错误"),

                          STATISTIC_DEFINED_ERROR("统计字段定义错误"),

                          UNSUPPORT_LINEBREAK("不支持换行符"),

                          DUPLICATE_DEFINED("重复定义"),

                          DATE_FORAMT_ERROR("日期类型转换错误"),

                          EXTENSION_ERROR(" 加载扩展服务失败"),

                          COLUMN_TYPE_ERROR("字段类型定义错误"),

                          INSTANTIATION_ERROR("对象实例化错误"),

                          FUNCTION_ERROR("函数定义错误"),

                          SORT_ERROR("排序异常"),

                          NEED_SORTED("需要排序"),

                          ROWS_AFFECTED_ERROR("计算影响行数"),

                          TEMPLATE_ERROR("数据定义模板错误"),

                          RESOURCE_ERROR("资源加载异常"),

                          TYPE_CONVERTION_ERROR("类型转换失败"),

                          TYPE_GET_PROPERTY_ERROR("类型转换属性获取失败"),

                          FORMAT_ERROR("格式化错误"),

                          CONDITION_ERROR("条件值配置错误");

    private final String desc;

    RdfErrorEnum(String desc) {
        this.desc = desc;
    }

    /**
    * Getter method for property <tt>desc</tt>.
    * 
    * @return property value of desc
    */
    public String getDesc() {
        return desc;
    }
}
