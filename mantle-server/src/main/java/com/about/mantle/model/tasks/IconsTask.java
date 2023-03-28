package com.about.mantle.model.tasks;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.about.globe.core.task.annotation.Task;
import com.about.globe.core.task.annotation.TaskParameter;
import com.about.globe.core.task.annotation.Tasks;
import com.about.mantle.model.IconValue;

@Tasks
public class IconsTask {

	@Task(name = "icons")
	public List<IconValue> getIcons(@TaskParameter(name = "srcBase") String srcBase,
			@TaskParameter(name = "srcExt") String srcExt, @TaskParameter(name = "type") String type,
			@TaskParameter(name = "sizes") String sizes) {
		return Arrays.stream(StringUtils.split(sizes, ','))
				.map(size -> new IconValue(String.format("%s-%s.%s", srcBase, size, srcExt), type, size))
				.collect(Collectors.toList());
	}
}
