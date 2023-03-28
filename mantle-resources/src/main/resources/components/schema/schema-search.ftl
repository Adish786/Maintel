<#include "schema-macros.ftl" />
<script type="application/ld+json">
<#compress>
{
	"@context": "http://schema.org",
    "@type": "SearchResultsPage",
    "headline": "Search Results from ${model.domain}",
    "url": "${model.searchUrl}",
    <#if model.logoWidth?has_content && model.logoHeight?has_content && model.verticalName?has_content && model.imageId?has_content>
        <@publisher width=model.logoWidth height=model.logoHeight verticalName=model.verticalName imageId=model.imageId />,
    </#if>
    "description": "Find what you're looking for on ${model.domain}.",
    "articleBody": ""
}
</#compress>
</script>