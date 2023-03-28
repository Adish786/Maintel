package com.about.mantle.venus.utils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.about.venus.core.driver.proxy.VenusHarRequest;
import com.about.venus.core.utils.Url;
import com.google.common.collect.ListMultimap;

public class RTBParams {
	private ListMultimap<String, String> RTBParams;

	private JSONArray slotParams;
	private JSONObject pjParams;
	private String bbParams;

	public RTBParams(VenusHarRequest request) {
		RTBParams = request.url().parameters();

		slotParams = slotsArray();
		pjParams = pjParams();
		bbParams = bbParams();
	}
	
	public RTBParams(Url url) {
		RTBParams = url.parameters();
	}
	
	public JSONArray slotsArray() {
		if (RTBParams.get("slots")!= null && (RTBParams.get("slots").size()!=0)) {
			return new JSONArray(RTBParams.get("slots").get(0).toString());
		}
		return null;
	}
	
	public RTBTax getSlotsArrayWithIndex(int index) {
		if(index < slotParams.length()) {
			return new RTBTax(new JSONObject(slotParams.get(index).toString()));
		}
		return null;
	}
	
	public JSONObject pjParams() {
		if (RTBParams.get("pj")!= null && (RTBParams.get("pj").size()!=0)) {
			return new JSONObject(RTBParams.get("pj").get(0));
		}
		return null;
	}
	
	public String pjTax0() {
		if (pjParams.get("tax0")!= null ) {
			return pjParams.get("tax0").toString();
		}
		return null;
	}
	
	public String pjTax1() {
		if (pjParams.get("tax1")!= null ) {
			return pjParams.get("tax1").toString();
		}
		return null;
	}
	
	public String pjTax2() {
		if (pjParams.get("tax2")!= null ) {
			return pjParams.get("tax2").toString();
		}
		return null;
	}
	
	public String pjTax3() {
		if (pjParams.get("tax3")!= null ) {
			return pjParams.get("tax3").toString();
		}
		return null;
	}
	
	public String pjSisection() {
		if (pjParams.get("si_section")!= null ) {
			return pjParams.get("si_section").toString();
		}
		return null;
	}
	
	public String bbParams() {
		if (RTBParams.get("bb")!= null && (RTBParams.get("bb").size()!=0)) {
			return RTBParams.get("bb").get(0).toString();
		}
		return null;
	}
	
}
