<#ftl output_format="XML" auto_esc=true>
<item>
<#if model.title?has_content>
    <title>${model.title}</title>
</#if>
<#if model.description?has_content>
    <description>${model.description?no_esc}</description>
</#if>
<#if model.link?has_content>
    <link>${model.link}</link>
</#if>
<#list model.inlineImagesAndPinterestImages as image>
    <#if image??>
        <enclosure url="${image.url}" length="${image.length}" type="${image.type}" />
    </#if>
</#list>
</item>