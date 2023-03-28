<#compress>
	<#escape x as x?json_string>
	 	<#list model.listOfKeyTerms as listOfKeyTerm>
	 		<#list listOfKeyTerm as keyTerm>
				<#if (keyTerm.term)?has_content && (keyTerm.definition)?has_content>
					{
		 				"@type": "DefinedTerm",
						"name": "${keyTerm.term}",
			     		"description": "${keyTerm.definition}"
					}
				</#if>
				<#sep>,</#sep><#lt>
			</#list>
			<#sep>,</#sep><#lt>
		</#list>
	</#escape>
</#compress>