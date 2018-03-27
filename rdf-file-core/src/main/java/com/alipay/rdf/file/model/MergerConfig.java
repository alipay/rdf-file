package com.alipay.rdf.file.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 * 
 * @author hongwei.quhw
 * @version $Id: MergerConfig.java, v 0.1 2017年4月20日 下午7:37:44 hongwei.quhw Exp $
 */
public class MergerConfig {
    /** 头分片路径*/
    private List<PathHolder> headFilePaths;
    /** body分片路径*/
    private List<PathHolder> bodyFilePaths;
    /** tail分片路径*/
    private List<PathHolder> tailFilePaths;
    /** 已经存在的完整文件路径*/
    private List<PathHolder> existFilePaths;
    /**是否采用输入流合并*/
    private boolean          streamAppend;

    public List<PathHolder> getHeadFilePaths() {
        return headFilePaths;
    }

    public void setHeadFilePaths(List<String> headFilePaths) {
        this.headFilePaths = popluate(headFilePaths);
    }

    public void setHeadFilePathHolders(List<PathHolder> headFilePaths) {
        this.headFilePaths = headFilePaths;
    }

    public List<PathHolder> getBodyFilePaths() {
        return bodyFilePaths;
    }

    public void setBodyFilePaths(List<String> bodyFilePaths) {
        this.bodyFilePaths = popluate(bodyFilePaths);
    }

    public void setBodyFilePathHolders(List<PathHolder> bodyFilePaths) {
        this.bodyFilePaths = bodyFilePaths;
    }

    public List<PathHolder> getTailFilePaths() {
        return tailFilePaths;
    }

    public void setTailFilePaths(List<String> tailFilePaths) {
        this.tailFilePaths = popluate(tailFilePaths);
    }

    public void setTailFilePathHolders(List<PathHolder> tailFilePaths) {
        this.tailFilePaths = tailFilePaths;
    }

    public List<PathHolder> getExistFilePaths() {
        return existFilePaths;
    }

    public void setExistFilePaths(List<String> existFilePaths) {
        this.existFilePaths = popluate(existFilePaths);
    }

    public void setExistFilePathHolders(List<PathHolder> existFilePaths) {
        this.existFilePaths = existFilePaths;
    }

    public boolean isStreamAppend() {
        return streamAppend;
    }

    public void setStreamAppend(boolean streamAppend) {
        this.streamAppend = streamAppend;
    }

    private List<PathHolder> popluate(List<String> filePaths) {
        if (null == filePaths || 0 == filePaths.size()) {
            return null;
        }

        List<PathHolder> holders = new ArrayList<PathHolder>();
        for (String path : filePaths) {
            holders.add(new PathHolder(path, null));
        }

        return holders;
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer("MergerConfig=[");
        buffer.append("streamAppend=").append(streamAppend);
        buffer.append(",headFilePaths=").append(headFilePaths);
        buffer.append(",bodyFilePaths=").append(bodyFilePaths);
        buffer.append(",tailFilePaths=").append(tailFilePaths);
        buffer.append(",existFilePaths=").append(existFilePaths);
        buffer.append("]");

        return buffer.toString();
    }

    public class PathHolder {
        private String        filePath;
        private StorageConfig storageConfig;

        public PathHolder(String filePath, StorageConfig storageConfig) {
            this.filePath = filePath;
            this.storageConfig = storageConfig;
        }

        public String getFilePath() {
            return filePath;
        }

        public StorageConfig getStorageConfig() {
            return storageConfig;
        }

        public void setStorageConfig(StorageConfig storageConfig) {
            this.storageConfig = storageConfig;
        }

        @Override
        public String toString() {
            return "PathHolder[filePath=" + filePath + ", storageConfig=" + storageConfig + "]";
        }
    }
}
