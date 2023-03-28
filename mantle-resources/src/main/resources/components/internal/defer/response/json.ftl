<#-- THIS FILE IS USED BY GLOBE INTERNALS, DO NOT MODIFY UNLESS YOU ARE SURE OF WHAT YOU ARE DOING  -->
<#assign manifest_bak=manifest />
{
<#if manifest.locations??>
	"externalScripts": [
	<#list model.externalScripts as script>
		"${model.prefix + script.resource.path?replace('/static/', '/', 'f')}"<#if script_has_next>,</#if>
	</#list>
	],
	"externalStylesheets": [
	<#list model.externalStylesheets as stylesheet>
		"${model.prefix + stylesheet.resource.path?replace('/static/', '/', 'f')}"<#if stylesheet_has_next>,</#if>
	</#list>
	],
	"inlineSVGs":[
		<#list model.inlineSvgs as svg>
			<#assign capturedSVG><symbol id="${svg.resourceHandle.id}"><#include "${svg.resource.path}"></symbol></#assign>
			{
				"svg": "${svg.resource.path}",
				"content": "${capturedSVG?json_string}"
			}<#sep>,</#sep>
		</#list>
	]
	<#if manifest.locations?keys?has_content>,</#if>
<#list manifest.locations?keys as componentId>
<#assign capturedHtml><@location name=componentId tag="" /></#assign>
	"${componentId}": {
		"html":"${capturedHtml?json_string}",
		"inlineScripts":[
		<#list manifest.locations[componentId] as manifest>
		<#list manifest.allManifestsIncludeThis as manifest>
			<#assign model = manifest.model />
			<#list manifest.scripts as script>
			<#if (script.resourceHandle.evaluated) && (ResourceType.LOCAL = script.resource.type)>
			<#assign capturedScript><#include "${script.resource.path}"></#assign>
			{
				"script": "${script.resource.path}",
				"content": "${capturedScript?json_string}"
			},
			</#if>
			</#list>
		</#list>
		</#list>
			{}
		],
		"inlineStylesheets":[
		<#list manifest.locations[componentId] as manifest>
		<#list manifest.allManifestsIncludeThis as manifest>
			<#assign model = manifest.model />
			<#list manifest.stylesheets as stylesheet>
			<#if (stylesheet.resourceHandle.evaluated) && (ResourceType.LOCAL = stylesheet.resource.type)>
			<#assign capturedStylesheet><#include "${stylesheet.resource.path}"></#assign>
			{
				"stylesheet": "${stylesheet.resource.path}",
				"content": "${capturedStylesheet?json_string}"
			},
			</#if>
			</#list>
		</#list>
		</#list>
			{}
		]
	}<#if componentId_has_next>,</#if>
</#list>
</#if>
}
<#assign manifest = manifest_bak />
<#assign model = manifest.model />