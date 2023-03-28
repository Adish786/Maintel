package com.about.mantle.venus.utils;

import java.util.Hashtable;

public class TemplateMap {
	private String templateID;
	private String globeTmpl;
	private String actorId;	
	private String leaId;
	private String leuId;
	private Hashtable<String, String> viewTypeMap;

	public TemplateMap(String templateId, String globeTmpl) {
		this.templateID = templateId;
		this.globeTmpl = globeTmpl;
	}
	
	public TemplateMap(Hashtable<String, String> viewTypeMap) {
		this.viewTypeMap = viewTypeMap;
	}

	public String templateId() {
		return templateID;
	}

	public String globeTmpl() {
		return globeTmpl;
	}
	
	public String globeTmpl(String viewType) {
		return viewTypeMap.get(viewType);
	}
	
	public Hashtable<String, String> viewTypeMap() {
		return viewTypeMap;
	}

	public String actorId() {
		return actorId;
	}

	public void setActorId(String actorIdParam) {
		actorId = actorIdParam;
	}

	public String leaId() {
		return leaId;
	}
		
	public void setLeaId(String leaIdParam) {
		leaId = leaIdParam;
	}
	
	public String leuId() {
		return leuId;
	}
		
	public void setLeuId(String leuIdParam) {
		leuId = leuIdParam;
	}		

}