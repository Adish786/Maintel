<#if (model.audience)?has_content>
    ,"audience": {
        "@type":"${model.audienceType}"
        ,"audienceType": [
            <#list model.audience as item>
                "${item}"<#sep>,</#sep>
            </#list>
        ]
    }
</#if>