<#assign characterLimit = model.characterLimit?has_content?then(model.characterLimit,500) />
<#assign characterPosition = 0 />
<#assign chopped = false />

<@component class="js-inline-chop" >
    <#if model.contents?has_content && model.contents?is_enumerable >
        <#list model.contents as item>
            <#if ((characterPosition + item.characterCount) >= characterLimit) && !chopped>
              	<@chopContentInline content=item.content />
	            <#assign chopped = true />
            <#elseif chopped >
                <div class="inline-chop-content inline-chop-content--hidden js-inline-chop-content"> ${item.content}</div>
            <#else>
                ${item.content}
            </#if>
            <#assign characterPosition += item.characterCount />
        </#list>
    <#elseif model.contents?has_content>
        <#if model.contents?is_string>
            <@chopContentInline content=model.contents />
        <#else>
            ${model.contents!""}
        </#if>
    </#if>
</@component>

<#--   We have following cases here.
    1. For valid HTML content - We get three chopped blocks 
       i.e. HTML content ending just before last ending tag - ending HTML tag itself - second HTML content block
    2. For valid HTML content - We get only one block back  (e.g. no chopping's done if content is
       of 200 chars and we asked to chop on 500 chars)
    3. For plain text - We get two chopped blocks back
    4. For plain text - We get only one block back (no chopping just like case #2)
-->
<#macro chopContentInline content>
    <#assign choppedContent = utils.chopContentInline(content, "chars:" + characterLimit?c + ";tags:p,ul,ol;") />
    <#-- For case 2 and 4, just show first block -->
    ${choppedContent[0].content}
    <#--For case #1, show chop button after first block and before ending tag of first block, so that it looks inline -->
    <#if choppedContent?size gt 2>
    	    <@location name="inline-chop-button" class="js-inline-chop-expand inline-chop__expand-container" tag="span"/>
    	    ${choppedContent[1].content}
    	    <@hideInlineChop content=choppedContent[2].content />
    </#if>
    <#-- For case #3, show chop button just before showing second block -->
    <#if choppedContent?size == 2>
        <@location name="inline-chop-button" class="js-inline-chop-expand inline-chop__expand-container" tag="span"/>
        <@hideInlineChop content=choppedContent[1].content />
    </#if>
</#macro>

<#macro hideInlineChop content>
	<div class="inline-chop-content inline-chop-content--hidden js-inline-chop-content">
        	${content}
    	</div>
</#macro>