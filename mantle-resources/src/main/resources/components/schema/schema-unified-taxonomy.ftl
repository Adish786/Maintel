<#include "schema-macros.ftl" />
<#compress>
<#assign allDocuments =  (model.documents![]) + (model.docsFromScBlocks![]) />
<#assign urls=(allDocuments)?map(item -> ((item.url)!(item.document.url)!'')) />
<#assign includedUrls = 0 />
<#escape x as x?json_string>
    "@type": [<@listDelimitedValues itemList=model.primarySchemaTypes delimiter="," defaultVal="WebPage" />]
    ,"itemListElement":[
    <#list urls as url>
        <#-- must be a legit url AND must be the first of its kind (dedupe) -->
        <#if (url?trim?length > 0) && (url_index == urls?seq_index_of(url))>
            <#if (includedUrls > 0)>,</#if>
            <#assign includedUrls++ />
            {
                "@type": "ListItem"
                ,"position": ${includedUrls?c}
                ,"url": "${url}"
            }
        </#if>
    </#list>
    ]
    ,"numberOfItems": ${includedUrls?c}

    <@location name="extraProperties" tag="" />

    <@location name="mainEntityOfPage" tag=""/>
    
    <#if (manifest.locations['aboutProperties']![])?size gt 0>
	,"about": [
		<@location name="aboutProperties" tag="" separator=","/>
	 ]
	</#if>
</#escape>
</#compress>