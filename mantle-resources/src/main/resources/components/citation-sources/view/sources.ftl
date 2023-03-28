<#assign sources=model.sources![] />
<#if model.sorted!false>
	<#assign sources=sources?sort />
</#if>
<#list sources>
<@component>
	<#assign sourcesListTag=(model.numbered!false)?then("ol", "ul")>
	<${sourcesListTag} class="mntl-sources__content">
		<#items as item>
			<#if item?is_hash>
				<li class="mntl-sources__source" id="citation-${item.refId?c}">${item.source}</li>
			<#elseif item?is_string>
				<li class="mntl-sources__source">${item}</li>
			</#if>
		</#items>
	</${sourcesListTag}>
</@component>
</#list>
