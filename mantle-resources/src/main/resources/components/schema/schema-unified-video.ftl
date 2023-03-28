<#if model.videos?size gt 0>
,"video": <#rt>
<#if model.videos?size gt 1>[</#if><#t>
<#list model.videos as video>
{
	"@type": "VideoObject",
	<@videoObject videoBlock=video />
}<#sep>,</#sep>
</#list>
<#if model.videos?size gt 1>]</#if><#t>
</#if>