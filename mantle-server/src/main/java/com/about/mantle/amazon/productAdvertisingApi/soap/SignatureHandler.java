package com.about.mantle.amazon.productAdvertisingApi.soap;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Set;
import java.util.TimeZone;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.w3c.dom.Node;

/**
 *
 * Stolen from https://github.com/malkusch/amazon-ecs-api/blob/master/src/main/java/de/malkusch/amazon/ecs/SignatureHandler.java
 * commit 78e84ee2d18a7cc2d5e222c80056df18dfba3352
 *
 * SoapHandler for adding the authentication headers to an Amazon Product
 * Advertising API call
 * <p>
 * This SoapHandler generates the signature for the soap call. Amazon's
 * authentication SOAP headers AWSAccessKeyId, Timestamp and Signature will be
 * added to the SOAP-request.
 * <p>
 * This class is thread safe.
 *
 * @author Markus Malkusch <markus@malkusch.de>
 * @see http://docs.amazonwebservices.com/AWSECommerceService/latest/DG/NotUsingWSSecurity.html
 */
public class SignatureHandler implements SOAPHandler<SOAPMessageContext> {

    public final static String ACCESS_KEY = "AWSAccessKeyId";
    public final static String TIMESTAMP = "Timestamp";
    public final static String SIGNATURE = "Signature";
    public final static String NAMESPACE = "http://security.amazonaws.com/doc/2007-01-01/";
    public final static String SIGN_ALGORITHM = "HmacSHA256";

    private String accessKey;
    private SimpleDateFormat dateFormat;
    private SecretKeySpec secretKeySpec;

    public SignatureHandler(String accessKey, String secretKey) {
        this.accessKey = accessKey;

        dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        secretKeySpec = new SecretKeySpec(secretKey.getBytes(), SIGN_ALGORITHM);
    }

    /**
     * Add authentication headers to outgoing soap calls.
     * <p>
     * This method is thread safe.
     */
    @Override
    public boolean handleMessage(SOAPMessageContext messagecontext) {
        try {
            Boolean outbound = (Boolean) messagecontext
                    .get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
            if (!outbound) {
                return true;

            }

            SOAPMessage soapMessage = messagecontext.getMessage();
            SOAPBody soapBody = soapMessage.getSOAPBody();
            Node operation = soapBody.getFirstChild();

            String timeStamp = dateFormat.format(Calendar.getInstance()
                    .getTime());
            String signature = getSignature(operation.getLocalName(), timeStamp);

            // Add the authentication headers
            SOAPEnvelope soapEnv = soapMessage.getSOAPPart().getEnvelope();
            SOAPHeader header = soapEnv.getHeader();
            if (header == null) {
                header = soapEnv.addHeader();

            }
            addHeader(header, ACCESS_KEY, accessKey);
            addHeader(header, TIMESTAMP, timeStamp);
            addHeader(header, SIGNATURE, signature);

            return true;

        } catch (SOAPException e) {
            throw new RuntimeException(e);

        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);

        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);

        }
    }

    private void addHeader(SOAPHeader header, String name, String value)
            throws SOAPException {
        header.addChildElement(new QName(NAMESPACE, name)).addTextNode(value);
    }

    private String getSignature(String operation, String timeStamp)
            throws NoSuchAlgorithmException, UnsupportedEncodingException,
            InvalidKeyException {
        String toSign = operation + timeStamp;
        byte[] toSignBytes = toSign.getBytes("UTF-8");

        Mac signer = Mac.getInstance(SIGN_ALGORITHM);
        signer.init(secretKeySpec);
        signer.update(toSignBytes);
        byte[] signBytes = signer.doFinal();

        return DatatypeConverter.printBase64Binary(signBytes);
    }

    @Override
    public void close(MessageContext messagecontext) {
    }

    @Override
    public Set<QName> getHeaders() {
        return null;
    }

    @Override
    public boolean handleFault(SOAPMessageContext messagecontext) {
        return true;
    }

}
