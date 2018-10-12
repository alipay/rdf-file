package com.alipay.rdf.file.condition;

import java.util.List;

import com.alipay.rdf.file.exception.RdfErrorEnum;
import com.alipay.rdf.file.exception.RdfFileException;
import com.alipay.rdf.file.loader.TemplateLoader;
import com.alipay.rdf.file.meta.FileBodyMeta;
import com.alipay.rdf.file.meta.FileColumnMeta;
import com.alipay.rdf.file.meta.FileMeta;
import com.alipay.rdf.file.model.FileConfig;
import com.alipay.rdf.file.model.FileDataTypeEnum;
import com.alipay.rdf.file.util.BeanMapWrapper;

public class RowConditionUtil {

    public static List<FileColumnMeta> getDeserializeColumns(FileConfig config, String[] row,
                                                             FileDataTypeEnum rowType) {
        FileMeta fileMeta = TemplateLoader.load(config);

        if (!fileMeta.isMultiBody()) {
            return fileMeta.getColumns(rowType);
        }

        for (FileBodyMeta bodyMeta : fileMeta.getBodyMetas()) {
            if (bodyMeta.getRowCondition().deserialize(config, row)) {
                return bodyMeta.getColumns();
            }
        }

        //TODO
        throw new RdfFileException("", RdfErrorEnum.UNSUPPORTED_OPERATION);
    }

    public static List<FileColumnMeta> getSerializeColumns(FileConfig config, BeanMapWrapper row,
                                                           FileDataTypeEnum rowType) {
        FileMeta fileMeta = TemplateLoader.load(config);

        if (!fileMeta.isMultiBody()) {
            return fileMeta.getColumns(rowType);
        }

        for (FileBodyMeta bodyMeta : fileMeta.getBodyMetas()) {
            if (bodyMeta.getRowCondition().serialize(config, row)) {
                return bodyMeta.getColumns();
            }
        }

        //TODO
        throw new RdfFileException("", RdfErrorEnum.UNSUPPORTED_OPERATION);
    }
}
