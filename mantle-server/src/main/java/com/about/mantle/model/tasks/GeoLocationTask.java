package com.about.mantle.model.tasks;

import javax.servlet.http.Cookie;

import com.about.globe.core.http.RequestContext;
import com.about.globe.core.task.annotation.RequestContextTaskParameter;
import com.about.globe.core.task.annotation.Task;
import com.about.globe.core.task.annotation.Tasks;

@Tasks
public class GeoLocationTask {

	@Task(name = "showDisclaimer")
	public Boolean showDisclaimer(@RequestContextTaskParameter RequestContext requestContext) {

		Boolean answer = false;

		Cookie showCompliance = requestContext.getCookie("abtCookieDisclaimer");
		// check if cookie is present
		if (showCompliance == null) {
			answer = requestContext.getGeoData().isInEuropeanUnion();
		}

		return answer;
	}
}
