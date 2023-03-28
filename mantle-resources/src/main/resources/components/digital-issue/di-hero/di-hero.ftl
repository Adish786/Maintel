<@component tracking_container=true style=getDigitalIssueCssVariables(model.digitalIssueCssVariables)>
    <div class="mntl-di-hero__image-wrapper">
        <@location name="image" tag="" />
    </div>

    <div class="mntl-di-hero__text">

        <#if model.title?has_content>
            <h1 class="mntl-di-hero__text-title ${model.titleClass!'type--elephant'}">${model.title}</h1>
        </#if>

        <#if model.subtitle?has_content>
            <p class="mntl-di-hero__text-subtitle ${model.subtitleClass!'type--goat'}">${model.subtitle}</p>
        </#if>

        <@location name="post-title" tag="" />
    </div>

</@component>