package com.about.mantle.model.extended;

import java.io.Serializable;

import com.about.mantle.model.extended.docv2.SliceableListEx;

public class NotificationEx implements Serializable {
	private static final long serialVersionUID = 1L;
	private Long docId;
	private SliceableListEx<NotificationItem> notificationItems;

	public Long getDocId() {
		return docId;
	}

	public void setDocId(Long docId) {
		this.docId = docId;
	}

	public SliceableListEx<NotificationItem> getNotificationItems() {
		return notificationItems;
	}

	public void setNotificationItems(SliceableListEx<NotificationItem> notificationItems) {
		this.notificationItems = notificationItems;
	}
	
	public static class NotificationItem implements Serializable {
		private static final long serialVersionUID = 1L;
		private Long startDate;
		private Long endDate;
		private String type;
		private String content;
		private String header;
		private String linkText;
		private String linkHref;
		private String moniker;

		public Long getStartDate() {
			return startDate;
		}

		public void setStartDate(Long startDate) {
			this.startDate = startDate;
		}

		public Long getEndDate() {
			return endDate;
		}

		public void setEndDate(Long endDate) {
			this.endDate = endDate;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public String getContent() {
			return content;
		}

		public void setContent(String content) {
			this.content = content;
		}
		
		public String getHeader() {
			return header;
		}

		public void setHeader(String header) {
			this.header = header;
		}
		
		public String getLinkText() {
			return linkText;
		}

		public void setLinkText(String linkText) {
			this.linkText = linkText;
		}
		
		public String getLinkHref() {
			return linkHref;
		}

		public void setLinkHref(String linkHref) {
			this.linkHref = linkHref;
		}

		public String getMoniker() {
			return moniker;
		}

		public void setMoniker(String moniker) {
			this.moniker = moniker;
		}

		@Override
		public String toString() {
			final StringBuilder sb = new StringBuilder("NotificationItem{");
			sb.append("startDate=").append(startDate);
			sb.append(", endDate=").append(endDate);
			sb.append(", type='").append(type).append('\'');
			sb.append(", content='").append(content).append('\'');
			sb.append(", header='").append(header).append('\'');
			sb.append(", linkText='").append(linkText).append('\'');
			sb.append(", linkHref='").append(linkHref).append('\'');
			sb.append(", moniker='").append(moniker).append('\'');
			sb.append('}');
			return sb.toString();
		}
	}
}
