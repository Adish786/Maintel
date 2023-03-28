<#compress>
	<#assign injectedCSS>
		<#list requestContext.tests?keys as testKey>
			<#if testKey?starts_with("cssJsInjection") && (requestContext.tests[testKey].payload[0])?has_content>
				/* Injected via proctor test ${testKey} */
				${requestContext.tests[testKey].payload[0]}
			</#if>
		</#list>
	</#assign>
	<#if injectedCSS?has_content>
		<style type="text/css">${injectedCSS}</style>
	</#if>
</#compress>