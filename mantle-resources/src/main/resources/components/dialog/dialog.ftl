<#assign hasBackground = model.backgroundUrl?has_content />

<@component class="dialog ${hasBackground?then('dialog--background', '')}">
    <div class="dialog__overlay" tabindex="-1" data-a11y-dialog-hide></div>

    <div role="dialog" class="dialog__content" aria-labelledby="${manifest.instanceId}-title">

        <#if hasBackground>
            <#assign backgroundUrl><@thumborUrl img=model.backgroundUrl maxWidth=500 maxHeight=800 filters=[imageFilter.noUpscale()] /></#assign>
            <div class="dialog__background" tabindex="-1" style="background-image: url('${backgroundUrl}')"></div>
        </#if>

        <div class="dialog__heading">
            <span id="${manifest.instanceId}-title" class="dialog__title ${model.headingClass!''}">${model.heading}</span>
            <button data-a11y-dialog-hide class="dialog__close" data-click-tracked="${model.enableCloseButtonTracking?c}" aria-label="Close this dialog window">
                <@svg name=model.closeIcon />
            </button>
            <@location name="heading" class="dialog__heading-content" />
        </div>

        <@location name="content" class="dialog__main" />
    </div>
</@component>