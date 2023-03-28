<#assign entityData = model.document.entity.data />
<#if entityData.issuer?has_content >
,"brand": {
    "@type": "Thing",
    "name": "${entityData.issuer}"
}
</#if>