<#if (model.image.objectId)?has_content>
    <@thumborPrimaryImg
        class=extractClasses(manifest.component.referenceIdChain) + ' ' + extractClasses(model.class!'')
        alt=model.alt!''
        originalWidth=model.width!model.image.width
        originalHeight=model.height!model.image.height
        providedImageSources=model.providedImageSources
        blurryPlaceholder=model.blurryPlaceholder!true
        placeholder=model.placeholder!true
     />
</#if>