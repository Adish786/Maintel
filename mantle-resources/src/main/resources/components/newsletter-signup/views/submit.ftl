<@component class="mntl-newsletter-submit__input-wrapper">
    <#if model.sharedservices!false>
        <input type="hidden" name="objectorObjectIds" value="relationship-builder,acquisition" />
    <#else>
        <input type="hidden" name="list[]" value="${model.listName}" />
        <input type="hidden" name="customEventName" value="${model.customEventName!''}" />
        <#if model.csrfToken?has_content><input type="hidden" value="${model.csrfToken}" name="CSRFToken" /></#if>
    </#if>

    <label class="mntl-newsletter-submit__label is-vishidden" for="${manifest.instanceId}__input">Email Address</label>
    <input class="mntl-newsletter-submit__input" id="${manifest.instanceId}__input" type="email" name="email" placeholder="${model.placeholderText!''}" required pattern="\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,4}\b" />
    <@location name="submitButton" tag="" />
</@component>