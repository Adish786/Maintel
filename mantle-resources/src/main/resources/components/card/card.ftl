<#-- card -->
<#assign url><@rewriteHref href=model.url/></#assign>
<#assign hasImage = (model.image.objectId)?has_content />

<#if model.imageFilters??>
    <#assign thumborFilter = [model.imageFilters] />
</#if>

<#macro setTagAttr value><#if value?has_content><#assign tagAttr>data-tag="${value}"</#assign><#else><#assign tagAttr = '' /></#if></#macro>

<#if model.taxonomy?has_content>
    <#if model.taxLevels?has_content>
        <#assign taxLevelsAttr><#compress>
            <#list model.taxLevels as taxLevel>
                <#if (model.taxonomy[taxLevel].shortHeading)?has_content>${model.taxonomy[taxLevel].shortHeading}<#sep>${model.taxLevelSep!' '}</#if><#t>
            </#list>
        </#compress></#assign>
    </#if>
    <#if model.taxTag?has_content>
        <@setTagAttr (model.taxonomy[(model.taxTag?string == 'deepest' || (model.taxTag >= model.taxonomy?size))?then(model.taxonomy?size-1, model.taxTag)].shortHeading) />
    <#elseif model.tag?has_content>
        <@setTagAttr model.tag />
    </#if>
<#elseif model.tag?has_content>
    <@setTagAttr model.tag />
<#else>
    <@setTagAttr '' />
</#if>

<#assign cardClass = "card" />
<#if !hasImage>
    <#assign cardClass = cardClass + " card--no-image" />
</#if>
<#if model.sponsored!false>
    <#assign cardClass = cardClass + " card--sponsored" />
</#if>

<@component tag="a" href=url data\-doc\-id=model.docId!'' data\-tax\-levels=taxLevelsAttr!'' data\-cta=model.cta class=cardClass>

    <@location name="card__top" />

    <#if hasImage>
        <#assign imgHeight = model.height!0 >
        <#assign ratio = model.image.width/model.image.height >
        <#if model.minImgRatio?has_content && ratio lt model.minImgRatio >
            <#assign ratio = model.minImgRatio>
            <#assign imgHeight = model.width/ratio >
        </#if>
        <div class="card__media" ${tagAttr!''}>
            <@thumborImg
                class="card__img ${model.imgClass!''}"
                img=model.image
                width=model.width
                height=imgHeight
                originalWidth=model.width
                originalHeight=imgHeight
                alt=model.image.alt!''
                lazyload=model.lazyLoad!false
                forceSize=model.forceSize!false
                srcset=model.srcset!{}
                data_dim_ratio=ratio
                filters=thumborFilter
                placeholder=model.placeholder!false
             />
        </div>
    </#if>

    <div class="card__content" ${tagAttr!''}>
        <div class="card__header"<#if model.kicker?has_content> data-kicker="${model.kicker}"</#if>></div>
        <${model.headingTag!"span"} class="card__title"><span class="card__title-text ${model.titleTextClass!''}">${model.title}</span></${model.headingTag!"span"}>
        <#if model.byline?has_content>
            <div class="card__byline mntl-card__byline ${model.bylineClass!''}" data-byline="${model.byline}"></div>
        </#if>
        <@location name="content" tag="" />
    </div>

    <@location name="card__footer" />

</@component>
