package com.about.mantle.model.extended.docv2;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableMap;

public class TopicMapDocumentEx extends BaseDocumentEx {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private MapInfo mapInfo;
	private IntroCardEx introCard;
	private SliceableListEx<CategoryEx> cardCategories = SliceableListEx.emptyList();
	private SliceableListEx<CardEx> cards = SliceableListEx.emptyList();
	private Map<String,CategoryEx> categoryMap = ImmutableMap.<String, CategoryEx>of();
	
	public MapInfo getMapInfo() {
		return mapInfo;
	}
	public void setMapInfo(MapInfo mapInfo) {
		this.mapInfo = mapInfo;
	}
	public IntroCardEx getIntroCard() {
		return introCard;
	}
	public void setIntroCard(IntroCardEx introCard) {
		this.introCard = introCard;
	}
	public SliceableListEx<CategoryEx> getCardCategories() {
		return cardCategories;
	}
	public void setCardCategories(SliceableListEx<CategoryEx> cardCategories) {
		this.cardCategories = SliceableListEx.emptyIfNull(cardCategories);
		this.categoryMap = this.cardCategories.getList().stream().collect(Collectors.toMap(CategoryEx::getName, Function.identity()));
	}
	public SliceableListEx<CardEx> getCards() {
		return cards;
	}
	public void setCards(SliceableListEx<CardEx> cards) {
		this.cards = SliceableListEx.emptyIfNull(cards);
	}
	public Map<String, CategoryEx> getCategoryMap() {
		return categoryMap;
	}

	public void setCategoryMap(Map<String, CategoryEx> categoryMap) {
		this.categoryMap = categoryMap;
	}
	
	public static class Coordinate implements Serializable {
		private static final long serialVersionUID = 1L;

		private BigDecimal latitude;
		private BigDecimal longitude;

		public BigDecimal getLatitude() {
			return latitude;
		}

		public void setLatitude(BigDecimal latitude) {
			this.latitude = latitude;
		}

		public BigDecimal getLongitude() {
			return longitude;
		}

		public void setLongitude(BigDecimal longitude) {
			this.longitude = longitude;
		}
	}
	
	public static class MapConfig implements Serializable {
		private static final long serialVersionUID = 1L;

		private Coordinate center;
		private Integer zoomLevel;

		public Coordinate getCenter() {
			return center;
		}

		public void setCenter(Coordinate center) {
			this.center = center;
		}

		public Integer getZoomLevel() {
			return zoomLevel;
		}

		public void setZoomLevel(Integer zoomLevel) {
			this.zoomLevel = zoomLevel;
		}

	}

	public static class MapInfo implements Serializable {

		private static final long serialVersionUID = 1L;

		private MapConfig desktop;
		private MapConfig tablet;
		private MapConfig mobile;

		public MapConfig getDesktop() {
			return desktop;
		}

		public void setDesktop(MapConfig desktop) {
			this.desktop = desktop;
		}

		public MapConfig getTablet() {
			return tablet;
		}

		public void setTablet(MapConfig tablet) {
			this.tablet = tablet;
		}

		public MapConfig getMobile() {
			return mobile;
		}

		public void setMobile(MapConfig mobile) {
			this.mobile = mobile;
		}

	}
}
