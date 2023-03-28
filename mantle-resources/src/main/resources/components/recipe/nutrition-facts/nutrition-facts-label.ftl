<#assign nutritionFact = model.nutritionFacts />
<#assign recipeServing = (model.document.recipeServing)!'' />

<@component>
    <button class="mntl-nutrition-facts-label__button mntl-nutrition-facts-label__button--show ${model.buttonClass}">
        <span class="mntl-nutrition-facts-label__button-text">Show Full Nutrition Label</span>
        <span class="mntl-nutrition-facts-label__button-text">Hide Full Nutrition Label</span>
    </button>

    <div class="mntl-nutrition-facts-label__wrapper mntl-nutrition-facts-label__wrapper--collapsed">
        <div class="mntl-nutrition-facts-label__contents">
            <table class="mntl-nutrition-facts-label__table">
                <thead class="mntl-nutrition-facts-label__table-head">
                    <tr>
                        <th class="mntl-nutrition-facts-label__table-head-title ${model.tableHeaderTitleClass}" colspan="2">Nutrition Facts</th>
                    </tr>
                    <tr class="mntl-nutrition-facts-label__servings">
                        <#if recipeServing?has_content && recipeServing.quantity?has_content>
                            <#assign recipeServingRange><@quantity quantityRange=recipeServing.quantity /></#assign>
                            <th class="mntl-nutrition-facts-label__table-head-subtitle" colspan="2">
                                <span class="mntl-nutrition-facts-label__table-head-pretext">Servings Per Recipe</span>
                                <span>${recipeServingRange?chop_linebreak?trim}</span>
                            </th>
                        </#if>
                    </tr>
                    <tr class="mntl-nutrition-facts-label__calories">
                        <#if nutritionFact['calories']?has_content && nutritionFact['calories'].quantity??>
                            <th class="mntl-nutrition-facts-label__table-head-subtitle" colspan="2">
                                <span class="mntl-nutrition-facts-label__table-head-pretext">Calories</span>
                                <span>${(nutritionFact['calories'].quantity!0)?round?c}</span>
                            </th>
                        </#if>
                    </tr>
                </thead>
                <tbody class="mntl-nutrition-facts-label__table-body ${model.tableBodyClass}">
                    <tr class="mntl-nutrition-facts-label__table-dv-row ${model.tableDvClass}">
                        <td colspan="2">% Daily Value *</td>
                    </tr>

                    <#if nutritionFact['totalFat']?has_content && nutritionFact['totalFat'].quantity??>
                        <tr>
                            <td>
                                <span class="mntl-nutrition-facts-label__nutrient-name mntl-nutrition-facts-label__nutrient-name--has-postfix">Total Fat</span>
                                ${(nutritionFact['totalFat'].quantity!0)?round?c}g
                            </td>

                            <td>
                                ${((nutritionFact['totalFat'].quantity!0) / 78 * 100)?round}%
                            </td>
                        </tr>
                    </#if>

                    <#if nutritionFact['saturatedFat']?has_content && nutritionFact['saturatedFat'].quantity??>
                        <tr>
                            <td>
                                <span class="mntl-nutrition-facts-label__nutrient-name mntl-nutrition-facts-label__nutrient-name--has-postfix">Saturated Fat</span>
                                ${(nutritionFact['saturatedFat'].quantity!0)?round?c}g
                            </td>

                            <td>
                                ${((nutritionFact['saturatedFat'].quantity!0) / 20 * 100)?round}%
                            </td>
                        </tr>
                    </#if>

                    <#if nutritionFact['cholesterol']?has_content && nutritionFact['cholesterol'].quantity??>
                        <tr>
                            <td>
                                <span class="mntl-nutrition-facts-label__nutrient-name mntl-nutrition-facts-label__nutrient-name--has-postfix">Cholesterol</span>
                                ${(nutritionFact['cholesterol'].quantity!0)?round?c}mg
                            </td>

                            <td>
                                ${((nutritionFact['cholesterol'].quantity!0) / 300 * 100)?round}%
                            </td>
                        </tr>
                    </#if>

                    <#if nutritionFact['sodium']?has_content && nutritionFact['sodium'].quantity??>
                        <tr>
                            <td>
                                <span class="mntl-nutrition-facts-label__nutrient-name mntl-nutrition-facts-label__nutrient-name--has-postfix">Sodium</span>
                                ${(nutritionFact['sodium'].quantity!0)?round?c}mg
                            </td>

                            <td>
                                ${((nutritionFact['sodium'].quantity!0) / 2300 * 100)?round}%
                            </td>
                        </tr>
                    </#if>

                    <#if nutritionFact['carbohydrate']?has_content && nutritionFact['carbohydrate'].quantity??>
                        <tr>
                            <td>
                                <span class="mntl-nutrition-facts-label__nutrient-name mntl-nutrition-facts-label__nutrient-name--has-postfix">Total Carbohydrate</span>
                                ${(nutritionFact['carbohydrate'].quantity!0)?round?c}g
                            </td>

                            <td>
                                ${((nutritionFact['carbohydrate'].quantity!0) / 275 * 100)?round}%
                            </td>
                        </tr>
                    </#if>

                    <#if nutritionFact['dietaryFiber']?has_content && nutritionFact['dietaryFiber'].quantity??>
                        <tr>
                            <td>
                                <span class="mntl-nutrition-facts-label__nutrient-name mntl-nutrition-facts-label__nutrient-name--has-postfix">Dietary Fiber</span>
                                ${(nutritionFact['dietaryFiber'].quantity!0)?round?c}g
                            </td>

                            <td>
                                ${((nutritionFact['dietaryFiber'].quantity!0) / 28 * 100)?round}%
                            </td>
                        </tr>
                    </#if>

                    <#if nutritionFact['totalSugars']?has_content && nutritionFact['totalSugars'].quantity??>
                        <tr>
                            <td colspan="2">
                                <span class="mntl-nutrition-facts-label__nutrient-name mntl-nutrition-facts-label__nutrient-name--has-postfix">Total Sugars</span>
                                ${(nutritionFact['totalSugars'].quantity!0)?round?c}g
                            </td>
                        </tr>
                    </#if>

                    <#if nutritionFact['addedSugars']?has_content && nutritionFact['addedSugars'].quantity??>
                        <tr>
                            <td>
                                <span class="mntl-nutrition-facts-label__nutrient-name">Includes</span>
                                ${(nutritionFact['addedSugars'].quantity!0)?round?c}g
                                <span class="mntl-nutrition-facts-label__nutrient-name">Added Sugars</span>
                            </td>
                            <td>${(nutritionFact['addedSugars'].quantity!0) / 50 * 100}%</td>
                        </tr>
                    </#if>

                    <#if nutritionFact['protein']?has_content && nutritionFact['protein'].quantity??>
                        <tr>
                            <td colspan="2">
                                <span class="mntl-nutrition-facts-label__nutrient-name mntl-nutrition-facts-label__nutrient-name--has-postfix">Protein</span>
                                ${(nutritionFact['protein'].quantity!0)?round?c}g
                            </td>
                        </tr>
                    </#if>

                    <#if nutritionFact['vitaminD']?has_content && nutritionFact['vitaminD'].quantity??>
                        <tr>
                            <td>
                                <span class="mntl-nutrition-facts-label__nutrient-name mntl-nutrition-facts-label__nutrient-name--has-postfix">Vitamin D</span>
                                ${(nutritionFact['vitaminD'].quantity!0)?round?c}mcg
                            </td>

                            <td>
                                ${((nutritionFact['vitaminD'].quantity!0) / 20 * 100)?round}%
                            </td>
                        </tr>
                    </#if>

                    <#if nutritionFact['vitaminC']?has_content && nutritionFact['vitaminC'].quantity??>
                        <tr>
                            <td>
                                <span class="mntl-nutrition-facts-label__nutrient-name mntl-nutrition-facts-label__nutrient-name--has-postfix">Vitamin C</span>
                                ${(nutritionFact['vitaminC'].quantity!0)?round?c}mg
                            </td>

                            <td>
                                ${((nutritionFact['vitaminC'].quantity!0) / 20 * 100)?round}%
                            </td>
                        </tr>
                    </#if>

                    <#if nutritionFact['calcium']?has_content && nutritionFact['calcium'].quantity??>
                        <tr>
                            <td>
                                <span class="mntl-nutrition-facts-label__nutrient-name mntl-nutrition-facts-label__nutrient-name--has-postfix">Calcium</span>
                                ${(nutritionFact['calcium'].quantity!0)?round?c}mg
                            </td>

                            <td>
                                ${((nutritionFact['calcium'].quantity!0) / 1300 * 100)?round}%
                            </td>
                        </tr>
                    </#if>

                    <#if nutritionFact['iron']?has_content && nutritionFact['iron'].quantity??>
                        <tr>
                            <td>
                                <span class="mntl-nutrition-facts-label__nutrient-name mntl-nutrition-facts-label__nutrient-name--has-postfix">Iron</span>
                                ${(nutritionFact['iron'].quantity!0)?round?c}mg
                            </td>

                            <td>
                                ${((nutritionFact['iron'].quantity!0) / 18 * 100)?round}%
                            </td>
                        </tr>
                    </#if>

                    <#if nutritionFact['potassium']?has_content && nutritionFact['potassium'].quantity??>
                        <tr>
                            <td>
                                <span class="mntl-nutrition-facts-label__nutrient-name mntl-nutrition-facts-label__nutrient-name--has-postfix">Potassium</span>
                                ${(nutritionFact['potassium'].quantity!0)?round?c}mg
                            </td>
                            <td>
                                ${((nutritionFact['potassium'].quantity!0) / 4700 * 100)?round}%
                            </td>
                        </tr>
                    </#if>

                </tbody>
            </table>

            <@location name="disclaimers" tag="" />
        </div>
    </div>
</@component>
