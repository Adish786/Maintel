package com.about.mantle.venus.utils;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

public class ProjectDomainMap {

	private static BiMap<String, String> projectToDomain = HashBiMap.create();

	static {
		projectToDomain.put("health", "verywell");
		projectToDomain.put("reference", "thoughtco");
		projectToDomain.put("tech", "lifewire");
		projectToDomain.put("money", "thebalancemoney");
		projectToDomain.put("travel", "tripsavvy");
		projectToDomain.put("lifestyle", "thespruce");
		projectToDomain.put("white-label", "microverss");
		projectToDomain.put("fasion", "liveabout");
		projectToDomain.put("espanol", "aboutespanol");
		
	}

	
	public static BiMap<String, String> getDomain(){
		return projectToDomain;
	}
	public static BiMap<String, String> getProject(){
		return projectToDomain.inverse();
	}
}
