package com.about.mantle.handlers.methods;

import com.about.globe.core.exception.GlobeNotFoundException;

/**
 * This class provides template names for rendering purpose for bunch of end-points that mantle 
 * provides out of the box. If vertical wants to provide specific template name for an end-point then
 * they should be overriding appropriate method below and provide their template name. By default, 
 * if vertical doesn't override a method of some template then mantle will render 404 (applies to most of the template).
 * e.g. mantle doesn't define what should be search template name out of the box, thus renders 404 page for search end-point.
 */
public class CommonTemplateNames {

	protected String homeTemplateName() {
		return "homeTemplate";
	}

	protected String pageNotFoundTemplateName() {
		return "pageNotFoundTemplate";
	}

	protected String pageGoneTemplateName() {
		return "pageGoneTemplate";
	}

	protected String plComponentTemplateName() {
		return "mntl-pl-component";
	}

	protected String editPlComponentTemplateName() {
		return "mntl-edit-pl-component";
	}

	protected String plTemplateName() {
		return "mntl-pl";
	}

	protected String privacyRequestTemplate() {
		return "privacyRequestTemplate";
	}
	
	protected String facebookShareRedirectTemplateName() {
		return "mntl-facebook-share-redirect-template";
	}

	protected String searchTemplateName() {
		return "searchTemplate";
	}
	
	protected String facebookInstantArticleTemplate() {
		throw new GlobeNotFoundException("Facebook instant article template not found.");
	}

	protected String appleNewsTemplate() {
		throw new GlobeNotFoundException("Apple news article template not found.");
	}

	protected String embedEndPointTemplate() {
		return "mntl-embed-endpoint-template";
	}

	protected String logoutTemplateName() {
		return "logoutTemplate";
	}
	
}
