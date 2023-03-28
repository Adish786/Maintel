<#include "schema-macros.ftl" />
<script type="application/ld+json">
<#compress>
<#escape x as x?json_string>
{
	"@context": "http://schema.org",
	"@type": "${model.type}",
	"headline": "${model.headline}"
	<#if model.mainEntityOfPage?has_content>	
		,"mainEntityOfPage": {
			"@type": "${model.mainEntityOfPage.type}",
			"@id": "${model.mainEntityOfPage.id}"
		}
	</#if>
	<#if model.potentialAction?has_content>
		,"potentialAction": {
			"@type": "${model.potentialAction.type}",
			"target": "${model.potentialAction.target}",
			"query-input": "${model.potentialAction.queryInput}"
		}
	</#if>
	<#if model.logoWidth?has_content && model.logoHeight?has_content && model.verticalName?has_content && model.imageId?has_content>
		,<@publisher width=model.logoWidth height=model.logoHeight verticalName=model.verticalName imageId=model.imageId />
	</#if>
	<#if model.description?has_content>
		,"description": "${model.description}"
	</#if>
}
</#escape>
</#compress>
</script>