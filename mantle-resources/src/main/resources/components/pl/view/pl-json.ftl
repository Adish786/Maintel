<#macro buildCategory categories >
	<#list categories as category>
        {
        "name": "${category.displayName}",
        <#if (category.children?filter(child -> child.component??)?size > 0)>
        "components": [
            <#list category.children?filter(child -> child.component??) as child>
            {
                "name": "${child.displayName}",
                "uri": "/pattern-library-component?id=${child.component.id}&categoryUri=${child.component.categoryUri}"
            },
            </#list>
        ],
        </#if>
        <#if (category.children?filter(child -> !(child.component??))?size > 0)>
        "categories": [
            <@buildCategory categories=category.children?filter(child -> !(child.component??)) />
        ],
        </#if>
        },
        </#list>
</#macro>

{
"categories": [
    <@buildCategory categories=model.plNavigation />
    ]
}

