<#include "schema-macros.ftl" />
<script type="application/ld+json">
<#compress>
<@itemList name=(model.document.heading)!'' description=(model.document.summary.description)!'' numberOfItems=model.document.items.list?size>
	<#list model.document.items.list as item>
		<#assign position = item_index + 1 />
		<#assign url =  model.document.url + '#' + position/>
		<@itemListElement position=position name=(item.link.text)!'' url=url description=item.description!'' image=item.image!{} />
		<#if item_has_next>,</#if>
	</#list>
</@itemList>
</#compress>
</script>
