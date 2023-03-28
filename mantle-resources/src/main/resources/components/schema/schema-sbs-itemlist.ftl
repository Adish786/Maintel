<#include "schema-macros.ftl" />
<script type="application/ld+json">
<#compress>
<@itemList name=(model.document.heading)!'' description=(model.document.summary.description)!'' numberOfItems=model.document.pages.list?size>
	<#list model.document.pages.list as item>
		<#assign position = item_index + 1 />
		<#assign url =  model.document.url + '#' + position/>
		<#assign description='' />
		<#list item.content.list as itemContent>
			<#assign description+=itemContent.content!'' />
		</#list>
		<@itemListElement
			position=position
			name=(item.heading)!''
			url=url
			description=description?html
			image=(item.images.list[0])!{}
		/>
		<#if item_has_next>,</#if>
	</#list>
</@itemList>
</#compress>
</script>
