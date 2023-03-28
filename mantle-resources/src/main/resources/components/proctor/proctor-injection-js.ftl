<#compress>
	<#assign injectedJS>
		<#list requestContext.tests?keys as testKey>
			<#if testKey?starts_with("cssJsInjection") && (requestContext.tests[testKey].payload[1])?has_content>
				/* Injected via proctor test ${testKey} */
				${requestContext.tests[testKey].payload[1]}
			</#if>
		</#list>
	</#assign>
	<#if injectedJS?has_content>
		<script type="text/javascript">${injectedJS}</script>
	</#if>
</#compress>