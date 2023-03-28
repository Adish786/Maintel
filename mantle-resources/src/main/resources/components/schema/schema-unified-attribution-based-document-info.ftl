<#include "schema-macros.ftl" />
<#compress>
	<#escape x as x?json_string>
		 <#-- BIO documents' heading is hard-coded to `BIO`, so we prefer title first for bio docs -->
		<@compress single_line=true>
		,"headline": 
			<#if model.document.templateType=='BIO'>
			    "${model.document.title!model.document.heading}"
			<#else>
			    "${model.document.heading!model.document.title}"
			</#if>
		</@compress>
		<#if (model.potentialAction)?has_content>
		,"potentialAction": {
			"@type": "${model.potentialAction.type}",
			"target": "${model.potentialAction.target}",
			"query-input": "${model.potentialAction.queryInput}"
		}
		</#if>

		<#if (model.datePublished)?has_content>
		,"datePublished": "${model.datePublished}"
		</#if>

		<#if (model.dateModified)?has_content>
		,"dateModified": "${model.dateModified}"
		</#if>

		<#if (model.document.templateType != 'TAXONOMYSC') && (model.document.templateType != 'TAXONOMY')>
			<#if (model.document.guestAuthor.link.text)?has_content>
				,"author": [<@rawAuthor guestAuthorData=(model.document.guestAuthor) />]
			<#elseif model.authorAttributions?has_content>
				,<@listAuthors attributionModels=(model.authorAttributions)!{} role="author" />
			</#if>
			<#if model.contributorAttributions?has_content>
				,<@listAuthors attributionModels=(model.contributorAttributions)!{} role="contributor"/>
			</#if>
			<#if model.editorAttributions?has_content>
				,<@listAuthors attributionModels=(model.editorAttributions)!{} role="editor"/>
			</#if>
		</#if>

		<#if (model.document.summary.description)?has_content>
		,"description": "${model.document.summary.description}"
		</#if>
	</#escape>
</#compress>
