<#if (model.originalComments?has_content && model.originalComments.feedbacks?has_content && model.originalComments.feedbacks.list?has_content)>
	<#compress>
		<#escape x as x?json_string>
			,"review": [
			<#list model.originalComments.feedbacks.list as originalComment>
			  {
			    "@type": "Review",
			    <#if originalComment.starRating?has_content>
				    "reviewRating": {
				      "@type": "Rating",
				      "ratingValue": "${originalComment.starRating}"
				    },
				</#if>   
			    "author": {
			      "@type": "Person",
			      "name": "${originalComment.displayName}"
			    },
			    "reviewBody": "${processSchemaString(originalComment.review)}"
			  }
			  <#sep>,</#sep><#lt>
			</#list>  
			]
		</#escape>
	</#compress>
</#if>