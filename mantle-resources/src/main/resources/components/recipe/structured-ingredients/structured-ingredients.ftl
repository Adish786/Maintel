<#if model.list?has_content>
    <@component>
        <@location name="heading" />
        <#list model.list as listSection>
            <#if listSection.heading?has_content>
                <p class="mntl-structured-ingredients__list-heading ${model.listHeadingClass!''}">${listSection.heading}</p>
            </#if>
            <ul class="mntl-structured-ingredients__list">
                <#list listSection.ingredients.list as listItem>
                    <#if listItem.annotatedIngredient?has_content>
                        <li
                           class="mntl-structured-ingredients__list-item ${model.listItemClass!''}"
                           <#if model.useLRS && listItem.meredithId?has_content>
                                data-id="${listItem.meredithId}"
                           </#if>
                        >
                            ${listItem.annotatedIngredient}
                        </li>
                    </#if>
                </#list>
            </ul>
        </#list>
    </@component>
</#if>