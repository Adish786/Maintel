package com.about.mantle.model.tasks;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.about.globe.core.task.annotation.Task;
import com.about.globe.core.task.annotation.TaskParameter;
import com.about.globe.core.task.annotation.Tasks;
import com.about.mantle.model.extended.TaxeneNodeEx;
import com.about.mantle.model.extended.docv2.BaseDocumentEx;

@Tasks
public class SailthruTagsTask {

	private static final Logger logger = LoggerFactory.getLogger(SailthruTagsTask.class);

	private final String verticalBrandName;

	public SailthruTagsTask(String domain) {
		this.verticalBrandName = getVerticalBrandName(domain);
	}

	@Task(name = "sailthruTags")
	public List<String> sailthruTags(@TaskParameter(name = "ancestors") List<TaxeneNodeEx> nodes) {
		List<String> answer = new ArrayList<>();
		if (nodes != null && nodes.size() > 0) {
			// starting at 1 to skip the root whose slug is typically 'root'
			for (int i = 1; i < nodes.size(); ++i) {
				String slug = slug(nodes.get(i));
				if (slug != null) {
					answer.add(slug);
				}
			}
		}
		return answer;
	}

	@Task(name = "sailthruContentType")
	public String sailthruContentType(@TaskParameter(name = "document") BaseDocumentEx document) {
		switch (document.getTemplateType()) {
		case BIO:
		case CATEGORY:
		case IMAGEGALLERY:
		case JWPLAYERVIDEO:
		case LANDINGPAGE:
		case LEGACY:
		case PROGRAMMEDSUMMARY:
		case REDIRECT:
		case TAXONOMY:
		case TAXONOMYSC:
		case TOPIC:
		case USERPATH:
			// @sbraswell doesn't want the tag on these templates
			return null;
		default:
			return document.getTemplateType().name();
		}
	}

	@Task(name = "sailthruVertical")
	public String sailthruVertical() {
		return verticalBrandName;
	}

	private static String getVerticalBrandName(String domain) {
		String root = StringUtils.removeEnd(domain,  ".com");
		if (root == null || root.isEmpty()) {
			logger.error("Expected non-empty domain");
			return null;
		}
		return root.toUpperCase();
	}

	private static String slug(TaxeneNodeEx node) {
		return node == null || node.getDocument() == null ? null : node.getDocument().getSlug();
	}

}
