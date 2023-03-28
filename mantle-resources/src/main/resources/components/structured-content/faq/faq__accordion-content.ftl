<@component>
    <#if itemContent.answer?has_content>
        <div class="faq-accordion__item-answer">
            ${itemContent.answer}
        </div>
        <#if itemContent.featureLink?has_content && itemContent.featureTitle?has_content>
            <div class="faq-accordion__feature-link">
                <#if model.featurePrefix?has_content>
                    <span>${model.featurePrefix}</span>
                </#if>
                <@location
                tag="span"
                name="feature-link"
                models={"uri": itemContent.featureLink, "text": itemContent.featureTitle}
                />
            </div>
        </#if>
    </#if>
</@component>
