<!DOCTYPE html>
<#assign attrs = model.attrs!{} />
<#-- Dumps versions of each module as data attribs of the `html` element.  Used by javascript to be able to statically
     pull resources, because our resources are namespaced by version.  The 'default' has no module associated with it,
     as we don't always know the module name -->
<#if model.moduleVersions?has_content>
	<#assign versions = {} />
	<#list model.moduleVersions?keys as key>
		<#assign versions = versions + {'data_' + key + '_version': model.moduleVersions[key]} />
	</#list>
	<#assign model = model + {'attrs': attrs + versions} />
</#if>
<@component tag="html" data_resource_version="${projectInfo.version}" data_ab=testIds()>
<!--
<globe-environment environment="${configContext.environment}" application="${configContext.application}" dataCenter="${configContext.dataCenter}"/>
-->
	<@location name="head" tag="head" />
	<body<#if model.bodyId?has_content> id="${model.bodyId}"</#if>><#lt>
		<@location name="body" tag="" /><#lt>
		<@location name="postBody" tag="" /><#lt>
	</body><#lt>
</@component>