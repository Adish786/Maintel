package com.about.mantle.model.extended.docv2.sc.blocks;

import com.about.mantle.model.extended.docv2.sc.AbstractStructuredContentContentEx;
import com.about.mantle.model.extended.docv2.sc.AbstractStructuredContentDataEx;

public class StructuredContentIframeEx extends AbstractStructuredContentContentEx<StructuredContentIframeEx.StructuredContentIframeDataEx> {

    /**
     * Implements the `IframeContentData` definition of the Structured Content schema
     */
    public static class StructuredContentIframeDataEx extends AbstractStructuredContentDataEx {

    	private String uri;
    	private String caption;
    	private String title;
    	
    	public String getUri() {
    		return uri;
    	}
    	public void setUri(String uri) {
    		this.uri = uri;
    	}
    	public String getCaption() {
    		return caption;
    	}
    	public void setCaption(String caption) {
    		this.caption = caption;
    	}
    	public String getTitle() {
    		return title;
    	}
    	public void setTitle(String title) {
    		this.title = title;
    	}

        @Override
        public String toString() {
            return "StructuredContentIframeDataEx{" +
                    "uri=" + uri + ", " +
            			"caption=" + caption + ", " +
                    "title=" + title +
                    '}';
        }
    }
}
