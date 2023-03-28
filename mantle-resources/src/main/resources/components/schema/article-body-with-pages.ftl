<#compress>
<#escape x as x?json_string>
<#list model.document.pages.list as page><#list page.content.list as block>${block.content?html}</#list></#list>
</#escape>
</#compress>