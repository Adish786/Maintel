<#assign recipeTimes = getRecipeSCTimeRanges(model.document) />
<#assign prepTime = recipeTimes.prepTimeRange.minValue!recipeTimes.prepTime />
<#assign cookTime = recipeTimes.cookTimeRange.minValue!recipeTimes.cookTime />
<#assign totalTime = recipeTimes.totalTimeRange.minValue!recipeTimes.totalTime />
<#assign customTime = recipeTimes.customTime.customTimeMin!'' />
<#assign customTimeExists = customTime?has_content && model.customTimeLabel?has_content />
<#assign typedTimes = recipeTimes.typedTimes />

<#assign recipeServingExists = recipeServing?has_content && recipeServing.quantity?has_content />
<#assign recipeYieldExists = recipeYield?has_content && recipeYield.quantity?has_content />
<#assign recipeServing = (model.document.recipeServing)!'' />
<#assign recipeYield = (model.document.recipeYield)!'' />
<#assign fallbackYield = (model.document.yield)!'' />

<#assign recipeDetails = [] />

<#if prepTime?has_content>
    <#assign recipeDetails += [{"label": "Prep Time", "value": calculateTime(prepTime)}] />
</#if>

<#if cookTime?has_content>
    <#assign recipeDetails += [{"label": "Cook Time", "value": calculateTime(cookTime)}] />
</#if>

<#if customTimeExists>
    <#assign recipeDetails += [{"label": model.customTimeLabel, "value": calculateTime(customTime)}] />
</#if>

<#if typedTimes?has_content>
    <#list typedTimes as typedTime>
        <#assign recipeDetails += [{"label": typedTime.type, "value": calculateTime(typedTime.minValue)}] />
    </#list>
</#if>

<#if totalTime?has_content && totalTime gt 0>
    <#assign recipeDetails += [{"label": "Total Time", "value": calculateTime(totalTime)}] />
</#if>

<#if recipeServingExists>
    <#assign recipeServingRange><@quantity quantityRange=recipeServing.quantity /></#assign>
    <#assign recipeDetails += [{"label": "Servings", "value": recipeServingRange?chop_linebreak?trim+" "+(recipeServing.unit!"")}] />
</#if>

<#if recipeYieldExists>
    <#assign recipeYieldRange><@quantity quantityRange=recipeYield.quantity /></#assign>
    <#assign recipeDetails += [{"label": "Yield", "value": recipeYieldRange?chop_linebreak?trim+" "+recipeYield.unit!""}] />
<#elseif fallbackYield?has_content>
    <#assign recipeDetails += [{"label": "Yield", "value": fallbackYield}] />
</#if>

<#if recipeDetails?has_content>
    <@component>
        <div class="mntl-recipe-details__content">
            <#list recipeDetails as recipeDetail>
                <div class="mntl-recipe-details__item">
                    <div class="mntl-recipe-details__label">${recipeDetail.label}:</div>
                    <div class="mntl-recipe-details__value">${recipeDetail.value}</div>
                </div>
            </#list>
        </div>
        <@location name="nutrition-link" class="mntl-recipe-details__nutrition-link-container" />
    </@component>
</#if>