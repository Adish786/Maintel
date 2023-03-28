<@compress single_line=true>
<html>
	<head>
	</head>
	<body>
		<#if (model.untrustedTrackingCodes)?has_content>
			<#list model.untrustedTrackingCodes as untrustedTrackingCode>
				${untrustedTrackingCode}
			</#list>
		</#if>
	</body>
</html>
</@compress>