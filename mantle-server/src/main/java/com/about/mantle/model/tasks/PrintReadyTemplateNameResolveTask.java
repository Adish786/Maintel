package com.about.mantle.model.tasks;

import com.about.globe.core.http.RequestContext;
import com.about.globe.core.task.annotation.RequestContextTaskParameter;
import com.about.globe.core.task.annotation.Task;
import com.about.globe.core.task.annotation.Tasks;
import com.about.hippodrome.metrics.annotation.TimedComponent;

@Tasks
public class PrintReadyTemplateNameResolveTask {

	private TemplateNameResolveTask defaultTemplateNameResolveTask;
	
	public PrintReadyTemplateNameResolveTask(TemplateNameResolveTask defaultTemplateNameResolveTask) {
		this.defaultTemplateNameResolveTask = defaultTemplateNameResolveTask;
	}

	@Task(name = "getPrintReadyTemplateName")
	@TimedComponent(category = "task")
	public String getTemplateName(@RequestContextTaskParameter RequestContext requestContext) {
		String defaultTemplateName = defaultTemplateNameResolveTask.getTemplateName(requestContext);
		if (defaultTemplateName == null) return null;
		return defaultTemplateName+"Print";
	}

}
