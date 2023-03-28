<@compress single_line=true>
<?xml version="1.0" encoding="UTF-8"?>
<sitemapindex xmlns="http://www.sitemaps.org/schemas/sitemap/0.9">
	<#if model.sitemapUrlPrefix?has_content && model.numSiteMapIndexes gt 0>
		<#list 1..model.numSiteMapIndexes as fileNum>
			<sitemap>
				<loc>${model.sitemapUrlPrefix}_${fileNum}.xml</loc>
				<lastmod>${.now?date?iso_utc}</lastmod>
			</sitemap>
		</#list>
	</#if>
</sitemapindex>
</@compress>