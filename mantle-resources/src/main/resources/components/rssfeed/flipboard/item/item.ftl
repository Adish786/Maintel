<#ftl output_format="XML" auto_esc=true>
<#if model??>
	<item>
	<#if model.title?has_content>
		<title>${model.title}</title>
	</#if>
	<#if model.link?has_content>
		<link>${model.link}</link>
	</#if>
	<#if model.description?has_content>
	    <description>${model.description?no_esc}</description>
	</#if>
	<#if model.pubDate?has_content>
		<pubDate>${model.pubDate.toString("yyyy-MM-dd'T'HH:mm:ss+00:00")}</pubDate>
	</#if>
	<#if model.guid?has_content>
		<guid>${model.guid}</guid>
	</#if>
	<#if model.creator?has_content>
		<dc:creator>${model.creator}</dc:creator>
	</#if>
	<#if (model.enclosure.url)?has_content>
		<enclosure url="${model.enclosure.url}" length="${model.enclosure.length}" type="${model.enclosure.type}" />
	</#if>
	<#if model.categories?has_content>
		<#list model.categories![] as category>
		<category>${category}</category>
		</#list>
	</#if>
	</item>
</#if>