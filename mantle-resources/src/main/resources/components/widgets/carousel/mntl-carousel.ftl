<@component class="mntl-carousel">
    <#if model.headerTitle?has_content>
        <div class="mntl-carousel__header">
        <#if model.headerCta?has_content>
            <div class="mntl-carousel__header-cta">${model.headerCta}</div>
        </#if>
            <div class="mntl-carousel__header-title">${model.headerTitle}</div>
        </div>
    </#if>

    <div class="mntl-carousel__wrapper">
        <@location name="pre-carousel" tag="" />
        <#if model.leftSvgName?has_content>
            <div class="mntl-carousel__arrow mntl-carousel__arrow--left" data-click-tracked="true">
                <@svg name="${model.leftSvgName}" classes="${model.leftSvgClasses!''}" />
            </div>
        </#if>

        <#if model.carouselItems?has_content>
            <@location name="carousel-items" tag="ul" class="mntl-carousel__items" />
        </#if>
        
        <#if model.rightSvgName?has_content>
            <div class="mntl-carousel__arrow mntl-carousel__arrow--right" data-click-tracked="true">
                <@svg name="${model.rightSvgName}" classes="${model.rightSvgClasses!''}" />
            </div>
        </#if>
        <@location name="post-carousel" tag="" />
    </div>
</@component>