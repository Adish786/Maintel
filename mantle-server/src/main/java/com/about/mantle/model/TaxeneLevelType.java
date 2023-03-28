package com.about.mantle.model;

public enum TaxeneLevelType {
	ROOT(0),
	LEVEL1(1, ROOT),
	LEVEL2(2, LEVEL1),
	LEVEL3(3, LEVEL2),
	LEVEL4(4, LEVEL3),
	LEVEL5(5, LEVEL4);
	
	private int level;
	private TaxeneLevelType parent;
	
	private TaxeneLevelType(Integer level) {
		this(level, null);
	}
	
	private TaxeneLevelType(Integer level, TaxeneLevelType parent) {
		this.level = level;
		this.parent = parent;
	}
	
	public static TaxeneLevelType fromLevelId(int level) {
		for (TaxeneLevelType levelType : TaxeneLevelType.values()) {
			if (levelType.level == level) return levelType;
		}
		return null;
	}
	
	public int getLevelId() {
		return level;
	}
	
	public TaxeneLevelType getParent() {
		return parent;
	}
	
}
