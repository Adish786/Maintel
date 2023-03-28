package com.about.mantle.spring.jetty;

import com.about.globe.core.servlet.GlobalErrorPageServlet;
import com.google.common.net.MediaType;
import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.server.handler.ErrorHandler;
import org.eclipse.jetty.util.BufferUtil;

import java.nio.ByteBuffer;

public class MantleErrorHandler extends ErrorHandler {
    @Override
    public ByteBuffer badMessageError(int status, String reason, HttpFields fields) {
        fields.put(HttpHeader.CONTENT_TYPE.asString(), MediaType.HTML_UTF_8.toString());
        fields.put(HttpHeader.CACHE_CONTROL.asString(), "no-cache");
        fields.put(HttpHeader.CONTENT_ENCODING, "UTF-8");
        // TODO - some way to get hostname/version for response?
        return BufferUtil.toBuffer(GlobalErrorPageServlet.getErrorPageBytes(status));
    }
}