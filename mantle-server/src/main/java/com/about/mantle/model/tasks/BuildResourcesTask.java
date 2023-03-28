package com.about.mantle.model.tasks;

import com.about.globe.core.task.annotation.Task;
import com.about.globe.core.task.annotation.Tasks;
import com.about.hippodrome.util.projectinfo.ProjectInfo;

import java.util.HashMap;
import java.util.Map;

@Tasks
public class BuildResourcesTask {

    private final ProjectInfo projectInfo;

    public BuildResourcesTask(ProjectInfo projectInfo){
        this.projectInfo = projectInfo;
    }

    @Task(name = "buildToolVersions")
    public Map<String, String> buildToolVersions() {

        Map<String, String> buildToolVersions = new HashMap<>();

        if(projectInfo.getGradleBuildVersion() != null){
            buildToolVersions.put("Globe-Gradle-Plugin-Version", projectInfo.getGradleBuildVersion());
        }

        if(projectInfo.getFeBuildVersion() != null){
            buildToolVersions.put("Frontend-Build-Version", projectInfo.getFeBuildVersion());
        }

        return buildToolVersions;
    }
}
