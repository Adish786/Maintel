package com.about.mantle.model.extended.docv2.sc.blocks;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import com.about.mantle.model.extended.docv2.sc.AbstractStructuredContentContentEx;
import org.apache.commons.compress.compressors.deflate.DeflateCompressorInputStream;
import org.apache.commons.compress.compressors.deflate.DeflateCompressorOutputStream;

import com.about.mantle.model.extended.docv2.sc.AbstractStructuredContentDataEx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implements the `HtmlContent` definition of the Structured Content schema
 */
public class StructuredContentHtmlEx extends AbstractStructuredContentContentEx<StructuredContentHtmlEx.StructuredContentHtmlDataEx> {
    private static Logger logger = LoggerFactory.getLogger(StructuredContentHtmlEx.class);

    public StructuredContentHtmlEx() {
       // empty constructor needed for Jackson
    }
    
    public StructuredContentHtmlEx(String html, boolean wasSliced) {
    	StructuredContentHtmlDataEx data = new StructuredContentHtmlDataEx();
        data.setHtml(html);
        this.setData(data);
        this.setType("HTML");
        this.setWasSliced(wasSliced);
     }

	/**
     * Implements the `HtmlContentData` definition of the Structured Content schema
     */
    public static class StructuredContentHtmlDataEx extends AbstractStructuredContentDataEx {

        /* Stores a compressed representation of the block's html string content. Compression is handled in the
         * getHtml and setHtml methods to make this compression transparent to any classes consuming this content.
         */
        private byte[] htmlBytes;
        /* String should only be used as a fallback if an error occurs compressing content.
         */
        private String html = null;

        public String getHtml() {
            // If string is set, return that value.
            if (html != null) return html;
            try (
                ByteArrayInputStream dIn = new ByteArrayInputStream(htmlBytes);
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                DeflateCompressorInputStream defIn = new DeflateCompressorInputStream(dIn);
            ){
                byte[] buffer = new byte[1024];
                int n = 0;
                while (-1 != (n = defIn.read(buffer))) {
                    out.write(buffer, 0, n);
                }
                return new String(out.toByteArray());
            } catch (Exception e) {
                logger.error("Error decompressing structured content html string.", e);
            }
            return null;
        }

        public void setHtml(String html) {
            try (
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                DeflateCompressorOutputStream defOut = new DeflateCompressorOutputStream(out);
            ){
                defOut.write(html.getBytes());
                defOut.finish();
                htmlBytes = out.toByteArray();
            } catch (Exception e) {
                logger.error("Error compressing structured content html string.", e);
                // Fall back to string if issue is encountered compressing string.
                this.html = html;
            }
        }
    	
    	@Override
        public String toString() {
            return "StructuredContentHtmlDataEx{" +
                    "html='" + getHtml() + '\'' +
                    '}';
        }
    }
}
