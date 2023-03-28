package com.about.mantle.spring;

import java.io.IOException;
/**
 * Utility class to find projectinfo from manifest.mf file. 
 * Spring boot META-INF directory is not placed under regular classes directory,
 * instead it is parallel to classes directory. This looks for that location,
 * and reads project info from manifest.mf file.
 */
import java.net.URL;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.about.hippodrome.util.projectinfo.ManifestProjectInfoFinder;
import com.about.hippodrome.util.projectinfo.ProjectInfo;
import com.about.hippodrome.util.projectinfo.ProjectInfoFinder;

public class SpringBootManifestProjectInfoFinder implements ProjectInfoFinder {

	private static Logger logger = LoggerFactory.getLogger(SpringBootManifestProjectInfoFinder.class);

	private final ProjectInfo projectInfo;

	public SpringBootManifestProjectInfoFinder(Class<?> mainClass) {
		this.projectInfo = loadProjectInfo(mainClass);
	}

	private ProjectInfo loadProjectInfo(Class<?> mainClass) {
		ProjectInfo answer = null;
		String manifestResource = "";
		try {
			// try first to get the manifest from spring boot, if not found then fall back to
			// default one. This will serve for both Jar and IDE deployments
			String classResource = "/BOOT-INF/classes/" + mainClass.getName().replace('.', '/') + ".class";
			URL resource = mainClass.getResource(classResource);
			if (resource == null) {
				answer = new ManifestProjectInfoFinder(mainClass).find();
			} else {
				manifestResource = resource.toString().replace(classResource, "/META-INF/MANIFEST.MF");
				answer = ProjectInfo.from(new URL(manifestResource));
			}

		} catch (IOException | RuntimeException e) {
			// Catching all because this should never cause an application failure
			logger.error("Failed to get project info from manifest: " + manifestResource, e);
			answer = new ProjectInfo("ERROR", "ERROR", "ERROR", DateTime.now());
		}

		return answer;
	}

	@Override
	public ProjectInfo find() {
		return this.projectInfo;
	}
}
