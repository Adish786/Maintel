User-agent: *

Disallow: *quizResult=
Disallow: *.pdf
Disallow: *globeTest_
Disallow: *globeNoTest
Disallow: *globeResource
Disallow: *?kw
Disallow: /embed?
<#if model.additionalDisallowAllPaths?has_content>
<#list model.additionalDisallowAllPaths as additionalPaths>
Disallow: ${additionalPaths}
</#list>
</#if>

User-agent: Pinterest
Disallow:

User-agent: Pinterestbot
Disallow:
<#if model.sitemapUrl?has_content>

Sitemap: ${model.sitemapUrl}
</#if>
<#if model.googleNewsSitemapUrl?has_content>

Sitemap: ${model.googleNewsSitemapUrl}
</#if>
