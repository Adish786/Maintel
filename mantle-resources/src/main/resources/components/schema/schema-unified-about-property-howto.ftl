<#include "schema-macros.ftl" />
<#compress>
<#escape x as x?json_string>
{
"@type": "HowTo"
	<#-- Required Fields per Google Spec -->
,"name": "${model.document.heading}"

<#if (model.document.getImageForUsage('FINALPROJECT').objectId)?has_content>
,"image": <@imageObject image=model.document.getImageForUsage('FINALPROJECT') />
</#if>
<#-- Recommended Fields per Google Spec -->
<#if (model.document.estimatedCost.price)?has_content>
,"estimatedCost": {
	"@type": "MonetaryAmount",
	"currency": "${model.document.estimatedCost.currency!'USD'}",
	"value": "${model.document.estimatedCost.price}"
}
</#if>
<#if model.document.materialGroups??>
	<#assign totalMaterialList = [] />

	<#list model.document.materialGroups.list as materialList>
		<#if materialList.materials?has_content>
			<#assign totalMaterialList = totalMaterialList + materialList.materials.list />
		</#if>
	</#list>

	<#if totalMaterialList?has_content>
		,"supply": [
			<#list totalMaterialList as material>
				{
					"@type": "HowToSupply",
					"name": "${material.description}"
					<#if (material.quantityRange)?has_content>
					,"requiredQuantity": "<@quantity quantityRange=material.quantityRange /><#if (material.unit)?has_content> ${material.unit}</#if>"
					</#if>
				}<#sep>,
			</#list>
		]
	</#if>
</#if>
<#if model.document.toolGroups??>
    <@toolMaterialGroup toolList=model.document.toolGroups.list />
</#if>
<#if (model.document.summary.description)?has_content>
    ,"description": "${model.document.summary.description}"
</#if>
<#assign hasMinWorkingTime = (model.document.workingTimeRange)?has_content && (model.document.workingTimeRange.min)?has_content && (model.document.workingTimeRange.min) gt 0 />
<#assign hasMaxWorkingTime = (model.document.workingTimeRange)?has_content && (model.document.workingTimeRange.max)?has_content && (model.document.workingTimeRange.max) gt 0 />
<#if hasMinWorkingTime || hasMaxWorkingTime>
	<#if hasMaxWorkingTime>
		,"performTime": "PT${(model.document.workingTimeRange.max/1000/60)?c}M"
	<#else>
		,"performTime": "PT${(model.document.workingTimeRange.min/1000/60)?c}M"
	</#if>
<#elseif (model.document.workingTime)?has_content && (model.document.workingTime) gt 0>
    ,"performTime": "PT${(model.document.workingTime/1000/60)?c}M"
</#if>
<#assign hasMinTotalTime = (model.document.totalTimeRange)?has_content && (model.document.totalTimeRange.min)?has_content && (model.document.totalTimeRange.min) gt 0 />
<#assign hasMaxTotalTime = (model.document.totalTimeRange)?has_content && (model.document.totalTimeRange.max)?has_content && (model.document.totalTimeRange.max) gt 0 />
<#if hasMinTotalTime || hasMaxTotalTime>
	<#if hasMaxTotalTime>
		,"totalTime": "PT${(model.document.totalTimeRange.max/1000/60)?c}M"
	<#else>
		,"totalTime": "PT${(model.document.totalTimeRange.min/1000/60)?c}M"
	</#if>
<#elseif (model.document.totalTime)?has_content && (model.document.totalTime) gt 0>
    ,"totalTime": "PT${(model.document.totalTime/1000/60)?c}M"
</#if>
<#if (model.howToInstructions![])?size gt 0>
    ,"step": [
        <#if model.howToInstructions?size == 1><#t>
            <@howToSteps steps=model.howToInstructions[0].steps listUrl=true />
        <#else><#t>
            <#list model.howToInstructions as instruction>
		    	<#assign processedSectionName = processSchemaString(instruction.name!'')>
		    	<#if ((processedSectionName?length) > 0)>
		    		<#compress>
		    		{
		    			"@type": "HowToSection",
		    			"name": "${processedSectionName}",
		    			"itemListElement": [
                            <@howToSteps steps=instruction.steps listUrl=true />
		    			]
		    		}<#sep>,
		    		</#compress>
		    	</#if>
            </#list>
        </#if><#t>
    ]
</#if>
<#if model.document.yield?has_content>
	,"yield": "${model.document.yield}"
</#if>
	<@location name="extraProperties" tag="" />
}
</#escape>
</#compress>
