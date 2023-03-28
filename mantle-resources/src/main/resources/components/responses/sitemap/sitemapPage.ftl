<@compress single_line=true>
<?xml version="1.0" encoding="UTF-8"?>
<urlset xmlns="http://www.sitemaps.org/schemas/sitemap/0.9">
	<#list model.items![] as item>
		<url>
			<loc>${item.url!''}</loc>
			<lastmod>${item.displayedDateInNewYorkTimeZone!''}</lastmod>
			<changefreq>daily</changefreq>
			<priority>0.8</priority>
		</url>
	</#list>
</urlset>
</@compress>