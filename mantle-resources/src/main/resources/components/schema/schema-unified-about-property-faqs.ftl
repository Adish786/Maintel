<#compress>
	<#escape x as x?json_string>
		{
			"@type": "FAQPage",
			"mainEntity": [
			 	<#list model.listOfSchemaQuestionAndAnswers as questionAnswer>
                    <#if (questionAnswer.question)?has_content && (questionAnswer.answer)?has_content>
                        {
                            "@type": "Question",
                            "name": "${questionAnswer.question}",
                            "acceptedAnswer": {
                                "@type": "Answer",
                                "text": "${questionAnswer.answer}"
                            }
                        }
                    </#if>
                    <#sep>,</#sep><#lt>
                </#list>
			]
		}
	</#escape>
</#compress>