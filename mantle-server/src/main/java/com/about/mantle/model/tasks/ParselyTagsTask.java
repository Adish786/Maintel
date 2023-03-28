package com.about.mantle.model.tasks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.about.globe.core.task.annotation.Task;
import com.about.globe.core.task.annotation.TaskParameter;
import com.about.globe.core.task.annotation.Tasks;
import com.about.mantle.model.extended.TaxeneNodeEx;
import com.about.mantle.model.extended.docv2.BaseDocumentEx;

@Tasks
public class ParselyTagsTask {

    private static final List<String> affiliatesGroups = Arrays.asList("COMMERCE", "COMMERCENEWSDEALS");

    @Task(name="parselyTags")
    public List<String> getParselyTags(
        @TaskParameter(name = "taxeneRelationParentsPrimaryAndSecondary") List<TaxeneNodeEx> nodes) {
        return getParselyTags(null, nodes);
    }

    @Task(name="parselyTags")
    public List<String> getParselyTags(    
        @TaskParameter(name = "document") BaseDocumentEx document, 
        @TaskParameter(name = "taxeneRelationParentsPrimaryAndSecondary") List<TaxeneNodeEx> nodes) {
        List<String> tags = new ArrayList<>();

        if (!showParselyMetaTags(document)) {
            return null;
        }

        if (nodes != null) {
            for (int i = 0; i < nodes.size(); i++) {
                String tag = metaTag(nodes.get(i));
                if (tag != null) {
                    tags.add(tag);
                }
            }
        }

        if (document != null && affiliatesGroups.contains(document.getRevenueGroup())) {
            tags.add("affiliate");
        }

        return tags;
    }
    
    @Task(name = "parselySection")
    public String getParselySection(
        @TaskParameter(name = "ancestors") List<TaxeneNodeEx> nodes) {
        return getParselySection(null, nodes);
    }

    @Task(name = "parselySection")
    public String getParselySection(
        @TaskParameter(name = "document") BaseDocumentEx document,
        @TaskParameter(name = "ancestors") List<TaxeneNodeEx> nodes) {

        if(!showParselyMetaTags(document)) {
            return null;
        }

        // We go to first index so we skip the root node (homepage). If the size is only 1 that means we are on pages that don't have more relationships than the homepage.(about-us)
        return nodes == null || nodes.size() <= 1 ? null : metaTag(nodes.get(1));
    }
    
    private static String metaTag(TaxeneNodeEx node) {
		return (node == null || node.getDocument() == null) ? null : node.getDocument().getShortHeading();
	}

    private static boolean showParselyMetaTags(BaseDocumentEx document) {
        boolean shouldShow = false;

        if (document == null) {
            return false;
        }

        switch(document.getTemplateType()) {
            case STRUCTUREDCONTENT:
            case QUIZ:
            case LISTSC:
            case RECIPESC:
            case TERMSC:
            case REVIEWSC:
            case HOWTO:
            case ENTITYREFERENCE:
                shouldShow = true;
                break;
        }

        return shouldShow;
    }
}
