package com.about.mantle.model.services;

import java.text.MessageFormat;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.about.mantle.model.extended.curatedlist.DocumentCuratedListEx;
import com.about.mantle.model.extended.curatedlist.DocumentCuratedListOfListEx;
import com.about.mantle.model.extended.curatedlist.ImageCuratedListEx;
import com.about.mantle.model.extended.docv2.SliceableListEx;

public interface CuratedListService {

	SliceableListEx<ImageCuratedListEx> getImageListByName(String listName, Boolean activeOnly, Integer pageNum,
			Integer itemsPerPage, Long activeDate);

	SliceableListEx<DocumentCuratedListEx> getDocumentSummaryListByName(String listName, Boolean activeOnly, Integer pageNum,
			Integer itemsPerPage, Long activeDate);

	SliceableListEx<DocumentCuratedListEx> getDocumentSummaryListByName(String listName, Boolean activeOnly, Integer pageNum,
			Integer itemsPerPage, Long activeDate, String projection);

	SliceableListEx<DocumentCuratedListEx> getDocumentSummaryListHistoryByName(String listName, Integer historyDepth,
			Integer pageNum, Integer itemsPerPage);

	SliceableListEx<DocumentCuratedListOfListEx> getDocumentSummaryListOfListByName(String listName, Boolean activeOnly,
			Integer pageNum, Integer itemsPerPage, Long activeDate);

	SliceableListEx<DocumentCuratedListOfListEx> getDocumentSummaryListOfListByName(String listName, Boolean activeOnly,
			Integer pageNum, Integer itemsPerPage, Long activeDate, String projection);

	public static class CuratedListRequest {
		private String listName;
		private boolean activeOnly;
		private Long activeDateMillis;

		private CuratedListRequest(Builder builder) {
			listName = builder.listName;
			activeOnly = builder.activeOnly;
			if(builder.activeDateMillis != null) {
				activeDateMillis = builder.activeDateMillis;
				activeOnly = true;
			}
		}

		public static Builder builder() {
			return new CuratedListRequest.Builder();
		}

		public String getListName() {
			return listName;
		}

		public boolean isActiveOnly() {
			return this.activeOnly;
		}

		public Long getActiveDate() {
			return activeDateMillis;
		}

		@Override
		public int hashCode() {
			return new HashCodeBuilder()
				.append(listName)
				.append(activeOnly)
				.append(activeDateMillis)
				.build();
		}

		@Override
		public String toString() {
			return MessageFormat.format("{0},{1},{2}", listName, activeOnly, activeDateMillis);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}

			if (!(obj instanceof CuratedListRequest)) {
				return false;
			}

			CuratedListRequest other = (CuratedListRequest) obj;

			return new EqualsBuilder()
					.append(listName, other.listName)
					.append(activeOnly, other.activeOnly)
					.append(activeDateMillis, other.activeDateMillis)
					.build();
		}


		/**
		 *
		 * {@link CuratedListRequest} Builder
		 *
		 */
		public static class Builder {
			private String listName;
			private boolean activeOnly;
			private Long activeDateMillis;

			public Builder setListName(String listName) {
				this.listName = listName;
				return this;
			}

			public Builder setActiveOnly(Boolean activeOnly) {
				if(activeOnly != null) {
					this.activeOnly = activeOnly;
				}
				return this;
			}

			public Builder setActiveDateMillis(Long activeDateMillis) {
				this.activeDateMillis = activeDateMillis;
				return this;
			}

			public CuratedListRequest build() {
				return new CuratedListRequest(this);
			}
		}
	}
	
}
