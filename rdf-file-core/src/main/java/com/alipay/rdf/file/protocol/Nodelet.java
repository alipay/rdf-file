package com.alipay.rdf.file.protocol;

import org.w3c.dom.Element;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 *
 * @author hongwei.quhw
 * @version $Id: Nodelet.java, v 0.1 2018年3月12日 下午4:23:27 hongwei.quhw Exp $
 */
public interface Nodelet {
    void process(String xpath, Element node);
}
