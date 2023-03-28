<#include "schema-macros.ftl" />
"@type": [<@listDelimitedValues itemList=model.primarySchemaTypes delimiter="," defaultVal="Article" />]

	<#-- NOTE: preceding property should NOT end with comma because components being injected
	           in the locations that follow are expected to start with their own commas -->
	
	<@location name="extraProperties" tag="" />
	
	<@location name="mainEntityOfPage" tag="" />

<#if (manifest.locations['aboutProperties']![])?size gt 0>
, "about": [
	<@location name="aboutProperties" tag="" separator=","/>
  ]
</#if>