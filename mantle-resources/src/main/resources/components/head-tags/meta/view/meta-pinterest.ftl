<!-- Pinterest Pins -->
<#if model.title?has_content>
	<meta itemprop="name" content="${model.title?replace("\"", "&quot;")}" />
</#if>
<#if model.section?has_content>
	<meta property="article:section" content="${model.section}" />
</#if>
