<#list model?keys as key>
    <#if key?starts_with("og:") && model[key]?has_content>
        <meta property="${key}" content="${model[key]}"/>
    </#if>
</#list>