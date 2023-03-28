<#include "schema-macros.ftl" />
<#assign recipeTimes = getRecipeTimes(model.document)!{"prepTime":0,"cookTime":0,"totalTime":0} />
<script type="application/ld+json">
<#compress>
<#escape x as x?json_string>
{
	"@context": "http://schema.org",
	"@type": "Recipe",
	"name": "${model.document.heading}",
	"headline": "${model.document.heading}"
	<#if (model.document.getImageForUsage('PRIMARY').objectId)?has_content>
		,"image": {
			"@type": "ImageObject",
			"url": "<@thumborUrl img=model.document.getImageForUsage('PRIMARY') maxWidth=model.document.getImageForUsage('PRIMARY').width maxHeight=model.document.getImageForUsage('PRIMARY').height ignoreMaxBytes=utils.uncappedImageWidthsEnabled() />",
			"height": ${model.document.getImageForUsage('PRIMARY').height?c},
			"width": ${model.document.getImageForUsage('PRIMARY').width?c}
		}
	</#if>
		,"aggregateRating": {
		    "@type": "AggregateRating",
		    "ratingValue": "${model.ratingValue!'0'}",
		    "reviewCount": "${(model.ratingCount!'0')?c}"
		}
	<#if (model.document.summary.description)?has_content && model.document.summary.description??>
		,"description": "${model.document.summary.description}"
	</#if>
	<#if recipeTimes.prepTime?has_content>
		,"prepTime": "PT${recipeTimes.prepTime?c}M"
	</#if>
	<#if recipeTimes.cookTime?has_content>
		,"cookTime": "PT${recipeTimes.cookTime?c}M"
	</#if>
	<#if recipeTimes.totalTime?has_content>
		,"totalTime": "PT${recipeTimes.totalTime?c}M"
	</#if>
	<#if model.document.yield?has_content>
		,"recipeYield": "${model.document.yield}"
	</#if>
	<#if (model.document.dates.firstPublished)?has_content>
		,"datePublished": "${model.document.dates.firstPublished}"
	</#if>
	<#if (model.document.dates.displayed)?has_content>
		,"dateModified": "${model.document.dates.displayed}"
	</#if>
	<#if model.attributionModels?has_content>
	    ,<@listAuthors attributionModels=(model.authorAttributions)!{} role="author" />
	</#if>
	<#if (model.document.url)?has_content && model.logoWidth?has_content && model.logoHeight?has_content && model.verticalName?has_content && model.imageId?has_content>
		,<@publisher width=model.logoWidth height=model.logoHeight verticalName=model.verticalName imageId=model.imageId />
	</#if>
	<#if (model.document.grouping.cuisine)?has_content>
		,"recipeCuisine": [
		<#list model.document.grouping.cuisine.list as cuisine>
			"${cuisine}"
			<#if cuisine_has_next>,</#if>
		</#list>
		]
	</#if>
	<#if (model.document.grouping.course)?has_content>
		,"recipeCategory": [
		<#list model.document.grouping.course.list as course>
			"${course}"
			<#if course_has_next>,</#if>
		</#list>
		]
	</#if>
	<#if model.document.ingredient?has_content && (splitIngredients(model.document.ingredient)?size > 0)>
		,"recipeIngredient": [
			<#assign firstIngredient = true />
			<#list splitIngredients(model.document.ingredient) as ingredient>
				<#if (ingredient?trim)?has_content>
					<#if firstIngredient><#assign firstIngredient = false /><#else>,</#if>
					"${ingredient?trim}"
				</#if>
			</#list>
		]
	</#if>
	<#if (model.document.analysis.nutrition)?has_content>
		<#assign nutrition = model.document.analysis.nutrition />
		,"nutrition": {
			"@type": "NutritionInformation"
	    	<#if nutrition.kiloCalories?has_content>
	    		,"calories": "${nutrition.kiloCalories?round?c} calories"
	    	</#if>
	    	<#if nutrition.carbohydrate?has_content>
	    		,"carbohydrateContent": "${nutrition.carbohydrate?round?c} grams"
	    	</#if>
	    	<#if nutrition.cholesterol?has_content>
	    		,"cholesterolContent": "${nutrition.cholesterol?round?c} miligrams"
	    	</#if>
			<#if nutrition.saturatedFat?has_content || nutrition.monoUnsaturatedFat?has_content || nutrition.polyUnsaturatedFat?has_content>
				,"fatContent": "${(nutrition.saturatedFat!0 + nutrition.monoUnsaturatedFat!0 + nutrition.polyUnsaturatedFat!0)?round?c} grams"
			</#if>
			<#if nutrition.saturatedFat?has_content>
				,"saturatedFatContent": "${nutrition.saturatedFat} grams"
			</#if>
			<#if nutrition.monoUnsaturatedFat?has_content || nutrition.polyUnsaturatedFat?has_content>
				,"unsaturatedFatContent": "${(nutrition.monoUnsaturatedFat!0 + nutrition.polyUnsaturatedFat!0)?round?c} grams"
			</#if>
			<#-- TODO: unsaturatedFatContent -->
			<#if model.document.yield?has_content>
				,"servingSize": "${model.document.yield}"
			</#if>
			<#if nutrition.fiber?has_content>
	    		,"fiberContent": "${nutrition.fiber?round?c} grams"
	    	</#if>
			<#if nutrition.protein?has_content>
	    		,"proteinContent": "${nutrition.protein?round?c} grams"
	    	</#if>
			<#if nutrition.sodium?has_content>
	    		,"sodiumContent": "${nutrition.sodium?round?c} miligrams"
	    	</#if>
			<#if nutrition.sugar?has_content>
	    		,"sugarContent": "${nutrition.sugar?round?c} grams"
	    	</#if>
		}
	</#if>
	<#if (model.document.nutritionFacts)?has_content>
		<#assign nutrition = model.document.nutritionFacts/>
		,"nutrition": {
			"@type": "NutritionInformation"
	    	<#if nutrition["calories"]?has_content && nutrition["calories"].quantity??>
	    		,"calories": "${nutrition["calories"].quantity?round} ${nutrition["calories"].unit}"
	    	</#if>
	    	<#list ['carbohydrate', 'cholesterol', 'protein', 'saturatedFat', 'sodium'] as fact>
    			<#if nutrition[fact]?has_content && nutrition[fact].quantity??>
        			,"${fact}Content": "${nutrition[fact].quantity?round} ${nutrition[fact].unit}"
				</#if>
			</#list>
			<#if nutrition["totalFat"]?has_content && nutrition["totalFat"].quantity??>
	    		,"fatContent": "${nutrition["totalFat"].quantity?round} ${nutrition["totalFat"].unit}"
	    	</#if>
			<#if model.document.yield?has_content>
				,"servingSize": "${model.document.yield}"
			</#if>
			<#if nutrition["dietaryFiber"]?has_content && nutrition["dietaryFiber"].quantity??>
	    		,"fiberContent": "${nutrition["dietaryFiber"].quantity?round} ${nutrition["dietaryFiber"].unit}"
	    	</#if>
			<#if nutrition["totalSugars"]?has_content && nutrition["totalSugars"].quantity??>
	    		,"sugarContent": "${nutrition["totalSugars"].quantity?round} ${nutrition["totalSugars"].unit}"
	    	</#if>
			<#if (nutrition["monounsaturatedFat"]?has_content && nutrition["monounsaturatedFat"].quantity??) || (nutrition["polyunsaturatedFat"]?has_content && nutrition["monounsaturatedFat"].quantity??)>
				,"unsaturatedFatContent": "${(nutrition["monounsaturatedFat"].quantity!0 + nutrition["polyunsaturatedFat"].quantity!0)?round} g"
			</#if>
		}
	</#if>
	<#if model.document.instruction?has_content>
		,"recipeInstructions": "<#list model.document.instruction.list as block>${block.content}</#list>"
	</#if>
}
</#escape>
</#compress>
</script>
