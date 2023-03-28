package com.about.mantle.model.tasks;

import com.about.globe.core.task.annotation.Task;
import com.about.globe.core.task.annotation.TaskParameter;
import com.about.globe.core.task.annotation.Tasks;

@Tasks
public class ToolsTask {
	
	@Task(name = "getToolName")
	public String getToolName(@TaskParameter(name = "url") String url){
		return url.substring(url.lastIndexOf("/") +1);
	}

}
