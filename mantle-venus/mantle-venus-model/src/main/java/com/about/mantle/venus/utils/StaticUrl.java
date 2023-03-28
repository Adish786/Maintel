package com.about.mantle.venus.utils;

import com.about.venus.core.utils.ConfigurationProperties;

public enum StaticUrl {
	
	
	HOMEPAGE("https://www.domain.com/"),
	ABOUTUS("https://www.domain.com/about-us"),
    SEARCH ("https://www.domain.com/search"),
    LEGAL("https://www.domain.com/legal"),
    PRIVACY_POLICY("https://www.domain.com/legal#privacy"),
    CAREERS("http://jobs.jobvite.com/dotdash"),
    CONTACT("https://www.domain.com/about-us#ContactUs"),
    TERMS_OF_USE("https://www.domain.com/legal#terms"),
    ADVERTISE("http://mediakit.domain.com/project-advertising/"),
	ROBOTS_TXT("https://www.domain.com/robots.txt"),
	PATTERN_LIB("https://www.domain.com/pattern-library/");
	
	private String url;
	private String project = ConfigurationProperties.getTargetProject("about");
	private final String domain = ProjectDomainMap.getDomain().get(project) ;
	private StaticUrl(String url) {
		this.url = url.replace("domain",domain ).replace("project", project);
	}
	
	public String url() {
		return url;
	}
	

}