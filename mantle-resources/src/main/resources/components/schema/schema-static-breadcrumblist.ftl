<script type="application/ld+json">
<#compress>
<#escape x as x?json_string>
{
"@context": "http://schema.org",
"@type": "BreadcrumbList",
"itemListElement":[
<#list model.breadcrumbs.items as item>
	{
		"@type": "ListItem",
		"position": ${item_index + 1},
		"item": {
			"@id": "${item.id}",
			"name": "${item.name}"
		}
	}
	<#if item_has_next>,</#if>
</#list>
]
}
</#escape>
</#compress>
</script>