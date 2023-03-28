<#assign robotsString = model.directives.imagePreviewLarge />
<#assign robotsString = robotsString + ', ' +  model.robots!'NOODP, NOYDIR'>
<#if (model.supplementalDirectives)?has_content>
	<#list model.supplementalDirectives as directive>
		<#assign robotsString += ', ' + model.directives[directive] >
	</#list>
</#if>

<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">

<#-- max-image-preview is set here to prevent this value from being lost by verticals
    overriding model.robots.
-->
<meta name="robots" content="${robotsString}" />

<meta name="viewport" content="width=device-width, initial-scale=1.0">

<#compress>
	<link rel="canonical" href="${requestContext.canonicalUrl?html}" />

	<#if (model.title)?has_content>
		<title><@stripHtml string=model.title /></title>
	</#if>

	<#if (model.description)?has_content>
		<meta name="description" content="${model.description?replace("\"", "&quot;")}" itemprop="description">
	</#if>

	<#if (model.sailthruContentType)?has_content>
		<meta name="emailcontenttype" content="${model.sailthruContentType}" />
	</#if>

	<#if (model.sailthruVertical)?has_content>
		<meta name="emailvertical" content="${model.sailthruVertical}" />
	</#if>

	<#if (model.sailthruAuthor)?has_content && model.sailthruAuthor != "Staff Author">
		<meta name="sailthru.author" content="${model.sailthruAuthor}" />
	</#if>

	<#if (model.sailthruTags)?has_content>
		<meta name="sailthru.tags" content="${model.sailthruTags?join(',')?replace(' ', '_')}" />
	</#if>

	<#if (model.parselySection)?has_content>
		<meta name="parsely-section" content="${model.parselySection}" />
	</#if>

	<#if (model.parselyTags)?has_content>
		<meta name="parsely-tags" content="${model.parselyTags?join(',')}" />
	</#if>

	<#if (model.revenueGroup)?has_content>
		<meta name="revenuegroup" content="${model.revenueGroup}" />
	</#if>

	<@location name="content" tag="" /> <#-- PINTEREST, FB, TWITTER -->
</#compress>