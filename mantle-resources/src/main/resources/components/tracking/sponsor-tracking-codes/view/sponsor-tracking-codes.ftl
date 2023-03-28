<@compress single_line=true>
<@component>
<#if (model.trustedTrackingCodes)?has_content>
	<#list model.trustedTrackingCodes as trustedTrackingCode>
		${trustedTrackingCode}
	</#list>
</#if>
<#if (model.hasUntrustedTrackingCodes)!false>
	<iframe style="width:0;height:0;border:0;" src="${model.untrustedTrackingCodesUrl}"></iframe>
</#if>
</@component>
</@compress>