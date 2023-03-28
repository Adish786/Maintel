<#include "schema-macros.ftl" />
<#assign contentBlocks = model.document.items.list />
<#assign isMultipleImages = (model.imageCount!0) &gt; 0 />
<#list contentBlocks as block>
    <#if block.image?? && block.image.objectId??>
    	<#if isMultipleImages>
    	,
		<#else>
			<#assign isMultipleImages = true />
		</#if>
        <@imageObject image=block.image />
    </#if>
</#list>
