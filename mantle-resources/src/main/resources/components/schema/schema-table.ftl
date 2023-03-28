<#include "schema-macros.ftl" />
<#assign tables = model.document.getContentsListOfType('TABLE') />
<#list tables as table>
    <#if table.data.title??>
        <#if (tables?size > 0) >,</#if>
        <@tableObject title=table.title />
    </#if>
</#list>