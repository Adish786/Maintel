package com.about.mantle.visual;

import com.about.venus.core.driver.WebElementEx;
import org.openqa.selenium.By;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VisualTestData {
	public static List<String> dummyGlobalComps;
	public static List<String> hideGlobalComps;
	public static List<String> deleteGlobalComps = dummyGlobalComps = hideGlobalComps = new ArrayList<>();
	
	private String componentUnderTest;
	private Object elementLocator;
	private boolean withFullScroll;
	private List<String> dummyComps;
	private List<String> hideComps;
	private List<String> deleteComps = dummyComps = hideComps = new ArrayList<>();
	private Map<String,String> hideAttribute = new HashMap<String, String>();
	private Map<String,String> updateAttribute = new HashMap<String, String>();
	private List<String> attributeValues;
	private Object elementToClick;
	private boolean enableClick = false;
	private boolean isProctorTest = false;

	public boolean isWithFullScroll() {
		return withFullScroll;
	}

	public void setWithFullScroll(boolean withFullScroll) {
		this.withFullScroll = withFullScroll;
	}

	public String getComponentUnderTest() {
		return componentUnderTest;
	}

	public void setComponentUnderTest(String componentUnderTest) {
		this.componentUnderTest = componentUnderTest;
	}

	public Object getElementLocator() {

		return elementLocator;
	}

	public void setElementLocator(By elementLocator) {
		this.elementLocator = elementLocator;
	}

	public void setElementLocator(WebElementEx elementLocator) {
		this.elementLocator = elementLocator;
	}

	public List<String> getDeleteComps() {
		return deleteComps;
	}

	public void setDeleteComps(List<String> deleteComps) {
		this.deleteComps = deleteComps;
	}

	public List<String> getDummyComps() {
		return dummyComps;
	}

	public void setDummyComps(List<String> dummyComps) {
		this.dummyComps = dummyComps;
	}

	public List<String> getHideComps() {
		return hideComps;
	}

	public void setHideAttribute(Map<String, String> hideAttribute) {
		this.hideAttribute = hideAttribute;
	}

	public Map<String, String> getHideAttributes() {
		return this.hideAttribute;
	}

	public void setUpdateAttribute(Map<String, String> updateAttribute) {
		this.updateAttribute = updateAttribute;
	}

	public Map<String, String> getUpdateAttributes() {
		return this.updateAttribute;
	}

	public void setAttributeValues(List<String> attributeValues) {
		this.attributeValues = attributeValues;
	}
	
	public List<String> getAttributeValues() {
		return attributeValues;
	}

	public void setHideComps(List<String> hideComps) {
		this.hideComps = hideComps;
	}
	
	public Object getElementToClick() {
		return elementToClick;
	}

	public boolean getEnableClick() {
		return this.enableClick;
	}

	public void setElementToClick(By elementToClick, boolean enableClick) {
		this.elementToClick = elementToClick;
		this.enableClick = enableClick;
	}

	public void setElementToClick(WebElementEx elementToClick, boolean enableClick) {
		this.elementToClick = elementToClick;
		this.enableClick = enableClick;
	}
	
	public boolean isProctorTest() {
		return this.isProctorTest;
	}
	
	public void setProctorTest(boolean isProctorTest) {
		this.isProctorTest = isProctorTest;
	}

}
