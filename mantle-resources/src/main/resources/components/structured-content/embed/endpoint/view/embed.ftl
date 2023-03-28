<@component data\-provider="${model.embed.provider!''}">
<#compress>
<#if (model.embed.type)?has_content>
	<#switch model.embed.type>
		<#case "HTML">
			${model.embed.content}
			<#break>
		<#case "IMG">
			<img class="embed-img embed-img--${model.embed.provider}" src="${model.embed.content}" />
			<#break>
		<#default>
			<#break>
	</#switch>
</#if>
</#compress>
</@component>