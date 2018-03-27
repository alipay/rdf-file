package com.alipay.rdf.file.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 * 
 * 校验结果
 * 
 * @author hongwei.quhw
 * @version $Id: ValidateResult.java, v 0.1 2017年8月3日 下午2:51:39 hongwei.quhw Exp $
 */
public class ValidateResult {
    /**  */
    private boolean      success   = true;;
    private List<String> errorMsgs = new ArrayList<String>();
    private Exception    ex;

    /**
     * 构造失败结果
     * 
     * @param errorMsg
     * @return
     */
    public ValidateResult fail(String errorMsg) {
        this.success = false;
        errorMsgs.add(errorMsg);
        return this;
    }

    /**
     * 构造失败结果
     * 
     * @param ex
     * @return
     */
    public ValidateResult fail(Exception ex) {
        this.success = false;
        errorMsgs.add(ex.getMessage());
        this.ex = ex;
        return this;
    }

    /**
     * 构造失败结果
     * 
     * @param errorMsg
     * @param ex
     * @return
     */
    public ValidateResult fail(String errorMsg, Exception ex) {
        this.success = false;
        errorMsgs.add(errorMsg);
        errorMsgs.add(ex.getMessage());
        this.ex = ex;
        return this;
    }

    /**
     * 是否成功
     * 
     * @return
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * 获取错误码
     * 
     * @return
     */
    public String getErrorMsg() {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < errorMsgs.size(); i++) {
            buffer.append(errorMsgs.get(i));
            if (i < errorMsgs.size() - 1) {
                buffer.append("\r\n");
            }

        }

        return buffer.toString();
    }

    /**
     * Getter method for property <tt>ex</tt>.
     * 
     * @return property value of ex
     */
    public Exception getEx() {
        return ex;
    }

}
