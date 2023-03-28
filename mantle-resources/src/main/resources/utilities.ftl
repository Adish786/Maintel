<@compress single_line=true><#-- This line must be first-->
<#macro location name tag="div" id="" class="" models={} overrideParams=[] overrideManifest={} markup=false separator="" params...>
    <#if overrideParams?has_content>
		<#local finalParams = overrideParams />
	<#else>
		<#local finalParams = params />
	</#if>
	<#local finalClass = "loc " + name />
	<#if class?has_content>
		<#local finalClass = finalClass + " " + class />
	</#if>
	<#if overrideManifest?has_content>
		<#local manifest_bak=overrideManifest />
		<#assign manifest = overrideManifest />
	<#else>
		<#local manifest_bak=manifest />
	</#if>
	<#local model_bak = model />
	<#if manifest.locations[name]??>
		<@optionalTag tag=tag id=id class=finalClass overrideParams=finalParams>
			<#local locationHasContent = false />
			<#list manifest.locations[name] as childManifest>
				<#assign manifest = childManifest />
				<#assign model = manifest.model />
				<#if finalParams?has_content>
					<#assign model = model + finalParams />
				</#if>
                <#if models?has_content>
                    <#assign model=model+models/>
                </#if>
			    <#local tempWhoCares = manifest.componentInfo.start() />
				<#local componentMarkup>
					<#include "/${manifest.template}">
				</#local>
			    <#if markup && (((manifest.component.previewType)!(manifest.component.infoComponent.previewType))!"")=="markup">
			    	<pre class="code"><code>${componentMarkup?html?trim}</code></pre>
				<#elseif componentMarkup?trim?length gt 0>
					<#if separator?has_content && locationHasContent>${separator}</#if>
					${componentMarkup}
					<#local locationHasContent = true />
				</#if>
			    <#local tempWhoCares = manifest.componentInfo.end() />
			    <#local tempWhoCares = manifest.componentInfo.incrementRenderCount() />
			</#list>
		</@optionalTag>
    </#if>
    <#assign manifest = manifest_bak />
    <#assign model = model_bak />
</#macro>

<#macro endComment class><#if !utils.suppressEndComments(requestContext)><#compress><!-- end: ${class} --></#compress></#if></#macro>

<#macro optionalTag tag id="" class="" showEndComment=false overrideParams=[] closeTag=true params...>
	<#local nested><#nested/></#local>
	<#if overrideParams?has_content>
		<#local finalParams = overrideParams />
	<#else>
		<#local finalParams = params />
	</#if>
	<#if tag?has_content>
		<${tag}<#if id?has_content> id="${id}"</#if><#if class?has_content> class="${class}"</#if><#list finalParams?keys as param> ${param?replace('_','-')}<#if finalParams[param]?has_content>="${finalParams[param]?replace("\"", "&quot;")}"</#if></#list>><#t>
			<#if nested?has_content && nested?trim?has_content>
				<#-- this line is here to ensure correct html output formatting --><#nt>
				${nested}<#t>
			</#if>
		<#if closeTag></${tag}></#if><#if showEndComment && class?has_content><@endComment class=class /></#if><#t>
		<#-- this line is here to ensure correct html output formatting --><#nt>
	<#else>
		<#compress>${nested}</#compress><#t>
		<#-- this line is here to ensure correct html output formatting --><#nt>
	</#if>
</#macro>

<#macro component tag="div" class="" tracking_container=false ordinal="" closeTag=true params...>
	<#compress>
		<#-- Start: Build Class List -->
		<#local tagClass = "comp" + " " + extractClasses(model.class)/>
		<#list manifest.component.referenceIdChain as reference>
			<#local tagClass = tagClass + " " + reference />
		</#list>

		<#if class?has_content>
			<#local tagClass = tagClass + " " + class />
		</#if>
		<#-- End: Build Class List -->
		<#local deferred = manifest.component.deferred />
		<#local renderable = manifest.component.isRenderable(requestContext) />
		<#local finalParams = {} />

		<#if (params?size > 0)>
			<#local finalParams = params />
		</#if>
		<#if model.attrs?has_content>
			<#local finalParams = finalParams + model.attrs />
		</#if>

		<#if (tracking_container || (model.trackingContainer?? && model.trackingContainer))>
			<#local finalParams = finalParams + {'data_tracking_container': 'true'} />
		</#if>

		<#if (ordinal?has_content)>
			<#local finalParams = finalParams + {'data_ordinal': ordinal} />
		<#elseif (model.ordinal?has_content)>
			<#local finalParams = finalParams + {'data_ordinal': model.ordinal} />
		</#if>

		<#if deferred && !renderable>
			<#local finalParams = finalParams + {
				'data_defer': manifest.component.deferment,
				'data_defer_params': (manifest.component.deferParams)!''
			} />
		</#if>
	</#compress>
	<@optionalTag
		tag=(deferred && renderable)?then('', tag)
		id=(manifest.instanceId + model.idSuffix!'')
		class=tagClass
		overrideParams=finalParams
		showEndComment=true
		closeTag=closeTag>
		<#if (!deferred || renderable)><#nested></#if>
	</@optionalTag>
</#macro>

<#macro stripHtml string="">
<#compress>
    ${string?replace("<[^>]*>", " ", "r")}
</#compress>
</#macro>

<#macro truncateText string count>
    <#if (string?length > count)>
		<#local trimmedString = string[0..(count - 1)] />
		<#if (string?last_index_of(" ") > trimmedString?length)>
			${trimmedString[0..(trimmedString?last_index_of(" ") - 1)]}&hellip;
		<#else>
			${trimmedString}&hellip;
		</#if>
    <#else>
        ${string}
    </#if>
</#macro>

<#macro truncateAndStripText string count>
	<#local strippedText>
		<@stripHtml string />
	</#local>
	<@truncateText strippedText count />
</#macro>

<#macro a href type="" external="" safelist=false overrideManifest={} rel="" target="" tag="" forceNoSponsored=false attributes...>
<#compress>
	<#if overrideManifest?has_content>
		<#local m = overrideManifest />
	<#else>
		<#local m = manifest />
	</#if>
	<#if external?has_content>
		<#local externalUrl = external />
	<#else>
		<#local externalUrl = !utils.isValidInternalUrl(requestContext, href) />
	</#if>

	<#local relValue = rel />
	<#local targetValue = target />
	<#local finalAttributes = {} />

	<#if (attributes?size > 0)>
		<#local finalAttributes = attributes />
	</#if>
	<#if model.attrs?has_content>
		<#local finalAttributes = finalAttributes + model.attrs />
	</#if>
    <#local processedElement = utils.processElementRewriters("a", href, finalAttributes, requestContext) />
    <#if processedElement?has_content>
        <#local finalAttributes = processedElement.attributes />
        <#local href = processedElement.href />
    </#if>
	<#if href?has_content>
	<a
		href="${utils.rewriteContentUrl(href, requestContext)}"
		<#if externalUrl>
			<#local relValue = relValue + " noopener" />
			<#local targetValue = "_blank" />
		</#if>
		<#if targetValue?has_content>
			target="${targetValue}"
		</#if>

    <#-- if the link is eligible to check if it's present in safelist (most enternal links are) and if it's not in safelist then add nofollow to it.
     If the link is not eligible to check if it's present in safelist (internal links are), just move on as it will be a follow link!
     -->
		<#if safelist && utils.isNoFollow(requestContext, href)>
			<#local relValue = relValue + " nofollow" />
		</#if>
		<#if utils.isSponsored(requestContext, href)>
			<#if !relValue?contains("nofollow")>
				<#local relValue = relValue + " nofollow" />
			</#if>
			<#if !forceNoSponsored>
				<#local relValue = relValue + " sponsored" />
			</#if>
		</#if>
		<#if utils.isCaes(requestContext, href)>
            <#if !relValue?contains("noskim")>
                <#local relValue = relValue + " noskim" />
            </#if>
        <#else>
            <#local relValue = relValue + " nocaes" />
        </#if>
		<#if relValue?has_content>
			rel="${relValue?trim}"
		</#if>
		<#if type?has_content>data-type="${type}"</#if>
		<#if finalAttributes?has_content>
			<#list finalAttributes as key, val>
                <#if val?html?has_content>
                    ${key?replace('_','-')}="${val?html}"
                </#if>
            </#list>
		</#if>
	><#nested></a><@endComment class=(finalAttributes.class)!'' />
	<#else>
    	<@component tag=tag?has_content?then(tag, "span") class=(finalAttributes.class)!''><#nested></@component><@endComment class=(finalAttributes.class)!'' />
	</#if>

</#compress>
</#macro>

<#-- TODO: why doesn't this just use optionalTag? -->
<#macro button class="btn" type="button" attributes...>
<#compress>
	<button
		class="${class}"
		type="${type}"
		<#list attributes?keys as attr_key>
			<#if attributes[attr_key]?html?has_content>
				${attr_key?replace('_','-')}="${attributes[attr_key]?html}"
			</#if>
		</#list>
	><#nested>
	</button><@endComment class=class!'' />
</#compress>
</#macro>

<#--
	Default Max Bytes for serving thumbor images is set to 150kb : max_bytes(150000).
	Max bytes will be ignored in the filters parameter if ignoreMaxBytes is true.
	WebP is disabled by default but may be overriden by setting `webpAuto` param. (1)
	- this is disabled to avoid forcing webp optimizations on shared urls and meta tags.
-->
<#macro thumborUrl img maxWidth maxHeight=0 fitInStyle="" forceSize=false cropSetting="" webpAuto=false filters=[imageFilter.noUpscale(),'max_bytes(150000)','strip_icc()'] ignoreMaxBytes=false >
<#compress>
	<#if img?is_hash>
		<#local imageUrl = img.objectId />
		<#local addWatermark = utils.isWatermarkedImage(img) />
	<#else>
		<#local imageUrl = img />
		<#local addWatermark = false />
	</#if>

	<#-- 1 -->
	<#if webpAuto && (requestContext.headers.accept)?has_content && requestContext.headers.accept?contains('webp') >
		<#local filters = filters + ['format(webp)'] />
	</#if>

	${utils.getThumborUrl(imageUrl, maxWidth, maxHeight, fitInStyle, forceSize, requestContext, cropSetting, filters, addWatermark, ignoreMaxBytes)}
</#compress>
</#macro>

<#macro absoluteHref href>${requestContext.urlData.scheme?lower_case}://${requestContext.getServerName()}<#if requestContext.getServerPort() != 80>:${requestContext.getServerPort()?c}</#if>${href}</#macro>

<#--
	Default Max Bytes for serving thumbor images is set to 150kb : max_bytes(150000).
	Because this macro is only used on non-converged components, we are not filtering Max Bytes from the image src.
	WebP is enabled by default but may be overriden by setting `webpAuto` param. (1)
	TODO: Remove customPlaceholder parameter as part of image convergence (AXIS-2268)
-->
<#macro thumborImg img width height=0 alt="" class="" fitInStyle="" forceSize=false lazyload=true cropSetting="" srcset={} filters=[imageFilter.noUpscale(),'max_bytes(150000)','strip_icc()'] placeholder=false originalWidth=0 originalHeight=0 webpAuto=true customPlaceholder="" params...>
<#compress>
	<#if img?is_hash>
        <#local imageUrl = img.objectId />
        <#local addWatermark = utils.isWatermarkedImage(img) />
        <#if img.encodedThumbnail?has_content>
        	<#local blurryPlaceholder = img.encodedThumbnail />
        <#else>
        	<#local blurryPlaceholder = ''/>
        </#if>
    <#else>
        <#local imageUrl = img />
        <#local addWatermark = false />
        <#local blurryPlaceholder = ''/>
    </#if>

	<#-- 1 -->
	<#if webpAuto && (requestContext.headers.accept)?has_content && requestContext.headers.accept?contains('webp') >
		<#local filters = filters + ['format(webp)'] />
	</#if>

	<#local attrPrefix = lazyload?string("data-", "") />

	<@wrapInPlaceHolder placeholder originalWidth originalHeight>
        <img

		<#if srcset?has_content>
			${attrPrefix}srcset="${utils.getSrcsetThumbor(imageUrl, srcset.minWidth, srcset.maxWidth, srcset.maxHeight!0, srcset.stepCount!3, fitInStyle, forceSize, requestContext, cropSetting, filters, addWatermark)}"
			<#if srcset.sizes?has_content>
				${attrPrefix}sizes="${srcset.sizes}"
			</#if>
		</#if>

		${attrPrefix}src="${utils.getImageSrc(imageUrl, width, height, fitInStyle, forceSize, requestContext, cropSetting, filters, addWatermark)}"

		<#if alt?has_content>
			alt="${alt?html}"
		</#if>

		<#if class?has_content || lazyload>
			class="<#if lazyload>lazyload</#if><#if class?has_content> ${class}</#if>"
		</#if>

        width="${width?c}"
        height="${height?c}"

		<#if (params?size > 0)>
			<#list params?keys as param> ${param?replace('_','-')}="${params[param]?html}"</#list>
		</#if>

        />
		<#if lazyload>
			<noscript>
				<img src="${utils.getImageSrc(imageUrl, width, height, fitInStyle, forceSize, requestContext, cropSetting, filters, addWatermark)}"
					<#if srcset?has_content>
						srcset="${utils.getSrcsetThumbor(imageUrl, srcset.minWidth, srcset.maxWidth, srcset.maxHeight!0, srcset.stepCount!3, fitInStyle, forceSize, requestContext, cropSetting, filters, addWatermark)}"
					</#if>
					alt="${alt}"
					width="${width?c}"
					height="${height?c}"
					class="img--noscript <#if class?has_content> ${class}</#if>" />
			</noscript>
		</#if>
  	</@wrapInPlaceHolder>
</#compress>
</#macro>

<#macro thumborPrimaryImg  class="" alt="" originalWidth=0 originalHeight=0 providedImageSources="" placeholder=true blurryPlaceholder=false params...>
<#compress>
    <@wrapInPlaceHolder placeholder originalWidth originalHeight>
        <img

	    <#if providedImageSources?has_content>
			<#-- placeholder must be set to true in order to have a blurry placeholder regardless of value of blurryPlaceholder -->
	    	<#local lazyload = placeholder &&
                blurryPlaceholder &&
                (providedImageSources['blurryPlaceholder']?has_content || utils.generateBlurryPlaceholderIfMissingFromSelene())
                />

	        <#if providedImageSources['src']?has_content>
	 	        src=${providedImageSources['src']}
		    </#if>

		    <#if lazyload && providedImageSources['blurryPlaceholder']?has_content>
                style="--blurry: url('${utils.getBlurryPlaceholder(providedImageSources['blurryPlaceholder'])}')"
                onload="(function(e){e.classList.add('loaded')})(this)"
			<#elseif lazyload && utils.generateBlurryPlaceholderIfMissingFromSelene()>
                style="--blurry: url('${utils.generateBlurryPlaceholder(requestContext, providedImageSources['objectId'])}')"
                src="${utils.generateBlurryPlaceholder(requestContext, providedImageSources['objectId'])}"
                onload="(function(e){e.classList.add('loaded')})(this)"
            </#if>

            <#if providedImageSources['srcset']?has_content>
                srcset=${providedImageSources['srcset']}
		    </#if>

            <#if providedImageSources['sizes']?has_content>
                sizes=${providedImageSources['sizes']}
            </#if>

	    </#if>

		<#if alt?has_content>
			alt="${alt?html}"
		</#if>

		<#if class?has_content>
			class="<#if class?has_content> ${class}</#if><#if placeholder && blurryPlaceholder> mntl-primary-image--blurry</#if>"
		</#if>

        width="${originalWidth?c}"
        height="${originalHeight?c}"

		<#if (params?size > 0)>
            <#list params?keys as param> ${param?replace('_','-')}="${params[param]?html}"</#list>
        </#if>

        />

		<noscript>
			<img
				<#if providedImageSources['src']?has_content>
					src=${providedImageSources['src']}
				</#if>
				<#if providedImageSources['srcset']?has_content>
					srcset=${providedImageSources['srcset']}
				</#if>
				alt="${alt}"
				width="${originalWidth?c}"
				height="${originalHeight?c}"
				class="primary-img--noscript <#if class?has_content> ${class}</#if>" />
		</noscript>

    </@wrapInPlaceHolder>

</#compress>
</#macro>

<#macro wrapInPlaceHolder placeholder originalWidth originalHeight>

   <#local showPlaceholder = placeholder && (originalWidth > 0) && (originalHeight > 0) />

   <#if showPlaceholder>
      <#local imgRatio = ((originalHeight / originalWidth) * 100)?string["0.0;; roundingMode=down"] />
       <#-- adds a placeholder div around the image with padding equal to the size of the new resized image.
			 the number operation and rounding is just to truncate the decimal and make sure it doesn't get rounded up (so 75.9645 will be 75.9).
			 this is because thumbor also does some rounding down on their resizing, so using the full decimal will result in a placeholder a few px taller than
			 the actual image.
	     -->
	    <div class="img-placeholder" style="padding-bottom:${imgRatio}%;">
	</#if>

	<#nested>

	<#if showPlaceholder></div></#if>
</#macro>

<#macro svg name classes="">
    <svg class="icon ${name} ${extractClasses(classes)}">
        <use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="#${name}"></use>
    </svg>
</#macro>

<#function testIds prefix="" separator=",">
<#local ids><#if requestContext.tests?has_content><#list requestContext.tests?values as bucket><#if bucket.trackingId?has_content>${prefix}${bucket.trackingId}${bucket_has_next?string(separator,"")}</#if></#list></#if></#local>
<#return ids!"">
</#function>

<#-- This function is used to convert a map to a javascript object -->
<#macro convertHashToJs hash>
	{
		<#local first = true />
		<#list hash?keys as key>
			<#if first><#local first = false><#else>,</#if>${key}: <#if hash[key]?is_boolean>${hash[key]?c}<#else><#if hash[key]?is_string>'</#if>${hash[key]?html}<#if hash[key]?is_string>'</#if></#if>
		</#list>
	}
</#macro>

<#function getRecipeTimes document>
    <#if (document.prepTime)?has_content>
        <#local prepTime = convertHoursToMinutes(document.prepTime)>
    <#else>
        <#local prepTime = 0>
    </#if>
    <#if (document.cookTime)?has_content>
        <#local cookTime = convertHoursToMinutes(document.cookTime)>
    <#else>
        <#local cookTime = 0>
    </#if>
    <#-- customTime intentionally removed from totalTime -->
    <#local totalTime = cookTime + prepTime>
    <#return {"prepTime": prepTime, "cookTime": cookTime, "totalTime": totalTime} />
</#function>

<#-- If document is missing a corresponding min/max, this function will return both with the existing value (e.g. only prepTimeMin is set so prepTimeMax will be set to prepTimeMin) -->
<#function getTimeRangeMinMax timeRange fallbackTime=null>
    <#local timeMin = ''>
    <#local timeMax = ''>

    <#if timeRange?has_content>
            <#local timeMin = timeRange.min?has_content?then(convertMillisecondsToMinutes(timeRange.min), '')>
            <#local timeMax = timeRange.max?has_content?then(convertMillisecondsToMinutes(timeRange.max), timeMin)>
            <#local timeMin = timeMin?has_content?then(timeMin, timeMax)> <#-- ensures min and max are same value if one field is empty -->
    <#elseif fallbackTime?has_content>
        <#local timeMin = convertMillisecondsToMinutes(fallbackTime)>
        <#local timeMax = timeMin>
    </#if>

    <#return {"min": timeMin, "max": timeMax}>
</#function>

<#function getRecipeSCTimeRanges document>
    <#local prepTimeRange = getTimeRangeMinMax((document.prepTimeRange)!'', (document.prepTime)!'')>
    <#local prepTimeMin =  prepTimeRange.min>
    <#local prepTimeMax =  prepTimeRange.max>

    <#local cookTimeRange = getTimeRangeMinMax((document.cookTimeRange)!'', (document.cookTime)!'')>
    <#local cookTimeMin =  cookTimeRange.min>
    <#local cookTimeMax =  cookTimeRange.max>

    <#local customTimeRange = getTimeRangeMinMax((document.customTimeRange.time)!'', (document.customTime.time)!'')>
    <#local customTimeMin =  customTimeRange.min>
    <#local customTimeMax =  customTimeRange.max>

	<#local typedTimes = [] />
	<#local totalTimeRange = {} />
	<#local typedTimeRangeData = document.getTypedTimeRanges()!'' />

	<#if typedTimeRangeData?has_content>
		<#list typedTimeRangeData.getList() as typedTime>
			<#local typedTimeType = typedTime.getRecipeTimeType()>
			<#local typedTimeRange = typedTime.getTimeRange()>
			<#local typedTimeMin =  typedTimeRange.min?has_content?then(convertMillisecondsToMinutes(typedTimeRange.min), '')>
			<#local typedTimeMax =  typedTimeRange.max?has_content?then(convertMillisecondsToMinutes(typedTimeRange.max), typedTimeMin)>

			<#if typedTimeType == "TOTAL">
				<#local totalTimeRange = {"minValue": typedTimeMin, "maxValue": typedTimeMax} />
			<#else>
				<#local typedTimes = typedTimes + [{"type": utils.formatUpperUnderscoreCaseToSentenceCase(typedTimeType) + " Time", "minValue": typedTimeMin, "maxValue": typedTimeMax}] />
			</#if>
		</#list>
	</#if>

	<#if !totalTimeRange?has_content>
		<#local totalTimeMin = prepTimeMin?has_content?then(prepTimeMin, 0) + cookTimeMin?has_content?then(cookTimeMin, 0) + customTimeMin?has_content?then(customTimeMin, 0)>
		<#local totalTimeMax = prepTimeMax?has_content?then(prepTimeMax, 0) + cookTimeMax?has_content?then(cookTimeMax, 0) + customTimeMax?has_content?then(customTimeMax, 0)>
		<#local totalTimeRange = {"minValue": totalTimeMin, "maxValue": totalTimeMax} />
	</#if>

    <#return {"prepTimeRange": {"minValue": prepTimeMin, "maxValue": prepTimeMax}, "cookTimeRange": {"minValue": cookTimeMin, "maxValue": cookTimeMax}, "totalTimeRange": totalTimeRange, "customTime": {"customTimeMin": customTimeMin, "customTimeMax": customTimeMax}, "customLabel": (document.customTimeRange.label)?has_content?then(document.customTimeRange.label, (document.customTime.label)!''), "typedTimes": typedTimes}>
</#function>

<#function convertMillisecondsToMinutes time>
    <#return ((time?replace(',', '')?number) / 1000) / 60>
</#function>

<#function convertHoursToMinutes time>
    <#local timeHours = time?split(":")[0]?trim?number>
    <#local timeMinutes = time?split(":")[1]?trim?number>
    <#return 0 + (timeHours * 60) + timeMinutes  >
</#function>

<#function splitIngredients ingredients>
	<#local split = ingredients?replace("(\n\n)+", "\n", "r") />
	<#local split = split?replace("(<p>|<\\/p>)", "", "r") />
	<#local split = split?split("\n", "r") />
	<#return split />
</#function>

<#macro quantity quantityRange>
    <#if quantityRange.min?has_content><@parseQuantity numerator=quantityRange.min.numerator denominator=quantityRange.min.denominator /></#if>
    <#if quantityRange.max?has_content> to <@parseQuantity numerator=quantityRange.max.numerator denominator=quantityRange.max.denominator /></#if><#t>
</#macro>

<#macro parseQuantity numerator denominator>
    <#compress>
        <#if denominator == 1>
        ${numerator?string}
    <#else>
        <#if (numerator > denominator)>
            <#local fractionInt = (numerator / denominator)?int />
            <#local remainingNumerator = numerator % denominator />
            ${fractionInt?string} ${remainingNumerator?string}/${denominator?string}
        <#else>
            ${numerator?string}/${denominator?string}
      </#if>
    </#if>
  </#compress>
</#macro>

<#function extractClasses classes="">
	<#if classes?? && classes?has_content>
		<#if classes?is_sequence>
			<#return classes?join(" ") />
		<#else>
			<#return classes />
		</#if>
	<#else>
		<#return "" />
	</#if>
</#function>

<#-- NOTE: This is meant to be used by components which are meant to use the component
            id as the prefix to classnames for nested elements. This simplifies the naming
            of those classnames as it is automatically pulled from the component's ids of the root
            implementation (e.g. mantle) and the begininning of the reference chain (e.g. the vetical)
            This provides a class that the vertical can use for vertical specific styling and it also
            provides a consistent classname which can be targeted by styles through the whole reference chain
            (e.g. mantle -> lithosphere -> commerce -> tech.
            Example:
                Tech adds in own styles in tech-sc-block-comparisonlist which is resolved by TechRenderUtils
                for comparison-list blocks

                In comparison-list.ftl
                <div class="${generateReferenceIdClasses('__wrapper')}">
                Generates
                <div class=tech-sc-block-comparisonlist__wrapper mntl-sc-block-comparisonlist__wrapper')}">
-->
<#function generateReferenceIdClasses suffix="">
	<#local referenceIds = [manifest.component.referenceId, manifest.component.referenceRootId] />
	<#local filteredReferenceIds = [] />
	<#local classes = [] />
	<#list referenceIds as referenceId>
		<#if !filteredReferenceIds?seq_contains(referenceId)>
			<#local filteredReferenceIds = filteredReferenceIds + [referenceId] />
		</#if>
	</#list>
	<#list filteredReferenceIds as filteredReferenceId>
		<#local classes = classes + [filteredReferenceId + suffix] />
	</#list>
	<#return classes?join(" ") />
</#function>

<#macro rewriteHref href>${utils.rewriteContentUrl(href, requestContext)?html?replace("&amp;", "&")}</#macro>

<#-- Creates 1 row of an html table -->
<#macro tableRow cells columnAttributes tag="td" openingBrace="<" closingBrace=">" >
    <#local t=tag />
    <#list cells>
        ${openingBrace}tr${closingBrace}
            <#items as cell>
                <#local t=((columnAttributes.list[cell?index].isHeading)!false)?then("th", tag) />
                <#-- We want to use ?html encoding in certain situations (schema). We can tell whether to use ?html by the openingBrace param -->
                ${openingBrace}${t}${closingBrace}<#if openingBrace == '<'>${(cell.value)!}<#else>${(cell.value?html)!}</#if>${openingBrace}/${t}${closingBrace}
            </#items>
        ${openingBrace}/tr${closingBrace}
    </#list>
</#macro>

<#function rssTimeFormat time format="">
	<#local datetimeFormat="yyyy-MM-dd'T'HH:mm:ss.SSS" />
  <#if format == "timeZone">
    <#local rssDateFormat="EEE, dd MMMM yyyy HH:mm:ss zzz"/>
  <#else/>
    <#local rssDateFormat="EEE, dd MMMM yyyy HH:mm:ss Z" />
  </#if>
	<#return time?datetime(datetimeFormat)?string(rssDateFormat) />
</#function>

<#function isZero value>
    <#if (value?is_string)>
        <#return value?trim == '0' />
    <#else>
        <#return value?c == '0' />
    </#if>
</#function>

<#--  new image macro  -->
<#macro thumborUniversalImg
			img
			width
			height
			alt
			class
			srcset
			sizes
			lazyload
			placeholder
			placeholderWidth
			placeholderHeight
			expand
			role
			filters
			fitInStyle
			idPrefix
			useHiResLightbox
			hiResLightboxWidth
			inheritedParams={}
            isPrimaryImage=false
			params...>
<#compress>

    <#local imageUrl = img?is_hash?then(img.objectId, img) />
	<#local attrPrefix = lazyload?string("data-", "") />
	<#local ignoreMaxBytesOutsideSrcset = utils.uncappedImageWidthsEnabled() />

	<#--  if the macro is being used to load a primary image, then it will have inherited params that should be applied (ie. onload, style)  -->
	<#local allParams = params + inheritedParams />

	<#if model.imgAttrs?has_content>
		<#local allParams = allParams + model.imgAttrs />
	</#if>

    <#--  check does not work for Safari on Mac OS, currently not possible to detect when a user is on the proper Mac OS version for webp  -->
    <#if utils.useWebp(requestContext) >
        <#local filters = filters + ['format(webp)'] />
    </#if>

	<#--  ensure html width and height attributes are never 0  -->
	<#if width == 0 || height == 0>
		<#local imgWidth = placeholderWidth?c />
		<#local imgHeight = placeholderHeight?c />
	<#else>
		<#local imgWidth = width?c />
		<#local imgHeight = height?c />
	</#if>

    <#--  if it is a gif then use the thumborGif macro to load video  -->
    <#if imageUrl?contains(".gif")>
        <@thumborGif
            img=img
            width=width
            height=height
            placeholderWidth=placeholderWidth
            placeholderHeight=placeholderHeight
            class=class
            srcset=srcset
            sizes=sizes
            placeholder=placeholder
            fitInStyle=fitInStyle
            alt=alt
            inheritedParams=allParams
            isPrimaryImage=isPrimaryImage />

		<#return><#--  return will exit out of the macro so that the below code is not evaluated  -->
    </#if>

	<@wrapInPlaceHolder placeholder placeholderWidth placeholderHeight>
		<img
			<#if isPrimaryImage>
				<#local imageSrc = imageUrl />
				<#local imageSrcset = srcset />
				<#local imageSizes = sizes />
			<#else>
				<#local imageSrc = "\"" + utils.getImageSrc(imageUrl, width, height, fitInStyle, false, requestContext, '', filters, false, ignoreMaxBytesOutsideSrcset) + "\"" />
				<#local imageSrcset = srcset?has_content?then("\"" + utils.getSrcsetThumbor(imageUrl, srcset.minWidth, srcset.maxWidth, srcset.maxHeight!0, srcset.stepCount!3, fitInStyle, false, requestContext, '', filters, false) + "\"", '') />
				<#local imageSizes = sizes?has_content?then("\"" + sizes + "\"", '') />
			</#if>

			${attrPrefix}src=${imageSrc}
			width="${imgWidth}"
			height="${imgHeight}"

			<#if imageSrcset?has_content && imageSizes?has_content>
				${attrPrefix}srcset=${imageSrcset}
				${attrPrefix}sizes=${imageSizes}
			</#if>

			<#if alt?has_content>
				alt="${alt?html}"
			</#if>

			<#if class?has_content || lazyload>
				class="<#if lazyload>lazyload</#if><#if class?has_content> ${class}</#if>"
			</#if>

			<#if expand != 0>
				data-expand="${expand?c}"
			</#if>

			<#if useHiResLightbox>
				data-hi-res-src="${utils.getImageSrc(imageUrl, [hiResLightboxWidth, imgWidth?number]?min, 0, fitInStyle, false, requestContext, '', filters, false)}"
			</#if>

			<#if role?has_content>
				role=${role}
			</#if>

			<#if idPrefix?has_content>
				id=${idPrefix + manifest.instanceId?split('_')[1]}
			</#if>

			<#if allParams?has_content>
				<#list allParams as param, value>
					${param}="${value?html}"
				</#list>
			</#if>
		/>

		<#if lazyload || isPrimaryImage>
			<noscript>
				<img
					src=${imageSrc}
					width="${imgWidth}"
					height="${imgHeight}"
					class="${isPrimaryImage?then('loaded primary-', '')}img--noscript <#if class?has_content> ${class}</#if>"
					<#if imageSrcset?has_content && imageSizes?has_content>
						srcset=${imageSrcset}
						sizes=${imageSizes}
					</#if>
					<#if alt?has_content>
						alt="${alt?html}"
					</#if>
					<#if role?has_content>
						role=${role}
					</#if>
				/>
			</noscript>
		</#if>
	</@wrapInPlaceHolder>
</#compress>
</#macro>

<#--  new primary image macro  -->
<#--  primary image macro will extract values from providedImageSources and then pass those along with more primary image related attributes  -->
<#macro thumborUniversalPrimaryImg
			providedImageSources
			alt
			class
			placeholder
			placeholderWidth
			placeholderHeight
			params...>
<#compress>

    <#if providedImageSources?has_content>
        <#local img = providedImageSources['src'] />
        <#local srcset = providedImageSources['srcset']!"" />
        <#local sizes = providedImageSources['sizes']!"" />
        <#local classes = class />
        <#local onload = "" />
        <#local style = "" />

        <#local hasBlurryPlaceholder = providedImageSources['blurryPlaceholder']?has_content />
        <#local lazyload = placeholder && (hasBlurryPlaceholder || utils.generateBlurryPlaceholderIfMissingFromSelene()) />

        <#if lazyload>
            <#local onload = "(function(e){e.classList.add('loaded')})(this)" />
            <#local classes += " mntl-primary-image--blurry" />
            <#if hasBlurryPlaceholder>
                <#local style = "--blurry: url('${utils.getBlurryPlaceholder(providedImageSources['blurryPlaceholder'])}')" />
            <#else>
                <#local style = "--blurry: url('${utils.generateBlurryPlaceholder(requestContext, providedImageSources['objectId'])}')" />
            </#if>
        </#if>

        <#--  pass values on to the universal image macro  -->
        <@thumborUniversalImg
            img=img
            width=placeholderWidth
            height=placeholderHeight
            alt=alt
            class=classes
            fitInStyle=""
            lazyload=false
            srcset=srcset
            sizes=sizes
            filters=[]
            placeholder=placeholder
            placeholderWidth=placeholderWidth
            placeholderHeight=placeholderHeight
            style=style
            onload=onload
            expand=0
            role=""
			idPrefix=""
            inheritedParams=params
			useHiResLightbox=false
			hiResLightboxWidth=0
            isPrimaryImage=true/>
    </#if>
</#compress>
</#macro>

<#--  macro to load gif as video  -->
<#macro thumborGif
			img
			width
			height
			alt
			class
			srcset
			sizes
			placeholder
			placeholderWidth
			placeholderHeight
			fitInStyle
			inheritedParams={}
            isPrimaryImage=false>
<#compress>

    <#local imageUrl = img?is_hash?then(img.objectId, img) />
    <#local format = utils.useWebm(requestContext)?then('webm', 'mp4') />
    <#local isSrcsetHash = srcset?is_hash />

	<#--  ensure html width and height attributes are never 0  -->
	<#if width == 0 || height == 0>
		<#local imgWidth = placeholderWidth?c />
		<#local imgHeight = placeholderHeight?c />
	<#else>
		<#local imgWidth = width?c />
		<#local imgHeight = height?c />
	</#if>

    <#if isPrimaryImage>
        <#local videoSrc = imageUrl />
        <#local videoSrcset = srcset />
        <#local videoSizes = sizes />
    <#else>
        <#local videoSrc = "\"" + utils.getImageSrc(imageUrl, width, height, fitInStyle, false, requestContext, '', ['gifv(' + format + ')'], false) + "\"" />
        <#local videoSrcset = isSrcsetHash?then("\"" + utils.getSrcsetThumbor(imageUrl, srcset.minWidth, srcset.maxWidth, srcset.maxHeight!0, srcset.stepCount!3, fitInStyle, false, requestContext, '', ['gifv(' + format + ')'], false) + "\"", '') />
        <#local videoSizes = sizes?has_content?then("\"" + sizes + "\"", '') />
    </#if>

    <@wrapInPlaceHolder placeholder placeholderWidth placeholderHeight>
        <video
            autoplay
            loop
            playsinline
            muted
            class="mntl-gif__video ${class}"
           	width="${imgWidth}"
			height="${imgHeight}"

            <#if alt?has_content>
                aria-label="${alt?html}"
            </#if>

            <#if videoSrcset?has_content && videoSizes?has_content>
                data-srcset-${format}=${videoSrcset}
                data-sizes=${videoSizes}
            <#else>
                data-src=${videoSrc}
            </#if>

            <#--  if the macro is being used to load an image or primary image, then it will have inherited params that should be applied (ie. onload, style)  -->
            <#if (inheritedParams?size > 0)>
                <#list inheritedParams as param, value>
                    <#if param == 'onload'>
                        oncanplay=${value}
                    <#else>
                        ${param}="${value?html}"
                    </#if>
                </#list>
            </#if>>
        </video>

        <#--  fallback video source should always use the max width from the srcset (if available), and if that doesn't exist it should be the width.
            this will ensure that even if we do not have the JS to dynamically add the sources, we will still only serve the largest size specified by the srcset
            this will be smaller than serving the full sized video based on it's intrinsic height and width -->
        <noscript>
            <video
                autoplay
                loop
                playsinline
                muted
                class="mntl-gif__video mntl-gif__video--fallback <#if class?has_content>${class}</#if> ${isPrimaryImage?then('loaded primary-img--noscript', '')}"
                width="${imgWidth}"
				height="${imgHeight}"
                aria-label="${alt?html}"
				<#if isPrimaryImage>
					src=${imageUrl}
				<#else>
					src="${utils.getImageSrc(imageUrl, isSrcsetHash?then(srcset.maxWidth!width, width), isSrcsetHash?then(srcset.maxHeight!height, height), fitInStyle, false, requestContext, '', ['gifv(' + format + ')'], false)}"
				</#if>>
			</video>
        </noscript>
    </@wrapInPlaceHolder>
</#compress>
</#macro>

<#--
Returns a formatted time range in weeks, days, hours, and minutes, with shortened time unit labels.
Will only show the two largest time units for the min and max. Ex: '2 days, 4 hours - 1 day, 2 hours'.
Will combine the units if both the min and max have the same singular unit. Ex: '1 - 4 days'.

@param minTime {Long} the minimum time in ms
@param maxTime {Long} the maximum time in ms
@return {String} the formatted time range
-->
<#function calculateRange minTime maxTime>
    <#assign formattedMinTime = calculateExtendedTime(minTime) />
    <#assign formattedMaxTime = calculateExtendedTime(maxTime) />
    <#assign formattedRange = '${formattedMinTime} - ${formattedMaxTime}' />

    <#--  If a formatted time has only one label, it will look like '{amount} {unit}'  -->
    <#assign minTimeHasSingularLabel = !(formattedMinTime?contains(',')) />
    <#assign maxTimeHasSingularLabel = !(formattedMaxTime?contains(',')) />

    <#if minTimeHasSingularLabel && maxTimeHasSingularLabel>
        <#assign minTimeWordList = formattedMinTime?split(' ') />
        <#assign minTimeUnit = minTimeWordList[0] />
        <#assign minTimeLabel = minTimeWordList[1]?remove_ending('s') />

        <#assign maxTimeWordList = formattedMaxTime?split(' ') />
        <#assign maxTimeUnit = maxTimeWordList[0] />
        <#assign maxTimeLabel = maxTimeWordList[1]?remove_ending('s') />

        <#if minTimeLabel == maxTimeLabel>
            <#assign formattedRange = '${minTimeUnit} - ${maxTimeUnit} ${maxTimeLabel}s' />
        </#if>
    </#if>

    <#return formattedRange>
</#function>

<#--
Returns a formatted time in weeks, days, hours, and minutes, with shortened time unit labels.
Will only show the two largest time units. Ex: '7 weeks, 3 days'.

@param time {Long} the time in ms
@return {String} the formatted time
-->
<#function calculateExtendedTime time>
    <#assign totalMinutes = ((time / 1000) / 60)?floor />
    <#assign totalHours = (totalMinutes / 60)?floor />
    <#assign totalDays = (totalHours / 24)?floor />
    <#assign totalWeeks = (totalDays / 7)?floor />

    <#assign leftoverMinutes = totalMinutes % 60 />
    <#assign leftoverHours = totalHours % 24 />
    <#assign leftoverDays = totalDays % 7 />

    <#assign minuteLabel = getLabel(leftoverMinutes, 'minute') />
    <#assign hourLabel = getLabel(leftoverHours, 'hour') />
    <#assign dayLabel = getLabel(leftoverDays, 'day') />
    <#assign weekLabel = getLabel(totalWeeks, 'week') />

    <#assign firstLabel = '' />
    <#assign secondLabel = '' />

    <#if totalWeeks gte 1>
        <#assign firstLabel = weekLabel />
    <#elseif totalDays gte 1>
        <#assign firstLabel = dayLabel />
    <#elseif totalHours gte 1>
        <#assign firstLabel = hourLabel />
    <#else>
        <#assign firstLabel = minuteLabel />
    </#if>

    <#if leftoverDays gte 1 && dayLabel != firstLabel>
        <#assign secondLabel = ', ${dayLabel}' />
    <#elseif leftoverHours gte 1 && hourLabel != firstLabel>
        <#assign secondLabel = ', ${hourLabel}' />
    <#elseif leftoverMinutes gte 1 && minuteLabel != firstLabel>
        <#assign secondLabel = ', ${minuteLabel}' />
    </#if>

    <#return '${firstLabel}${secondLabel}' />
</#function>


<#--
Returns either a formatted range of time or a single time based on the data available, using weeks, days, hours, and minutes.
Prioritizes the range data over the single time. If there is only one of the min/max, that single time will be displayed.

@param minTime {Long} the minimum time in ms
@param maxTime {Long} the maximum time in ms
@param singleTime {Long} a single time in ms
@return {String} the formatted time or time range
-->
<#function getTimeText timeRange='' singleTime=''>
    <#assign minTime = (timeRange?has_content && timeRange.min?has_content && timeRange.min gt 0)?then(timeRange.min, '') />
    <#assign maxTime = (timeRange?has_content && timeRange.max?has_content && timeRange.max gt 0)?then(timeRange.max, '') />
    <#assign singleTime = (singleTime?has_content && singleTime gt 0)?then(singleTime, '') />

    <#if minTime?has_content || maxTime?has_content>
        <#if minTime?has_content && maxTime?has_content>
            <#return calculateRange(minTime, maxTime) />
        <#elseif minTime?has_content>
            <#return calculateExtendedTime(minTime) />
        <#else>
            <#return calculateExtendedTime(maxTime) />
        </#if>
    <#elseif singleTime?has_content>
        <#return calculateExtendedTime(singleTime) />
    <#else>
        <#return '' />
    </#if>
</#function>

<#--
Returns a formatted time value. If the time is under one hour, return the number of minutes.
Otherwise, return the time as `x hrs y mins`, or `1 day x hrs y mins` if total hrs is greater than 24.
-->
<#function calculateTime data>
	<#assign metaText = " " />
	<#assign dataNumber = data?replace(',', '')?number />
	<#if (dataNumber < 60)>
        <#assign metaText = dataNumber + " mins" />
    <#else>
		<#assign days = (dataNumber / 1440)?int />
		<#assign minutes = (dataNumber % 60)?int />

		<#if (days > 0)>
            <#assign hours = ((dataNumber % 1440) / 60)?int />
			<#if (days > 1)>
				<#assign dayUnits = " days" />
			<#else>
				<#assign dayUnits = " day" />
			</#if>

			<#assign metaText = days + dayUnits />
		<#else>
			<#assign hours = (dataNumber / 60)?int />
        </#if>

		<#if (hours > 0)>
        	<#assign metaText += " " + hours + " hrs" />
        </#if>

		<#if (minutes > 0)>
            <#assign metaText += " " + minutes + " mins" />
        </#if>
   </#if>
   <#return metaText>
</#function>

<#--
Takes a map of css variable names and values and returns
a "style" attribute compatible list of css variables, prepended
with --di-

@param styleVariable {Map} the map of css variable names and values
-->
<#function getDigitalIssueCssVariables digitalIssueCssVariables>
    <#local style = "" />

	<#if digitalIssueCssVariables?has_content>
		<#list digitalIssueCssVariables as variableName, variableValue>
			<#local style = style + "--di-" + variableName + ": " + variableValue + "; " />
		</#list>
	</#if>

    <#return style />
</#function>

</@compress><#-- This line must be last -->
