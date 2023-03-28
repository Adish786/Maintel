<item>
	<title>${model.item.title?xml}</title>
	<link>${model.item.link?xml}</link>
	<pubDate>${rssTimeFormat(model.item.pubDate.plusSeconds(model.pubDateOffset!0), "timeZone")?xml}</pubDate>
	<author>${model.item.author?xml}</author>
	<content:encoded><![CDATA[
		${model.item.htmlContent}
	]]></content:encoded>
	<#if (model.item.heroImage.objectId)?has_content>
		<amzn:heroImage>
			<@thumborUrl img=model.item.heroImage maxWidth=model.item.heroImage.width maxHeight=model.item.heroImage.height />
		</amzn:heroImage>
		<#if (model.item.heroImageCaption)?has_content>
			<amzn:heroImageCaption><![CDATA[
				${model.item.heroImageCaption}
			]]></amzn:heroImageCaption>
		</#if>
	</#if>
	<#if model.item.subheading?has_content>
	<amzn:subtitle><![CDATA[
		${model.item.subheading}
	]]></amzn:subtitle>
	</#if>
	<#if model.item.introText?has_content>
	<amzn:introText><![CDATA[
		${model.item.introText}
	]]></amzn:introText>
	</#if>
	<amzn:products>
		<#list model.item.products as product>
		<amzn:product>
			<amzn:productURL>${product.url?xml}</amzn:productURL>
			<#if product.headline?has_content>
			<amzn:productHeadline><![CDATA[
				${product.headline}
			]]></amzn:productHeadline>
			</#if>
			<#if product.summary?has_content>
			<amzn:productSummary><![CDATA[
				<#if product.summaryHeading?has_content><strong>${product.summaryHeading}:</strong> </#if>${product.summary}
			]]></amzn:productSummary>
			</#if>
			<#if product.award?has_content>
			<amzn:award><![CDATA[
				${product.award}
			]]></amzn:award>
			</#if>
			<#if product.rank?has_content>
			<amzn:rank>${product.rank}</amzn:rank>
			</#if>
			<#if product.rating?has_content>
			<amzn:rating>
				<amzn:ratingValue>${product.rating.ratingValue}</amzn:ratingValue>
				<amzn:bestRating>${product.rating.bestRating}</amzn:bestRating>
				<amzn:worstRating>${product.rating.worstRating}</amzn:worstRating>
				<amzn:applyToVariants>${product.rating.applyToVariants?c}</amzn:applyToVariants>
			</amzn:rating>
			</#if>
		</amzn:product>
		</#list>
	</amzn:products>
	<amzn:indexContent>False</amzn:indexContent>
</item>