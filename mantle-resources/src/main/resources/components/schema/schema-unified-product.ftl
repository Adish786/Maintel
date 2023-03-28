<#if (model.vendorBrand)?has_content >
,"brand": {
	"@type": "Brand",
	"name" : "${model.vendorBrand}"
}
</#if>

<#if (model.entityData.color)?has_content >
,"color": "${model.entityData.color}"
</#if>
<#if (model.gtin)?has_content >
,"GTIN": "${model.gtin}"
</#if>
<#if (model.mpn)?has_content>
,"MPN": "${model.mpn}"  
</#if>

<#if (model.entityData.priceInfo.getSimpleDisplayText())?has_content >
,"offers": {
    "@type": "Offer",
    "priceCurrency": "${model.entityData.priceInfo.currency}",
    "price": "${model.entityData.priceInfo.getSimpleDisplayText()}"
}
</#if>
<@location name="extraProperties" tag="" />