<#include "schema-macros.ftl" />
<#assign contentBlocks = model.document.pages.list />
<#list contentBlocks as block>
    <#if block.images??>
    	<#if block_index == 0>,</#if>
        <#list block.images.list as image>
            <@imageObject image=image />
            <#if (image_has_next)>,</#if>
        </#list>
        <#if (block_has_next && contentBlocks[block_index + 1].images.totalSize > 0)>,</#if>
    </#if>
</#list>