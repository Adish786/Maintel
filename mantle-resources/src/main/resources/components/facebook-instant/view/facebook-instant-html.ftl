<!doctype html>
<html lang="en" prefix="op: http://media.facebook.com/op#">
	<head>
		<meta charset="utf-8">
		<link rel="canonical" href="${model.url}">
		<meta property="op:markup_version" content="v1.0">
		<meta property="fb:article_style" content="${model.articleStyle!'default'}">
		<meta property="fb:use_automatic_ad_placement" content="true">
	</head>
	<body>
		<article>
			<header>
				<figure class="op-ad"> 
					<iframe width="${model.adWidth}" height="${model.adHeight}" style="border:0; margin:0;" src="https://www.facebook.com/adnw_request?placement=${model.adPlacementId}&adtype=${model.adType}"></iframe> 
				</figure> 
			
				<#if model.kicker?has_content>
					<${model.kickerHeadingTag!"span"} class="op-kicker">${model.kicker}</${model.kickerHeadingTag!"span"}>
				</#if>
			
				<${model.headingTag!"span"} class="mntl-fb-instant-html__heading">${model.heading}</${model.headingTag!"span"}>
				<#if model.subheading?has_content>
					<${model.subheadingTag!"span"} class="mntl-fb-instant-html__subheading">${model.subheading}</${model.subheadingTag!"span"}>
				</#if>

				<#-- The date and time when your article was originally published -->
				<time class="op-published" datetime="${model.firstPublished}">${model.firstPublished.toString('MMMM d, h:mm a')}</time>

				<#-- The date and time when your article was last updated -->
				<time class="op-modified" datetime="${model.lastPublished}">${model.lastPublished.toString('MMMM d, h:mm a')}</time>

				<#-- The authors of your article -->
				<address>
					<#if model.authorUrl?has_content>
						<a href="${model.authorUrl}">${model.authorName}</a>
					<#else>
						<a>${model.authorName}</a>
					</#if>
					${model.authorText!''}
				</address>
			</header>
			
			<@location name="content" tag="" />

			<@location name="tracking" tag="" />

			<footer>
				<#-- Copyright details for your article -->
				<#assign datetime = .now>
				<small>&copy; ${datetime?string("yyyy")} ${model.footerCopy!'About.com'} &mdash; All rights reserved.</small>
			</footer>
		</article>
	</body>
</html>