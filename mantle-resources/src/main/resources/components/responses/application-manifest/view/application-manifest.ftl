{
	"short_name": "${model.shortName}",
	"name": "${model.name}",
	"description": "${model.description}",
	"display": "${model.display}",
	<#if model.backgroundColor??>"background_color": "${model.backgroundColor}",</#if>
	<#if model.themeColor??>"theme_color": "${model.themeColor}",</#if>
	<#if model.icons??>
	"icons": [
		<#list model.icons as icon>
		{
			"src": "${icon.src}",
			"type": "${icon.type}",
			"sizes": "${icon.sizes}"
		}<#sep>,</#sep>
		</#list>
	],
	</#if>
	"start_url": "${model.startUrl}"
}