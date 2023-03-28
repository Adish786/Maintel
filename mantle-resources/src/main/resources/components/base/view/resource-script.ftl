<#--

Filter params:
evaluatedOnly	- filter to scripts marked evaluated (default = false)
group	- filter to scripts in a specifc group, like 'top', 'bottom', etc (default = 'top')

Option params:
inline	- if true scripts will be inlined into rendered response within script block

Backup global manifest
--><#assign manifest_bak=manifest /><#--

--><#assign manifest = model.manifest!manifest.root /><#--
--><#assign group = model.group!'top' /><#--
--><#assign evaluated = model.evaluatedOnly!false /><#--
--><#assign inline = model.inline || evaluated /><#--

--><#if evaluated><#--
	--><script type="text/javascript"><#if group == 'bottom'>Mntl.utilities.scriptsOnLoad(document.querySelectorAll('script[data-glb-js="${group}"]'), function() {</#if><#--
		// Evaluated scripts need models available since they are freemarker templates  
		// Global var added to address hoisting issues identified in GLBE-9047. Will be cleaned up in GLBE-9049
		-->var Mntl = window.Mntl || {};<#--
		--><#list manifest.allManifestsIncludeThis as manifest><#--
			--><#assign model = manifest.model /><#--
			--><#list manifest.scripts as script><#--
				--><#if (group = script.resourceHandle.group) && (evaluated = script.resourceHandle.evaluated)><#--
					--><#include "/${script.resource.path}"><#--
				--></#if><#--
			--></#list><#--
		--></#list><#--
	--><#if group == 'bottom'>});</#if></script><#--
--><#else><#--
	--><#assign scripts = resources.scripts(requestContext, model.evaluatedOnly, model.group, model.groupOrder!null, manifest) /><#--
	--><#if inline><#--
		--><script type="text/javascript"><#-- 
			// Global var added to address hoisting issues identified in GLBE-9047. Will be cleaned up in GLBE-9049
			-->var Mntl = window.Mntl || {};<#--
		--><#list scripts as script><#--
			--><#include "/${script.resource.path}" parse=false><#--
		--></#list><#--
		--></script><#--
	--><#else><#--
		// Handle scripts that will be linked to externally
		--><#list scripts as script><#--
			--><script type="text/javascript" data-glb-js="${group}" src="${(model.prefix)!"NO PREFIX FOUND"}${(script.module.projectInfo.version)!"NO VERSION FOUND"}${(script.resource.path)!"NO RESOURCE PATH FOUND"?replace('/static/mantle/', '/')}"<#if group == 'bottom'><#if requestContext.resourceConcat> async</#if> defer="true"</#if>></script>
<#-- Newline so scripts render nicely in source
		--></#list><#--
	--></#if><#--
--></#if><#--

Restore globals
--><#assign manifest = manifest_bak /><#--
--><#assign model = manifest.model />
