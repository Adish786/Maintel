<@component>
    <#if (model.image.objectId)?has_content>
        <div class="${generateReferenceIdClasses('__header-link')}">
            <@a href="${ model.articleUrl }" target="_blank" safelist=true>
                <@location name="review-image" />
            </@a>
        </div>
    </#if>
    <@location name="rating" tag="" />
    <@location name="entity-link" />
    <div class="${generateReferenceIdClasses('__quick-facts')}">
        <ul class="${generateReferenceIdClasses('__quick-list')}">
            <#list model.reviewEntity.quickFacts.list as quickFact>
                <li class="${generateReferenceIdClasses('__quick-list__item')}">
                    ${quickFact}
                </li>
            </#list>
        </ul>
        <@location name="article-link" />
    </div>
    <@location name="entity-link" class="${generateReferenceIdClasses('__trailing-links')}" />
</@component>