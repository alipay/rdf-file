package com.alipay.rdf.file.codec;

import com.alipay.rdf.file.spi.RdfFileRowCodecSpi;

/**
 * @Author: hongwei.quhw 2021/7/30 3:19 下午
 */
public abstract class AbstractRowCodec implements RdfFileRowCodecSpi {
    @Override
    public String postSerialize(String line, RowCodecContext ctx) {
        return line;
    }

    @Override
    public String preDeserialize(String line, RowCodecContext ctx) {
        return line;
    }
}
