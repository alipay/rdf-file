/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.alipay.rdf.file.operation;

/**
 *
 * @author haofan.whf
 * @version $Id: SftpOperationResponse.java, v 0.1 2018年10月07日 上午12:29 haofan.whf Exp $
 */
public class SftpOperationResponse<T> {

    private boolean isSuccess;

    private T data;

    private Throwable error;

    public boolean isSuccess() {
        return isSuccess;
    }

    /**
     * Setter method for property success.
     *
     * @param success value to be assigned to property success
     */
    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    /**
     * Getter method for property data.
     *
     * @return property value of data
     */
    public T getData() {
        return data;
    }

    /**
     * Setter method for property data.
     *
     * @param data value to be assigned to property data
     */
    public void setData(T data) {
        this.data = data;
    }

    /**
     * Getter method for property error.
     *
     * @return property value of error
     */
    public Throwable getError() {
        return error;
    }

    /**
     * Setter method for property error.
     *
     * @param error value to be assigned to property error
     */
    public void setError(Throwable error) {
        this.error = error;
    }

    @Override
    public String toString() {
        return "SftpOperationResponse{" +
                "isSuccess=" + isSuccess +
                ", data=" + data +
                ", error=" + error +
                '}';
    }
}