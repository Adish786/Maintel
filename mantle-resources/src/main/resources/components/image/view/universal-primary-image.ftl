<@component tag="figure" class="primary-image">
    <div class="primary-image__media">
        <#if (model.image.objectId)?has_content>
            <@thumborUniversalPrimaryImg
                class="primary-image__image"
                alt=model.alt
                providedImageSources=model.providedImageSources
                placeholder=model.placeholder
                placeholderWidth=model.placeholderWidth!model.width
                placeholderHeight=model.placeholderHeight!model.height
            />
        </#if>
    </div>
    <@location name="bottom" tag="" />
</@component>