<#compress>
    <#assign data = model.jsondoc />
    <#assign generic_name = data.generic_name!'' />
    <#assign brand_names = data.brand_names![] />
    <#assign warning = model.document.warning!'' />
    <#assign route = (data.routes[0])!'' />
    <#assign therapeutic = data.therapeutic!'' />
	<#escape x as x?json_string>
        {
            "@type": "Drug"
            <#if generic_name?has_content>
                ,"nonProprietaryName": "${generic_name}"
            </#if>
            <#if (brand_names?size > 0)>
                ,"proprietaryName": [
                    <#list brand_names>
                        <#items as brand_name>
                            <#if brand_name?has_content>
                                "${brand_name}"<#sep>,</#sep>
                            </#if>
                        </#items>
                    </#list>
                ]
            </#if>
            <#if warning?has_content>
                ,"warning": "${processSchemaString(warning)}"
            </#if>
            <#if route?has_content>
                ,"administrationRoute": "${route}"
            </#if>
            <#if therapeutic?has_content>
                ,"drugClass": "${therapeutic}"
            </#if>
        }
	</#escape>
</#compress>