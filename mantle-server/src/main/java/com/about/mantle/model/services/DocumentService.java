package com.about.mantle.model.services;

import com.about.mantle.model.tasks.DocumentTask;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.commons.lang3.math.NumberUtils;

import com.about.mantle.model.extended.TemplateTypeEx;
import com.about.mantle.model.extended.docv2.BaseDocumentEx;
import com.about.mantle.model.extended.docv2.BaseDocumentEx.State;
import com.about.mantle.model.extended.docv2.RedirectDocumentEx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public interface DocumentService {
	public static final Logger logger = LoggerFactory.getLogger(DocumentTask.class);
	
	BaseDocumentEx getDocument(DocumentReadRequestContext documentRequestContext);

	/**
     * Creates a redirect document.  Useful if a provided document was available but is perhaps on another vertical.
     *
     * @param url
     * @param targetUrl
     * @return
     */
    public static RedirectDocumentEx createRedirectDocument(String url, String targetUrl) {
        RedirectDocumentEx document = new RedirectDocumentEx();
        document.setTemplateType(TemplateTypeEx.REDIRECT);
        document.setUrl(url);
        document.setTarget(targetUrl);
        return document;
    }

	/**
	 * Creates a {@link DocumentReadRequestContext} that, for caching reasons, always includes doc summaries and follows
	 * redirects for doc summaries.
	 */
	public static DocumentReadRequestContext createDocumentRequestContext(String key, State state, Long activeDate) {

		DocumentReadRequestContext.Builder documentRequestContext = new DocumentReadRequestContext.Builder()
				.setState(state).setActiveDate(activeDate)
				.setUrl(key);

		return documentRequestContext.build();
	}
	
	/**
	 * Runs processing on document 
	 * 
	 * @param document Document to be modified
	 * @return modified document
	 */
	public BaseDocumentEx processDocument(BaseDocumentEx document);

	/**
	 * Convenience method that returns a document when given a document ID and request parameters
	 * @param key Key of document to fetch
	 * @param params Params for getting State and Date values
	 * @return Document to be fetched
	 */
	default BaseDocumentEx getDocument(String key, final Map<String, String[]> params) {
		State rcState = getStateFromRequestParameters(params);
		Long rcDate = getDateFromRequestParameters(params);

		return getDocument(createDocumentRequestContext(key, rcState, rcDate));
	}

	public static State getStateFromRequestParameters(final Map<String, String[]> params) {
		State answer = State.ACTIVE;
		if (params.containsKey("state")) {
			try {
				answer = State.valueOf(params.get("state")[0].toUpperCase());
			} catch (Exception e) {
				logger.warn("Bad state value provided for state request param", e);
			}

		}
		return answer;
	}

	public static Long getDateFromRequestParameters(final Map<String, String[]> params) {
		Long answer = null;
		if (params.containsKey("et")) {
			try {
				answer = Long.valueOf(params.get("et")[0]);
			} catch (Exception e) {
				logger.warn("Non-number value provided for et request param", e);
			}
		}
		return answer;
	}

    
    public static class DocumentReadRequestContext {
    	private final String url;
    	private final Long docId;
    	private final State state;
    	private final Long activeDate;
    	private final boolean useCache;

    	private DocumentReadRequestContext(Builder builder) {
    		if (builder.url == null && builder.docId == null)
    			throw new IllegalArgumentException("Both docId and url are not set.");
    		if (builder.url != null && builder.docId != null)
    			throw new IllegalArgumentException("DocId and url are mutually exclusive.");

    		url = builder.url;
    		docId = builder.docId;
    		state = builder.state;
    		activeDate = builder.activeDate;
    		useCache = (builder.useCache == null) ? true : builder.useCache;
    	}

    	public String getKey() {
    		return docId != null ? docId.toString() : url;
    	}

    	public String getUrl() {
    		return url;
    	}

    	public Long getDocId() {
    		return docId;
    	}

    	public State getState() {
    		return state;
    	}

    	public Long getActiveDate() {
    		return activeDate;
    	}

    	public boolean getUseCache() {
    		return useCache;
    	}

    	@Override
    	public int hashCode() {
    		// @formatter:off
    		return new HashCodeBuilder()
    				.append(url)
    				.append(docId)
    				.append(state)
    				.append(activeDate)
    				.append(useCache)
    				.build();
    		// @formatter:on
    	}

    	@Override
    	public boolean equals(Object obj) {
    		if (this == obj) return true;
    		if (!(obj instanceof DocumentReadRequestContext)) return false;

    		DocumentReadRequestContext other = (DocumentReadRequestContext) obj;
    		// @formatter:off
    		return new EqualsBuilder()
    				.append(url, other.url)
    				.append(docId, other.docId)
    				.append(state, other.state)
    				.append(activeDate, other.activeDate)
    				.append(useCache, other.useCache)
    				.build();
    		// @formatter:on
    	}

    	@Override
    	public String toString() {
    		// @formatter:off
    		return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
    				.append("url", url)
    				.append("docId", docId)
    				.append("state", state)
    				.append("activedate",  activeDate)
    				.append("useCache", useCache)
    				.appendSuper(super.toString())
    				.build();
    		// @formatter:on
    	}

    	public static class Builder {
    		private String url;
    		private Long docId;
    		private State state;
    		private Long activeDate;
    		private Boolean useCache;

    		public Builder setUrl(String url) {
    			if (url != null) {
    				if (this.url != null) throw new IllegalArgumentException("Url is already set.");
    				if (NumberUtils.isDigits(url)) {
    					setDocId(Long.valueOf(url));
    				} else {
    					this.url = url;
    				}
    			}
    			return this;
    		}

    		public Builder setDocId(Long docId) {
    			if (docId != null) {
    				if (this.docId != null) throw new IllegalArgumentException("DocId is already set.");
    				this.docId = docId;
    			}
    			return this;
    		}

    		public Builder setState(State state) {
    			this.state = state;
    			return this;
    		}

    		public Builder setActiveDate(Long activeDate) {
    			this.activeDate = activeDate;
    			return this;
    		}

    		public Builder setUseCache(Boolean useCache) {
    			this.useCache = useCache;
    			return this;
    		}
    		
    		public DocumentReadRequestContext build() {
    			return new DocumentReadRequestContext(this);
    		}
    	}
    }
}
