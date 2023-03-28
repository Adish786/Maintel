package com.about.mantle.model.extended;

import java.io.Serializable;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import com.about.hippodrome.url.info.NodeType;
import com.about.mantle.model.extended.docv2.BaseDocumentEx;
import com.about.mantle.model.extended.docv2.BaseDocumentEx.State;
import com.about.mantle.model.extended.docv2.BaseDocumentEx.Vertical;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class DeionSearchResultItemEx implements Serializable {
	private static final long serialVersionUID = 1L;

	private String key;
	private String revisionKey;
	private String authorKey;

	private String url;
	private String rootUrl;
	private TemplateTypeEx templateType;
	private NodeType nodeType;
	private String dirName;
	private String channel;

	private State state;
	private DateTime activeDate;

	private DateTime createDate;
	private DateTime updateDate;
	private DateTime displayed;

	private String title;
	private String socialTitle;
	private String heading;
	private String description;
	private String intro;
	private String termName;

	private Integer totalPage;
	private Integer page;

	private String target;

	private DateTime lastModified;

	private Long docId;

	private Vertical vertical;
	
	private BaseDocumentEx document;

	private String entityRefInfoId;
	
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getRevisionKey() {
		return revisionKey;
	}

	public void setRevisionKey(String revisionKey) {
		this.revisionKey = revisionKey;
	}

	public String getAuthorKey() {
		return authorKey;
	}

	public void setAuthorKey(String authorKey) {
		this.authorKey = authorKey;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getRootUrl() {
		return rootUrl;
	}

	public void setRootUrl(String rootUrl) {
		this.rootUrl = rootUrl;
	}

	public TemplateTypeEx getTemplateTypeEx() {
		return templateType;
	}

	public void setTemplateTypeEx(TemplateTypeEx templateType) {
		this.templateType = templateType;
	}

	public NodeType getNodeType() {
		return nodeType;
	}

	public void setNodeType(NodeType nodeType) {
		this.nodeType = nodeType;
	}

	public String getDirName() {
		return dirName;
	}

	public void setDirName(String dirName) {
		this.dirName = dirName;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public DateTime getActiveDate() {
		return activeDate;
	}

	public void setActiveDate(DateTime activeDate) {
		this.activeDate = activeDate;
	}

	public DateTime getCreateDate() {
		return createDate;
	}

	public void setCreateDate(DateTime createDate) {
		this.createDate = createDate;
	}

	public DateTime getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(DateTime updateDate) {
		this.updateDate = updateDate;
	}

	public DateTime getDisplayed() {
		return displayed;
	}
	
	@JsonIgnore
	public DateTime getDisplayedDateInNewYorkTimeZone() {
		return displayed.withZone(DateTimeZone.forID("America/New_York"));
	}

	public void setDisplayed(DateTime displayed) {
		this.displayed = displayed;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSocialTitle() {
		return socialTitle;
	}

	public void setSocialTitle(String socialTitle) {
		this.socialTitle = socialTitle;
	}

	public String getHeading() {
		return heading;
	}

	public void setHeading(String heading) {
		this.heading = heading;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getIntro() {
		return intro;
	}

	public void setIntro(String intro) {
		this.intro = intro;
	}

	public String getTermName() {
		return termName;
	}

	public void setTermName(String termName) {
		this.termName = termName;
	}

	public Integer getTotalPage() {
		return totalPage;
	}

	public void setTotalPage(Integer totalPage) {
		this.totalPage = totalPage;
	}

	public Integer getPage() {
		return page;
	}

	public void setPage(Integer page) {
		this.page = page;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public DateTime getLastModified() {
		return lastModified;
	}

	public void setLastModified(DateTime lastModified) {
		this.lastModified = lastModified;
	}

	public Long getDocId() {
		return docId;
	}

	public void setDocId(Long docId) {
		this.docId = docId;
	}

	public Vertical getVertical() {
		return vertical;
	}

	public void setVertical(Vertical vertical) {
		this.vertical = vertical;
	}
	
	public BaseDocumentEx getDocument() {
		return document;
	}

	public void setDocument(BaseDocumentEx document) {
		this.document = document;
	}

	public String getEntityRefInfoId() {
		return entityRefInfoId;
	}

	public void setEntityRefInfoId(String entityRefInfoId) {
		this.entityRefInfoId = entityRefInfoId;
	}
}
