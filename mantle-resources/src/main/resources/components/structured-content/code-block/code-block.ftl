<#escape x as x?html>
<@component tag="pre" data\-type="code">
    <#list model.codeLines as lineOfCode>
        <code>${lineOfCode}</code>
    </#list>
</@component>
</#escape>