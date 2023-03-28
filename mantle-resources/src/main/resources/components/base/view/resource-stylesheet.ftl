<#--

Filter params:
evaluatedOnly	- filter to stylesheets marked evaluated (default = false)
group	- filter to stylesheets in a specific group, like 'top', 'bottom', etc (default = 'top')

Backup global manifest
--><#assign manifest_bak=manifest /><#--

--><#assign manifest = model.manifest!manifest.root /><#--
--><#assign group = model.group!'top' /><#--
--><#assign evaluated = model.evaluatedOnly!false /><#--
--><#assign enableAsyncCss = model.enableAsyncCss /><#--
--><#assign inline = (requestContext.userAgent.deviceCategory != 'pc' && requestContext.getParameterSingle('globeResourceInline')! != 'false') || (enableAsyncCss && group != 'async') /><#--

--><#if evaluated><#--
	--><style type="text/css"><#--
	// Evaluated stylesheets need models available since they are freemarker templates
	--><#list manifest.allManifestsIncludeThis as manifest><#--
		--><#assign model = manifest.model /><#--
		--><#list manifest.stylesheets as stylesheet><#--
			--><#if (group = stylesheet.resourceHandle.group) && (evaluated = stylesheet.resourceHandle.evaluated)><#--
				--><#include "/${stylesheet.resource.path}"><#--
			--></#if><#--
		--></#list><#--
	--></#list><#--
	--></style><#--
--><#else><#--
	--><#assign stylesheets = resources.stylesheets(requestContext, model.evaluatedOnly, model.group, model.groupOrder!null, manifest) /><#--
	--><#if stylesheets?has_content><#--
		--><#if inline><#--
			// mobile and tablet devices benefit from having their stylesheets inlined
			--><style type="text/css"><#--
				--><#list stylesheets as stylesheet><#--
					--><#include "/${stylesheet.resource.path}" parse=false><#--
				--></#list><#--
			--></style><#--
		--><#else><#--
			--><#list stylesheets as stylesheet><#--
				--><#if enableAsyncCss><#--
					--><link rel="preload" href="${(model.prefix)!"NO PREFIX FOUND"}${(stylesheet.module.projectInfo.version)!"NO VERSION FOUND"}${(stylesheet.resource.path)!"NO RESOURCE PATH FOUND"?replace('/static/mantle/', '/')}" as="style" onload="this.rel='stylesheet'"/><#--
					--><noscript><link rel="stylesheet" href="${(model.prefix)!"NO PREFIX FOUND"}${(stylesheet.module.projectInfo.version)!"NO VERSION FOUND"}${(stylesheet.resource.path)!"NO RESOURCE PATH FOUND"?replace('/static/mantle/', '/')}"></noscript><#--
				--><#else><#--
					// Handle stylesheets that will be linked to externally
					--><link type="text/css" rel="stylesheet" data-glb-css href="${(model.prefix)!"NO PREFIX FOUND"}${(stylesheet.module.projectInfo.version)!"NO VERSION FOUND"}${(stylesheet.resource.path)!"NO RESOURCE PATH FOUND"?replace('/static/mantle/', '/')}"/><#--
				--></#if><#--
				// Newline so stylesheets render nicely in source
			--></#list><#--
		--></#if><#--
	--></#if><#--
--></#if><#--

Restore globals
--><#assign manifest = manifest_bak /><#--
--><#assign model = manifest.model />