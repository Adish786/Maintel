<#macro listElements nodes>
<#local position = 1 />
<#compress>
<#escape x as x?json_string>
[
<#list nodes as node>
	<#if (position > 1)>,</#if>
	{
		"@type": "ListItem",
		"position": ${position},
		"item": {
			"@id": "${(node.document.url)!''}",
			"name": "${(node.document.shortHeading)!(node.document.bestTitle)!''}"
		}
	}
	<#local position = position + 1 />
</#list>
]
</#escape>
</#compress>
</#macro>
<#assign nodes = model.schemaBreadcrumb/>
<#if model.unified!false>
,"breadcrumb": {
<#else><#-- TODO: clean this up once non-unified schemas are removed -->
<script type="application/ld+json">
{
"@context": "http://schema.org",
</#if>
"@type": "BreadcrumbList",
"itemListElement": <@listElements nodes=nodes />
<#if model.unified!false>
}
<#else><#-- TODO: clean this up once non-unified schemas are removed -->
}
</script>
</#if>