package com.about.mantle.render.freemarker;

import org.joda.time.DateTimeZone;

import com.about.globe.core.definition.resource.ResourceAggregator;
import com.about.globe.core.exception.GlobeRenderException;
import com.about.globe.core.http.LinkProviderFactory;
import com.about.globe.core.http.RequestContext;
import com.about.globe.core.model.EnvironmentConfig;
import com.about.globe.core.render.CoreRenderUtils;
import com.about.globe.core.render.freemarker.FreeMarkerRenderingEngine;
import com.about.hippodrome.util.projectinfo.ProjectInfo;
import com.about.mantle.model.extended.TemplateTypeEx;
import com.about.mantle.model.services.SocialLinkService.SocialNetwork;
import com.about.mantle.model.tasks.ResourceTasks;
import com.about.mantle.render.image.ImageType;
import com.about.mantle.render.image.filter.ImageFilterBuilder;

import freemarker.cache.TemplateLoader;
import freemarker.template.SimpleHash;

public class MantleFreeMarkerRenderingEngine extends FreeMarkerRenderingEngine {
	private final LinkProviderFactory linkProviderFactory;
	private final ResourceTasks resourceTasks;

	public MantleFreeMarkerRenderingEngine(TemplateLoader templateLoader, CoreRenderUtils renderUtils,
			boolean debugEnabled, EnvironmentConfig environmentConfig, ProjectInfo projectInfo,
			ResourceAggregator resourceAggregator, LinkProviderFactory linkProviderFactory,
			ResourceTasks resourceTasks) {
		super(templateLoader, renderUtils, debugEnabled, environmentConfig, projectInfo, resourceAggregator);
		this.linkProviderFactory = linkProviderFactory;
		this.resourceTasks = resourceTasks;
	}

	@Override
	protected void appendDataModel(SimpleHash dataModel, RequestContext requestContext) throws GlobeRenderException {
		dataModel.put("links", linkProviderFactory.create(requestContext));
		dataModel.put("resources", resourceTasks);
	}

	@Override
	protected void appendEnums(SimpleHash dataModel) throws GlobeRenderException {
		super.appendEnums(dataModel);
		appendEnum(dataModel, "imageType", ImageType.class);
		appendEnum(dataModel, "socialNetworkType", SocialNetwork.class);
		appendEnum(dataModel, "templateType", TemplateTypeEx.class);
	}

	@Override
	protected void appendStatics(SimpleHash dataModel) throws GlobeRenderException {
		appendStatic(dataModel, "imageFilter", ImageFilterBuilder.class);
		appendStatic(dataModel, "DateTimeZone", DateTimeZone.class);
	}
}
