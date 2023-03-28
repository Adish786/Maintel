package com.about.mantle.infocat.model;

import org.joda.time.DateTime;
import java.io.Serializable;

public class Audit implements Serializable {
	private static final long serialVersionUID = 1L;

	private DateTime createdDate;
	private String createdUserFullName;
	private DateTime lastUpdatedDate;
	private String lastUpdatedUserFullName;

	public DateTime getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(DateTime createdDate) {
		this.createdDate = createdDate;
	}

	public String getCreatedUserFullName() {
		return createdUserFullName;
	}

	public void setCreatedUserFullName(String createdUserFullName) {
		this.createdUserFullName = createdUserFullName;
	}

	public DateTime getLastUpdatedDate() {
		return lastUpdatedDate;
	}

	public void setLastUpdatedDate(DateTime lastUpdatedDate) {
		this.lastUpdatedDate = lastUpdatedDate;
	}

	public String getLastUpdatedUserFullName() {
		return lastUpdatedUserFullName;
	}

	public void setLastUpdatedUserFullName(String lastUpdatedUserFullName) {
		this.lastUpdatedUserFullName = lastUpdatedUserFullName;
	}

	@Override
	public String toString() {
		final StringBuffer sb = new StringBuffer("Audit{");
		sb.append("createdDate='").append(createdDate);
		sb.append("', createdUserFullName='").append(createdUserFullName);
		sb.append("', lastUpdatedDate='").append(lastUpdatedDate);
		sb.append("', lastUpdatedUserFullName='").append(lastUpdatedUserFullName);
		sb.append("'}");
		return sb.toString();
	}
}