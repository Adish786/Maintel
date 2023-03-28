<#compress>
    <#if (model.plugins?has_content)>
        <#list model.plugins as plugin>
            window.Mntl.externalizeLinks.addPlugin(${plugin});
        </#list>
    </#if>

    <#if !(externalizedLinksInstantiate??)>
        window.Mntl.externalizeLinks.init();
        <#assign externalizedLinksInstantiate=true>
    </#if>
</#compress>
