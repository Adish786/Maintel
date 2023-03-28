<#include "schema-macros.ftl" />
<#compress>
<#escape x as x?json_string>
	    "@type": [<@listDelimitedValues itemList=model.primarySchemaTypes delimiter="," defaultVal="WebPage" />]
	    ,"@id": "${model.document.url}"
		<@location name="breadcrumb" tag="" />
		<@location name="extraProperties" tag="" />
</#escape>
</#compress>