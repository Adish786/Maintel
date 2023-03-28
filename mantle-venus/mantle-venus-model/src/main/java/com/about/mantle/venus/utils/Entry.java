package com.about.mantle.venus.utils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Entry {
	
	public static final Entry NULL = new Entry();

	private DocType docType;
	private UrlLog url;
	private String userId;
	private String webPlatform;
	private String client;
	private String layout;
	private String userAgent;
	private String globeTmpl;
	private String authId;
	private String sessId;
	private boolean isPv;
	private String actorId;
	private String reqId;
	private String leaId;
	private String leuId;
	

	public String getWebPlatform() {
		return webPlatform;
	}

	public void setWebPlatform(String webPlatform) {
		this.webPlatform = webPlatform;
	}

	public String getClient() {
		return client;
	}

	public void setClient(String client) {
		this.client = client;
	}

	public String getLayout() {
		return layout;
	}

	public void setLayout(String layout) {
		this.layout = layout;
	}


	public String getUserAgent() {
		return userAgent;
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

	public String getGlobeTmpl() {
		return globeTmpl;
	}

	public void setGlobeTmpl(String globeTmpl) {
		this.globeTmpl = globeTmpl;
	}

	public String getAuthId() {
		return authId;
	}

	public void setAuthId(String authId) {
		this.authId = authId;
	}

	public DocType getDocType() {
		return docType;
	}

	public void setDocType(DocType docType) {
		this.docType = docType;
	}

	public String getSessId() {
		return sessId;
	}

	public void setSessId(String sessId) {
		this.sessId = sessId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public UrlLog getUrl() {
		return url;
	}

	public void setUrl(UrlLog url) {
		this.url = url;
	}

	public boolean getIsPv() {
		return isPv;
	}

	public void setIsPv(boolean isPv) {
		this.isPv = isPv;
	}

	public String getActorId() {
		return actorId;
	}

	public void setActorId(String actorId) {
		this.actorId = actorId;
	}
	
	public String getLeaId() {
		return leaId;
	}
	
	public void setLeaId(String leaId) {
		this.leaId = leaId;
	}
	
	public String getLeuId() {
		return leuId;
	}
	
	public void setLeuId(String leuId) {
		this.leuId = leuId;
	}

	public String getReqId() {
		return reqId;
	}

	public void setReqId(String reqId) {
		this.reqId = reqId;
	}

	@Override
	public String toString() {
		return "Entry [userId=" + userId + ", url=" + url + "]";
	}

}