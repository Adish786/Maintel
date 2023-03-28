package com.about.mantle.model.debug;

import java.util.List;

/**
 * Contains the name of the task, it's return type and parameters
 * 
 * Used to display available tasks in AppTasksController
 */
public class TaskOutput implements Comparable<TaskOutput>{
	
	private String taskName;
	private String returnType;
	List<TaskParameterOutput> taskInputs;
	
	public String getReturnType() {
		return returnType;
	}
	
	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}
	
	public List<TaskParameterOutput> getTaskInputs() {
		return taskInputs;
	}
	
	public void setTaskInputConfigurations(List<TaskParameterOutput> configurations) {
		this.taskInputs = configurations;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	@Override
	public int compareTo(TaskOutput o) {
		
		//Keeps everything in alphabetical order regardless of casing 
		//otherwise will see AUTHOR, DOCUMENT, TaxeneNodeConfigs, aboutFollowSocialLinks
		int compare = taskName.toLowerCase().compareTo(o.taskName.toLowerCase());
		
		
		//If the name matches, order based on the number of inputs
		if(compare == 0) {
			int size = taskInputs != null ? taskInputs.size() : 0;
			int osize = o.taskInputs != null ? o.taskInputs.size() : 0;
			compare = size - osize;
		}
		
		return compare;
	}
	
	public static class TaskParameterOutput {
		private String name;
		private String type;
		
		public String getName() {
			return name;
		}
		
		public void setName(String name) {
			this.name = name;
		}
		
		public String getType() {
			return type;
		}
		
		public void setType(String type) {
			this.type = type;;
		}

	}


}
