<#macro transformNutritionalType type>
    <#compress>
        <#switch type>
            <#case 'carbohydrate'>
                Carbs
                <#break>
            <#case 'saturatedFat'>
                Saturated Fat
                <#break>
            <#case 'totalFat'>
                Fat
                <#break>
            <#case 'dietaryFiber'>
                Dietary Fiber
                <#break>
            <#case 'totalSugars'>
                Total Sugars
                <#break>
            <#default>
                ${type?capitalize}
        </#switch>
    </#compress>
</#macro>

<#macro tableFact name info>
    <tr class="mntl-nutrition-facts-summary__table-row">
        <td class="mntl-nutrition-facts-summary__table-cell ${model.nutrientUnitClass}">${info.quantity?round}${info.unit} </td>
        <td class="mntl-nutrition-facts-summary__table-cell ${model.nutrientNameClass}"><@transformNutritionalType type=name /></td>
    </tr>
</#macro>

<@component>
    <#assign nutrition = model.nutritionFacts />

    <h2 class="mntl-nutrition-facts-summary__heading ${model.headingClass}" colspan="2">Nutrition Facts <span class="mntl-nutrition-facts-summary__heading-aside ${model.headingAsideClass}">(per serving)</h2>
    <table class="mntl-nutrition-facts-summary__table">
        <tbody class="mntl-nutrition-facts-summary__table-body">
            <#if nutrition['calories']?has_content && nutrition['calories'].quantity??>
                <tr class="mntl-nutrition-facts-summary__table-row">
                    <td class="mntl-nutrition-facts-summary__table-cell ${model.nutrientUnitClass}">${nutrition['calories'].quantity?round?c}</td>
                    <td class="mntl-nutrition-facts-summary__table-cell ${model.nutrientNameClass}g">Calories</td>
                </tr>
            </#if>
            <#list ['totalFat', 'carbohydrate', 'protein'] as fact>
                <#if (nutrition[fact]?has_content && nutrition[fact].quantity??)>
                    <@tableFact name=fact info=nutrition[fact] />
                </#if>
            </#list>
        </tbody>
    </table>

    <@location name="content" tag="" />
</@component>
