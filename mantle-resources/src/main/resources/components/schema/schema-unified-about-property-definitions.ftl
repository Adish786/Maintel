<#compress>
	<#escape x as x?json_string>
	 	<#list model.definitions as definition>
			<#if (definition.name)?has_content && (definition.description)?has_content>
				{ 
				"@type": "DefinedTerm",
				"name": "${processSchemaString(definition.name)}",
				"description": "${processSchemaString(definition.description)}"
				}
			</#if>
			<#sep>,</#sep><#lt>
		</#list>
	</#escape>
</#compress>
