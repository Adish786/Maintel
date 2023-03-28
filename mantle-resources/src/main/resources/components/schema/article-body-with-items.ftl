
<#compress>
<#escape x as x?json_string>
<#list model.document.items.list as item>
    <#t>&lt;h3&gt;${((item.link.text)!'')?html}&lt;/h3&gt;
    <#t>${((item.description)!'')?html}
</#list>
</#escape>
</#compress>
