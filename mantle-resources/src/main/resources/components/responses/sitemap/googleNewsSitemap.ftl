<@compress single_line=true>
<?xml version="1.0" encoding="UTF-8"?>
<urlset xmlns="http://www.sitemaps.org/schemas/sitemap/0.9" xmlns:news="http://www.google.com/schemas/sitemap-news/0.9" xmlns:image="http://www.google.com/schemas/sitemap-image/1.1">
	<#list model.items![] as item>
		<url>
			<loc>${item.url}</loc>
			<lastmod>${item.news.lastModDateInNewYorkTimeZone}</lastmod>
			<news:news>
			    <news:publication>
			        <news:language>en</news:language>
			        <news:name>${item.news.publication.name}</news:name>
			    </news:publication>
			    <news:publication_date>${item.news.publicationDateInNewYorkTimeZone}</news:publication_date>
			    <news:title> ${item.news.title?xml}</news:title>
		    </news:news>
			<#if (item.image.objectId)?has_content>
			    <image:image>
			        <image:loc>
                        <@thumborUrl img=item.image maxWidth=item.image.width maxHeight=item.image.height ignoreMaxBytes=utils.uncappedImageWidthsEnabled() />
			        </image:loc>
			    </image:image>
			</#if>
		</url>
	</#list>
</urlset>
</@compress>