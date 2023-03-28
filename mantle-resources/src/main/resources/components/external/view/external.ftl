<#assign externalComponent=model.externalComponent.get() />

<#if (externalComponent.svg)?has_content>
<svg class="mntl-svg-resource is-hidden">
	<defs>
		${externalComponent.svg}
	</defs>
</svg>
</#if>
<@component tag=model.tag!"span">
${externalComponent.html}
</@component>
