<@component class="mntl-journey-carousel">
    <#if model.headerTitle?has_content>
        <div class="mntl-journey-carousel__header">
            <div class="mntl-journey-carousel__header-cta">${model.headerCta}</div>
            <div class="mntl-journey-carousel__header-title">${model.headerTitle}</div>
        </div>
    </#if>

    <div class="mntl-journey-carousel__wrapper">
        <@location name="pre-carousel" tag="" />
        <#if model.leftSvgName?has_content>
            <div class="mntl-journey-carousel__arrow mntl-journey-carousel__arrow--left" data-click-tracked="true">
                <@svg name="${model.leftSvgName}" classes="${model.leftSvgClasses!''}" />
            </div>
        </#if>
        <#if model.journeyDocumentList?has_content>
            <@location name="carousel-items" tag="ul" class="mntl-journey-carousel__items" />
        </#if>
        <#if model.rightSvgName?has_content>
            <div class="mntl-journey-carousel__arrow mntl-journey-carousel__arrow--right" data-click-tracked="true">
                <@svg name="${model.rightSvgName}" classes="${model.rightSvgClasses!''}" />
            </div>
        </#if>
        <@location name="post-carousel" tag="" />
    </div>
</@component>
