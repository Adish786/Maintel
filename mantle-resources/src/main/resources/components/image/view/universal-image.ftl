<@optionalTag tag="${model.tag!''}" class="${extractClasses(manifest.component.referenceIdChain)} ${extractClasses(model.tagClass)} universal-image__container">
    <#if model.externalUrl?has_content>
        <@wrapInPlaceHolder model.placeholder model.placeholderWidth!model.width model.placeholderHeight!model.height>
            <img
                class="${extractClasses(model.class)} ${model.lazyload?then('lazyload', '')} universal-image__image"
                src="${model.lazyload?then('', model.externalUrl)}"
                width="${model.width?c}"
                height="${model.height?c}"
                alt="${model.alt}"
                role="${model.role}"
                data-expand="${model.expand?c}"
                data-src="${model.lazyload?then(model.externalUrl, '')}"
            />
        </@wrapInPlaceHolder>
    <#elseif (model.image)?has_content>
        <@thumborUniversalImg
            class=extractClasses(model.class) + " universal-image__image"
            img=model.image
            width=model.width
            height=model.height
            alt=model.alt
            lazyload=model.lazyload
            srcset=model.srcset
            sizes=model.sizes
            placeholderWidth=model.placeholderWidth!model.width
            placeholderHeight=model.placeholderHeight!model.height
            placeholder=model.placeholder
            fitInStyle=model.fitInStyle
            filters=model.filters
            expand=model.expand
            role=model.role
            idPrefix=model.idPrefix
            useHiResLightbox=model.useHiResLightbox
            hiResLightboxWidth=model.hiResLightboxWidth
        />
    </#if>
</@optionalTag>