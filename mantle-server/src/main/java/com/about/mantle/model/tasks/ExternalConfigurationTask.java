package com.about.mantle.model.tasks;

import com.about.globe.core.http.RequestContext;
import com.about.globe.core.task.annotation.RequestContextTaskParameter;
import com.about.globe.core.task.annotation.Task;
import com.about.globe.core.task.annotation.TaskParameter;
import com.about.globe.core.task.annotation.Tasks;
import com.about.hippodrome.metrics.annotation.TimedComponent;
import com.about.mantle.model.ConfigValue;
import com.netflix.archaius.api.PropertyFactory;

/*
 * RequestContext in Task methods allows for subclasses to override these tasks and utilize it, 
 * even if mantle does not require it.
 */
@Tasks
public class ExternalConfigurationTask {

	protected final PropertyFactory propertyFactory;

	public ExternalConfigurationTask(PropertyFactory propertyFactory) {
		this.propertyFactory = propertyFactory;
	}
	
	@Task(name = "externalConfig")
	public ConfigValue<String> getExternalConfig(@RequestContextTaskParameter RequestContext requestContext, 
			@TaskParameter(name = "key") String key) {
		return getConfigValue(requestContext, key, null);
	}
	
	@Task(name = "externalConfig")
	@TimedComponent(category = "task")
	public ConfigValue<String> getConfigValue(@RequestContextTaskParameter RequestContext requestContext,
			@TaskParameter(name = "key") String key,
			@TaskParameter(name = "default") String defaultValue) {
		return getExternalConfig(requestContext, key, defaultValue, String.class);
	}

	@Task(name = "externalConfig")
	@TimedComponent(category = "task")
	public <T> ConfigValue<T> getExternalConfig(@RequestContextTaskParameter RequestContext requestContext,
			@TaskParameter(name = "key") String key,
			@TaskParameter(name = "default") T defaultValue, @TaskParameter(name = "type") Class<T> type) {
		return new ConfigValue<T>(key, propertyFactory.getProperty(key).asType(type, defaultValue).get());
	}
	
}
