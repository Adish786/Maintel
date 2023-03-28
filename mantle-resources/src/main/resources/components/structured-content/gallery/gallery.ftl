<#-- Component tag is not closed to allow contents of gallery to be added as child elements.-->
<#-- A matching EndGroup is added automatically to provide the closing tag.-->
<@component tag="div" closeTag=false>
    <#if model.title?has_content && model.titleTag?has_content>
        <${model.titleTag} class="mntl-sc-block-gallery__heading">${model.title}</${model.titleTag}>
    </#if>

    <div class="mntl-sc-block-gallery__info">
        <div class="slide-counter">
            <span class="slide-counter__current"></span>
            <span class="slide-counter__total"></span>
        </div>

        <div class="slide-caption">
            <div class="slide-caption__desc"></div>
            <div class="slide-caption__owner"></div>
        </div>
    </div>

    <@location name="gallery" tag="" />

    <div class="slides">
        <@location name="slides" tag="" />
        <#-- each slide will go in here -->
    <#-- NOTE that any additions below this line will go insides the slides div above, e.g.
    <div class="gallery">
        [content location]
        <div class="slides">
            [additions after slides]
    The closing divs for slides and the gallery itself are done inside gallery-endgroup.ftl -->
</@component>