package com.about.mantle.amazon.productAdvertisingApi.soap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import java.io.ByteArrayOutputStream;
import java.util.Set;

/**
 * A logging handler for debug purposes.  Thanks to https://stackoverflow.com/a/1957777/295797
 */
public class SoapLoggingHandler implements SOAPHandler<SOAPMessageContext> {

    private static Logger logger = LoggerFactory.getLogger(SoapLoggingHandler.class);

    @Override
    public Set<QName> getHeaders() {
        return null; // will handle all headers
    }

    @Override
    public boolean handleMessage(SOAPMessageContext context) {
        if (logger.isDebugEnabled()) {
            logger.debug(buildLoggingString(context));
        }
        return true; // true = continue after this handler
    }

    @Override
    public boolean handleFault(SOAPMessageContext context) {
        logger.error("SOAP Fault: {}", buildLoggingString(context));
        return true; // true = continue after this handler
    }

    @Override
    public void close(MessageContext context) {
        // nothing to clean up
    }

    private String buildLoggingString(SOAPMessageContext context) {

        StringBuilder sb = new StringBuilder();
        sb.append((boolean) context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY) ?
                "Outbound" : "Inbound");
        sb.append(" SOAP message at endpoint `");
        sb.append(context.get(BindingProvider.ENDPOINT_ADDRESS_PROPERTY));
        sb.append("` with content: ");

        SOAPMessage message = context.getMessage();
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            message.writeTo(os);
            sb.append(os.toString());
        } catch (Exception e) {
            logger.error("Couldn't log SOAP message", e);
        }

        return sb.toString();
    }

}
