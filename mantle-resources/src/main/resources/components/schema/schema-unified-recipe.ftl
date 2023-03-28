<#assign recipeServing = (model.document.recipeServing)!'' />
<#assign recipeYield = (model.document.recipeYield)!'' />
<#assign oldYieldHasContent = model.document.yield?has_content />
<#assign recipeServingHasContent = recipeServing?has_content && recipeServing.quantity?has_content />
<#assign recipeServingQuantity = recipeServingHasContent?then((recipeServing.quantity.max?has_content?then(recipeServing.quantity.max ,recipeServing.quantity.min)), 0) />
<#assign recipeYieldHasContent = recipeYield?has_content && recipeYield.quantity?has_content />
<#assign recipeYieldQuantity = recipeYieldHasContent?then(((recipeYield.quantity.max?has_content?then(recipeYield.quantity.max ,recipeYield.quantity.min)) + recipeYield.unit?has_content?then(" ${recipeYield.unit}", "")), "") />
<#assign oldYieldYieldHasContent = false />
<#assign oldYieldServingsHasContent = false />

<#if !recipeServingHasContent && !recipeYieldHasContent && oldYieldHasContent>
    <#assign yieldContents = model.document.yield?split("(") />
    <#list 0..yieldContents?size-1 as i>
        <#if i == 0>
            <#assign oldYieldYield = yieldContents[i]?trim />
            <#assign oldYieldYieldHasContent = true />
        <#else>
            <#assign oldYieldServings = yieldContents[i]?replace("[a-zA-Z&) ]+", "", "r") />
            <#assign oldYieldServingsHasContent = true />
        </#if>
    </#list>
</#if>

<#function getUnsaturatedFat nutritionList>
    <#return (((nutritionList["monounsaturatedFat"].quantity)!0) + ((nutritionList["polyunsaturatedFat"].quantity)!0))?round>
</#function>

<#assign recipeTimes = getRecipeSCTimeRanges(model.document) />

<#macro getRecipeTimeDisplay timeField>
    <#assign timeMin = recipeTimes[timeField + "Range"].minValue />
    <#assign timeMax = recipeTimes[timeField + "Range"].maxValue />

    <#if timeMin?has_content && timeMax?has_content && timeMin != timeMax>
        ,"${timeField}": {"@type": "Duration", "minValue": "PT${timeMin?c}M", "maxValue": "PT${timeMax?c}M"}
    <#elseif timeMin?has_content || timeMax?has_content>
        ,"${timeField}": "PT${timeMin?has_content?then(timeMin?c, timeMax?c)}M"
    </#if>
</#macro>

<#escape x as x?json_string>
<#-- Required Fields per Google Spec -->
,"name": "${model.document.heading}"

<#-- Recommended Fields per Google Spec -->
<#if ((model.ratingValue)?number != 0)>
,"aggregateRating": {
    "@type": "AggregateRating",
    "ratingValue": "${(model.ratingValue)?number?c}",
    "ratingCount": "${((model.ratingCount) != 0)?then((model.ratingCount)?c, 1)}"
}
</#if>

<@getRecipeTimeDisplay timeField="cookTime" />

<#if (model.document.keywords)?has_content>
    ,"keywords": "${model.document.keywords.list?join(", ")}"
</#if>
<#if (model.nutritionFacts)?has_content>
	,"nutrition": {
		"@type": "NutritionInformation"

	<#assign nutrition = model.nutritionFacts/>
	<#assign nutritionSchemaMap = {
	    "calories": "calories",
	    "carbohydrate": "carbohydrateContent",
	    "cholesterol": "cholesterolContent",
	    "dietaryFiber": "fiberContent",
	    "protein": "proteinContent",
	    "saturatedFat": "saturatedFatContent",
	    "sodium": "sodiumContent",
	    "totalSugars": "sugarContent",
	    "totalFat": "fatContent",
	    "trans-fatty-acid" : "transFatContent"
	}/>

	<#list nutritionSchemaMap as nutritionProperty, schemaProperty>
        <#if (nutrition[nutritionProperty].quantity)?has_content>
            ,"${schemaProperty}": "${nutrition[nutritionProperty].quantity?round?c} ${nutrition[nutritionProperty].unit}"
        </#if>
    </#list>
	<#if (model.document.yield)?has_content>
		,"servingSize": "${model.document.yield}"
	</#if>
	,"unsaturatedFatContent": "${getUnsaturatedFat(nutrition)?c} g"
	}
</#if>

<@getRecipeTimeDisplay timeField="prepTime" />

<#assign recipeCategory = ((model.document.grouping.course.list)![]) + ((model.document.grouping.dish.list)![]) />
<#if recipeCategory?has_content>
	, "recipeCategory": [<#list recipeCategory as category>"${category}"<#sep>,</#sep></#list>]
</#if>
<#if (model.document.grouping.cuisine.list)?has_content>
	,"recipeCuisine": [<#list model.document.grouping.cuisine.list as cuisine>"${cuisine}"<#sep>,</#sep></#list>]
</#if>

<#if (model.document.ingredient.list)?has_content>
    ,"recipeIngredient": [
        <#assign firstIngredient = true />
        <#list model.document.ingredient.list as ingredient>
            <#compress>
            <#assign ingredientString = processSchemaString(ingredient)>
            <#if ((ingredientString?length) > 0)>
                <#if firstIngredient>
                    <#assign firstIngredient = false />
                <#else>
                    ,
                </#if>
                "${ingredientString}"
            </#if>
            </#compress>
        </#list>
    ]
<#elseif (model.document.ingredientGroups.list)?has_content>
    ,"recipeIngredient": [
        <#assign firstIngredient = true />
        <#list model.document.ingredientGroups.list as ingredientGroupList>
            <#list ingredientGroupList.ingredients.list as ingredient>
                <#compress>
                <#assign ingredientString = processSchemaString(ingredient.originalIngredientText)>
                <#if ((ingredientString?length) > 0)>
                    <#if firstIngredient>
                        <#assign firstIngredient = false />
                    <#else>
                        ,
                    </#if>
                    "${ingredientString}"
                </#if>
                </#compress>
            </#list>
        </#list>
    ]
</#if>

<#if model.document.equipmentGroups??>
    <@toolMaterialGroup toolList=model.document.equipmentGroups.list />
</#if>
<#if (model.recipeInstructions![])?size gt 0>
    ,"recipeInstructions": [
        <#if model.recipeInstructions?size == 1><#t>
            <@howToSteps steps=model.recipeInstructions[0].steps />
        <#else><#t>
            <#assign firstSection = true />
            <#list model.recipeInstructions as instruction>
		    	<#assign processedSectionName = processSchemaString(instruction.name!'')>
		    	<#if ((processedSectionName?length) > 0)>
		    		<#compress>
		    		<#if firstSection>
		    			<#assign firstSection = false />
		    		<#else>
		    			,
		    		</#if>
		    		{
		    			"@type": "HowToSection",
		    			"name": "${processedSectionName}",
		    			"itemListElement": [
                            <@howToSteps steps=instruction.steps />
		    			]
		    		}
		    		</#compress>
		    	</#if>
            </#list>
        </#if><#t>
    ]
</#if>

<#if model.simplifiedRecipeYield>
    <#if recipeServingHasContent>
        ,"recipeYield": ["${recipeServingQuantity}"]
    </#if>
<#else>
    <#if recipeServingHasContent && recipeYieldHasContent>
        ,"recipeYield": ["${recipeServingQuantity}", "${recipeYieldQuantity}"]
    <#elseif recipeServingHasContent>
        ,"recipeYield": "${recipeServingQuantity}"
    <#elseif recipeYieldHasContent>
        ,"recipeYield": "${recipeYieldQuantity}"
    <#elseif oldYieldServingsHasContent && oldYieldYieldHasContent>
        ,"recipeYield": ["${oldYieldServings}", "${oldYieldYield}"]
    <#elseif oldYieldServingsHasContent>
        ,"recipeYield": "${oldYieldServings}"
    <#elseif oldYieldYieldHasContent>
        ,"recipeYield": "${oldYieldYield}"
    </#if>
</#if>

<@getRecipeTimeDisplay timeField="totalTime" />

</#escape>