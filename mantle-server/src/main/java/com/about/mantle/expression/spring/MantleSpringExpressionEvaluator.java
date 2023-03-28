package com.about.mantle.expression.spring;

import java.util.Map;

import org.springframework.expression.ExpressionParser;

import com.about.globe.core.expression.spring.SpringExpressionEvaluator;
import com.about.globe.core.http.RequestContext;
import com.about.globe.core.model.extended.Configs;
import com.about.globe.core.render.CoreRenderUtils;
import com.about.globe.core.testing.GlobeBucket;
import com.about.hippodrome.util.projectinfo.ProjectInfo;

public class MantleSpringExpressionEvaluator extends SpringExpressionEvaluator {

	public MantleSpringExpressionEvaluator(ExpressionParser parser, CoreRenderUtils renderUtils,
			ProjectInfo projectInfo) {
		super(parser, renderUtils, projectInfo);
	}

	@Override
	@SuppressWarnings("unchecked")
	protected Object createContextRootObject(Map<String, Object> customContext) {

		Object requestContext = customContext.get("requestContext");
		if (!(requestContext instanceof RequestContext)) requestContext = null;

		Object tests = customContext.get("tests");
		if (!(tests instanceof Map)) tests = null;

		Object configs = customContext.get("configs");
		if (!(configs instanceof Configs)) configs = null;

		Object messages = customContext.get("messages");
		if (!(messages instanceof Map)) messages = null;

		return new MantleContextRootObject((RequestContext) requestContext, (Map<String, GlobeBucket>) tests,
				(Configs) configs, (Map<String, String>) messages, getRenderUtils(), getProjectInfo());
	}

}
