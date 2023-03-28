<#ftl output_format="XML">
<rss version="2.0"
	xmlns:content="http://purl.org/rss/1.0/modules/content/"
	xmlns:dc="http://purl.org/dc/elements/1.1/"
	xmlns:amzn="https://amazon.com/ospublishing/1.0/">
	<channel>
		<title>${model.title}</title>
		<link>${model.link}</link>
		<description>${model.description}</description>
		<language>${model.language}</language>
		<lastBuildDate>${rssTimeFormat(model.lastBuildDate)}</lastBuildDate>
		<amzn:rssVersion>1.0</amzn:rssVersion>
		<#if model.logo?has_content><image>${model.logo}</image></#if>
		<@location name="items" tag="" />
	</channel>
</rss>