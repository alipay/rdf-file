/*
 * Ant Group
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.alipay.rdf.file.common;

import com.alipay.rdf.file.interfaces.FileFactory;
import com.alipay.rdf.file.interfaces.FileWriter;
import com.alipay.rdf.file.model.FileConfig;
import com.alipay.rdf.file.util.RdfFileUtil;

/**
 * @author quhongwei
 * @version FileWriterTemplate.java, v 0.1 2023年03月24日 2:07 下午 quhongwei Exp $
 */
public abstract class FileWriterTemplate {
    private final FileWriter fileWriter;

    public FileWriterTemplate(FileConfig fileConfig) {
        this.fileWriter = FileFactory.createWriter(fileConfig);
    }

    public void process() throws Throwable {
        try {
            doProcess(fileWriter);
        } catch (Throwable t) {
            RdfFileUtil.setWriteError(fileWriter);

            if (t instanceof RuntimeException) {
                throw t;
            } else {
                throw new RuntimeException("rdf-file#FileWriterTemplate file write error.", t);
            }
        } finally {
            fileWriter.close();
        }
    }

    protected abstract void doProcess(FileWriter fileWriter) throws Throwable;
}