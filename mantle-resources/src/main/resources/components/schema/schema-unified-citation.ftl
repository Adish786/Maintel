,"citation": [
    <#if (model.document.sources.list)?has_content>
        <#list model.document.sources.list as source>
            <#-- strip <p> that wrap sources -->        
            "${source?remove_beginning("<p>")?remove_ending("</p>")?html}"<#sep>,</#sep>
        </#list>
    </#if>

    <#if (model.citationSources)?has_content>
        <#list model.citationSources as citation>
            <#-- only add leading comma if there were sources already rendered -->        
            <#if (model.document.sources.list)?has_content && citation_index == 0>,</#if>"${citation.source?remove_beginning("<p>")?remove_ending("</p>")?html}"<#sep>,</#sep>
        </#list>
    </#if>
]