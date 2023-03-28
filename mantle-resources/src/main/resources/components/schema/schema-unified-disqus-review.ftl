<#if model.originalComments?has_content>
	<#compress>
		<#escape x as x?json_string>
			,"review": [
			<#list model.originalComments as originalComment>
			  {
			    "@type": "Review",
			    <#if originalComment.author.threadRating?has_content>
				    "reviewRating": {
				      "@type": "Rating",
				      "ratingValue": "${originalComment.author.threadRating?c}"
				    },
				 </#if>   
			    "author": {
			      "@type": "Person",
			      "name": "${originalComment.author.name}"
			    },
			    "reviewBody": "${originalComment.rawMessage}"
			  }
			  <#sep>,</#sep><#lt>
			</#list>  
			]
		</#escape>
	</#compress>
</#if>