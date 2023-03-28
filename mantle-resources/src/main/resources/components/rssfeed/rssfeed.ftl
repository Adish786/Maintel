<#ftl output_format="XML" auto_esc=true>
<#if model??>
	<#if model.xmlHeader??>
	<@compress single_line=true>
	<?xml
	version="${model.xmlHeader['version']}"
	encoding="${model.xmlHeader['encoding']}"
	?>
	</@compress>
	</#if>
		
	<@compress single_line=true>
	<rss
	<#list model.rssAttributes?keys as key>
	${key}="${model.rssAttributes[key]}"
	</#list>
	>
	</@compress>
		
	<channel>
		<#if model.title?has_content>
		<title>${model.title}</title>
		</#if>
		<#if model.link?has_content>
		<link>${model.link}</link>
		</#if>
		<#if model.description?has_content>
		<description>${model.description}</description>
		</#if>
		<#if model.language?has_content>
		<language>${model.language}</language>
		</#if>
		<@location name="channel-additional-fields" tag=""/>
		<@location name="channel-items" tag=""/>
	</channel>
</rss>
</#if>