<#if manifest.locations??>
	<#list manifest.locations?keys as loc>
		<@location name=loc tag="" />
	</#list>
</#if>