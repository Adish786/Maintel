package com.about.mantle.model.tasks;

import com.about.globe.core.http.RequestContext;
import com.about.globe.core.model.extended.Configs;
import com.about.globe.core.task.annotation.RequestContextTaskParameter;
import com.about.globe.core.task.annotation.TaskParameter;
import com.about.hippodrome.url.UrlData;

public interface NodeConfigurationTask {

	Configs getNodeConfigs(@RequestContextTaskParameter RequestContext requestContext);
	
	Configs getNodeConfigs(UrlData urlData);

	Configs getNodeConfigs(@TaskParameter(name = "url") String url);

	Configs getNodeConfigs(@TaskParameter(name = "docId") Long docId);
}
