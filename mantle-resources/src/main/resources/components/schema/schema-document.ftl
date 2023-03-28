<#include "schema-macros.ftl" />
<script type="application/ld+json">
<#compress>
<#assign imageCount = 0 />
<#escape x as x?json_string>
{
	"@context": "http://schema.org",
	"@type": "${model.type}",
	"mainEntityOfPage": {
		"@type": "Webpage",
		"@id": "${model.document.url}"
	},
	"headline": "${(model.document.heading!model.document.title)}"
	<#if model.potentialAction?has_content>
		,"potentialAction": {
			"@type": "${model.potentialAction.type}",
			"target": "${model.potentialAction.target}",
			"query-input": "${model.potentialAction.queryInput}"
		}
	</#if>
	<#if (model.numOfImages > 0)>
		,"image": [
			<#if (model.image.objectId)?has_content>
				{
					"@type": "ImageObject",
					"url": "<@thumborUrl img=model.image maxWidth=model.image.width maxHeight=model.image.height filters=[imageFilter.noUpscale(), 'fill(${model.imageBackgroundColor},1)'] />",
					"height": ${model.image.height?c},
					"width": ${model.image.width?c}
				}
				<#assign imageCount = 1 />
			</#if>
			<@location name="inlineImages" models={"imageCount": imageCount} tag="" />
		]
	</#if>
	<#if model.datePublished?has_content>
		,"datePublished": "${model.datePublished}"
	</#if>
	<#if model.dateModified?has_content>
		,"dateModified": "${model.dateModified}"
	</#if>
    <#if (model.document.guestAuthor.link.text)?has_content>
        ,"author": [<@rawAuthor guestAuthorData=(model.document.guestAuthor) />]
    <#elseif model.authorAttributions?has_content>
        ,<@listAuthors attributionModels=(model.authorAttributions)!{} role="author" />
    </#if>
	<#if (model.document.url)?has_content && model.logoWidth?has_content && model.logoHeight?has_content && model.verticalName?has_content && model.imageId?has_content>
		,<@publisher width=model.logoWidth height=model.logoHeight verticalName=model.verticalName imageId=model.imageId publishingPrinciples=model.publishingPrinciples sameAs=model.sameAs orgData=model.parentOrganization!{} addressFlag=model.addressFlag!false/>
	</#if>
	<#if (model.document.summary.description)?has_content>
		,"description": "${model.document.summary.description}"
	</#if>
	<#if manifest.locations['articleBody']?has_content>
		,"articleBody": "<@location name="articleBody" tag="" />"
	</#if>
}
</#escape>
</#compress>
</script>
