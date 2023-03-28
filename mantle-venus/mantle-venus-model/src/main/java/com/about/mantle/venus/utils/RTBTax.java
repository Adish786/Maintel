package com.about.mantle.venus.utils;

import org.json.JSONObject;

public class RTBTax  {
	private JSONObject jsonObj;

	public RTBTax(JSONObject obj) {
		this.jsonObj = obj;		
	}
	public String s() {
		return jsonObj.get("s").toString();
	}
	
	public String sd() {
		return jsonObj.get("sd").toString();
	}
	
	public String kv() {
		return jsonObj.get("kv").toString();
	}
	
}
