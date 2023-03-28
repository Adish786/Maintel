<@component>
    <div class="mntl-author-tooltip__top <#if !model.image?has_content>mntl-author-tooltip__top--no-image</#if>">
        <#if model.image?has_content>
            <div class="mntl-author-tooltip__image-wrapper">
                <@thumborImg class='mntl-author-tooltip__image'
                    img=model.image
                    width=model.imageWidth!model.image.width
                    height=model.imageHeight!(model.image.height!0)
                    alt=model.imageAlt!(model.image.alt!'')
                    lazyload=true
                    srcset=model.imageSrcset
                    originalWidth=(model.image.width)!0
                    originalHeight=(model.image.height)!0
                    placeholder=true />
            </div>
        </#if>
        <@location name="preBio" tag="" />
        <div class="mntl-author-tooltip__bio">
            ${model.bio}
        </div>
    </div>
    <#if model.learnMoreLink?has_content>
        <div class="mntl-author-tooltip__bottom">
            <#if model.learnMoreText?has_content>
                <span class="mntl-author-tooltip__learn-more-text">${model.learnMoreText}</span>
            </#if>
            <@a class="mntl-author-tooltip__learn-more-link" href="${model.learnMoreLink}">${model.learnMoreLinkText}</@a>
        </div>
    </#if>
</@component>