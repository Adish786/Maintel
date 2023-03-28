<#macro thumborImg img width height=0 alt="" class="" fitInStyle="" forceSize=false lazyload=true cropSetting="" srcset={} filters=[imageFilter.noUpscale()] params...>
<#compress>
	<#if img?is_hash>
		<#assign imageUrl = img.objectId />
	<#else>
		<#assign imageUrl = img />
	</#if>
	<#assign attrPrefix = lazyload?string("data-", "") />

	<img

	<#if srcset?has_content>
		${attrPrefix}srcset="${utils.getSrcsetThumbor(imageUrl, srcset.minWidth, srcset.maxWidth, srcset.maxHeight!0, srcset.stepCount!3, fitInStyle, forceSize, requestContext, cropSetting, filters)}"

		<#if srcset.sizes?has_content>
			${attrPrefix}sizes="${srcset.sizes}"
		</#if>
	</#if>

	${attrPrefix}src="${utils.getImageSrc(imageUrl, width, height, fitInStyle, forceSize, requestContext, cropSetting, filters, false)}"

	<#if alt?has_content>
		alt="${alt?html}"
	</#if>

	<#if class?has_content || lazyload>
		class="<#if lazyload>lazyload</#if><#if class?has_content> ${class}</#if>"
	</#if>

	<#if (params?size > 0)><#list params?keys as param> ${param?replace('_','-')}="${params[param]?html}"</#list></#if>
	/>
</#compress>
</#macro>

<@thumborImg img=model.img width=model.width height=model.height forceSize=model.forceSize lazyload=model.lazyload filters=model.filters />
