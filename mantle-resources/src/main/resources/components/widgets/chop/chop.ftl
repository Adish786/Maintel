<#assign chopHeightStyle=((requestContext.userAgent.familyName)?has_content && requestContext.userAgent.familyName == 'IE' && (requestContext.userAgent.versionMajor)?has_content && requestContext.userAgent.versionMajor == '11')?then('max-height', '--chop-height') />
<@location
	id="${manifest.instanceId}--chop-content"
	name="chop-content"
	class="${model['chop-class']!''} ${(model['chop-height']??)?then('is-chopped', '')}"
	style="${(model['chop-height']?? && model['chop-height'] != 'none')?then((chopHeightStyle + ': ' + model['chop-height'] + 'px'), '')}" />
<@component data_chop_height=model.chop\-height class=(model['chop-height']??)?then('', 'is-hidden')>
    <@location name="chop-trigger" tag="span" />
</@component>
<#-- fallback for browsers with JS disabled to force the chop open -->
<noscript><#rt>
	<style><#t>
		#${manifest.instanceId}--chop-content {<#t>
			max-height: initial;<#t>
		}<#t>
		#${manifest.instanceId} {<#t>
			display: none;<#t>
		}<#t>
	</style><#t>
</noscript>
